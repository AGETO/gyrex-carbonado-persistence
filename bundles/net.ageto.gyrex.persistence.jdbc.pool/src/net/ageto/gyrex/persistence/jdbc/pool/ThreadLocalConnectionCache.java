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
package net.ageto.gyrex.persistence.jdbc.pool;

import net.ageto.gyrex.persistence.jdbc.pool.internal.cache.ThreadLocalCache;

/**
 * Allows to activate or deactivate a thread specific connection caching.
 * <p>
 * When performing mass operations from within a single thread it might be
 * beneficial to bind a connection to the current thread. This avoids potential
 * expensive check-ins to and check-outs from underlying pool.
 * </p>
 */
public class ThreadLocalConnectionCache {

	/**
	 * Activates the current thread connection cache if not already active.
	 * <p>
	 * Note, callers <strong>must</strong> use the returned state to decide if
	 * deactivation is necessary!
	 * 
	 * <pre>
	 * boolean activated = ThreadLocalConnectionCache.activate();
	 * try {
	 *    ...
	 * } finally {
	 *    if (activated) 
	 *       ThreadLocalConnectionCache.deactivate();
	 * }
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @return <code>true</code> if the cache was not active before and been
	 *         activated, <code>false</code> if it was already active.
	 */
	public static boolean activate() {
		if (ThreadLocalCache.isActive())
			return false; // already active

		// activate
		ThreadLocalCache.activate();

		// return active state
		return ThreadLocalCache.isActive();
	}

	/**
	 * Deactivate the current thread connection cache and releases all cached
	 * connections.
	 */
	public static void deactivate() {
		ThreadLocalCache.deactivateAndReleaseAllConnections();
	}

}
