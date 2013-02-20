package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.eclipse.gyrex.monitoring.metrics.StopWatch;
import org.eclipse.gyrex.monitoring.metrics.TimerMetric;

public final class TracingResultSetHandler implements InvocationHandler {

	private final ResultSet rs;
	private final TimerMetric metric;

	public TracingResultSetHandler(final ResultSet rs, final TimerMetric metric) {
		this.rs = rs;
		this.metric = metric;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {

		// need to handle hashCode and equals within the proxy (for proper usage in collections)
		if (method.getName().equals("hashCode") && ((null == args) || (args.length == 0)))
			return System.identityHashCode(proxy); // rely entirely on identity
		else if (method.getName().equals("equals") && (null != args) && (args.length == 1))
			return proxy == args[0]; // rely entirely on identity
		else if (method.getName().equals("toString") && ((null == args) || (args.length == 0)))
			return toString(); // use ServiceProxy implementation

		final StopWatch watch = metric.processStarted();
		try {
			return method.invoke(rs, args);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		} catch (final Exception e) {
			throw new IllegalStateException(String.format("Error calling method '%s' on ResultSet (%s). %s", method.toString(), rs, e.getMessage()), e);
		} finally {
			watch.stop();
		}
	}
}