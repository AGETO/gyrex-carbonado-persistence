/**
 * Copyright (c) 2013 AGETO and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage.tracing;

import java.util.Map;

import org.eclipse.gyrex.common.identifiers.IdHelper;
import org.eclipse.gyrex.monitoring.metrics.MetricSet;
import org.eclipse.gyrex.monitoring.metrics.TimerMetric;

/**
 * A metric set for collecting database connection metrics.
 * <p>
 * This may be instantiated by clients and passed to
 * </p>
 */
public class ConnectionMetrics extends MetricSet {

	private final TimerMetric reads;
	private final TimerMetric other;

	/**
	 * Creates a new instance for collecting database connection metrics.
	 * 
	 * @param id
	 *            the metric id
	 * @param description
	 *            the metric description
	 * @param properties
	 *            the metric properties (each key
	 *            {@link IdHelper#isValidId(String) must be a valid identifier})
	 */
	public ConnectionMetrics(final String id, final String description, final Map<String, String> properties) {
		super(id, description, properties, new TimerMetric("reads"), new TimerMetric("other"));
		reads = getMetric(0, TimerMetric.class);
		other = getMetric(1, TimerMetric.class);
	}

	public TimerMetric getTimerForOther() {
		return other;
	}

	public TimerMetric getTimerForReadQueries() {
		return reads;
	}
}
