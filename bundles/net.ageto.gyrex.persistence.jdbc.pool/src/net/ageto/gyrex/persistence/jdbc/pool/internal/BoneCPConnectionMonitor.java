/**
 * Copyright (c) 2010 Gunnar Wagenknecht and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.jdbc.pool.internal;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.text.StrBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.PoolUtil;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import com.jolbox.bonecp.hooks.ConnectionState;

/**
 * Connection monitor for BoneCP.
 */
public class BoneCPConnectionMonitor extends AbstractConnectionHook {

	private static final Logger LOG = LoggerFactory.getLogger(BoneCPConnectionMonitor.class);

	// initial wait time for re-connects
	private static final int INITIAL_CONNECT_DELAY = 1000;

	// log error after 3rd attempt
	private static final int LOG_ERROR_THRESHOLD = INITIAL_CONNECT_DELAY * 2 * 2;

	// don't wait more than 30 seconds
	// (note, make sure this is higher than LOG_ERROR_THRESHOLD or errors will never be logged)
	private static final int MAX_CONNECT_DELAY = 30000;

	private final String poolId;
	private final String debugInfo;

	private volatile int delay;

	private final CopyOnWriteArraySet<ConnectionHandle> closingConnections = new CopyOnWriteArraySet<ConnectionHandle>();

	public BoneCPConnectionMonitor(final String poolId, final String debugInfo) {
		this.poolId = poolId;
		this.debugInfo = debugInfo;
	}

	private void closeConnectionHandle(final ConnectionHandle connection) {
		// ignore cyclic access
		if (closingConnections.contains(connection)) {
			if (PoolDebug.debug) {
				LOG.trace("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "ignoring cyclic close request", connection });
			}
			return;
		}

		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "closing connection", connection });
		}

		// protect against cyclic entrance (the call to close below can trigger the hook again)
		closingConnections.add(connection);
		try {
			connection.close();
		} catch (final SQLException e) {
			LOG.error("[{}] {}: Unable to close connection and return it to the pool: {}", new Object[] { poolId, debugInfo, getMessage(e) });
		} finally {
			closingConnections.remove(connection);
		}

		if (PoolDebug.debug) {
			LOG.trace("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection closed", connection });
		}

	}

	private String getMessage(final Throwable t) {
		if (t instanceof SQLException) {
			final StrBuilder message = new StrBuilder();
			for (final Throwable e : (SQLException) t) {
				message.appendSeparator("; ");
				if (e instanceof SQLException) {
					message.append(e.getMessage()).append(" SQL state (").append(((SQLException) e).getSQLState()).append(") vendor code (").append(((SQLException) e).getErrorCode()).append(')');
				} else {
					message.append(" caused by ").append(ExceptionUtils.getMessage(e));
				}
			}
			return message.toString();
		}
		return ExceptionUtils.getMessage(t);
	}

	private int nextDelay() {
		// check if it was resetted recently
		if (delay < INITIAL_CONNECT_DELAY) {
			return delay = INITIAL_CONNECT_DELAY;
		}

		// calculate next delay (but never more than MAX_CONNECT_DELAY)
		return delay = Math.min(MAX_CONNECT_DELAY, delay * 2);
	}

	@Override
	public void onAcquire(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection acquired", connection });
		}
		// reset delay on successful connect
		// (note, we set it to -1 because nextDelay() should return INITIAL_CONNECT_DELAY on next call)
		delay = -1;
	}

	@Override
	public boolean onAcquireFail(final Throwable t, final AcquireFailConfig acquireConfig) {
		// get next delay
		final int wait = nextDelay();

		// log error if we reached the maximum wait time
		if (wait > LOG_ERROR_THRESHOLD) {
			// log error (database connection could not be established)
			LOG.error("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, acquireConfig.getLogMessage(), getMessage(t), t });
		} else {
			// log warning
			LOG.warn("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, acquireConfig.getLogMessage(), getMessage(t) });
		}

		// log re-try message
		LOG.info("[{}] {}: Will try re-connect in {} seconds.", new Object[] { poolId, debugInfo, wait / 1000 });
		try {
			// wait
			Thread.sleep(wait);
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			// abort reconnect
			LOG.warn("[{}] {}: Interrupted while waiting. Aborting re-connect.", new Object[] { poolId, debugInfo });
			return false;
		}
	}

	@Override
	public void onCheckIn(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection check-in", connection });
		}

		// reset catalog on check-in
		try {
			if (connection.getCatalog() != null) {
				connection.setCatalog(null);
			}
		} catch (final SQLException ignored) {
			if (PoolDebug.debug) {
				LOG.debug("[{}] {}: unable to reset catalog on connection {} - {}", new Object[] { poolId, debugInfo, connection, ignored });
			}
		}
	}

	@Override
	public void onCheckOut(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection check-out", connection });
		}

		// never check out broken connections
		if (connection.isPossiblyBroken() && !connection.isConnectionAlive()) {
			LOG.warn("[{}] {}: Detected a broken connection during check-out. Connection will be closed to prevent connection leaks. ({})", new Object[] { poolId, debugInfo, connection });
			closeConnectionHandle(connection);
		}
	}

	@Override
	public boolean onConnectionException(final ConnectionHandle connection, final String state, final Throwable t) {
		return super.onConnectionException(connection, state, t);
	}

	@Override
	public void onDestroy(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection destroyed", connection });
		}
	}

	@Override
	public ConnectionState onMarkPossiblyBroken(final ConnectionHandle connection, final String state, final SQLException e) {
		// handle MySQL connection issues in a special way
		// (http://jolbox.com/forum/viewtopic.php?f=3&t=136&start=10#p694)
		if (state.equals("08S01")) {
			// the connection is broken; close it immediatly so that 
			// it will be released back to the pool and terminated together 
			// with all other connections
			closeConnectionHandle(connection);

			// terminate all other connections in pool
			LOG.error("[{}] {}: Detected MySQL connection issue. All connections in pool will be terminated.", new Object[] { poolId, debugInfo, state, getMessage(e) });
			return ConnectionState.TERMINATE_ALL_CONNECTIONS;
		}

		// default behavior
		return super.onMarkPossiblyBroken(connection, state, e);
	}

	@Override
	public void onQueryExecuteTimeLimitExceeded(final ConnectionHandle handle, final Statement statement, final String sql, final Map<Object, Object> logParams, final long timeElapsedInNs) {
		LOG.error("[{}] [SLOW QUERY] {} ({}ns): {}", new Object[] { poolId, debugInfo, timeElapsedInNs, PoolUtil.fillLogParams(sql, logParams) });
	}
}
