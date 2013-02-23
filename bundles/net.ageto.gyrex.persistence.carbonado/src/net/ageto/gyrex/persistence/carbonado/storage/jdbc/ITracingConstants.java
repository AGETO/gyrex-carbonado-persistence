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
package net.ageto.gyrex.persistence.carbonado.storage.jdbc;

/**
 * 
 */
public interface ITracingConstants {

	public static final String METRIC_ID_CONNECTION_WAITS = "net.ageto.gyrex.persistence.carbonado.storage.jdbc.connection.waits";
	public static final String METRIC_ID_FETCH = "net.ageto.gyrex.persistence.carbonado.storage.jdbc.statement.fetch";
	public static final String METRIC_ID_SELECTS = "net.ageto.gyrex.persistence.carbonado.storage.jdbc.statement.selects";
	public static final String METRIC_ID_OTHER = "net.ageto.gyrex.persistence.carbonado.storage.jdbc.statement.other";

}
