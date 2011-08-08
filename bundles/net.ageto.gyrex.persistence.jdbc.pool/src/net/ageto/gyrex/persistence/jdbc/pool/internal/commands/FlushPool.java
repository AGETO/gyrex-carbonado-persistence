/**
 * Copyright (c) 2011 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     <enter-developer-name-here> - initial API and implementation
 */
package net.ageto.gyrex.persistence.jdbc.pool.internal.commands;

import org.eclipse.gyrex.common.console.Command;
import org.eclipse.gyrex.common.identifiers.IdHelper;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolActivator;
import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolDefinition;

/**
 * Flushes a connection pool.
 */
public class FlushPool extends Command {

	@Argument(index = 0, usage = "an optional pool id filter string")
	String poolIdFilter;

	@Option(name = "-global", usage = "signals global flush to all pools on all nodes in the cluster")
	boolean signalGlobal;

	/**
	 * Creates a new instance.
	 */
	public FlushPool() {
		super("flushes connection pools (i.e. closes all connections)");
	}

	@Override
	protected void doExecute() throws Exception {
		// signal global
		if (signalGlobal) {
			printf("Sending global flush event...");
			PoolActivator.getInstance().getRegistry().flushGlobal();
			printf("Global flush event sent successfully.");
			return;
		}

		// check for exact filter match
		if ((null != poolIdFilter) && IdHelper.isValidId(poolIdFilter)) {
			final PoolDefinition pool = new PoolDefinition(poolIdFilter);
			if (pool.exists()) {
				flush(pool.getPoolId());
				return;
			}
		}

		// flush all known pools
		final String[] knownPoolIds = PoolDefinition.getKnownPoolIds();
		for (final String poolId : knownPoolIds) {
			if ((null == poolIdFilter) || StringUtils.containsIgnoreCase(poolId, poolIdFilter)) {
				flush(poolId);
			}
		}
	}

	private void flush(final String poolId) {
		printf("Flushing pool %s...", poolId);
		PoolActivator.getInstance().getRegistry().flushDataSource(poolId);
	}

}
