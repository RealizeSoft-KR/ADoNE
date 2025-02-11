/*****************************************************************************
 * Copyright (c) 2010 CEA
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Soyatec - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced icon support in message labels and signiture selection.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.IMaskManagedLabelEditPolicy;
import org.eclipse.papyrus.uml.diagram.common.editpolicies.UMLTextSelectionEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.MessageLabelEditPolicy.ICustomMessageLabel;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.tools.AdoneDragMessageNameEditPartsTracker;
import org.eclipse.swt.graphics.Image;

/**
 * Extends CustomMessageNameEditPart to support operation icons in message labels and enhances the
 * functionality by providing a dialog to select a message's signature. This class provides a mechanism
 * to add an operation icon to message names in UML diagrams, enhancing visual clarity, and allows for
 * easy modification of the message signature through a user-friendly dialog. The icon's path is specified,
 * but adjustments may be required to optimize the gap between the icon and the operation name, ensuring
 * a clear and informative representation in the diagram.
 */
public class AdoneMessageNameEditPart extends CustomMessageNameEditPart implements ICustomMessageLabel {

	private static final String UML_OPERAION_ICON_PATH = "/adone-icons/obj16/operation.gif";

	public AdoneMessageNameEditPart(View view) {
		super(view);
	}

	@Override
	protected Image getLabelIcon() {
		return UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_OPERAION_ICON_PATH);
	}

	@Override
	protected void refreshLabel() {
		EditPolicy maskLabelPolicy = getParent().getEditPolicy(IMaskManagedLabelEditPolicy.MASK_MANAGED_LABEL_EDIT_POLICY);
		if (maskLabelPolicy == null) {
			setLabelTextHelper(getFigure(), getLabelText());
			setLabelIconHelper(getFigure(), getLabelIcon());
		}
		Object pdEditPolicy = getEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE);
		if (pdEditPolicy instanceof UMLTextSelectionEditPolicy) {
			((UMLTextSelectionEditPolicy) pdEditPolicy).refreshFeedback();
		}
		Object sfEditPolicy = getEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE);
		if (sfEditPolicy instanceof UMLTextSelectionEditPolicy) {
			((UMLTextSelectionEditPolicy) sfEditPolicy).refreshFeedback();
		}

		// below codes insert operation icon as needed; note wide gap between icon and operation name (2024-02-02)
		// setLabelTextHelper(getFigure(), getLabelText());
		// setLabelIconHelper(getFigure(), getLabelIcon());
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		// Redefined tracker for message signature selection
		return new AdoneDragMessageNameEditPartsTracker(this) {
			@Override
			protected boolean isMove() {
				return true;
			}
		};
	}

}
