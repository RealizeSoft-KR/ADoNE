/*****************************************************************************
 * Copyright (c) 2017, 2018 CEA LIST, Christian W. Damus, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *   Christian W. Damus - bug 530201
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Redefines edit policies for enhanced interaction compartment editing capabilities.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneDragDropEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneMoveResizeXYLayoutEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneInteractionFragmentContainerCreationEditPolicy;

/**
 * Enhances the standard Interaction Compartment Edit Part with custom edit policies for creation, layout, and drag-and-drop functionalities.
 * It provides a refined interaction compartment editing experience by incorporating a new creation edit policy for interaction fragments,
 * a grid-based layout policy for structured arrangement, and an advanced drag-and-drop policy for intuitive manipulation of diagram elements.
 */
public class AdoneInteractionInteractionCompartmentEditPart extends CInteractionInteractionCompartmentEditPart {

	public AdoneInteractionInteractionCompartmentEditPart(View view) {
		super(view);
	}

	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new AdoneInteractionFragmentContainerCreationEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AdoneMoveResizeXYLayoutEditPolicy());
		installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new AdoneDragDropEditPolicy());

	}

}
