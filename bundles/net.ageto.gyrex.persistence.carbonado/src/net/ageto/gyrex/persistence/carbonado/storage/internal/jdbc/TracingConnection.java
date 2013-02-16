package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

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

import net.ageto.gyrex.persistence.carbonado.storage.tracing.TracingContext;

public class TracingConnection implements Connection {

	private final TracingContext tracingContext;
	private final Connection connection;

	TracingConnection(final Connection connection, final TracingContext tracingContext) {
		this.connection = connection;
		this.tracingContext = tracingContext;
	}

	@Override
	public void clearWarnings() throws SQLException {
		connection.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
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
		return new TracingStatement<Statement>(this, connection.createStatement(), tracingContext);
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return new TracingStatement<Statement>(this, connection.createStatement(resultSetType, resultSetConcurrency), tracingContext);
	}

	@Override
	public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return new TracingStatement<Statement>(this, connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability), tracingContext);
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
	public java.util.Properties getClientInfo() throws SQLException {
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
	public int getTransactionIsolation() throws SQLException {
		return connection.getTransactionIsolation();
	}

	@Override
	public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
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

	/**
	 * @since 1.2
	 */
	@Override
	public boolean isValid(final int timeout) throws SQLException {
		return connection.isValid(timeout);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String nativeSQL(final String sql) throws SQLException {
		return connection.nativeSQL(sql);
	}

	@Override
	public CallableStatement prepareCall(final String sql) throws SQLException {
		return new TracingCallableStatement(this, connection.prepareCall(sql), sql, tracingContext);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return new TracingCallableStatement(this, connection.prepareCall(sql, resultSetType, resultSetConcurrency), sql, tracingContext);
	}

	@Override
	public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return new TracingCallableStatement(this, connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql, autoGeneratedKeys), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int columnIndexes[]) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql, columnIndexes), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql, resultSetType, resultSetConcurrency), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql, tracingContext);
	}

	@Override
	public PreparedStatement prepareStatement(final String sql, final String columnNames[]) throws SQLException {
		return new TracingPreparedStatement<PreparedStatement>(this, connection.prepareStatement(sql, columnNames), sql, tracingContext);
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
	public void setClientInfo(final java.util.Properties properties) throws SQLClientInfoException {
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
	public void setTransactionIsolation(final int level) throws SQLException {
		connection.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(final java.util.Map<String, Class<?>> map) throws SQLException {
		connection.setTypeMap(map);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}
}
