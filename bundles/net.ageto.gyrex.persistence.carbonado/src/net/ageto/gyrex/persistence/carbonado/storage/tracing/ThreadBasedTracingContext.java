/**
 * Copyright (c) 2013 Gunnar Wagenknecht and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage.tracing;

import org.eclipse.gyrex.monitoring.metrics.TimerMetric;

import net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc.TracingStatement;

public class ThreadBasedTracingContext implements TracingContext {

	public static final ThreadBasedTracingContext INSTANCE = new ThreadBasedTracingContext();

	public static final ConnectionMetrics getCurrentThreadMetrics() {
		return INSTANCE.connectionMetrics.get();
	}

	public static final void resetCurrentThreadMetrics() {
		INSTANCE.connectionMetrics.set(null);
	}

	public static final void setCurrentThreadMetrics(final ConnectionMetrics metrics) {
		INSTANCE.connectionMetrics.set(metrics);
	}

	private final ThreadLocal<ConnectionMetrics> connectionMetrics = new ThreadLocal<ConnectionMetrics>();

	@Override
	public TimerMetric getTimer(final String name) {
		final ConnectionMetrics metrics = connectionMetrics.get();
		if (metrics == null) {
			return null;
		}
		if (TracingStatement.TIMER_SELECTS.equals(name)) {
			return metrics.getTimerForReadQueries();
		}
		return metrics.getTimerForOther();
	}

	@Override
	public boolean isEnabled() {
		return connectionMetrics.get() != null;
	}

}
