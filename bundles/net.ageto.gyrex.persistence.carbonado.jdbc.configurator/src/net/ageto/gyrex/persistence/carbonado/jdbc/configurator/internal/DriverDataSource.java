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

import javax.sql.DataSource;

import org.eclipse.equinox.security.storage.StorageException;

import org.eclipse.gyrex.persistence.storage.settings.IRepositoryPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DataSourceSupport;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A driver {@link DataSourceSupport}
 */
public class DriverDataSource extends DataSourceSupport {

	private final Logger LOG = LoggerFactory.getLogger(DriverDataSource.class);

	@Override
	public DataSource createDataSource(final String repositoryId, final IRepositoryPreferences preferences) {
		final DatabaseType type = getDatabaseType(preferences);
		if (null == type) {
			return null;
		}

		if (type == DatabaseType.MySQL) {
			final MysqlDataSource ds = new MysqlDataSource();

			// server
			ds.setServerName(getServerName(preferences));
			ds.setPort(getServerPort(preferences));

			try {
				ds.setUser(getUser(preferences));
				ds.setPassword(preferences.get(CarbonadoRepository.PASSWORD, "columbus"));
			} catch (final StorageException e) {
				LOG.error("Error while accessing secure Preferences for repository: " + repositoryId + ". Trying default values.", e);
				ds.setUser("columbus");
				ds.setPassword("columbus");
			}

			// database
			ds.setDatabaseName(getDatabaseName(preferences));
			ds.setUseInformationSchema(true);

			// connection
			ds.setConnectTimeout(5000);
			ds.setSocketTimeout(30000);

			// ha
			ds.setAutoReconnect(false);
			ds.setRoundRobinLoadBalance(true);

			// perf
			ds.setUseServerPreparedStmts(true);

			// UTF-8
			ds.setUseUnicode(true);
			ds.setCharacterSetResults("UTF-8");

			// easy-of-use / convenience
			ds.setZeroDateTimeBehavior("convertToNull");

			// don't convert to UTC, done by Carbonado
			ds.setUseGmtMillisForDatetimes(false);

//			ds.setNoTimezoneConversionForTimeType(true);

			return ds;
		}

		return null;
	}

	private String getDatabaseName(final IRepositoryPreferences prefs) {
		return prefs.get(CarbonadoRepository.DBNAME, "columbus");
	}

	@Override
	public String getDebugInfo(final IRepositoryPreferences preferences) {

		final StringBuilder info = new StringBuilder();
		info.append("MySQL ");
		try {
			info.append(getUser(preferences)).append('@');
		} catch (final StorageException e) {
			// ignore
		}
		info.append(getServerName(preferences)).append(':').append(getServerPort(preferences));
		info.append('/').append(getDatabaseName(preferences));
		return info.toString();
	}

	private String getServerName(final IRepositoryPreferences prefs) {
		return prefs.get(CarbonadoRepository.HOSTNAME, "localhost");
	}

	private int getServerPort(final IRepositoryPreferences prefs) {
		return prefs.getInt(CarbonadoRepository.PORT, 3306);
	}

	private String getUser(final IRepositoryPreferences sprefs) throws StorageException {
		return sprefs.get(CarbonadoRepository.USERNAME, "columbus");
	}
}
