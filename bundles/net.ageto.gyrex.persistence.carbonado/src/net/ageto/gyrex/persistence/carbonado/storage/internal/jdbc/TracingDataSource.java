package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.eclipse.gyrex.monitoring.metrics.StopWatch;
import org.eclipse.gyrex.monitoring.metrics.TimerMetric.TimerMetricFactory;
import org.eclipse.gyrex.monitoring.profiling.Profiler;
import org.eclipse.gyrex.monitoring.profiling.Transaction;

import net.ageto.gyrex.persistence.carbonado.storage.jdbc.ITracingConstants;

public class TracingDataSource implements DataSource {

	public static DataSource wrap(final DataSource ds) {
		if (ds == null)
			throw new IllegalArgumentException("DataSource must not be null!");
		return new TracingDataSource(ds);
	}

	private final DataSource ds;

	private TracingDataSource(final DataSource ds) {
		this.ds = ds;
	}

	public void close() throws SQLException {
		JdbcHelper.closeQuietly(ds);
	}

	@Override
	public Connection getConnection() throws SQLException {
		final Transaction tx = Profiler.getTransaction();
		if (tx == null)
			return ds.getConnection();

		final StopWatch watch = tx.getOrCreateMetric(ITracingConstants.METRIC_ID_CONNECTION_WAITS, TimerMetricFactory.NANOSECONDS).processStarted();
		try {
			return new TracingConnection(ds.getConnection());
		} finally {
			watch.stop();
		}
	}

	@Override
	public Connection getConnection(final String username, final String password) throws SQLException {
		final Transaction tx = Profiler.getTransaction();
		if (tx == null)
			return ds.getConnection(username, password);

		final StopWatch watch = tx.getOrCreateMetric(ITracingConstants.METRIC_ID_CONNECTION_WAITS, TimerMetricFactory.NANOSECONDS).processStarted();
		try {
			return new TracingConnection(ds.getConnection(username, password));
		} finally {
			watch.stop();
		}
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
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return ds.getParentLogger();
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
