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
package net.ageto.gyrex.persistence.jdbc.pool.internal.cache;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ageto.gyrex.persistence.jdbc.pool.ThreadLocalConnectionCache;

/**
 * Internal implementation of {@link ThreadLocalConnectionCache}
 */
public class ThreadLocalCache {

	private static final Logger LOG = LoggerFactory.getLogger(ThreadLocalCache.class);

	private static final ThreadLocal<Map<String, CachedConnection>> cachedConnections = new ThreadLocal<Map<String, CachedConnection>>();

	public static void activate() {
		// only activate if not already active
		if (!isActive()) {
			cachedConnections.set(new HashMap<String, CachedConnection>(5));
		}
	}

	public static Connection cacheAndWrapIfPossible(final String poolId, final Connection connection) {
		final Map<String, CachedConnection> connectionsByPoolId = cachedConnections.get();
		if (connectionsByPoolId == null)
			return connection; // cache disabled

		// sanity check
		if (connectionsByPoolId.containsKey(poolId))
			throw new IllegalStateException(String.format("Cached connection already available for pool '%s'!", poolId));

		// wrap into CachedConnection
		final CachedConnection cachedConnection = new CachedConnection(connection);
		connectionsByPoolId.put(poolId, cachedConnection);
		return cachedConnection;
	}

	public static void deactivateAndReleaseAllConnections() {
		final Map<String, CachedConnection> connectionsByPoolId = cachedConnections.get();
		if (connectionsByPoolId == null)
			return; // cache disabled

		// disable
		cachedConnections.set(null);

		// close all connections
		for (final Entry<String, CachedConnection> entry : connectionsByPoolId.entrySet()) {
			try {
				entry.getValue().closeUnderlyingConnection();
			} catch (final Exception e) {
				LOG.warn("Exception closing underlying connection of pool ({}). {}", entry.getKey(), e.getMessage(), e);
			}
		}

		connectionsByPoolId.clear();
	}

	public static Connection getCached(final String poolId) {
		final Map<String, CachedConnection> connectionsByPoolId = cachedConnections.get();
		return connectionsByPoolId != null ? connectionsByPoolId.get(poolId) : null;
	}

	public static boolean isActive() {
		return cachedConnections.get() != null;
	}

}
