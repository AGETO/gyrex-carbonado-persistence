package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import net.ageto.gyrex.persistence.carbonado.storage.tracing.TracingContext;

public class TracingDataSource implements DataSource {

	public static DataSource wrap(final DataSource ds, final TracingContext tracingContext) {
		if (ds == null) {
			throw new IllegalArgumentException("DataSource must not be null!");
		}
		return new TracingDataSource(ds, tracingContext);
	}

	private final DataSource ds;
	private final TracingContext tracingContext;

	private TracingDataSource(final DataSource ds, final TracingContext tracingContext) {
		this.ds = ds;
		this.tracingContext = tracingContext;
	}

	public void close() throws SQLException {
		JdbcHelper.closeQuietly(ds);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return new TracingConnection(ds.getConnection(), tracingContext);
	}

	@Override
	public Connection getConnection(final String username, final String password) throws SQLException {
		return new TracingConnection(ds.getConnection(username, password), tracingContext);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return ds.getLoginTimeout();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return ds.getLogWriter();
	}

	@Override
	public boolean isWrapperFor(final Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLoginTimeout(final int seconds) throws SQLException {
		ds.setLoginTimeout(seconds);
	}

	@Override
	public void setLogWriter(final PrintWriter writer) throws SQLException {
		ds.setLogWriter(writer);
	}

	@Override
	public <T> T unwrap(final Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}
}
