/*******************************************************************************
 * Copyright (c) 2010 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 *******************************************************************************/
package net.ageto.gyrex.persistence.carbonado.internal;

import org.eclipse.gyrex.common.runtime.BaseBundleActivator;
import org.eclipse.gyrex.common.services.IServiceProxy;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc.SchemaSupportTracker;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DataSourceSupport;

/**
 * Bundle activator.
 */
public class CarbonadoActivator extends BaseBundleActivator {

	public static final String SYMBOLIC_NAME = "net.ageto.gyrex.persistence.carbonado";
	private static volatile CarbonadoActivator instance;

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 */
	public static CarbonadoActivator getInstance() {
		final CarbonadoActivator activator = instance;
		if (activator == null) {
			throw new IllegalStateException("inactive");
		}
		return activator;
	}

	private SchemaSupportTracker schemaSupportTracker;
	private ServiceRegistration<?> repositoryProviderRegistration;
	private IServiceProxy<DataSourceSupport> dataSourceSupport;

	/**
	 * Creates a new instance.
	 */
	public CarbonadoActivator() {
		super(SYMBOLIC_NAME);
	}

	@Override
	protected void doStart(final BundleContext context) throws Exception {
		instance = this;

		schemaSupportTracker = new SchemaSupportTracker(context);
		schemaSupportTracker.open();

		dataSourceSupport = getServiceHelper().trackService(DataSourceSupport.class, context.createFilter(DataSourceSupport.POOL_FILTER));
	}

	@Override
	protected void doStop(final BundleContext context) throws Exception {
		instance = null;

		repositoryProviderRegistration.unregister();
		repositoryProviderRegistration = null;

		dataSourceSupport.dispose();
		dataSourceSupport = null;

		schemaSupportTracker.close();
		schemaSupportTracker = null;
	}

	/**
	 * Returns the dataSourceSupport.
	 * 
	 * @return the dataSourceSupport
	 */
	public DataSourceSupport getDataSourceSupport() {
		final IServiceProxy<DataSourceSupport> proxy = dataSourceSupport;
		if (null == proxy) {
			throw createBundleInactiveException();
		}
		return proxy.getService();
	}

	@Override
	protected Class getDebugOptions() {
		return CarbonadoDebug.class;
	}

	/**
	 * Returns the schemaSupportTracker.
	 * 
	 * @return the schemaSupportTracker
	 */
	public SchemaSupportTracker getSchemaSupportTracker() {
		final SchemaSupportTracker supportTracker = schemaSupportTracker;
		if (null == supportTracker) {
			throw createBundleInactiveException();
		}
		return supportTracker;
	}
}
