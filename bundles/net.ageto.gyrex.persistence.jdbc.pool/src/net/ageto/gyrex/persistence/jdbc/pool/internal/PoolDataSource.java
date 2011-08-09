/**
 * Copyright (c) 2011 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 */
package net.ageto.gyrex.persistence.jdbc.pool.internal;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data source which wraps another data source and configures the returned
 * connection to use a specific catalog.
 */
public class PoolDataSource implements DataSource {

	private static final Logger LOG = LoggerFactory.getLogger(PoolDataSource.class);

	private final String catalog;
	private final String poolId;

	private final String name;

	private final String description;

	/**
	 * Creates a new instance.
	 * 
	 * @param poolId
	 *            the data source to wrap
	 * @param catalog
	 *            the catalog to use for the connection
	 * @param description
	 *            the data source description (for debugging purposes)
	 * @param name
	 *            the data source name
	 */
	public PoolDataSource(final String poolId, final String catalog, final String name, final String description) {
		this.poolId = poolId;
		this.name = StringUtils.trimToNull(name);
		this.description = StringUtils.trimToNull(description);
		this.catalog = StringUtils.trimToNull(catalog);
	}

	private Connection configure(final Connection connection) throws SQLException {
		// set the catalog to operate it
		if (null != catalog) {
			if (PoolDebug.debug) {
				LOG.debug("Setting catalog {} for connection {} from pool {}", new Object[] { catalog, connection, poolId });
			}
			connection.setCatalog(catalog);
		}
		return connection;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return configure(getPoolDataSource().getConnection());
	}

	@Override
	public Connection getConnection(final String username, final String password) throws SQLException {
		return configure(getPoolDataSource().getConnection(username, password));
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return getPoolDataSource().getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return getPoolDataSource().getLogWriter();
	}

	private DataSource getPoolDataSource() {
		return PoolActivator.getInstance().getRegistry().getDataSource(poolId);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return getPoolDataSource().isWrapperFor(iface);
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		throw new SQLException("not allowed");
	}

	@Override
	public void setLogWriter(final PrintWriter out) throws SQLException {
		throw new SQLException("not allowed");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PoolDataSource ");
		builder.append(poolId).append(" [");
		String spearator = "";
		if (null != description) {
			builder.append(description);
			spearator = ", ";
		}
		if (null != name) {
			builder.append(spearator).append("name=").append(name);
			spearator = ", ";
		}
		if (null != catalog) {
			builder.append(spearator).append("catalog=").append(catalog);
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return getPoolDataSource().unwrap(iface);
	}

}
