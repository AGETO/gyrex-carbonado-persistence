package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Calendar;

import org.eclipse.gyrex.monitoring.metrics.StopWatch;
import org.eclipse.gyrex.monitoring.metrics.TimerMetric;

import net.ageto.gyrex.persistence.carbonado.storage.jdbc.ITracingConstants;

public class TracingPreparedStatement<T extends PreparedStatement> extends TracingStatement<T> implements PreparedStatement {
	protected final String sql;

	TracingPreparedStatement(final Connection connection, final T ps, final String sql) {
		super(connection, ps);
		this.sql = sql;
	}

	@Override
	public void addBatch() throws SQLException {
		statement.addBatch();
	}

	@Override
	public void clearParameters() throws SQLException {
		statement.clearParameters();
	}

	@Override
	public boolean execute() throws SQLException {
		final TimerMetric metric = getMetricForStatement(sql);
		if (metric == null)
			return statement.execute();

		final StopWatch watch = metric.processStarted();
		try {
			return statement.execute();
		} finally {
			watch.stop();
		}
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		final TimerMetric metric = getMetric(ITracingConstants.METRIC_ID_SELECTS);
		if (metric == null)
			return statement.executeQuery();

		final ResultSet rs;
		final StopWatch watch = metric.processStarted();
		try {
			rs = statement.executeQuery();
		} finally {
			watch.stop();
		}

		final TimerMetric rsMetric = getMetric(ITracingConstants.METRIC_ID_FETCH);
		if (rsMetric == null)
			return rs;

		return tracingResultSet(rs, rsMetric);
	}

	@Override
	public int executeUpdate() throws SQLException {
		final TimerMetric metric = getMetric(ITracingConstants.METRIC_ID_OTHER);
		if (metric == null)
			return statement.executeUpdate();

		final StopWatch watch = metric.processStarted();
		try {
			return statement.executeUpdate();
		} finally {
			watch.stop();
		}
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return statement.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return statement.getParameterMetaData();
	}

	@Override
	public void setArray(final int i, final Array x) throws SQLException {
		statement.setArray(i, x);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final java.io.InputStream x) throws SQLException {
		statement.setAsciiStream(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final java.io.InputStream x, final int length) throws SQLException {
		statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setAsciiStream(final int parameterIndex, final java.io.InputStream x, final long length) throws SQLException {
		statement.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
		statement.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final java.io.InputStream x) throws SQLException {
		statement.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final java.io.InputStream x, final int length) throws SQLException {
		statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(final int parameterIndex, final java.io.InputStream x, final long length) throws SQLException {
		statement.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBlob(final int i, final Blob x) throws SQLException {
		statement.setBlob(i, x);
	}

	@Override
	public void setBlob(final int parameterIndex, final java.io.InputStream inputStream) throws SQLException {
		statement.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setBlob(final int parameterIndex, final java.io.InputStream inputStream, final long length) throws SQLException {
		statement.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
		statement.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(final int parameterIndex, final byte x) throws SQLException {
		statement.setByte(parameterIndex, x);
	}

	@Override
	public void setBytes(final int parameterIndex, final byte x[]) throws SQLException {
		statement.setBytes(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final java.io.Reader reader) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final java.io.Reader reader, final int length) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setCharacterStream(final int parameterIndex, final java.io.Reader reader, final long length) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setClob(final int i, final Clob x) throws SQLException {
		statement.setClob(i, x);
	}

	@Override
	public void setClob(final int parameterIndex, final java.io.Reader reader) throws SQLException {
		statement.setClob(parameterIndex, reader);
	}

	@Override
	public void setClob(final int parameterIndex, final java.io.Reader reader, final long length) throws SQLException {
		statement.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setDate(final int parameterIndex, final java.sql.Date x) throws SQLException {
		statement.setDate(parameterIndex, x);
	}

	@Override
	public void setDate(final int parameterIndex, final java.sql.Date x, final Calendar cal) throws SQLException {
		statement.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setDouble(final int parameterIndex, final double x) throws SQLException {
		statement.setDouble(parameterIndex, x);
	}

	@Override
	public void setFloat(final int parameterIndex, final float x) throws SQLException {
		statement.setFloat(parameterIndex, x);
	}

	@Override
	public void setInt(final int parameterIndex, final int x) throws SQLException {
		statement.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(final int parameterIndex, final long x) throws SQLException {
		statement.setLong(parameterIndex, x);
	}

	@Override
	public void setNCharacterStream(final int parameterIndex, final java.io.Reader value) throws SQLException {
		statement.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(final int parameterIndex, final java.io.Reader value, final long length) throws SQLException {
		statement.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(final int parameterIndex, final java.io.Reader reader) throws SQLException {
		statement.setNClob(parameterIndex, reader);
	}

	@Override
	public void setNClob(final int parameterIndex, final java.io.Reader reader, final long length) throws SQLException {
		statement.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
		statement.setNClob(parameterIndex, value);
	}

	@Override
	public void setNString(final int parameterIndex, final String value) throws SQLException {
		statement.setNString(parameterIndex, value);
	}

	@Override
	public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
		statement.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
		statement.setNull(paramIndex, sqlType, typeName);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x) throws SQLException {
		statement.setObject(parameterIndex, x);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
		statement.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
		statement.setObject(parameterIndex, x, targetSqlType, scale);
	}

	@Override
	public void setRef(final int i, final Ref x) throws SQLException {
		statement.setRef(i, x);
	}

	@Override
	public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
		statement.setRowId(parameterIndex, x);
	}

	@Override
	public void setShort(final int parameterIndex, final short x) throws SQLException {
		statement.setShort(parameterIndex, x);
	}

	@Override
	public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
		statement.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public void setString(final int parameterIndex, final String x) throws SQLException {
		statement.setString(parameterIndex, x);
	}

	@Override
	public void setTime(final int parameterIndex, final java.sql.Time x) throws SQLException {
		statement.setTime(parameterIndex, x);
	}

	@Override
	public void setTime(final int parameterIndex, final java.sql.Time x, final Calendar cal) throws SQLException {
		statement.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(final int parameterIndex, final java.sql.Timestamp x) throws SQLException {
		statement.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setTimestamp(final int parameterIndex, final java.sql.Timestamp x, final Calendar cal) throws SQLException {
		statement.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(final int parameterIndex, final java.io.InputStream x, final int length) throws SQLException {
		statement.setUnicodeStream(parameterIndex, x, length);
	}

	@Override
	public void setURL(final int parameterIndex, final java.net.URL x) throws SQLException {
		statement.setURL(parameterIndex, x);
	}
}
