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
package net.ageto.gyrex.persistence.liquibase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import liquibase.Liquibase;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.lockservice.LockService;
import liquibase.resource.ResourceAccessor;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.SqlStatement;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.Bundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DatabaseSchemaSupport;
import net.ageto.gyrex.persistence.liquibase.internal.LiquibaseActivator;

/**
 * Base class for Carbonado schema support using Liqubase
 */
public class LiquibaseSchemaSupport extends DatabaseSchemaSupport {

	private static final Logger LOG = LoggerFactory.getLogger(LiquibaseSchemaSupport.class);

	final Bundle bundle;
	final IPath basePath;

	final ClassLoader bundleClassLoader = new ClassLoader() {
		@Override
		protected java.lang.Class<?> findClass(final String name) throws ClassNotFoundException {
			return bundle.loadClass(name);
		};

		@Override
		protected URL findResource(final String name) {
			return bundle.getResource(name);
		};

		@Override
		protected java.util.Enumeration<URL> findResources(final String name) throws IOException {
			return bundle.getResources(name);
		};
	};

	final ResourceAccessor bundleResourceAccessor = new ResourceAccessor() {
		@Override
		public InputStream getResourceAsStream(final String file) throws IOException {
			final URL entry = bundle.getEntry(basePath.append(file).toString());
			if (null != entry)
				return entry.openStream();
			return null;
		}

		@Override
		public Enumeration<URL> getResources(final String packageName) throws IOException {
			return bundle.getResources(basePath.append(packageName).toString());
		}

		@Override
		public ClassLoader toClassLoader() {
			return bundleClassLoader;
		}
	};

	private final ConcurrentMap<RepositoryContentType, String> changeLogsByContentTypeMap = new ConcurrentHashMap<RepositoryContentType, String>(1);

	/**
	 * Creates a new instance.
	 * 
	 * @param bundle
	 *            the bundle providing the change logs
	 * @param baseName
	 *            the base name for resolving change log files
	 * @param supportedContentTypes
	 *            the supported content types
	 */
	public LiquibaseSchemaSupport(final Bundle bundle, final String baseName) {
		this.bundle = bundle;
		basePath = new Path(baseName).makeAbsolute().addTrailingSeparator();
	}

	private String getChangeLogFile(final RepositoryContentType contentType) {
		final String changeLogFile = changeLogsByContentTypeMap.get(contentType);
		if (null == changeLogFile)
			throw new IllegalArgumentException(String.format("no change log file for content type: %s", contentType.toString()));
		return changeLogFile;
	}

	@Override
	public RepositoryContentType[] getSupportedContentTypes() {
		final Set<RepositoryContentType> set = changeLogsByContentTypeMap.keySet();
		return set.toArray(new RepositoryContentType[set.size()]);
	}

	@Override
	public IStatus isProvisioned(final CarbonadoRepository repository, final RepositoryContentType contentType, final Connection connection) throws IllegalArgumentException {
		LOG.debug("Verify content type '{}' for repository '{}'", contentType.getMediaType(), repository.getRepositoryId());

		final LiquibaseCallable<IStatus> callable = new LiquibaseCallable<IStatus>() {
			@Override
			public IStatus runWithLiquibase(final Liquibase liquibase) throws LiquibaseException {
				LOG.debug("Collecting pending change sets.");
				final List<ChangeSet> changeSets = liquibase.listUnrunChangeSets(repository.getRepositoryId());

				// check if fine
				if (changeSets.isEmpty()) {
					LOG.debug("No pending changes found. Database schema of repository {} is good to go.", repository.getRepositoryId());
					return new Status(IStatus.OK, LiquibaseActivator.SYMBOLIC_NAME, String.format("No pending changes for content '%s' version %s.", contentType.getMediaTypeSubType(), contentType.getVersion()));
				}

				// build list of changes
				final MultiStatus result = new MultiStatus(LiquibaseActivator.SYMBOLIC_NAME, 0, String.format("Pending changes for content '%s' version %s.", contentType.getMediaTypeSubType(), contentType.getVersion()), null);

				// log warning
				LOG.debug("Found pending change set. Database schema of repository {} needs to be migrated in order to support content of type {} (version {}).", new Object[] { repository.getDescription(), contentType.getMediaType(), contentType.getVersion() });

				// log pending change sets
				for (final ChangeSet changeSet : changeSets) {
					LOG.debug("Found pending change set: {}", changeSet.toString(true));

					final String id = changeSet.getId();
					final String description = StringUtils.isBlank(changeSet.getDescription()) ? changeSet.getFilePath() : changeSet.getDescription();
					final String author = StringUtils.isBlank(changeSet.getAuthor()) ? "Mr. UNKNOWN" : changeSet.getAuthor();

					final MultiStatus changeStatus = new MultiStatus(LiquibaseActivator.SYMBOLIC_NAME, 0, String.format("%s (by %s): %s", id, author, description), null);
					final List<Change> changes = changeSet.getChanges();
					for (final Change change : changes) {
						final SqlStatement[] statements = change.generateStatements(liquibase.getDatabase());
						for (final SqlStatement sqlStatement : statements) {
							final Sql[] sqls = SqlGeneratorFactory.getInstance().generateSql(sqlStatement, liquibase.getDatabase());
							for (final Sql sql : sqls) {
								changeStatus.add(new Status(IStatus.ERROR, LiquibaseActivator.SYMBOLIC_NAME, sql.toSql()));
							}
						}
					}
					result.add(changeStatus);
				}

				// give up
				return result;
			}
		};

		return runLiquibaseOperation(repository, contentType, connection, callable);
	}

