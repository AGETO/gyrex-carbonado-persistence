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

import java.sql.Connection;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;

/**
 * Support for verifying, initializing and migrating database schemas used by
 * JDBC Carbonado repositories.
 * <p>
 * Note, this class is part of a service provider API and might evolve faster
 * than usual.
 * </p>
 */
public abstract class DatabaseSchemaSupport {

	/** the OSGi service name a service must be registered under */
	public static final String SERVICE_NAME = DatabaseSchemaSupport.class.getName();

	/**
	 * Returns the supported content types.
	 * 
	 * @return a list of supported content types
	 */
	public abstract RepositoryContentType[] getSupportedContentTypes();

	/**
	 * Indicates if the specified content type is properly provisioned on the
	 * specified database.
	 * <p>
	 * If the method returns a status that is not {@link IStatus#isOK()} it can
	 * be assumed that the content type must be provisioned using
	 * {@link #provision(RepositoryContentType, Connection, IProgressMonitor)}.
	 * </p>
	 * 
	 * @param repository
	 *            the repository to check (may not be <code>null</code>)
	 * @param contentType
	 *            the content type (may not be <code>null</code>)
	 * @param connection
	 *            a connection to the database (may not be <code>null</code>)
	 * @return status with {@link IStatus#isOK()} returning <code>true</code> if
	 *         the specified content type is provisioned properly,
	 *         <code>false</code> otherwise
	 * @throws IllegalArgumentException
	 *             if the specified content type is not supported at all (eg.
	 *             incompatible)
	 */
	public abstract IStatus isProvisioned(CarbonadoRepository repository, RepositoryContentType contentType, Connection connection) throws IllegalArgumentException;

	/**
	 * Provisions the specified content type on the specified database.
	 * <p>
	 * The method returns a {@link IStatus status object} indicating the result
	 * of the provision operation. In case the {@link IStatus#getSeverity()
	 * severity} of the status object returned is one of {@link IStatus#OK},
	 * {@link IStatus#INFO} or {@link IStatus#WARNING} the provisioning
	 * operation is considered successful, i.e. subsequent calls to
	 * {@link #isProvisioned(RepositoryContentType, Connection)} will return
	 * <code>true</code> after this method returned. In all other cases the
	 * provisioning operation must be considered failed.
	 * </p>
	 * 
	 * @param repository
	 *            the repository to check (may not be <code>null</code>)
	 * @param contentType
	 *            the content type (may not be <code>null</code>)
	 * @param connection
	 *            a connection to the database (may not be <code>null</code>)
	 * @param progressMonitor
	 *            a monitor for reporting progress an checking cancellation if
	 *            supported (may be <code>null</code> if progress reporting is
	 *            not desired)
	 * @return a status indicating the result of the provisioning operation
	 * @throws IllegalArgumentException
	 *             if the content type is not supported at all (eg.
	 *             incompatible)
	 */
	public abstract IStatus provision(CarbonadoRepository repository, RepositoryContentType contentType, Connection connection, IProgressMonitor monitor) throws IllegalArgumentException;
}
