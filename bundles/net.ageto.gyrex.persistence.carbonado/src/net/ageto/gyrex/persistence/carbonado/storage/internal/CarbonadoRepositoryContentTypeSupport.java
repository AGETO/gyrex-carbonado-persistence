/**
 * Copyright (c) 2010 AGETO Service GmbH and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage.internal;

import java.sql.Connection;
import java.util.Locale;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;
import org.eclipse.gyrex.persistence.storage.content.RepositoryContentTypeSupport;
import org.eclipse.gyrex.persistence.storage.exceptions.ResourceFailureException;
import org.eclipse.gyrex.server.Platform;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.carbonado.internal.CarbonadoDebug;
import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;

import com.amazon.carbonado.FetchException;
import com.amazon.carbonado.repo.jdbc.JDBCConnectionCapability;

/**
 * support strategy for Carbonado based repositories.
 */
public class CarbonadoRepositoryContentTypeSupport extends RepositoryContentTypeSupport {

	private static final Logger LOG = LoggerFactory.getLogger(CarbonadoRepositoryContentTypeSupport.class);
	private final CarbonadoRepositoryImpl repository;

	/**
	 * Creates a new instance.
	 * 
	 * @param repository
	 * @param schemaSupportTracker
	 */
	public CarbonadoRepositoryContentTypeSupport(final CarbonadoRepositoryImpl repository) {
		this.repository = repository;
	}

	@Override
	public CarbonadoRepository getRepository() {
		return repository;
	}

	@Override
	public boolean isSupported(final RepositoryContentType contentType) {
		// ensure compatible type
		if (!contentType.getRepositoryTypeName().equals(CarbonadoRepository.class.getName())) {
			if (CarbonadoDebug.debug) {
				LOG.debug("Repository type name does not match. Found {}. Expected {}", contentType.getRepositoryTypeName(), CarbonadoRepository.class.getName());
			}
			return false;
		}

		// per definition the content type type must be "x-ageto-carbonado-repository"
		if (!contentType.getMediaTypeType().equals(CarbonadoRepository.MEDIA_TYPE_TYPE)) {
			if (CarbonadoDebug.debug) {
				LOG.debug("Media type type does not match. Found {}. Expected {}", contentType.getMediaTypeType(), CarbonadoRepository.MEDIA_TYPE_TYPE);
			}
			return false;
		}

		// check if JDBC database
		final JDBCConnectionCapability jdbcConnectionCapability = repository.getOrCreateRepository().getCapability(JDBCConnectionCapability.class);
		if (jdbcConnectionCapability == null) {
			// non JDBC repository means auto-updating schema
			return true;
		}

		// in order to simplify schema management on development systems we attempt to migrate them automatically
		migrateDatabaseSchema(jdbcConnectionCapability);

		// done
		return true;
	}

	void migrateDatabaseSchema(final JDBCConnectionCapability jdbcConnectionCapability) {
		// we only do this in development mode
		if (!Platform.inDevelopmentMode()) {
			return;
		}

		// we also only do this when running against a local database
		Connection connection = null;
		try {
			// get connection
			connection = jdbcConnectionCapability.getConnection();

			// check if we are connecting against a local database
			final String url = connection.getMetaData().getURL();
			if ((null == url) || !((url.toLowerCase(Locale.US).indexOf("localhost") >= 0) || (url.indexOf("127.0.0.1") >= 0))) {
				// not local, abort
				return;
			}
		} catch (final Exception e) {
			throw new ResourceFailureException(String.format("Unable to check database for schema migration. %s", ExceptionUtils.getRootCauseMessage(e)), e);
		} finally {
			try {
				jdbcConnectionCapability.yieldConnection(connection);
			} catch (final FetchException e) {
				throw new ResourceFailureException("Unable to properly return a database connection to the pool. This will lead to resource leaks! " + e.getMessage(), e);
			}
		}

		// important note for schema migration:
		// this has to be scheduled in a background thread in order to not interfere with the current transaction
		// this process must also be asynchronous in order to not cause any deadlocks with the current connection

		// schedule check including migration (and wait)
		repository.verifySchema(true, true);
	}

}
