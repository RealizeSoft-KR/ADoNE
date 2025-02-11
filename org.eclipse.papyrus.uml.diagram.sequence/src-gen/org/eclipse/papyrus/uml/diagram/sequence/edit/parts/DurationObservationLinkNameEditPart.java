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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.LabelDirectEditPolicy;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.emf.appearance.helper.VisualInformationPapyrusConstants;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.PapyrusLinkLabelDragPolicy;
import org.eclipse.papyrus.infra.gmfdiag.common.parsers.ParserUtil;
import org.eclipse.papyrus.uml.diagram.common.editparts.AbstractLinkLabelEditPart;
import org.eclipse.papyrus.uml.diagram.common.editparts.ILabelRoleProvider;
import org.eclipse.papyrus.uml.diagram.common.editpolicies.UMLTextSelectionEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLElementTypes;

/**
 * @generated
 */
public class DurationObservationLinkNameEditPart extends AbstractLinkLabelEditPart implements ILabelRoleProvider {

	/**
	 * @generated
	 */
	public static final String VISUAL_ID = "DurationObservation_NameLabel"; //$NON-NLS-1$

	/**
	 * @generated
	 */
	static {
		registerSnapBackPosition(UMLVisualIDRegistry.getType(org.eclipse.papyrus.uml.diagram.sequence.edit.parts.DurationObservationLinkNameEditPart.VISUAL_ID), new Point(1, -13));
	}

	/**
	 * @generated
	 */
	public DurationObservationLinkNameEditPart(View view) {
		super(view);
	}

	/**
	 * @generated Papyrus Generation
	 */
	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new LabelDirectEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new UMLTextSelectionEditPolicy());
		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new PapyrusLinkLabelDragPolicy());
	}

	/**
	 * @generated
	 */
	@Override
	public IParser getParser() {
		if (parser == null) {
			parser = ParserUtil.getParser(UMLElementTypes.DurationObservation_Edge, getParserElement(), this, VISUAL_ID);
		}
		return parser;
	}

	/**
	 * @generated
	 */
	@Override
	protected void handleNotificationEvent(Notification event) {
		if (event.getNewValue() instanceof EAnnotation && VisualInformationPapyrusConstants.DISPLAY_NAMELABELICON.equals(((EAnnotation) event.getNewValue()).getSource())) {
			refreshLabel();
		}
		super.handleNotificationEvent(event);
	}

	/**
	 * @generated
	 */
	@Override
	public String getLabelRole() {
		return "Name";//$NON-NLS-1$
	}

	/**
	 * @generated
	 */
	@Override
	public String getIconPathRole() {
		return "platform:/plugin/org.eclipse.papyrus.uml.diagram.common/icons/label_role/name.png";//$NON-NLS-1$
	}
}
