/**
 * Copyright (c) 2011 AGETO and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage;

/**
 * Interface with shared constants.
 */
public interface ICarbonadoRepositoryConstants {

	/**
	 * Key (value {@value #JDBC_POOL_ID}) for the repository preference which
	 * contains the id of a connection pool to use.
	 */
	String JDBC_POOL_ID = "poolId";

	/**
	 * Key (value {@value #JDBC_POOL_ID}) for the repository preference which
	 * contains the name of the database (schema) to use.
	 */
	String JDBC_DATABASE_NAME = "databaseName";

	/**
	 * identifier (value {@value #PROVIDER_ID}) of the Carbonado repository
	 * provider
	 */
	String PROVIDER_ID = "net.ageto.gyrex.persistence.carbonado";

}
