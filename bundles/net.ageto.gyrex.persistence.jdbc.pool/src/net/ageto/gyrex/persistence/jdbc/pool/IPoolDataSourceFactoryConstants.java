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
package net.ageto.gyrex.persistence.jdbc.pool;

import java.util.Properties;

import org.osgi.service.jdbc.DataSourceFactory;

/**
 * Interface with shared constants for the pooled {@link DataSourceFactory}
 * implementation.
 */
public interface IPoolDataSourceFactoryConstants {

	/**
	 * Service property value of
	 * {@link DataSourceFactory#OSGI_JDBC_DRIVER_CLASS} that will be used when
	 * registering the pooled {@link DataSourceFactory} implementation as an
	 * OSGi service.
	 */
	String DRIVER_CLASS = "net.ageto.gyrex.persistence.jdbc.pool.class";

	/**
	 * The "<code>poolId</code>" property that DataSource clients should supply
	 * a value for when calling
	 * {@link DataSourceFactory#createDataSource(Properties)}.
	 */
	String POOL_ID = "poolId";

	/**
	 * Preference key for specifying the number of partitions to use (value
	 * {@value #POOL_PARTITION_COUNT}).
	 */
	String POOL_PARTITION_COUNT = "partitionCount";

	/**
	 * Preference key for specifying the minimum number of connections per
	 * partition to use (value {@value #POOL_MIN_CONNECTIONS_PER_PARTITION}).
	 */
	String POOL_MIN_CONNECTIONS_PER_PARTITION = "minConnectionsPerPartition";

	/**
	 * Preference key for specifying the maximum number of connections per
	 * partition to use (value {@value #POOL_MAX_CONNECTIONS_PER_PARTITION}).
	 */
	String POOL_MAX_CONNECTIONS_PER_PARTITION = "maxConnectionsPerPartition";

	/**
	 * Preference key for specifying the connect timeout in milliseconds (value
	 * {@value #POOL_CONNECTION_TIMEOUT_IN_MS}).
	 */
	String POOL_CONNECTION_TIMEOUT_IN_MS = "connectionTimeoutInMs";

}
