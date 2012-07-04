/**
 * Copyright (c) 2010 Gunnar Wagenknecht and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.jdbc.configurator.internal;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.eclipse.gyrex.persistence.storage.settings.IRepositoryPreferences;
import org.eclipse.gyrex.server.Platform;

import net.ageto.gyrex.persistence.carbonado.jdbc.configurator.IPoolConstants;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DataSourceSupport;
import net.ageto.gyrex.persistence.jdbc.pool.internal.BoneCPConnectionMonitor;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * A pooled {@link DataSourceSupport}
 */
@SuppressWarnings("restriction")
public class PooledDataSource extends DataSourceSupport {

	private DataSourceSupport driverDataSource;

	@Override
	public DataSource createDataSource(final String repositoryId, final IRepositoryPreferences preferences) {

		// get driver DS first
		final DataSource driverDs = driverDataSource.createDataSource(repositoryId, preferences);
		if (null == driverDs) {
			return null;
		}

		// create pool DS
		final BoneCPDataSource ds = new BoneCPDataSource();

		// use our bundle class loader
		ds.setClassLoader(this.getClass().getClassLoader());

		// configure database
		ds.setDatasourceBean(driverDs);
		ds.setPoolName(repositoryId);

		// partitions
		if (Platform.inDevelopmentMode()) {
			// use a smaller pool in development mode
			ds.setPartitionCount(getPartitionCount(preferences, 1));
			ds.setMinConnectionsPerPartition(getMinConnectionsPerPartition(preferences, 1));
			ds.setMaxConnectionsPerPartition(getMaxConnectionsPerPartition(preferences, 5));

			// turn on tracking of unreleased connections in development mode only
			// FIXME: disabled because of issues with leaked resources
//			ds.setCloseConnectionWatch(true);
			// wait for a connection to be released back to the pool
//			ds.setCloseConnectionWatchTimeoutInMs(100000); // 10s
		} else {
			// use a larger pool in production mode
			ds.setPartitionCount(getPartitionCount(preferences, 3));
			ds.setMinConnectionsPerPartition(getMinConnectionsPerPartition(preferences, 1));
			ds.setMaxConnectionsPerPartition(getMaxConnectionsPerPartition(preferences, 5));
		}

		// connections
		ds.setIdleMaxAge(30, TimeUnit.MINUTES);
		ds.setIdleConnectionTestPeriod(5, TimeUnit.MINUTES);

		// monitoring
		ds.setQueryExecuteTimeLimit(2, TimeUnit.SECONDS);

		// connection monitoring
		ds.setConnectionHook(new BoneCPConnectionMonitor(repositoryId, driverDataSource.getDebugInfo(preferences)));

		// initialize pool lazy in order to allow starting server when database is down
		ds.setLazyInit(true);

		// in general, the system must be responsive
		// we therefore set a small connection timeout
		// the back-off strategy implemented in BoneCPConnectionMonitor will continue trying to establish a connection
		ds.setConnectionTimeout(getConnectionTimeoutInMs(preferences, Platform.inDevelopmentMode() ? 5000 : 500), TimeUnit.MILLISECONDS);

		// MySQL specific tuning
		if (getDatabaseType(preferences) == DatabaseType.MySQL) {
			ds.setConnectionTestStatement("/* ping */ SELECT 1");
		}

		// enable JMX
		ds.setDisableJMX(false);

		// enable statistics
		ds.setStatisticsEnabled(true);

		return ds;
	}

	private long getConnectionTimeoutInMs(final IRepositoryPreferences prefs, final long defaultValue) {
		final int connectionTimeoutMs = prefs.getInt(IPoolConstants.REPO_PREF_KEY_POOL_CONNECTION_TIMEOUT_IN_MS, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	@Override
	public String getDebugInfo(final IRepositoryPreferences preferences) {
		return "BoneCP wrapping ".concat(driverDataSource.getDebugInfo(preferences));
	}

	private int getMaxConnectionsPerPartition(final IRepositoryPreferences prefs, final int defaultValue) {
		final int connectionTimeoutMs = prefs.getInt(IPoolConstants.REPO_PREF_KEY_POOL_MAX_CONNECTIONS_PER_PARTITION, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	private int getMinConnectionsPerPartition(final IRepositoryPreferences prefs, final int defaultValue) {
		final int connectionTimeoutMs = prefs.getInt(IPoolConstants.REPO_PREF_KEY_POOL_MIN_CONNECTIONS_PER_PARTITION, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	private int getPartitionCount(final IRepositoryPreferences prefs, final int defaultValue) {
		final int connectionTimeoutMs = prefs.getInt(IPoolConstants.REPO_PREF_KEY_POOL_PARTITION_COUNT, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	/**
	 * Sets the driverDataSource.
	 * 
	 * @param driverDataSource
	 *            the driverDataSource to set
	 */
	public void setDriverDataSource(final DataSourceSupport driverDataSource) {
		this.driverDataSource = driverDataSource;
	}
}
