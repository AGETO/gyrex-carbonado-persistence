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

/**
 * A data source which wraps another data source and configures the returned
 * connection to use a specific catalog.
 */
public class PoolDataSource implements DataSource {

	private final String catalog;
	private final String poolId;

	/**
	 * Creates a new instance.
	 * 
	 * @param poolId
	 *            the data source to wrap
	 * @param catalog
	 *            the catalog to use for the connection
	 */
	public PoolDataSource(final String poolId, final String catalog) {
		this.poolId = poolId;
		this.catalog = StringUtils.trimToNull(catalog);
	}

	private Connection configure(final Connection connection) throws SQLException {
		// set the catalog to operate it
		if (null != catalog) {
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
		builder.append("ConnectionCustomizingDataSource [poolId=");
		builder.append(poolId);
		builder.append(", catalog=");
		builder.append(catalog);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return getPoolDataSource().unwrap(iface);
	}

}
