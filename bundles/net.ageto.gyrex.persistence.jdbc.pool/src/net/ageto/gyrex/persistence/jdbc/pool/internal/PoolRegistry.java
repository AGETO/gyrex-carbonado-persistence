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

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.eclipse.gyrex.preferences.CloudScope;
import org.eclipse.gyrex.server.Platform;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.prefs.BackingStoreException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.jdbc.pool.IPoolDataSourceFactoryConstants;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * A registry of pools.
 */
public class PoolRegistry implements IPoolDataSourceFactoryConstants {

	private static final String PREF_KEY_FLUSH_COUNT = "flushCount";
	private static final Logger LOG = LoggerFactory.getLogger(PoolRegistry.class);

	static String createDebugInfo(final PoolDefinition pool, final ServiceReference<DataSourceFactory> driverServiceRef) {
		final StringBuilder info = new StringBuilder();

		info.append("Pool ").append(pool.getPoolId()).append(" using ");

		String driverName = (String) driverServiceRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
		if (null == driverName) {
			driverName = (String) driverServiceRef.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
		}
		info.append(driverName).append(' ');

		try {
			final Properties driverProperties = pool.getDriverProperties();
			final String user = driverProperties.getProperty(DataSourceFactory.JDBC_USER);
			if (null != user) {
				info.append(user).append('@');
			}

			final String url = driverProperties.getProperty(DataSourceFactory.JDBC_URL);
			if (null != url) {
				info.append(url);
			}
			final String serverName = driverProperties.getProperty(DataSourceFactory.JDBC_SERVER_NAME);
			if (null != serverName) {
				info.append(serverName);
				final String portNumber = driverProperties.getProperty(DataSourceFactory.JDBC_PORT_NUMBER);
				if (null != portNumber) {
					info.append(':').append(portNumber);
				}
			}
		} catch (final BackingStoreException e) {
			info.append(e.getMessage());
		}

		return info.toString();
	}

	private final ConcurrentMap<String, BoneCPDataSource> poolDataSources = new ConcurrentHashMap<String, BoneCPDataSource>();

