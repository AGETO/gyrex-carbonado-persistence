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

import org.eclipse.gyrex.common.console.BaseCommandProvider;

import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * pool commands
 */
public class PoolCommands extends BaseCommandProvider {

	/**
	 * Creates a new instance.
	 */
	public PoolCommands() {
		registerCommand("ls", ListPools.class);

		registerCommand("create", CreatePool.class);
		registerCommand("configDriver", ConfigurePoolDriverProperty.class);

		registerCommand("flush", FlushPool.class);
		registerCommand("remove", RemovePool.class);
	}

	public void _pool(final CommandInterpreter ci) {
		execute(ci);
	}

	@Override
	protected String getCommandName() {
		return "pool";
	}

}
