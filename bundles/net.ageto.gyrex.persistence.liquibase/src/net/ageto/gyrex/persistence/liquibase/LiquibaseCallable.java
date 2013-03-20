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
package net.ageto.gyrex.persistence.liquibase;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

/**
 * A generic callable for running native Liquibase operations.
 */
public interface LiquibaseCallable<T> {

	T runWithLiquibase(Liquibase liquibase) throws LiquibaseException;

}
