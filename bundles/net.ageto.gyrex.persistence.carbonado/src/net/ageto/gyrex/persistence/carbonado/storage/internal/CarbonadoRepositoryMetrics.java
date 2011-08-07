/**
 * Copyright (c) 2009, 2010 AGETO Service GmbH and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage.internal;

import org.eclipse.gyrex.monitoring.metrics.MetricSet;
import org.eclipse.gyrex.monitoring.metrics.StatusMetric;

/**
 * Metrics for Carbonado Repository.
 */
public class CarbonadoRepositoryMetrics extends MetricSet {

	private final StatusMetric statusMetric;

	/**
	 * Creates a new instance.
	 * 
	 * @param id
	 * @param repositoryId
	 * @param metrics
	 */
	public CarbonadoRepositoryMetrics(final String id, final String repositoryId) {
		super(id, String.format("Metrics for repoistory %s.", repositoryId), new StatusMetric(id.concat(".status"), "constructed", "not initialized yet"));
		statusMetric = getMetric(0, StatusMetric.class);
	}

	/**
	 * Returns the statusMetric.
	 * 
	 * @return the statusMetric
	 */
	public StatusMetric getStatusMetric() {
		return statusMetric;
	}

}
