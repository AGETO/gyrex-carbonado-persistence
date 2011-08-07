/**
 * Copyright (c) 2009, 2010 AGETO Service GmbH and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gunnar Wagenknecht - initial API and implementation
 */
package net.ageto.gyrex.persistence.carbonado.storage.internal;

import org.eclipse.gyrex.persistence.storage.Repository;
import org.eclipse.gyrex.persistence.storage.provider.RepositoryProvider;
import org.eclipse.gyrex.persistence.storage.settings.IRepositoryPreferences;

import net.ageto.gyrex.persistence.carbonado.storage.CarbonadoRepository;

/**
 * Provider of C3P0 based JDBC repositories.
 */
public class CarbonadoRepositoryProvider extends RepositoryProvider {

	public static final String ID = "net.ageto.gyrex.persistence.carbonado";

	/**
	 * Creates a new instance.
	 */
	public CarbonadoRepositoryProvider() {
		super(ID, CarbonadoRepository.class);
	}

	@Override
	public Repository createRepositoryInstance(final String repositoryId, final IRepositoryPreferences repositoryPreferences) {
		try {
			return new CarbonadoRepositoryImpl(repositoryId, this, repositoryPreferences);
		} catch (final Exception e) {
			throw new IllegalStateException("Error initializing repository. " + e.getMessage(), e);
		}
	}
}
