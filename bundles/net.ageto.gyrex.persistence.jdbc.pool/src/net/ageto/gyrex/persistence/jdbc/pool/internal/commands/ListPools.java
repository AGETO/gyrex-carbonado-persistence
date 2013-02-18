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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.gyrex.common.console.Command;
import org.eclipse.gyrex.common.identifiers.IdHelper;

import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.prefs.BackingStoreException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import org.kohsuke.args4j.Argument;

import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolActivator;
import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolDefinition;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.ConnectionHandle;

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
		Arrays.sort(knownPoolIds);
		for (final String poolId : knownPoolIds) {
			if ((null == poolIdFilter) || StringUtils.containsIgnoreCase(poolId, poolIdFilter)) {
				if (PoolActivator.getInstance().getRegistry().isActive(poolId)) {
					printf("  %s (open)", poolId);
				} else {
					printf("  %s", poolId);
				}
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
			final SortedSet<String> names = new TreeSet<String>();
			final Enumeration<?> propertyNames = driverProperties.propertyNames();
			while (propertyNames.hasMoreElements()) {
				names.add((String) propertyNames.nextElement());
			}
			for (final String key : names) {
				printf("  %35s: %s", key, StringUtils.trimToEmpty(driverProperties.getProperty(key)));
			}
			printf(StringUtils.EMPTY);
		}

		printf("Pool Statistics:");
		final BoneCPDataSource dataSource = (BoneCPDataSource) PoolActivator.getInstance().getRegistry().getDataSource(pool.getPoolId());

		// test connectivity (this also works around a bug in BoneCP which would result in an NPE below)
		try {
			final Connection connection = dataSource.getConnection();
			if (connection instanceof ConnectionHandle) {
				printf("  %35s: %s", "connectionTest", ((ConnectionHandle) connection).isValid(1000) ? "OK" : "NOT OK");
			} else {
				printf("  %35s: %s", "connectionSample", connection.toString());
			}
			connection.close();
		} catch (final SQLException e) {
			printf("  unable to connect to pool: %s", e.getMessage());
		}

		// total number of leased connections
		printf("  %35s: %d", "totalLeased", dataSource.getTotalLeased());
		printf(StringUtils.EMPTY);

		printf("Effective Pool Config:");
		final TreeMap<String, String> poolConfig = readPoolConfig(dataSource);
		for (final Entry<String, String> entry : poolConfig.entrySet()) {
			printf("  %35s: %s", entry.getKey(), entry.getValue());
		}
	}

	private TreeMap<String, String> readPoolConfig(final BoneCPDataSource dataSource) {
		final TreeMap<String, String> poolConfig = new TreeMap<String, String>();
		final BoneCPConfig config = dataSource.getConfig();
		for (final Method method : BoneCPConfig.class.getDeclaredMethods()) {
			String key = null;
			if (method.getName().startsWith("is")) {
				key = WordUtils.uncapitalize(method.getName().substring(2));
			} else if (method.getName().startsWith("get")) {
				key = WordUtils.uncapitalize(method.getName().substring(3));
			} else {
				continue;
			}

			// skip deprecated methods
			if ((null != method.getAnnotation(Deprecated.class)) || key.equals("maxConnectionAge")) {
				continue;
			}

			if ((method.getParameterTypes().length == 0) && (method.getReturnType() != Void.TYPE)) {
				try {
					final Object value = method.invoke(config);
					if (null != value) {
						poolConfig.put(key, String.valueOf(value));
					}
				} catch (final Exception e) {
					// ignored
				}
			}
		}
		return poolConfig;
	}
}
