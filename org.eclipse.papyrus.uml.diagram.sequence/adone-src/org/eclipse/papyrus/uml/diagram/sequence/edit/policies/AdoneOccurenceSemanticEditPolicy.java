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
 *   RealizeSoft - prepared for future extension.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.ReorientRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultSemanticEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.util.DurationLinkUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.GeneralOrderingUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationUtil;

/**
 * Prepares for future enhancements by extending the DefaultSemanticEditPolicy. This class lays the groundwork for advanced
 * handling of occurrence semantics within sequence diagrams, including reconnection of source and target elements in relationships.
 * It anticipates further development to more intricately manage the semantics of occurrence specifications, enabling sophisticated
 * reorienting and connection adjustments as part of its future expansion.
 */
public class AdoneOccurenceSemanticEditPolicy extends DefaultSemanticEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (REQ_RECONNECT_SOURCE.equals(request.getType())
				&& relationshipSourceHasChanged((ReconnectRequest) request)) {
			EditPart connectionEP = ((ReconnectRequest) request)
					.getConnectionEditPart();
			if (ViewUtil.resolveSemanticElement((View) connectionEP.getModel()) == null) {
				return getReorientRefRelationshipSourceCommand((ReconnectRequest) request);
			} else {
				return getReorientRelationshipSourceCommand((ReconnectRequest) request);
			}
		} else if (REQ_RECONNECT_TARGET.equals(request.getType())
				&& relationshipTargetHasChanged((ReconnectRequest) request)) {
			EditPart connectionEP = ((ReconnectRequest) request)
					.getConnectionEditPart();
			if (ViewUtil.resolveSemanticElement((View) connectionEP.getModel()) == null) {
				return getReorientRefRelationshipTargetCommand((ReconnectRequest) request);
			} else {
				return getReorientRelationshipTargetCommand((ReconnectRequest) request);
			}
		}

		return super.getCommand(request);
	}

	protected boolean relationshipSourceHasChanged(ReconnectRequest request) {
		if (!request.getConnectionEditPart().getSource().equals(request.getTarget())) {
			// Connecting different edit parts
			return true;
		} else if (request.getConnectionEditPart().getModel() instanceof Edge) {
			// Connecting different occurrences on the same edit part (Source vs Target, Start vs Finish...)
			Edge edge = (Edge) request.getConnectionEditPart().getModel();
			return OccurrenceSpecificationUtil.getSourceOccurrence(edge) != OccurrenceSpecificationUtil.getOccurrence(request);
		}
		return false;
	}

	protected boolean relationshipTargetHasChanged(ReconnectRequest request) {
		if (!request.getConnectionEditPart().getTarget().equals(request.getTarget())) {
			// Connecting different edit parts
			return true;
		} else if (request.getConnectionEditPart().getModel() instanceof Edge) {
			// Connecting different occurrences on the same edit part (Source vs Target, Start vs Finish...)
			Edge edge = (Edge) request.getConnectionEditPart().getModel();
			return OccurrenceSpecificationUtil.getTargetOccurrence(edge) != OccurrenceSpecificationUtil.getOccurrence(request);
		}
		return false;
	}

	@Override
	protected Command getReorientRelationshipSourceCommand(ReconnectRequest request) {
		if (GeneralOrderingUtil.isGeneralOrderingLink(request) || DurationLinkUtil.isDurationLink(request)) {
			EObject connectionSemElement = ViewUtil.resolveSemanticElement(((View) request.getConnectionEditPart()
					.getModel()));
			EObject targetSemElement = OccurrenceSpecificationUtil.getOccurrence(request);
			EObject oldSemElement = OccurrenceSpecificationUtil.getSourceOccurrence((Edge) request.getConnectionEditPart().getModel());

			TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost())
					.getEditingDomain();
			ReorientRelationshipRequest semRequest = new ReorientRelationshipRequest(
					editingDomain, connectionSemElement, targetSemElement,
					oldSemElement, ReorientRequest.REORIENT_SOURCE);

			semRequest.addParameters(request.getExtendedData());

			return getSemanticCommand(semRequest);
		}

		return super.getReorientRefRelationshipSourceCommand(request);
	}

	@Override
	protected Command getReorientRelationshipTargetCommand(ReconnectRequest request) {
		if (GeneralOrderingUtil.isGeneralOrderingLink(request) || DurationLinkUtil.isDurationLink(request)) {
			EObject connectionSemElement = ViewUtil.resolveSemanticElement((View) request.getConnectionEditPart().getModel());
			EObject targetSemElement = OccurrenceSpecificationUtil.getOccurrence(request);
			EObject oldSemElement = OccurrenceSpecificationUtil.getTargetOccurrence((Edge) request.getConnectionEditPart().getModel());

			TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost())
					.getEditingDomain();
			ReorientRelationshipRequest semRequest = new ReorientRelationshipRequest(
					editingDomain, connectionSemElement, targetSemElement,
					oldSemElement, ReorientRequest.REORIENT_TARGET);

			semRequest.addParameters(request.getExtendedData());

			return getSemanticCommand(semRequest);
		}

		return super.getReorientRelationshipTargetCommand(request);
	}

}
