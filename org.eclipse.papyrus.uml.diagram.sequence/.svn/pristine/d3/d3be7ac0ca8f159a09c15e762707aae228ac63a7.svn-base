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
 *   RealizeSoft - initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.uml.tools.model.UmlUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;

/**
 * Provides utility functions for working with UML models within the Adone UML tooling environment.
 * This class facilitates access to model-related information, such as retrieving the currently active UML model
 * based on the context of the selected element or the active editor. It aims to streamline operations that involve
 * querying or manipulating UML models, enhancing the efficiency of model-based operations.
 */
public class AdoneUMLModelHelper {

	/**
	 * Retrieves a collection of active UML models related to the selected element. Typically, this method is used to
	 * obtain the root model of the current editing context, facilitating access to the model for further operations.
	 *
	 * @param selectedElement
	 *            The UML element for which the related model(s) are sought. This parameter may influence
	 *            the scope of the search or the specific models returned, depending on the implementation context.
	 * @return A collection containing the root model(s) associated with the selected element. Under typical usage, this
	 *         will contain a single model—the root model of the current editing context.
	 */
	public static Collection<Element> getActiveModels(Element selectedElement) {

		Collection<Element> modelList = new ArrayList<>();

		try {
			ServicesRegistry serviceRegistry = getActiveEditor().getAdapter(ServicesRegistry.class);
			Model rootModel = (Model) UmlUtils.getUmlModel(serviceRegistry).getResource().getContents().get(0);
			modelList.add(rootModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return modelList;
	}

	/**
	 * Retrieves the currently active editor within the workbench. This method is primarily used internally to
	 * obtain context-sensitive services or models related to the active editing session.
	 *
	 * @return The currently active IEditorPart instance, or null if no editor is active. This editor is used as a
	 *         context for accessing services and models related to the current user's editing session.
	 */
	private static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().getActiveEditor();
	}

}
