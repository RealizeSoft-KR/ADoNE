/**
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
 */
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.BorderDisplayEditPolicy;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultCompartmentSemanticEditPolicy;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultCreationEditPolicy;
import org.eclipse.papyrus.uml.diagram.common.editparts.AbstractListCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.common.editpolicies.PasteEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.CombinedCreationEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.CustomDiagramDragDropEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.ResizeOperandEditPolicy;

/**
 * @generated
 */
public class CombinedFragmentCombinedFragmentCompartmentEditPart extends AbstractListCompartmentEditPart {

	/**
	 * @generated
	 */
	public static final String VISUAL_ID = "CombinedFragment_SubfragmentCompartment"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	public CombinedFragmentCombinedFragmentCompartmentEditPart(View view) {
		super(view);
	}

	/**
	 * @generated
	 */
	@Override
	protected boolean hasModelChildrenChanged(Notification evt) {
		return false;
	}

	/**
	 * @generated
	 */
	@Override
	public String getCompartmentName() {
		return Messages.CombinedFragmentCombinedFragmentCompartmentEditPart_title;
	}

	/**
	 * @generated
	 */
	@Override
	public IFigure createFigure() {
		ResizableCompartmentFigure result = (ResizableCompartmentFigure) super.createFigure();
		result.setTitleVisibility(false);
		return result;
	}

	/**
	 * @generated
	 */
	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicyRoles.SEMANTIC_ROLE, new DefaultCompartmentSemanticEditPolicy());
		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new DefaultCreationEditPolicy());
		installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new DragDropEditPolicy());
		installEditPolicy(PasteEditPolicy.PASTE_ROLE, new PasteEditPolicy());
		// in Papyrus diagrams are not strongly synchronised
		// installEditPolicy(org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles.CANONICAL_ROLE, new org.eclipse.papyrus.uml.diagram.sequence.edit.policies.CombinedFragmentCombinedFragmentCompartmentCanonicalEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ResizeOperandEditPolicy());
		installEditPolicy(BorderDisplayEditPolicy.BORDER_DISPLAY_EDITPOLICY, new BorderDisplayEditPolicy());
		installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new CustomDiagramDragDropEditPolicy());
		installEditPolicy(EditPolicyRoles.CREATION_ROLE, new CombinedCreationEditPolicy());
	}
}
