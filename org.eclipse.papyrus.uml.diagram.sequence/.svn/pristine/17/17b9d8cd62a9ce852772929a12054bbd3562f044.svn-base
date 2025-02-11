/*****************************************************************************
 * Copyright (c) 2018 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced Combined Fragment creation by customizing edit policies.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneCombinedCreationEditPolicy;

/**
 * Extends the functionality for creating Combined Fragments by overriding the edit policy.
 */
public class AdoneCCombinedFragmentCombinedFragmentCompartmentEditPart extends CCombinedFragmentCombinedFragmentCompartmentEditPart {

	public AdoneCCombinedFragmentCombinedFragmentCompartmentEditPart(View view) {
		super(view);
	}

	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new AdoneCombinedCreationEditPolicy());
	}

}