	private final IPreferenceChangeListener flushListener = new IPreferenceChangeListener() {
		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			if (PREF_KEY_FLUSH_COUNT.equals(event.getKey())) {
				flushAll();
			}
		}
	};

	public void close() {
		// remove preference listener
		CloudScope.INSTANCE.getNode(PoolActivator.SYMBOLIC_NAME).removePreferenceChangeListener(flushListener);

		// flush all
		flushAll();
	}

	private BoneCPDataSource createPoolDataSource(final PoolDefinition pool) throws IllegalStateException {
		if (PoolDebug.debug) {
			LOG.debug("Initializing new connection pool {}", pool.getPoolId());
		}

		// create driver DS
		final String driverDataSourceFactoryFilter = pool.getDriverDataSourceFactoryFilter();
		if (null == driverDataSourceFactoryFilter) {
			throw new IllegalStateException("driverDataSourceFactoryFilter not configured");
		}
		Collection<ServiceReference<DataSourceFactory>> driverDataSourceFactoryServices;
		try {
			driverDataSourceFactoryServices = PoolActivator.getInstance().findDriverDataSourceFactoryServices(driverDataSourceFactoryFilter);
		} catch (final InvalidSyntaxException e) {
			throw new IllegalStateException(String.format("invalid driver filter '%s': %s", driverDataSourceFactoryFilter, e.getMessage()));
		}
		if (driverDataSourceFactoryServices.isEmpty()) {
			throw new IllegalStateException(String.format("no driver found matching filter '%s'", driverDataSourceFactoryFilter));
		}
		if (driverDataSourceFactoryServices.size() > 1) {
			LOG.warn("Multiple drivers found matching filter {}. ", driverDataSourceFactoryFilter);
		}
		ServiceReference<DataSourceFactory> driverDataSourceFactoryServiceReference = null;
		DataSource driverDs = null;
		for (final ServiceReference<DataSourceFactory> serviceReference : driverDataSourceFactoryServices) {
			final Bundle bundle = PoolActivator.getInstance().getBundle();
			if (null == bundle) {
				throw new IllegalStateException("inactive");
			}
			final BundleContext bundleContext = bundle.getBundleContext();
			if (null == bundleContext) {
				throw new IllegalStateException("missing bundle context");
			}
			final DataSourceFactory driverDataSourceFactory = bundleContext.getService(serviceReference);
			if (null == driverDataSourceFactory) {
				continue;
			}

			try {
				driverDataSourceFactoryServiceReference = serviceReference;
				driverDs = driverDataSourceFactory.createDataSource(pool.getDriverProperties());
			} catch (final Exception e) {
				throw new IllegalStateException(String.format("error creating driver data source using filter '%s': %s", driverDataSourceFactoryFilter, e.getMessage()), e);
			} finally {
				bundleContext.ungetService(serviceReference);
			}
			break;
		}
		if (null == driverDs) {
			throw new IllegalStateException(String.format("unable to create driver data source using filter '%s'", driverDataSourceFactoryFilter));
		}

		// create pool DS
		final BoneCPDataSource ds = new BoneCPDataSource();

		// use our bundle class loader
		ds.setClassLoader(this.getClass().getClassLoader());

		// configure database
		ds.setDatasourceBean(driverDs);
		ds.setPoolName(pool.getPoolId());

		// partitions
		if (Platform.inDevelopmentMode()) {
			// use a smaller pool in development mode
			ds.setPartitionCount(pool.getPartitionCount(1));
			ds.setMinConnectionsPerPartition(pool.getMinConnectionsPerPartition(1));
			ds.setMaxConnectionsPerPartition(pool.getMaxConnectionsPerPartition(5));

			// turn on tracking of unreleased connections in development mode only
			// FIXME: disabled because of issues with leaked resources
//			ds.setCloseConnectionWatch(true);
			// wait up to 1 minute for a connection to be released back to the pool
//			ds.setCloseConnectionWatchTimeout(600000); // 10min
		} else {
			// use a larger pool in production mode
			ds.setPartitionCount(pool.getPartitionCount(3));
			ds.setMinConnectionsPerPartition(pool.getMinConnectionsPerPartition(1));
			ds.setMaxConnectionsPerPartition(pool.getMaxConnectionsPerPartition(5));
		}

		// connections
		ds.setIdleMaxAge(30, TimeUnit.MINUTES);
		ds.setIdleConnectionTestPeriod(5, TimeUnit.MINUTES);

		// monitoring
		ds.setQueryExecuteTimeLimit(2, TimeUnit.SECONDS);

		// connection monitoring
		ds.setConnectionHook(new BoneCPConnectionMonitor(pool.getPoolId(), createDebugInfo(pool, driverDataSourceFactoryServiceReference)));

		// initialize pool lazy in order to allow starting server when database is down
		ds.setLazyInit(true);

		// in general, the system must be responsive
		// we therefore set a small connection timeout
		// the back-off strategy implemented in BoneCPConnectionMonitor will continue trying to establish a connection
		ds.setConnectionTimeout(pool.getConnectionTimeoutInMs(Platform.inDevelopmentMode() ? 5000 : 500), TimeUnit.MILLISECONDS);

		// database specific tuning
		ds.setConnectionTestStatement(getConnectionTestStatement(driverDataSourceFactoryServiceReference));

		// enable JMX
		ds.setDisableJMX(false);

		// enable statistics
		ds.setStatisticsEnabled(true);

		return ds;
	}

	/**
	 * flushes all repos
	 */
	void flushAll() {
		// use synchronized block to avoid concurrent pool creations
		synchronized (poolDataSources) {
			final Collection<BoneCPDataSource> values = poolDataSources.values();
			for (final Iterator stream = values.iterator(); stream.hasNext();) {
				final BoneCPDataSource pool = (BoneCPDataSource) stream.next();
				if (PoolDebug.debug) {
					LOG.debug("Flushing connection pool {}", pool);
				}
				pool.close();
				stream.remove();
			}
		}
		LOG.info("Successfully flushed all connection pools.");
	}

	public void flushDataSource(final String poolId) {
		// remove existing data source
		final BoneCPDataSource dataSource = poolDataSources.remove(poolId);
		if (null != dataSource) {
			if (PoolDebug.debug) {
				LOG.debug("Flushing connection pool {}", dataSource);
			}
			dataSource.close();
			LOG.info("Successfully flushed connection pool {}.", dataSource);
		}
	}

	public void flushGlobal() throws BackingStoreException {
		if (PoolDebug.debug) {
			LOG.debug("Sending global connection pool flush on all nodes in the cloud...");
		}

		// get node
		final IEclipsePreferences node = CloudScope.INSTANCE.getNode(PoolActivator.SYMBOLIC_NAME);

		// sync
		node.sync();

		// trigger an update to the preference value
		node.putLong(PREF_KEY_FLUSH_COUNT, node.getLong(PREF_KEY_FLUSH_COUNT, 0) + 1);

		// flush
		node.flush();

		LOG.info("Global connection pool flush sent to all nodes in the cloud (flushes so far: {}).", node.getInt(PREF_KEY_FLUSH_COUNT, 0));
	}

	private String getConnectionTestStatement(final ServiceReference<DataSourceFactory> serviceReference) {
		final String driverClass = (String) serviceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_CLASS);
		final String driverName = (String) serviceReference.getProperty(DataSourceFactory.OSGI_JDBC_DRIVER_NAME);
		if (StringUtils.containsIgnoreCase(driverName, "mysql") || StringUtils.containsIgnoreCase(driverClass, "mysql")) {
			return "/* ping */ SELECT 1";
		}
		// none
		return null;
	}

	public DataSource getDataSource(final String poolId) {
		BoneCPDataSource dataSource = poolDataSources.get(poolId);
		if (null != dataSource) {
			if (PoolDebug.debug) {
				LOG.debug("Returing initialized connection pool {}", dataSource);
			}
			return dataSource;
		}

		// attempt to load the pool
		final PoolDefinition pool = new PoolDefinition(poolId);

		// check that pool is defined
		try {
			if (!pool.exists()) {
				throw new IllegalStateException(String.format("pool '%s' not defined", poolId));
			}
		} catch (final BackingStoreException e) {
			throw new IllegalStateException(String.format("error accessing pool configuration for pool '%s', %s", poolId, e.getMessage()), e);
		}

		// create data source (use synchronized block to avoid concurrent pool creations)
		synchronized (poolDataSources) {
			dataSource = poolDataSources.get(poolId);
			if (null == dataSource) {
				dataSource = createPoolDataSource(pool);
				poolDataSources.put(poolId, dataSource);
				LOG.info("Successfully initialized connection pool {}.", dataSource);
			} else {
				if (PoolDebug.debug) {
					LOG.debug("Found initialized connection pool {}", dataSource);
				}
			}
		}

		// register with the preference node so that we can sync flushes across all nodes in the cloud
		CloudScope.INSTANCE.getNode(PoolActivator.SYMBOLIC_NAME).addPreferenceChangeListener(flushListener);

		// we should have a DS at this point
		if (PoolDebug.debug) {
			LOG.debug("Returing connection pool {}", dataSource);
		}
		return dataSource;
	}

	public boolean isActive(final String poolId) {
		return poolDataSources.containsKey(poolId);
	}

}