	@Override
	public IStatus provision(final CarbonadoRepository repository, final RepositoryContentType contentType, final Connection connection, final IProgressMonitor monitor) throws IllegalArgumentException {
		LOG.debug("Verify content type '{}' for repository '{}'", contentType.getMediaType(), repository.getRepositoryId());

		final LiquibaseCallable<IStatus> callable = new LiquibaseCallable<IStatus>() {
			@Override
			public IStatus runWithLiquibase(final Liquibase liquibase) throws LiquibaseException {
				LOG.debug("Collecting pending change sets...");
				final List<ChangeSet> changeSets = liquibase.listUnrunChangeSets(repository.getRepositoryId());

				// check if fine
				if (changeSets.isEmpty()) {
					LOG.debug("No pending change sets found.");
					return Status.OK_STATUS;
				}

				// log pending change sets
				for (final ChangeSet changeSet : changeSets) {
					LOG.debug("Found pending change set: {}", changeSet.toString(true));
				}

				LOG.info("Migrating database schema for repository {}.", repository.getDescription());

				// perform migration
				try {
					liquibase.update(repository.getRepositoryId());
				} catch (final LiquibaseException e) {
					LOG.error("Error while updating database schema for repository {}. {}", repository.getRepositoryId(), ExceptionUtils.getRootCauseMessage(e));
					throw new IllegalStateException("Failed updating database for repository '" + repository.getRepositoryId() + "'. " + e.getMessage(), e);
				}

				// sanity check
				final List<ChangeSet> unrunChangeSets = liquibase.listUnrunChangeSets(repository.getRepositoryId());
				if (!unrunChangeSets.isEmpty()) {
					for (final ChangeSet changeSet : unrunChangeSets) {
						LOG.debug("Found unrun change set AFTER migration: {}", changeSet.toString(true));
					}
					throw new IllegalStateException("Found pending change sets AFTER database migration for repository '" + repository.getRepositoryId() + "'. Please check database manually.");
				}

				LOG.info("Successfully migrated database schema for repository {}.", repository.getDescription());

				// fine
				return Status.OK_STATUS;
			}
		};

		return runLiquibaseOperation(repository, contentType, connection, callable);
	}

	public <T> T runLiquibaseOperation(final CarbonadoRepository repository, final RepositoryContentType contentType, final Connection connection, final LiquibaseCallable<T> callable) {
		final String changeLogFile = getChangeLogFile(contentType);

		Database database = null;
		try {
			// get database
			database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

			// configure lock timeout
			final LockService lockService = LockService.getInstance(database);
			lockService.setChangeLogLockRecheckTime(500); // recheck every 500ms
			lockService.setChangeLogLockWaitTime(2000); // wait not more than 2s

			// create Liquibase object
			final Liquibase liquibase = new Liquibase(changeLogFile, bundleResourceAccessor, database);

			return callable.runWithLiquibase(liquibase);
		} catch (final LiquibaseException e) {
			throw new IllegalStateException("Failed to collect pending change sets for repository '" + repository.getRepositoryId() + "'. " + e.getMessage(), e);
		} finally {
			// cleanup
			if (null != database) {
				LockService.removeInstance(database);
			}
		}
	}

	/**
	 * Sets a change log file for the specified content type.
	 * 
	 * @param contentType
	 *            the content type
	 * @param changeLogFile
	 *            the change log file
	 */
	public void setChangeLog(final RepositoryContentType contentType, final String changeLogFile) {
		changeLogsByContentTypeMap.put(contentType, changeLogFile);
	}
}
