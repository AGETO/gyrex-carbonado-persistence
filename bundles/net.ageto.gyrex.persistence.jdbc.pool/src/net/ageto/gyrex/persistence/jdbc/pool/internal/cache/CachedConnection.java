/*******************************************************************************
 * Copyright (c) 2013 <enter-company-name-here> and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 *******************************************************************************/
package net.ageto.gyrex.persistence.jdbc.pool.internal.cache;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * A non-closable, delegating connection
 */
public class CachedConnection implements Connection {

	private final Connection connection;

	public CachedConnection(final Connection connection) {
		this.connection = connection;
	}

	@Override
	public void abort(final Executor executor) throws SQLException {
		connection.abort(executor);
	}

	@Override
	public void clearWarnings() throws SQLException {
		connection.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		// no-op
		connection.close();
	}

	public void closeUnderlyingConnection() throws SQLException {
		connection.close();
	}

	@Override
	public void commit() throws SQLException {
		connection.commit();
	}

	@Override
	public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
		return connection.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return connection.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return connection.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return connection.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return connection.createSQLXML();
	}

	@Override
	public Statement createStatement() throws SQLException {
		return connection.createStatement();
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return connection.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
		return connection.createStruct(typeName, attributes);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return connection.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException {
		return connection.getCatalog();
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return connection.getClientInfo();
	}

	@Override
	public String getClientInfo(final String name) throws SQLException {
		return connection.getClientInfo(name);
	}

	@Override
	public int getHoldability() throws SQLException {
		return connection.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return connection.getNetworkTimeout();
	}

	@Override
	public String getSchema() throws SQLException {
		return connection.getSchema();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return connection.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return connection.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return connection.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return connection.isClosed();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return connection.isReadOnly();
	}

	@Override
	public boolean isValid(final int timeout) throws SQLException {
		return connection.isValid(timeout);
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		return connection.isWrapperFor(iface);
	}

	@Override
	public String nativeSQL(final String sql) throws SQLException {
		return connection.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(final String sql) throws SQLException {
		return connection.prepareCall(sql);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
		return connection.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
		return connection.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
		return connection.prepareStatement(sql, columnNames);
	}

	@Override
	public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
		connection.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback() throws SQLException {
		connection.rollback();
	}

	@Override
	public void rollback(final Savepoint savepoint) throws SQLException {
		connection.rollback(savepoint);
	}

	@Override
	public void setAutoCommit(final boolean autoCommit) throws SQLException {
		connection.setAutoCommit(autoCommit);
	}

	@Override
	public void setCatalog(final String catalog) throws SQLException {
		connection.setCatalog(catalog);
	}

	@Override
	public void setClientInfo(final Properties properties) throws SQLClientInfoException {
		connection.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
		connection.setClientInfo(name, value);
	}

	@Override
	public void setHoldability(final int holdability) throws SQLException {
		connection.setHoldability(holdability);
	}

	@Override
	public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
		connection.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setReadOnly(final boolean readOnly) throws SQLException {
		connection.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return connection.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(final String name) throws SQLException {
		return connection.setSavepoint(name);
	}

	@Override
	public void setSchema(final String schema) throws SQLException {
		connection.setSchema(schema);
	}

	@Override
	public void setTransactionIsolation(final int level) throws SQLException {
		connection.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
		connection.setTypeMap(map);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("NonClosableConnection [");
		if (connection != null) {
			builder.append("connection=");
			builder.append(connection);
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		return connection.unwrap(iface);
	}
}
