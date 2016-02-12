package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Calendar;

public class TracingCallableStatement extends TracingPreparedStatement<CallableStatement> implements CallableStatement {

	TracingCallableStatement(final Connection connection, final CallableStatement ps, final String sql) {
		super(connection, ps, sql);
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		statement.closeOnCompletion();
	}

	@Override
	public Array getArray(final int i) throws SQLException {
		return statement.getArray(i);
	}

	@Override
	public Array getArray(final String parameterName) throws SQLException {
		return statement.getArray(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(final int parameterIndex) throws SQLException {
		return statement.getBigDecimal(parameterIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
		return statement.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
		return statement.getBigDecimal(parameterName);
	}

	@Override
	public Blob getBlob(final int i) throws SQLException {
		return statement.getBlob(i);
	}

	@Override
	public Blob getBlob(final String parameterName) throws SQLException {
		return statement.getBlob(parameterName);
	}

	@Override
	public boolean getBoolean(final int parameterIndex) throws SQLException {
		return statement.getBoolean(parameterIndex);
	}

	@Override
	public boolean getBoolean(final String parameterName) throws SQLException {
		return statement.getBoolean(parameterName);
	}

	@Override
	public byte getByte(final int parameterIndex) throws SQLException {
		return statement.getByte(parameterIndex);
	}

	@Override
	public byte getByte(final String parameterName) throws SQLException {
		return statement.getByte(parameterName);
	}

	@Override
	public byte[] getBytes(final int parameterIndex) throws SQLException {
		return statement.getBytes(parameterIndex);
	}

	@Override
	public byte[] getBytes(final String parameterName) throws SQLException {
		return statement.getBytes(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public java.io.Reader getCharacterStream(final int parameterIndex) throws SQLException {
		return statement.getCharacterStream(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public java.io.Reader getCharacterStream(final String parameterName) throws SQLException {
		return statement.getCharacterStream(parameterName);
	}

	@Override
	public Clob getClob(final int i) throws SQLException {
		return statement.getClob(i);
	}

	@Override
	public Clob getClob(final String parameterName) throws SQLException {
		return statement.getClob(parameterName);
	}

	@Override
	public java.sql.Date getDate(final int parameterIndex) throws SQLException {
		return statement.getDate(parameterIndex);
	}

	@Override
	public java.sql.Date getDate(final int parameterIndex, final Calendar cal) throws SQLException {
		return statement.getDate(parameterIndex, cal);
	}

	@Override
	public java.sql.Date getDate(final String parameterName) throws SQLException {
		return statement.getDate(parameterName);
	}

	@Override
	public java.sql.Date getDate(final String parameterName, final Calendar cal) throws SQLException {
		return statement.getDate(parameterName, cal);
	}

	@Override
	public double getDouble(final int parameterIndex) throws SQLException {
		return statement.getDouble(parameterIndex);
	}

	@Override
	public double getDouble(final String parameterName) throws SQLException {
		return statement.getDouble(parameterName);
	}

	@Override
	public float getFloat(final int parameterIndex) throws SQLException {
		return statement.getFloat(parameterIndex);
	}

	@Override
	public float getFloat(final String parameterName) throws SQLException {
		return statement.getFloat(parameterName);
	}

	@Override
	public int getInt(final int parameterIndex) throws SQLException {
		return statement.getInt(parameterIndex);
	}

	@Override
	public int getInt(final String parameterName) throws SQLException {
		return statement.getInt(parameterName);
	}

	@Override
	public long getLong(final int parameterIndex) throws SQLException {
		return statement.getLong(parameterIndex);
	}

	@Override
	public long getLong(final String parameterName) throws SQLException {
		return statement.getLong(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public java.io.Reader getNCharacterStream(final int parameterIndex) throws SQLException {
		return statement.getNCharacterStream(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public java.io.Reader getNCharacterStream(final String parameterName) throws SQLException {
		return statement.getNCharacterStream(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public NClob getNClob(final int parameterIndex) throws SQLException {
		return statement.getNClob(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public NClob getNClob(final String parameterName) throws SQLException {
		return statement.getNClob(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public String getNString(final int parameterIndex) throws SQLException {
		return statement.getNString(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public String getNString(final String parameterName) throws SQLException {
		return statement.getNString(parameterName);
	}

	@Override
	public Object getObject(final int parameterIndex) throws SQLException {
		return statement.getObject(parameterIndex);
	}

	@Override
	public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
		return statement.getObject(parameterIndex, type);
	}

	@Override
	public Object getObject(final int i, final java.util.Map<String, Class<?>> map) throws SQLException {
		return statement.getObject(i, map);
	}

	@Override
	public Object getObject(final String parameterName) throws SQLException {
		return statement.getObject(parameterName);
	}

	@Override
	public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
		return statement.getObject(parameterName, type);
	}

	@Override
	public Object getObject(final String parameterName, final java.util.Map<String, Class<?>> map) throws SQLException {
		return statement.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(final int i) throws SQLException {
		return statement.getRef(i);
	}

	@Override
	public Ref getRef(final String parameterName) throws SQLException {
		return statement.getRef(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public RowId getRowId(final int parameterIndex) throws SQLException {
		return statement.getRowId(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public RowId getRowId(final String parameterName) throws SQLException {
		return statement.getRowId(parameterName);
	}

	@Override
	public short getShort(final int parameterIndex) throws SQLException {
		return statement.getShort(parameterIndex);
	}

	@Override
	public short getShort(final String parameterName) throws SQLException {
		return statement.getShort(parameterName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public SQLXML getSQLXML(final int parameterIndex) throws SQLException {
		return statement.getSQLXML(parameterIndex);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public SQLXML getSQLXML(final String parameterName) throws SQLException {
		return statement.getSQLXML(parameterName);
	}

	@Override
	public String getString(final int parameterIndex) throws SQLException {
		return statement.getString(parameterIndex);
	}

	@Override
	public String getString(final String parameterName) throws SQLException {
		return statement.getString(parameterName);
	}

	@Override
	public java.sql.Time getTime(final int parameterIndex) throws SQLException {
		return statement.getTime(parameterIndex);
	}

	@Override
	public java.sql.Time getTime(final int parameterIndex, final Calendar cal) throws SQLException {
		return statement.getTime(parameterIndex, cal);
	}

	@Override
	public java.sql.Time getTime(final String parameterName) throws SQLException {
		return statement.getTime(parameterName);
	}

	@Override
	public java.sql.Time getTime(final String parameterName, final Calendar cal) throws SQLException {
		return statement.getTime(parameterName, cal);
	}

	@Override
	public java.sql.Timestamp getTimestamp(final int parameterIndex) throws SQLException {
		return statement.getTimestamp(parameterIndex);
	}

	@Override
	public java.sql.Timestamp getTimestamp(final int parameterIndex, final Calendar cal) throws SQLException {
		return statement.getTimestamp(parameterIndex, cal);
	}

	@Override
	public java.sql.Timestamp getTimestamp(final String parameterName) throws SQLException {
		return statement.getTimestamp(parameterName);
	}

	@Override
	public java.sql.Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
		return statement.getTimestamp(parameterName, cal);
	}

	@Override
	public java.net.URL getURL(final int parameterIndex) throws SQLException {
		return statement.getURL(parameterIndex);
	}

	@Override
	public java.net.URL getURL(final String parameterName) throws SQLException {
		return statement.getURL(parameterName);
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return statement.isCloseOnCompletion();
	}

	@Override
	public void registerOutParameter(final int parameterIndex, final int sqlType) throws SQLException {
		statement.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
		statement.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public void registerOutParameter(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
		statement.registerOutParameter(paramIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLException {
		statement.registerOutParameter(parameterName, sqlType, typeName);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setAsciiStream(final String parameterName, final java.io.InputStream x) throws SQLException {
		statement.setAsciiStream(parameterName, x);
	}

	@Override
	public void setAsciiStream(final String parameterName, final java.io.InputStream x, final int length) throws SQLException {
		statement.setAsciiStream(parameterName, x, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setAsciiStream(final String parameterName, final java.io.InputStream x, final long length) throws SQLException {
		statement.setAsciiStream(parameterName, x, length);
	}

	@Override
	public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
		statement.setBigDecimal(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setBinaryStream(final String parameterName, final java.io.InputStream x) throws SQLException {
		statement.setBinaryStream(parameterName, x);
	}

	@Override
	public void setBinaryStream(final String parameterName, final java.io.InputStream x, final int length) throws SQLException {
		statement.setBinaryStream(parameterName, x, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setBinaryStream(final String parameterName, final java.io.InputStream x, final long length) throws SQLException {
		statement.setBinaryStream(parameterName, x, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setBlob(final String parameterName, final Blob x) throws SQLException {
		statement.setBlob(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setBlob(final String parameterName, final java.io.InputStream inputStream) throws SQLException {
		statement.setBlob(parameterName, inputStream);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setBlob(final String parameterName, final java.io.InputStream inputStream, final long length) throws SQLException {
		statement.setBlob(parameterName, inputStream, length);
	}

	@Override
	public void setBoolean(final String parameterName, final boolean x) throws SQLException {
		statement.setBoolean(parameterName, x);
	}

	@Override
	public void setByte(final String parameterName, final byte x) throws SQLException {
		statement.setByte(parameterName, x);
	}

	@Override
	public void setBytes(final String parameterName, final byte x[]) throws SQLException {
		statement.setBytes(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setCharacterStream(final String parameterName, final java.io.Reader reader) throws SQLException {
		statement.setCharacterStream(parameterName, reader);
	}

	@Override
	public void setCharacterStream(final String parameterName, final java.io.Reader reader, final int length) throws SQLException {
		statement.setCharacterStream(parameterName, reader, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setCharacterStream(final String parameterName, final java.io.Reader reader, final long length) throws SQLException {
		statement.setCharacterStream(parameterName, reader, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setClob(final String parameterName, final Clob x) throws SQLException {
		statement.setClob(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setClob(final String parameterName, final java.io.Reader reader) throws SQLException {
		statement.setClob(parameterName, reader);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setClob(final String parameterName, final java.io.Reader reader, final long length) throws SQLException {
		statement.setClob(parameterName, reader, length);
	}

	@Override
	public void setDate(final String parameterName, final java.sql.Date x) throws SQLException {
		statement.setDate(parameterName, x);
	}

	@Override
	public void setDate(final String parameterName, final java.sql.Date x, final Calendar cal) throws SQLException {
		statement.setDate(parameterName, x, cal);
	}

	@Override
	public void setDouble(final String parameterName, final double x) throws SQLException {
		statement.setDouble(parameterName, x);
	}

	@Override
	public void setFloat(final String parameterName, final float x) throws SQLException {
		statement.setFloat(parameterName, x);
	}

	@Override
	public void setInt(final String parameterName, final int x) throws SQLException {
		statement.setInt(parameterName, x);
	}

	@Override
	public void setLong(final String parameterName, final long x) throws SQLException {
		statement.setLong(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNCharacterStream(final String parameterName, final java.io.Reader value) throws SQLException {
		statement.setNCharacterStream(parameterName, value);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNCharacterStream(final String parameterName, final java.io.Reader value, final long length) throws SQLException {
		statement.setNCharacterStream(parameterName, value, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNClob(final String parameterName, final java.io.Reader reader) throws SQLException {
		statement.setNClob(parameterName, reader);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNClob(final String parameterName, final java.io.Reader reader, final long length) throws SQLException {
		statement.setNClob(parameterName, reader, length);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNClob(final String parameterName, final NClob value) throws SQLException {
		statement.setNClob(parameterName, value);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setNString(final String parameterName, final String value) throws SQLException {
		statement.setNString(parameterName, value);
	}

	@Override
	public void setNull(final String parameterName, final int sqlType) throws SQLException {
		statement.setNull(parameterName, sqlType);
	}

	@Override
	public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
		statement.setNull(parameterName, sqlType, typeName);
	}

	@Override
	public void setObject(final String parameterName, final Object x) throws SQLException {
		statement.setObject(parameterName, x);
	}

	@Override
	public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
		statement.setObject(parameterName, x, targetSqlType);
	}

	@Override
	public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale) throws SQLException {
		statement.setObject(parameterName, x, targetSqlType, scale);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setRowId(final String parameterName, final RowId x) throws SQLException {
		statement.setRowId(parameterName, x);
	}

	@Override
	public void setShort(final String parameterName, final short x) throws SQLException {
		statement.setShort(parameterName, x);
	}

	/**
	 * @since 1.2
	 */
	@Override
	public void setSQLXML(final String parameterName, final SQLXML xmlObject) throws SQLException {
		statement.setSQLXML(parameterName, xmlObject);
	}

	@Override
	public void setString(final String parameterName, final String x) throws SQLException {
		statement.setString(parameterName, x);
	}

	@Override
	public void setTime(final String parameterName, final java.sql.Time x) throws SQLException {
		statement.setTime(parameterName, x);
	}

	@Override
	public void setTime(final String parameterName, final java.sql.Time x, final Calendar cal) throws SQLException {
		statement.setTime(parameterName, x, cal);
	}

	@Override
	public void setTimestamp(final String parameterName, final java.sql.Timestamp x) throws SQLException {
		statement.setTimestamp(parameterName, x);
	}

	@Override
	public void setTimestamp(final String parameterName, final java.sql.Timestamp x, final Calendar cal) throws SQLException {
		statement.setTimestamp(parameterName, x, cal);
	}

	@Override
	public void setURL(final String parameterName, final java.net.URL val) throws SQLException {
		statement.setURL(parameterName, val);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return statement.wasNull();
	}

}
