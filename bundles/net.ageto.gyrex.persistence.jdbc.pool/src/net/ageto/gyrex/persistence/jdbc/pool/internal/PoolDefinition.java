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

import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.gyrex.common.identifiers.IdHelper;
import org.eclipse.gyrex.preferences.CloudScope;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.ageto.gyrex.persistence.jdbc.pool.IPoolDataSourceFactoryConstants;

/**
 * Wrapper around preferences which represents a pool definition.
 */
public class PoolDefinition implements IPoolDataSourceFactoryConstants {

	private static final String NODE_DRIVER_PROPERTIES = "driverProperties";
	private static final String PREF_KEY_DRIVER_DATA_SOURCE_FACTORY_FILTER = "driverDataSourceFactoryFilter";

	public static String[] getKnownPoolIds() throws BackingStoreException {
		return getPoolsNode().childrenNames();
	}

	private static Preferences getPoolsNode() {
		return CloudScope.INSTANCE.getNode(PoolActivator.SYMBOLIC_NAME).node("pools");
	}

	private final String poolId;

	/**
	 * Creates a new instance.
	 */
	public PoolDefinition(final String poolId) {
		if (!IdHelper.isValidId(poolId)) {
			throw new IllegalStateException(String.format("invalid pool id: %s", poolId));
		}
		this.poolId = poolId;
	}

	public boolean exists() throws BackingStoreException {
		return getPoolsNode().nodeExists(poolId);
	}

	public void flush() throws BackingStoreException {
		getPoolNode().flush();
	}

	public long getConnectionTimeoutInMs(final long defaultValue) {
		final int connectionTimeoutMs = getPoolNode().getInt(POOL_CONNECTION_TIMEOUT_IN_MS, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	public String getDriverDataSourceFactoryFilter() {
		return getPoolNode().get(PREF_KEY_DRIVER_DATA_SOURCE_FACTORY_FILTER, null);
	}

	public Properties getDriverProperties() throws BackingStoreException {
		final Preferences node = getPoolNode().node(NODE_DRIVER_PROPERTIES);
		final Properties driverSettings = new Properties();

		for (final String key : node.keys()) {
			driverSettings.put(key, node.get(key, null));
		}

		return driverSettings;
	}

	public int getMaxConnectionsPerPartition(final int defaultValue) {
		final int connectionTimeoutMs = getPoolNode().getInt(POOL_MAX_CONNECTIONS_PER_PARTITION, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	public int getMinConnectionsPerPartition(final int defaultValue) {
		final int connectionTimeoutMs = getPoolNode().getInt(POOL_MIN_CONNECTIONS_PER_PARTITION, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	public int getPartitionCount(final int defaultValue) {
		final int connectionTimeoutMs = getPoolNode().getInt(POOL_PARTITION_COUNT, -1);
		if (connectionTimeoutMs < 0) {
			return defaultValue;
		}
		return connectionTimeoutMs;
	}

	/**
	 * Returns the poolId.
	 * 
	 * @return the poolId
	 */
	public String getPoolId() {
		return poolId;
	}

	private Preferences getPoolNode() {
		return getPoolsNode().node(poolId);
	}

	public void remove() throws BackingStoreException {
		getPoolNode().removeNode();
		getPoolsNode().flush();
	}

	public void removeDriverProperty(final String key) throws BackingStoreException {
		final Preferences node = getPoolNode().node(NODE_DRIVER_PROPERTIES);
		node.remove(key);
	}

	public void setConnectionTimeoutInMs(final int connectionTimeout) {
		getPoolNode().putInt(POOL_CONNECTION_TIMEOUT_IN_MS, connectionTimeout);
	}

	public void setDriverDataSourceFactoryFilter(final String filter) {

		getPoolNode().put(PREF_KEY_DRIVER_DATA_SOURCE_FACTORY_FILTER, filter);
	}

	public void setDriverProperties(final Properties properties) throws BackingStoreException {
		final Preferences node = getPoolNode().node(NODE_DRIVER_PROPERTIES);

		if (null == properties) {
			node.removeNode();
			return;
		}

		// set all properties
		final Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			final String key = (String) propertyNames.nextElement();
			final String value = properties.getProperty(key);
			if (null != value) {
				node.put(key, value);
			} else {
				node.remove(key);
			}
		}

		// remove obsolete keys
		for (final String key : node.keys()) {
			if (!properties.containsKey(key)) {
				node.remove(key);
			}
		}
	}

	public void setDriverProperty(final String key, final String value) throws BackingStoreException {
		final Preferences node = getPoolNode().node(NODE_DRIVER_PROPERTIES);
		node.put(key, value);
	}

	public void setMaxConnectionsPerPartition(final int maxConnectionsPerPartition) {
		getPoolNode().putInt(POOL_MAX_CONNECTIONS_PER_PARTITION, maxConnectionsPerPartition);
	}

	public void setMinConnectionsPerPartition(final int minConnectionsPerPartition) {
		getPoolNode().putInt(POOL_MIN_CONNECTIONS_PER_PARTITION, minConnectionsPerPartition);
	}

	public void setPartitionCount(final int partitionCount) {
		getPoolNode().putInt(POOL_PARTITION_COUNT, partitionCount);
	}
}
