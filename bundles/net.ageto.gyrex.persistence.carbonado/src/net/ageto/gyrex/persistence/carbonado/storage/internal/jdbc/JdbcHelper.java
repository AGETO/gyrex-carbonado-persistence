/**
 * Copyright (c) 2013 Gunnar Wagenknecht and others.
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

import javax.sql.DataSource;

/**
 * Helper for JDBC.
 */
public class JdbcHelper {

	/**
	 * Calls "close" on a DataSource if it has a "close" method.
	 */
	public static void closeQuietly(final DataSource ds) {
		try {
			ds.getClass().getMethod("close").invoke(ds);
		} catch (final Exception e) {
			// ignore
		}
	}

	private JdbcHelper() {
		// empty
	}
}
