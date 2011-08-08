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

import org.kohsuke.args4j.Option;

import net.ageto.gyrex.persistence.jdbc.pool.internal.PoolDefinition;

/**
 *
 */
public class RemovePool extends Command {

	@Option(name = "-id", aliases = { "--pool-id" }, usage = "pool id", required = true)
	String poolId;

	/**
	 * Creates a new instance.
	 */
	public RemovePool() {
		super("removes a new pool");
	}

	@Override
	protected void doExecute() throws Exception {
		// check id
		if (!IdHelper.isValidId(poolId)) {
			printf("ERROR: invalid pool id");
			return;
		}

		// check exists
		final PoolDefinition pool = new PoolDefinition(poolId);
		if (!pool.exists()) {
			printf("ERROR: pool not found");
			return;
		}

		pool.remove();
		printf("Removed pool '%s'.", poolId);
	}

}
