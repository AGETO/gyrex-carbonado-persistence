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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;

import net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc.SchemaSupportTracker;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DataSourceSupport;
import net.ageto.gyrex.persistence.jdbc.pool.IPoolDataSourceFactoryConstants;

/**
 * Bundle activator.
 */
public class CarbonadoActivator extends BaseBundleActivator {

	public static final String SYMBOLIC_NAME = "net.ageto.gyrex.persistence.carbonado";
	private static volatile CarbonadoActivator instance;

	/**
	 * special filter for finding the pooled {@link DataSourceFactory}
	 */
	private static final String POOL_DSF_FILTER = "(&(" + Constants.OBJECTCLASS + "=" + DataSourceFactory.class.getName() + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=" + IPoolDataSourceFactoryConstants.DRIVER_CLASS + "))";

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

		dataSourceSupport = getServiceHelper().trackService(DataSourceSupport.class, DataSourceSupport.POOL_FILTER);
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

	public DataSourceFactory getPoolDataSourceFactory() {
		final Bundle bundle = getBundle();
		if (null == bundle) {
			throw createBundleInactiveException();
		}
		final BundleContext bundleContext = bundle.getBundleContext();
		if (null == bundleContext) {
			throw new IllegalStateException("missing bundle context");
		}

		try {
			return getServiceHelper().trackService(DataSourceFactory.class, POOL_DSF_FILTER).getService();
		} catch (final IllegalArgumentException e) {
			throw new IllegalStateException(String.format("Unable to locate pool data source factory. Please check the filter string. %s", e.getMessage()), e);
		}
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
