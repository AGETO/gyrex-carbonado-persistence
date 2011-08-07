package net.ageto.gyrex.persistence.liquibase.internal;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.slf4j.bridge.SLF4JBridgeHandler;

public class LiquibaseActivator implements BundleActivator {

	public static String SYMBOLIC_NAME = "net.ageto.gyrex.persistence.liquibase";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext bundleContext) throws Exception {
		LiquibaseActivator.context = bundleContext;

		// configure Liquibase to log to SLF4J
		final Logger logger = java.util.logging.Logger.getLogger("liquibase");

		// remove all existing handlers
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		// add SLF4J bridge
		logger.addHandler(new SLF4JBridgeHandler());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bundleContext) throws Exception {
		LiquibaseActivator.context = null;

		// de-configure Liquibase logger
		final Logger logger = java.util.logging.Logger.getLogger("liquibase");

		// remove all existing handlers
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
	}

}
