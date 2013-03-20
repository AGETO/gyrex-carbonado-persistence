/*******************************************************************************
 * Copyright (c) 2013 <enter-company-name-here> and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 *******************************************************************************/
package net.ageto.gyrex.persistence.carbonado.storage.internal;

import java.sql.Connection;
import java.util.Collection;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.carbonado.internal.CarbonadoActivator;
import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;

import com.amazon.carbonado.Repository;
import com.amazon.carbonado.repo.jdbc.JDBCConnectionCapability;

/**
 * Abstract base class for operations on database schemas.
 */
public abstract class SchemaSupportJob extends Job {

	private static final class RepositoryRule implements ISchedulingRule {
		private final String repositoryId;

		public RepositoryRule(final CarbonadoRepositoryImpl repository) {
			repositoryId = repository.getRepositoryId();
		}

		@Override
		public boolean contains(final ISchedulingRule rule) {
			return this == rule;
		}

		@Override
		public boolean isConflicting(final ISchedulingRule rule) {
			return (rule instanceof RepositoryRule) && repositoryId.equals(((RepositoryRule) rule).repositoryId);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(SchemaMigrationJob.class);

	private final CarbonadoRepositoryImpl repository;
	private final Collection<RepositoryContentType> contentTypes;
	private final boolean commitWhenDone;

	private IStatus schemaStatus;

	public static final Status NOT_AVAILABLE = new Status(IStatus.INFO, CarbonadoActivator.SYMBOLIC_NAME, "Status information not available.");

	public SchemaSupportJob(final CarbonadoRepositoryImpl repository, final Collection<RepositoryContentType> contentTypes, final boolean commitWhenDone, final String name) {
		super(name);
		this.repository = repository;
		this.contentTypes = contentTypes;
		this.commitWhenDone = commitWhenDone;
		setPriority(LONG);
		setRule(new RepositoryRule(repository));
	}

	IStatus buildStatusInternal(final int severity, final String message) {
		return new Status(severity, CarbonadoActivator.SYMBOLIC_NAME, message);
	}

	public Collection<RepositoryContentType> getContentTypes() {
		return contentTypes;
	}

	public CarbonadoRepository getRepository() {
		return repository;
	}

	/**
	 * The {@link #schemaStatus} is updated at every touch of the repository.
	 * 
	 * @return the current schemaStatus or
	 *         {@link SchemaMigrationJob#NOT_AVAILABLE} if none is set
	 */
	public IStatus getSchemaStatus() {
		final IStatus status = schemaStatus;
		return null != status ? status : NOT_AVAILABLE;
	}

	protected abstract IStatus processSchema(RepositoryContentType contentType, Connection connection, IProgressMonitor monitor);

	private IStatus processSchemas(final Collection<RepositoryContentType> contentTypes, final JDBCConnectionCapability jdbcConnectionCapability, final IProgressMonitor monitor) throws Exception {
		// spin the migration loop
		Connection connection = null;
		boolean wasAutoCommit = true; // default to auto-commit
		try {
			// get connection
			connection = jdbcConnectionCapability.getConnection();
			// remember auto-commit state
			wasAutoCommit = connection.getAutoCommit();

			// collect result
			final MultiStatus result = new MultiStatus(CarbonadoActivator.SYMBOLIC_NAME, 0, String.format("Database schema verification result for database %s.", repository.getDescription()), null);

			// verify schemas
			final SubMonitor subMonitor = SubMonitor.convert(monitor, contentTypes.size());
			for (final RepositoryContentType contentType : contentTypes) {
				result.add(processSchema(contentType, connection, subMonitor.newChild(1)));
			}

			// commit any pending changes if migration was allowed
			if (commitWhenDone) {
				connection.commit();
			} else {
				connection.rollback();
			}

			return result;
		} finally {
			if (null != connection) {
				try {
					// verify that auto-commit state was not modified
					if (wasAutoCommit != connection.getAutoCommit()) {
						// Carbonado uses auto-commit to detect if a transaction
						// was in progress whan the connection was acquired previously
						// in this case it does not close the connection, which is fine;
						// however, if any schema-support implementation removed the auto-commit flag
						// Carbonado will no longer close the connection because it thinks a
						// transaction is in progress;
						// thus we need to reset the auto-commit flag in this case!
						LOG.debug("Resetting auto-commit flag on connection {} due to modifications during schema migration", connection);
						connection.setAutoCommit(wasAutoCommit);
					}
					jdbcConnectionCapability.yieldConnection(connection);
				} catch (final Exception e) {
					throw new IllegalStateException("Unable to properly return a database connection to the pool. This will lead to resource leaks! " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public IStatus run(final IProgressMonitor progressMonitor) {
		final SubMonitor monitor = SubMonitor.convert(progressMonitor, String.format("Verifying database schema for '%s'...", repository.getRepositoryId()), 100);

		Repository cRepository;
		try {
			cRepository = repository.getOrCreateRepository();
		} catch (final Exception e) {
			LOG.error("Failed to open repository {}. {}", new Object[] { repository.getRepositoryId(), ExceptionUtils.getRootCauseMessage(e), e });
			final String message = String.format("Failed to open repository. Please check server logs. %s", ExceptionUtils.getRootCauseMessage(e));
			repository.setError(message);
			schemaStatus = buildStatusInternal(IStatus.CANCEL, message);
			return Status.CANCEL_STATUS;
		}

		// check if JDBC database
		final JDBCConnectionCapability jdbcConnectionCapability = cRepository.getCapability(JDBCConnectionCapability.class);
		if (jdbcConnectionCapability == null)
			// non JDBC repository means auto-updating schema
			return schemaStatus = Status.OK_STATUS;

		// check if transaction in progress
		if (cRepository.getTransactionIsolationLevel() != null) {
			LOG.warn("Carbonado repository '{}' (using database {}) is configured with a default transaction level. This is not supported by the current schema migration implementation.", repository.getRepositoryId(), repository.getDescription());
		}

		// quick check
		if (contentTypes.isEmpty()) {
			// don't fail although this should be a programming error
			LOG.debug("No content types assigned to repository '{}'.", repository.getDescription());
			repository.setError(null);
			return schemaStatus = Status.OK_STATUS;
		}

		// verify schemas
		try {
			schemaStatus = processSchemas(contentTypes, jdbcConnectionCapability, monitor.newChild(50));
		} catch (final Exception e) {
			LOG.error("Failed to verify database schema for database {} (repository {}). {}", new Object[] { cRepository.getName(), repository.getRepositoryId(), ExceptionUtils.getRootCauseMessage(e), e });
			final String message = String.format("Unable to verify database schema. Please check server logs. %s", ExceptionUtils.getRootCauseMessage(e));
			repository.setError(message);
			schemaStatus = buildStatusInternal(IStatus.CANCEL, message);
			return Status.CANCEL_STATUS;
		}

		// done
		if ((null != schemaStatus) && schemaStatus.matches(IStatus.ERROR)) {
			repository.setError(String.format("Database schema verification failed. Please check database %s.", repository.getDescription()));
		} else {
			repository.setError(null);
			schemaStatus = Status.OK_STATUS;
		}
		return Status.OK_STATUS;
	}

}