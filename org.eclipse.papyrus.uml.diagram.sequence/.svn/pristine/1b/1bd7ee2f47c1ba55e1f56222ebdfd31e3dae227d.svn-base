/*****************************************************************************
 * Copyright (c) 2011 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Patrick Tessier (CEA LIST) Patrick.tessier@cea.fr - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - enhanced connection handlers and ToolTip util.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.providers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.core.service.IProviderChangeListener;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.services.editpolicy.CreateEditPoliciesOperation;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.SequenceDiagramEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneSequenceConnectionHandleEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AnnotatedConnectionHandleEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AnnotatedLinkEndEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AnnotatedLinkStartEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneToolTipUtil;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Observation;

/**
 * Customizes edit policies for sequence diagrams in the Adone project. Enhances diagram editing
 * by removing unneeded connection handlers, adjusting lifeline connection handlers for easier
 * message creation, and expanding Tooltip Util's capabilities. Aimed at streamlining the user
 * experience and diagram interaction.
 */
public class AdoneEditPolicyProvider extends CustomEditPolicyProvider {

	@Override
	public void addProviderChangeListener(IProviderChangeListener listener) {
	}

	@Override
	public void createEditPolicies(final EditPart editPart) {

		if (editPart instanceof AdoneInteractionInteractionCompartmentEditPart) {
			editPart.removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
			installHighlightPolicy(editPart);
		} else if (editPart instanceof AdoneLifeLineEditPart) {
			editPart.removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
			editPart.installEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE, new AdoneSequenceConnectionHandleEditPolicy());
		} else if (editPart instanceof AdoneInteractionEditPart || editPart instanceof AdoneBehaviorExecutionSpecificationEditPart
				|| editPart instanceof AdoneCombinedFragmentEditPart || editPart instanceof AdoneInteractionOperandEditPart) {
			editPart.removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
		} else {

			installHighlightPolicy(editPart);
			// install annotated link edit policy.
			if (editPart instanceof IGraphicalEditPart) {
				Object model = editPart.getModel();
				if (model instanceof View) {
					View view = (View) model;
					EObject element = ViewUtil.resolveSemanticElement((View) model);
					if (element instanceof Element && editPart instanceof INodeEditPart) {
						installEditPolicy(editPart, new AnnotatedLinkEndEditPolicy(), AnnotatedLinkEndEditPolicy.ANNOTATED_LINK_END_ROLE);
					}
					if (editPart instanceof INodeEditPart && (element instanceof Constraint || element instanceof Observation || element instanceof Comment)) {
						installEditPolicy(editPart, new AnnotatedLinkStartEditPolicy(), AnnotatedLinkStartEditPolicy.ANNOTATED_LINK_START_ROLE);
						editPart.removeEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE);
						editPart.installEditPolicy(EditPolicyRoles.CONNECTION_HANDLES_ROLE, new AnnotatedConnectionHandleEditPolicy());
					}
					// Ordering fragments after moving and resizing, See https://bugs.eclipse.org/bugs/show_bug.cgi?id=403233
					if (view.isSetElement() && (view.getElement() instanceof InteractionFragment)) {
						// editPart.installEditPolicy(InteractionFragmentsOrderingEditPolicy.ORDERING_ROLE, new InteractionFragmentsOrderingEditPolicy());
					}
				}
			}

		}

		AdoneToolTipUtil.manageTooltipEditPolicy(editPart);

	}

	/**
	 * Safely install a EditPolicy, if the editpolicy with given role is existed in editpart, ignore it.
	 *
	 * @param editPart
	 * @param editPolicy
	 * @param role
	 */
	private void installEditPolicy(EditPart editPart, EditPolicy editPolicy, String role) {
		if (editPart == null || editPolicy == null) {
			return;
		}
		EditPolicy myEditPolicy = editPart.getEditPolicy(role);
		if (myEditPolicy == null) {
			editPart.installEditPolicy(role, editPolicy);
		}
	}

	private void installHighlightPolicy(EditPart editPart) {
		// installEditPolicy(editPart, new HighlightEditPolicy(), HighlightEditPolicy.HIGHLIGHT_ROLE);
	}

	@Override
	public boolean provides(IOperation operation) {
		CreateEditPoliciesOperation epOperation = (CreateEditPoliciesOperation) operation;
		if (!(epOperation.getEditPart() instanceof GraphicalEditPart) && !(epOperation.getEditPart() instanceof ConnectionEditPart)) {
			return false;
		}
		EditPart gep = epOperation.getEditPart();
		String diagramType = ((View) gep.getModel()).getDiagram().getType();
		if (SequenceDiagramEditPart.MODEL_ID.equals(diagramType)) {
			return true;
		}
		return false;
	}

	@Override
	public void removeProviderChangeListener(IProviderChangeListener listener) {
	}
}
