/*****************************************************************************
 * Copyright (c) 2018 CEA LIST, EclipseSource and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - replaced default editParts with customized versions for enhancement
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.providers;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.IEditPolicyProvider;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneOccurenceSemanticEditPolicy;

/**
 * Enhances Papyrus's EditPart provision by selectively replacing default EditParts with
 * customized versions for enhanced functionality.
 */
public class AdoneSemanticOccurrenceEditPolicyProvider extends AbstractProvider implements IEditPolicyProvider {

	@Override
	public boolean provides(IOperation operation) {
		if (false == operation instanceof CreateEditPoliciesOperation) {
			return false;
		}

		CreateEditPoliciesOperation op = (CreateEditPoliciesOperation) operation;
		EditPart editPart = op.getEditPart();

		// Only install this on Message EditParts. We also need that policy for ExecSpecs,
		// but CustomExecutionSpecificationEditPolicyProvider already takes care of that
		return editPart instanceof AbstractMessageEditPart;
	}

	@Override
	public void createEditPolicies(EditPart editPart) {
		editPart.installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new AdoneOccurenceSemanticEditPolicy());
	}

}
