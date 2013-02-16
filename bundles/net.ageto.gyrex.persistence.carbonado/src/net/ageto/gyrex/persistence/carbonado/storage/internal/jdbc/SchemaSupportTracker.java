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
package net.ageto.gyrex.persistence.carbonado.storage.internal.jdbc;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.gyrex.persistence.storage.content.RepositoryContentType;

import org.eclipse.core.runtime.OperationCanceledException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import net.ageto.gyrex.persistence.carbonado.storage.spi.jdbc.DatabaseSchemaSupport;

/**
 * Tracker for {@link DatabaseSchemaSupport} services.
 */
public class SchemaSupportTracker extends ServiceTracker<DatabaseSchemaSupport, DatabaseSchemaSupport> {

	private final ReadWriteLock schemaSupportByContentTypeMapLock = new ReentrantReadWriteLock();
	private final ConcurrentMap<RepositoryContentType, DatabaseSchemaSupport> schemaSupportByContentTypeMap = new ConcurrentHashMap<RepositoryContentType, DatabaseSchemaSupport>();

	public SchemaSupportTracker(final BundleContext context) {
		super(context, DatabaseSchemaSupport.SERVICE_NAME, null);
	}

	@Override
	public DatabaseSchemaSupport addingService(final ServiceReference<DatabaseSchemaSupport> reference) {
		final DatabaseSchemaSupport schemaSupport = super.addingService(reference);
		if (null != schemaSupport) {
			final Lock lock = schemaSupportByContentTypeMapLock.writeLock();
			lock.lock();
			try {
				for (final RepositoryContentType contentType : schemaSupport.getSupportedContentTypes()) {
					schemaSupportByContentTypeMap.put(contentType, schemaSupport);
				}
			} finally {
				lock.unlock();
			}
		}
		return schemaSupport;
	}

	public DatabaseSchemaSupport getSchemaSupport(final RepositoryContentType contentType) {
		final Lock lock = schemaSupportByContentTypeMapLock.readLock();
		try {
			if (!lock.tryLock(2, TimeUnit.SECONDS)) {
				throw new IllegalStateException("locked");
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new OperationCanceledException("interrupted");
		}
		try {
			return schemaSupportByContentTypeMap.get(contentType);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void removedService(final ServiceReference<DatabaseSchemaSupport> reference, final DatabaseSchemaSupport service) {
		final DatabaseSchemaSupport schemaSupport = service;
		if (null != schemaSupport) {
			final Lock lock = schemaSupportByContentTypeMapLock.writeLock();
			lock.lock();
			try {
				for (final RepositoryContentType contentType : schemaSupport.getSupportedContentTypes()) {
					schemaSupportByContentTypeMap.remove(contentType, schemaSupport);
				}
			} finally {
				lock.unlock();
			}
		}
		super.removedService(reference, service);
	}
}
