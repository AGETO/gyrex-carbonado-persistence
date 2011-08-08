package net.ageto.gyrex.persistence.jdbc.pool.internal;

import java.util.Collection;

import org.eclipse.gyrex.common.runtime.BaseBundleActivator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;

public class PoolActivator extends BaseBundleActivator {

	/**
	 * special filter for excluding the {@link PoolDataSourceFactory}
	 * implementation when looking for {@link DataSourceFactory}
	 */
	private static final String EXCLUDE_POOL_DSF_FILTER = "(!(osgi.jdbc.driver.class=net.ageto.gyrex.persistence.jdbc.pool.class))";

	public static final String SYMBOLIC_NAME = "net.ageto.gyrex.persistence.jdbc.pool";

	private static PoolActivator instance;

	/**
	 * Returns the instance.
	 * 
	 * @return the instance
	 */
	public static PoolActivator getInstance() {
		final PoolActivator activator = instance;
		if (null == activator) {
			throw new IllegalStateException("inactive");
		}
		return activator;
	}

	private PoolRegistry registry;

	/**
	 * Creates a new instance.
	 */
	public PoolActivator() {
		super(SYMBOLIC_NAME);
	}

	@Override
	protected void doStart(final BundleContext context) throws Exception {
		instance = this;

		registry = new PoolRegistry();
	}

	@Override
	protected void doStop(final BundleContext context) throws Exception {
		instance = null;

		registry.close();
		registry = null;
	}

	public Collection<ServiceReference<DataSourceFactory>> findDriverDataSourceFactoryServices(final String filter) throws InvalidSyntaxException {
		// make sure that we filter the pool DSF in any case
		final String finalFilter = null != filter ? String.format("(&%s(%s))", EXCLUDE_POOL_DSF_FILTER, filter) : EXCLUDE_POOL_DSF_FILTER;

		final Bundle bundle = getBundle();
		if (null == bundle) {
			throw createBundleInactiveException();
		}
		final BundleContext bundleContext = bundle.getBundleContext();
		if (null == bundleContext) {
			throw new IllegalStateException("missing bundle context");
		}

		return bundleContext.getServiceReferences(DataSourceFactory.class, finalFilter);
	}

	@Override
	protected Class getDebugOptions() {
		return PoolDebug.class;
	}

	public PoolRegistry getRegistry() {
		final PoolRegistry registry = this.registry;
		if (null == registry) {
			throw createBundleInactiveException();
		}
		return registry;
	}
}
