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
package net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc;

import javax.sql.DataSource;

import org.eclipse.gyrex.persistence.storage.settings.IRepositoryPreferences;

import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;

/**
 * Support for initializing {@link DataSource}.
 * <p>
 * Note, this class is part of a service provider API and might evolve faster
 * than usual.
 * </p>
 */
public abstract class DataSourceSupport {

	public static enum DatabaseType {
		MySQL
	}

	/** the OSGi service name a service must be registered under */
	public static final String SERVICE_NAME = DataSourceSupport.class.getName();

	/**
	 * Constant which defines the filter string for acquiring the service which
	 * specifies the connection pool data source.
	 */
	public static final String POOL_FILTER = "(&(objectClass=" + DataSourceSupport.class.getName() + ")(datasource.type=pool))";

	/**
	 * Constant which defines the filter string for acquiring the service which
	 * specifies the database driver data source.
	 */
	public static final String DRIVER_FILTER = "(&(objectClass=" + DataSourceSupport.class.getName() + ")(datasource.type=driver))";

	/**
	 * Creates and returns a {@link DataSource}.
	 * 
	 * @param repositoryId
	 *            the repository id
	 * @param preferences
	 *            the data source configuration
	 * @return the {@link DataSource}
	 */
	public abstract DataSource createDataSource(String repositoryId, IRepositoryPreferences preferences);

	protected DatabaseType getDatabaseType(final IRepositoryPreferences preferences) {
		return DatabaseType.valueOf(preferences.get(CarbonadoRepository.TYPE, DatabaseType.MySQL.name()));
	}

	/**
	 * Returns debug information suitable for logging and error reporting
	 * purposes.
	 * 
	 * @param preferences
	 *            the data source configuration
	 * @return debug information
	 */
	public abstract String getDebugInfo(final IRepositoryPreferences preferences);
}
