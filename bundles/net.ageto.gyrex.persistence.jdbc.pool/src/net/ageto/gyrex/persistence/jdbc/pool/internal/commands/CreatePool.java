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
import java.util.Properties;

import org.eclipse.gyrex.common.console.Command;
import org.eclipse.gyrex.common.identifiers.IdHelper;

import org.osgi.framework.ServiceReference;
import org.osgi.service.jdbc.DataSourceFactory;

import org.apache.commons.lang.StringUtils;

import org.kohsuke.args4j.Option;

import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolActivator;
import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolDefinition;

/**
 *
 */
public class CreatePool extends Command {

	@Option(name = "-id", aliases = { "--pool-id" }, usage = "pool id", required = true)
	String poolId;

	@Option(name = "-driver", aliases = { "--driver-class-name" }, usage = "driver class to use", required = true)
	String driverClass;

	@Option(name = "-url", usage = "driver connection url to use (can be used instead of host and port)")
	String url;

	@Option(name = "-host", usage = "database server host")
	String host;

	@Option(name = "-port", usage = "database server port")
	int port = -1;

	@Option(name = "-u", aliases = { "--user" }, usage = "database username")
	String user;

	@Option(name = "-p", aliases = { "--password" }, usage = "database password")
	String password;

	@Option(name = "-connectTimeout", aliases = { "--pool-connection-timeout" }, usage = "connection timeout in ms")
	int connectionTimeout = -1;

	@Option(name = "-partitions", aliases = { "--pool-partition-count" }, usage = "number of partitions in the pool")
	int partitionCount = -1;

	@Option(name = "-minConnections", aliases = { "--pool-minimum-connections-per-partition" }, usage = "minimum number of connections per pool partition")
	int minConnectionsPerPartition = -1;

	@Option(name = "-maxConnections", aliases = { "--pool-maximum-connections-per-partition" }, usage = "maximum number of connections per pool partition")
	int maxConnectionsPerPartition = -1;

	/**
	 * Creates a new instance.
	 */
	public CreatePool() {
		super("creates a new pool");
	}

	@Override
	protected void doExecute() throws Exception {
		// check id
		if (!IdHelper.isValidId(poolId)) {
			printf("ERROR: invalid pool id");
			return;
		}

		// check that driver can be found
		if (!IdHelper.isValidId(driverClass)) {
			printf("ERROR: invalid driver class name");
			return;
		}
		final String classFilter = String.format("osgi.jdbc.driver.class=%s", driverClass);
		final Collection<ServiceReference<DataSourceFactory>> services = PoolActivator.getInstance().findDriverDataSourceFactoryServices(classFilter);
		if (services.size() != 1) {
			printf("ERROR: multiple drivers available");
			for (final ServiceReference<DataSourceFactory> serviceReference : services) {
				printf("%s", serviceReference.toString());
			}
			return;
		}

		// check that either url or host name is specified
		if (StringUtils.isBlank(url) && StringUtils.isBlank(host)) {
			printf("ERROR: a connection url or a host name must be specified");
			return;
		} else if (StringUtils.isNotBlank(url) && StringUtils.isNotBlank(host)) {
			printf("WARNING: a connection url and a host name have been specified; only the connection url will be used");
			return;
		}

		// remove existing
		PoolDefinition pool = new PoolDefinition(poolId);
		if (pool.exists()) {
			printf("Removing existing pool '%s'...", poolId);
			pool.remove();
		}

		// configure new pool
		printf("Defining pool '%s'...", poolId);
		pool = new PoolDefinition(poolId);
		pool.setDriverDataSourceFactoryFilter(classFilter);

		// driver properties
		final Properties driverProperties = new Properties();
		if (StringUtils.isNotBlank(url)) {
			driverProperties.setProperty(DataSourceFactory.JDBC_URL, url);
		} else {
			driverProperties.setProperty(DataSourceFactory.JDBC_SERVER_NAME, host);
			if (port > 0) {
				driverProperties.setProperty(DataSourceFactory.JDBC_PORT_NUMBER, String.valueOf(port));
			}
		}
		if (StringUtils.isNotBlank(user)) {
			driverProperties.setProperty(DataSourceFactory.JDBC_USER, user);
		}
		if (StringUtils.isNotBlank(password)) {
			driverProperties.setProperty(DataSourceFactory.JDBC_PASSWORD, password);
		}
		pool.setDriverProperties(driverProperties);

		if (connectionTimeout >= 0) {
			pool.setConnectionTimeoutInMs(connectionTimeout);
		}
		if (partitionCount > 0) {
			pool.setPartitionCount(partitionCount);
		}
		if (minConnectionsPerPartition > 0) {
			pool.setMinConnectionsPerPartition(minConnectionsPerPartition);
		}
		if (maxConnectionsPerPartition > 0) {
			pool.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
		}
		pool.flush();

		printf("Created database pool '%s'", poolId);

	}

}
