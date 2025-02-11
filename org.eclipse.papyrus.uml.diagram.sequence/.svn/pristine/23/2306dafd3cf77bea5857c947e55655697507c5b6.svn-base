/*****************************************************************************
 * Copyright (c) 2024 RealizeSoft and others.
 *
 * All rights reserved. This file is part of a software program that is made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   RealizeSoft - Original development and implementation based on previous
 *                 work by CEA LIST and others under the same license.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.assitant;

import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.IModelingAssistantProvider;
import org.eclipse.papyrus.infra.gmfdiag.assistant.core.IModelingAssistantModelProvider;

import com.google.common.collect.Iterables;

/**
 * Provides modeling assistant providers based on a given resource URI.
 * This class is responsible for loading and providing access to modeling assistant providers
 * from a specified resource within a ResourceSet.
 */
public class AdoneModelingAssistantModelProvider implements IModelingAssistantModelProvider {

	// URI of the resource from which to load the modeling assistant providers.
	private final URI resourceURI;

	/**
	 * Constructs a modeling assistant model provider with the specified resource URI.
	 *
	 * @param resourceURI
	 *            the URI of the resource to load providers from
	 */
	public AdoneModelingAssistantModelProvider(URI resourceURI) {
		this.resourceURI = resourceURI;
	}

	/**
	 * Loads modeling assistant providers from the resource defined by the resourceURI.
	 * Attempts to load the resource with the given URI, falling back to a non-loading option in case of an error.
	 *
	 * @param resourceSet
	 *            the resource set in which to look for the resource
	 * @return an iterable of modeling assistant providers found in the resource, or an empty list if none are found or the resource fails to load
	 */
	@Override
	public Iterable<? extends IModelingAssistantProvider> loadProviders(ResourceSet resourceSet) {
		Iterable<? extends IModelingAssistantProvider> result;

		Resource resource = null;

		try {
			resource = resourceSet.getResource(resourceURI, true);
		} catch (Exception e) {
			e.printStackTrace();
			resource = resourceSet.getResource(resourceURI, false);
		}

		if (resource != null) {
			EcoreUtil.resolveAll(resource);
			result = Iterables.filter(resource.getContents(), IModelingAssistantProvider.class);
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

}
