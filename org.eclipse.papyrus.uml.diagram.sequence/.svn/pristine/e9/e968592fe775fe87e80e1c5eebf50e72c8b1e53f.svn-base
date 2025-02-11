/*****************************************************************************
 * Copyright (c) 2017 CEA LIST and others.
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
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Redefines edit policies and node shape for enhancement and
 *   prevent creation of InteractionOperands other than ALT CF
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeRequest;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneInteractionOperandDragDropEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneInteractionOperandResizePolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.InteractionOperandResizePolicy;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneInteractionOperandFigure;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionOperatorKind;

/**
 * Extends the standard Interaction Operand Edit Part with custom edit policies for resizing and drag-and-drop functionalities.
 * It aims to provide enhanced editing capabilities within interaction operands in sequence diagrams.
 * This class is prepared for future modifications, including potential redefinitions of default dimensions.
 */
public class AdoneInteractionOperandEditPart extends CInteractionOperandEditPart {

	// Placeholder for future value redefinition.
	public static int DEFAULT_HEIGHT = 40;
	public static int DEFAULT_WIDHT = 100;

	private EditPart activeCreateFeedbackEditPart;

	public AdoneInteractionOperandEditPart(View view) {
		super(view);
	}

	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		installEditPolicy(InteractionOperandResizePolicy.class.getSimpleName(), new AdoneInteractionOperandResizePolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new AdoneInteractionOperandDragDropEditPolicy());

	}

	@SuppressWarnings("unchecked")
	@Override
	public void showTargetFeedback(Request request) {

		try {
			if (request instanceof CreateUnspecifiedTypeRequest) {
				((CreateUnspecifiedTypeRequest) request).getElementTypes().forEach(t -> {
					CreateRequest req = ((CreateUnspecifiedTypeRequest) request).getRequestForType((IElementType) t);
					EditPart targetEP = getTargetEditPart(req);

					if (targetEP != null) {

						// To prevent creation of InteractionOperands in CombinedFragments other than ALT,
						// feedback is suppressed in such cases (2023-12-06).
						if (targetEP.getParent() instanceof CombinedFragmentEditPart) {

							CombinedFragmentEditPart parentCfEp = (CombinedFragmentEditPart) targetEP.getParent();
							CombinedFragment parentCf = (CombinedFragment) parentCfEp.resolveSemanticElement();

							if (!parentCf.getInteractionOperator().equals(InteractionOperatorKind.ALT_LITERAL)) {
								return;
							}
						}
					}

					if (activeCreateFeedbackEditPart != targetEP) {
						if (activeCreateFeedbackEditPart != null) {
							activeCreateFeedbackEditPart.eraseTargetFeedback(request);
						}
						activeCreateFeedbackEditPart = targetEP;
					}
					if (targetEP != this) {
						targetEP.showTargetFeedback(request);
					} else {
						super.showTargetFeedback(request);
					}
				});
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.showTargetFeedback(request);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void eraseTargetFeedback(Request request) {
		if (request instanceof CreateUnspecifiedTypeRequest) {
			((CreateUnspecifiedTypeRequest) request).getElementTypes().forEach(t -> {
				CreateRequest req = ((CreateUnspecifiedTypeRequest) request).getRequestForType((IElementType) t);
				EditPart targetEP = getTargetEditPart(req);
				if (activeCreateFeedbackEditPart != null && activeCreateFeedbackEditPart != this) {
					activeCreateFeedbackEditPart.eraseTargetFeedback(request);
					activeCreateFeedbackEditPart = null;
				}
				if (targetEP != this) {
					targetEP.eraseTargetFeedback(request);
				} else {
					super.eraseTargetFeedback(request);
				}
			});
			return;
		}
		super.eraseTargetFeedback(request);
	}

	@Override
	protected IFigure createNodeShape() {
		return primaryShape = new AdoneInteractionOperandFigure();
	}

}
