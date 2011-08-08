/**
 * Copyright (c) 2011 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.jdbc.pool.internal;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.eclipse.gyrex.common.identifiers.IdHelper;

import org.osgi.service.jdbc.DataSourceFactory;

import org.apache.commons.lang.StringUtils;

import net.ageto.gyrex.persistence.jdbc.pool.IPoolDataSourceFactoryConstants;

/**
 * {@link DataSourceFactory} using BoneCP connection pooling facility.
 */
public class PoolDataSourceFactory implements DataSourceFactory, IPoolDataSourceFactoryConstants {

	@Override
	public ConnectionPoolDataSource createConnectionPoolDataSource(final Properties props) throws SQLException {
		throw new SQLException("not allowed");
	}

	@Override
	public DataSource createDataSource(final Properties props) throws SQLException {
		final String poolId = props.getProperty(POOL_ID);
		if (!IdHelper.isValidId(poolId)) {
			throw new SQLException(String.format("invalid pool id '%s'; please use only a-z, 0-9, dot, dash and/or underscore", String.valueOf(poolId)));
		}

		final String databaseName = props.getProperty(JDBC_DATABASE_NAME);
		if (StringUtils.isBlank(databaseName)) {
			throw new SQLException(String.format("property '%s' not set", JDBC_DATABASE_NAME));
		}

		// return data source
		return new PoolDataSource(poolId, databaseName);
	}

	@Override
	public Driver createDriver(final Properties props) throws SQLException {
		throw new SQLException("not allowed");
	}

	@Override
	public XADataSource createXADataSource(final Properties props) throws SQLException {
		throw new SQLException("not allowed");
	}

}
