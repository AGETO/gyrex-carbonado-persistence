package net.ageto.gyrex.persistence.carbonado.jdbc.configurator.internal;

import org.eclipse.gyrex.common.runtime.BaseBundleActivator;

public class JdbcConfiguratorActivator extends BaseBundleActivator {

	/** SYMBOLIC_NAME */
	private static final String SYMBOLIC_NAME = "net.ageto.gyrex.persistence.carbonado.jdbc.configurator";

	/**
	 * Creates a new instance.
	 * 
	 * @param symbolicName
	 */
	public JdbcConfiguratorActivator() {
		super(SYMBOLIC_NAME);
	}

	@Override
	protected Class<JdbcConfiguratorDebug> getDebugOptions() {
		return JdbcConfiguratorDebug.class;
	}

}
