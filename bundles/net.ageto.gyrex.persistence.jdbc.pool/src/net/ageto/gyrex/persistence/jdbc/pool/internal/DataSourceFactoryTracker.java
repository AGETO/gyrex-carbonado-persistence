/**
 * Copyright (c) 2011 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 */
package net.ageto.gyrex.persistence.jdbc.pool.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.util.tracker.ServiceTracker;

/**
 *
 */
public class DataSourceFactoryTracker extends ServiceTracker<DataSourceFactory, DataSourceFactory> {

	/**
	 * Creates a new instance.
	 * 
	 * @param context
	 */
	public DataSourceFactoryTracker(final BundleContext context) {
		super(context, DataSourceFactory.class, null);
	}

}
