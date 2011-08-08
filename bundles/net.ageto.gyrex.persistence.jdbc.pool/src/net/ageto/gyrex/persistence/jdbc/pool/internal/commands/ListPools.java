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
package net.ageto.gyrex.persistence.jdbc.pool.internal.commands;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.gyrex.common.console.Command;
import org.eclipse.gyrex.common.identifiers.IdHelper;

import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.prefs.BackingStoreException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.Argument;

import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolActivator;
import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolDefinition;

/**
 *
 */
public class ListPools extends Command {

	@Argument(index = 0, usage = "an optional pool id filter string")
	private String poolIdFilter;

	/**
	 * Creates a new instance.
	 */
	public ListPools() {
		super("lists all available pools");
	}

	@Override
	protected void doExecute() throws Exception {

		// check for exact filter match
		if ((null != poolIdFilter) && IdHelper.isValidId(poolIdFilter)) {
			final PoolDefinition pool = new PoolDefinition(poolIdFilter);
			if (pool.exists()) {
				printPool(pool);
				return;
			}
		}

		// list all known ids
		printf("Known Pools:");
		final String[] knownPoolIds = PoolDefinition.getKnownPoolIds();
		for (final String poolId : knownPoolIds) {
			if ((null == poolIdFilter) || StringUtils.containsIgnoreCase(poolId, poolIdFilter)) {
				printf("  %s", poolId);
			}
		}

		// list all known drivers
		printf("Known drivers:");
		final Collection<ServiceReference<DataSourceFactory>> services = PoolActivator.getInstance().findDriverDataSourceFactoryServices(null);
		if (!services.isEmpty()) {
			for (final ServiceReference<DataSourceFactory> serviceReference : services) {
				printf("  %s", serviceReference.toString());
			}
		} else {
			printf("  None");
		}

	}

	private void printPool(final PoolDefinition pool) throws BackingStoreException {
		printf("Pool %s", pool.getPoolId());
		printf(StringUtils.EMPTY);
		final Properties driverProperties = pool.getDriverProperties();
		if (!driverProperties.isEmpty()) {
			printf("Driver Properties:");
			final Enumeration<?> names = driverProperties.propertyNames();
			while (names.hasMoreElements()) {
				final String key = (String) names.nextElement();
				printf("%20s: %s", key, StringUtils.trimToEmpty(driverProperties.getProperty(key)));
			}
		}
	}

}
