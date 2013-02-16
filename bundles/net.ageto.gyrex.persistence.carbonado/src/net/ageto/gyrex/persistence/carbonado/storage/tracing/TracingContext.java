package net.ageto.gyrex.persistence.carbonado.storage.tracing;

import org.eclipse.gyrex.monitoring.metrics.TimerMetric;

/**
 * A context for tracing metrics
 */
public interface TracingContext {

	TimerMetric getTimer(String name);

	boolean isEnabled();

}
