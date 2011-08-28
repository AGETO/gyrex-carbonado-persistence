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

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.PoolUtil;
import com.jolbox.bonecp.hooks.AbstractConnectionHook;
import com.jolbox.bonecp.hooks.AcquireFailConfig;

/**
 * Connection monitor for BoneCP.
 */
public class BoneCPConnectionMonitor extends AbstractConnectionHook {

	private static final Logger LOG = LoggerFactory.getLogger(BoneCPConnectionMonitor.class);

	private static final int INITIAL_CONNECT_DELAY = 1000;
	private static final int MAX_CONNECT_DELAY = 30000; // don't wait more than 30 seconds

	private final String poolId;
	private final String debugInfo;

	private volatile int delay;

	public BoneCPConnectionMonitor(final String poolId, final String debugInfo) {
		this.poolId = poolId;
		this.debugInfo = debugInfo;
	}

	private int nextDelay() {
		if (delay < INITIAL_CONNECT_DELAY) {
			return delay = INITIAL_CONNECT_DELAY;
		}
		return delay = Math.min(MAX_CONNECT_DELAY, delay * 2);
	}

	@Override
	public void onAcquire(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection acquired", connection });
		}
		// reset delay on successful connect
		delay = INITIAL_CONNECT_DELAY;
	}

	@Override
	public boolean onAcquireFail(final Throwable t, final AcquireFailConfig acquireConfig) {
		// log warning
		LOG.warn("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, acquireConfig.getLogMessage(), ExceptionUtils.getMessage(t) });

		// get next delay
		final int wait = nextDelay();

		// log re-try message
		LOG.info("[{}] {}: Will try re-connect in {} seconds.", new Object[] { poolId, debugInfo, wait / 1000 });
		try {
			// wait
			Thread.sleep(wait);
			return true;
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			// abort reconnect
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
	}

	@Override
	public boolean onConnectionException(final ConnectionHandle connection, final String state, final Throwable t) {
		// if it's a communication exception, a mysql deadlock or an implementation-specific error code, flag this connection as being potentially broken.
		// state == 40001 is mysql specific triggered when a deadlock is detected
		// state == HY000 is firebird specific triggered when a connection is broken
		final char firstChar = state.charAt(0);
		if (state.equals("40001") || state.equals("HY000") || state.startsWith("08") || ((firstChar >= '5') && (firstChar <= '9')) || ((firstChar >= 'I') && (firstChar <= 'Z'))) {
			// assume broken
			LOG.warn("[{}] {}: Broken connection - State {} - {}", new Object[] { poolId, debugInfo, state, ExceptionUtils.getMessage(t) });

			// handle MySQL connection issues in a special way
			// (http://jolbox.com/forum/viewtopic.php?f=3&t=136&start=10#p694)
			if (state.equals("08S01")) {
				LOG.warn("[{}] {}: Detected MySQL connection issue. Attempting termination of all connections in pool.", new Object[] { poolId, debugInfo, state, ExceptionUtils.getMessage(t) });
				final BoneCP pool = connection.getPool();
				try {
					final Method terminateAllConnections = pool.getClass().getDeclaredMethod("terminateAllConnections", (Class[]) null);
					if (!terminateAllConnections.isAccessible()) {
						terminateAllConnections.setAccessible(true);
					}
					terminateAllConnections.invoke(pool, (Object[]) null);
				} catch (final Exception e) {
					// ignore
					LOG.warn("[{}] {}: Unable to terminate all existing connections. Pool might contain broken connections. {}", new Object[] { poolId, debugInfo, ExceptionUtils.getMessage(t) });
				}
			}

			return true;
		}

		// not broken
		return false;
	}

	@Override
	public void onDestroy(final ConnectionHandle connection) {
		if (PoolDebug.debug) {
			LOG.debug("[{}] {}: {} - {}", new Object[] { poolId, debugInfo, "connection destroyed", connection });
		}
	}

	@Override
	public void onQueryExecuteTimeLimitExceeded(final ConnectionHandle handle, final Statement statement, final String sql, final Map<Object, Object> logParams, final long timeElapsedInNs) {
		LOG.warn("[{}] [SLOW QUERY] {} ({}ns): {}", new Object[] { poolId, debugInfo, timeElapsedInNs, PoolUtil.fillLogParams(sql, logParams) });
	}
}
