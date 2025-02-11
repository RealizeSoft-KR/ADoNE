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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.core.sashwindows.di.service.IPageManager;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.infra.emf.utils.ServiceUtilsForEObject;
import org.eclipse.uml2.uml.Interaction;

/**
 * Provides utility methods related to Papyrus Diagrams
 */
public class AdoneDiagramUtil {

	/**
	 * Retrieves all sequence diagrams associated with a given interaction. This method scans through all diagrams
	 * in the model to find those that are directly associated with the specified interaction. It also handles and removes
	 * any diagrams that are found to be dangling or have lost their model elements due to potential inconsistencies in the model.
	 *
	 * @param interaction
	 *            The Interaction object for which sequence diagrams are to be retrieved.
	 * @return A list of Diagram objects representing all the sequence diagrams associated with the interaction.
	 */
	public static List<Diagram> getAllSequenceDiagrams(Interaction interaction) {

		List<Diagram> diagrams = new ArrayList<>();

		try {

			ServicesRegistry serviceRegistry = ServiceUtilsForEObject.getInstance().getServiceRegistry(interaction);

			IPageManager pageMngr = ServiceUtils.getInstance().getIPageManager(serviceRegistry);

			Map<Diagram, String> removeTargetDiagram = new HashMap<>();

			for (Object id : pageMngr.allPages()) {

				if (id instanceof Diagram) {

					Diagram page = (Diagram) id;

					// Suspected case where diagram information remains after model information has been deleted. Further investigation required
					if (page.getElement() == null) {
						System.out.println(page.getName() + " diagram's model element was not found. Skipping.");
						removeTargetDiagram.put(page, null);
						continue;
					}

					if (page.getElement().equals(interaction)) {
						diagrams.add(page);
					}
				}
			}

			// Forcefully remove diagrams that have become dangling due to unstable saves, etc.
			for (Diagram diagram : removeTargetDiagram.keySet()) {
				System.out.println(diagram.getName() + " diagram as its model element was not found.");
				ViewUtil.destroy(diagram);
			}

		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return diagrams;
	}

}
