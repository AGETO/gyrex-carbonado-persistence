/**
 * Copyright (c) 2011 Gunnar Wagenknecht and others.
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
import java.util.Collection;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import org.apache.commons.lang.exception.ExceptionUtils;

import net.ageto.gyrex.persistence.carbonado.internal.CarbonadoActivator;
import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DatabaseSchemaSupport;

/**
 * Migrates a database schema of a JDBC based Carbonado Repository.
 */
public class SchemaMigrationJob extends SchemaSupportJob {

	private final boolean migrate;

	public SchemaMigrationJob(final CarbonadoRepositoryImpl repository, final Collection<RepositoryContentType> contentTypes, final boolean migrate) {
		super(repository, contentTypes, migrate, String.format("Verify database schema of '%s'", repository.getDescription()));
		this.migrate = migrate;
	}

	@Override
	protected IStatus processSchema(final RepositoryContentType contentType, final Connection connection, final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, String.format("Verifying '%s' for repository '%s'", contentType.getMediaType(), getRepository().getRepositoryId()), 20);

		final DatabaseSchemaSupport schemaSupport = CarbonadoActivator.getInstance().getSchemaSupportTracker().getSchemaSupport(contentType);
		if (null == schemaSupport)
			// no schema support but JDBC repository means "managed externally" (so we assume "ok")
			return Status.OK_STATUS;

		// check if provisioned first
		final IStatus provisioningStatus;
		try {
			provisioningStatus = schemaSupport.isProvisioned(getRepository(), contentType, connection);
			if (provisioningStatus.isOK())
				// all good
				return provisioningStatus;
		} catch (final Exception e) {
			// this is not good in any case, if we can't verify that a content type
			// is provisioned, we need to abort and report any error that occurred
			// only fail if not migrating, otherwise we do make a migration attempt
			throw new IllegalStateException(String.format("Failed to check provisioning status for content type '%s'. %s", contentType, ExceptionUtils.getRootCauseMessage(e)), e);
		}

		subMonitor.worked(10);

		// attempt migration
		if (migrate) {
			final IStatus result = schemaSupport.provision(getRepository(), contentType, connection, subMonitor.newChild(10));
			if (result.matches(IStatus.CANCEL | IStatus.ERROR))
				return result;

			// all good
			return Status.OK_STATUS;
		}

		return provisioningStatus;
	}
}
