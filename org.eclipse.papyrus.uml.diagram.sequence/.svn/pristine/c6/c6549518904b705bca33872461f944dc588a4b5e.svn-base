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
 *
 *		CEA LIST - Initial API and implementation
 *      Vincent Lorenzo - bug 492522
 *      Benoit Maggi (CEA LIST) benoit.maggi@cea.fr - bug 514289
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Customized message handling in sequence diagrams,
 *   focusing on signature updates and comprehensive deletion with layout integrity
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.commands.wrappers.GMFtoGEFCommandWrapper;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultSemanticEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneSetMessageSignatureCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneMoveInteractionFragmentElementRequest;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneSetMessageSignatureRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

/**
 * Implements a semantic edit policy for Adone messages, extending the default behavior to include custom command handling
 * for message signature updates and message deletion enhancements. This policy facilitates the update of message signatures
 * through a dialog at a specified endpoint and ensures the comprehensive deletion of messages and associated elements like
 * BehaviorExecutionSpecifications (BES), maintaining the sequence diagram's integrity.
 *
 * Key functionalities include:
 * - Handling AdoneSetMessageSignatureRequest by updating message signatures and presenting a method selection dialog.
 * - Enhancing the message deletion process to also remove directly connected BES and adjust related EditParts' positions.
 *
 * The class also addresses layout adjustments post-deletion, with certain operations under review for potential future inclusion
 * after thorough testing, particularly concerning the positioning and resizing of lifelines and EditParts.
 */
public class AdoneMessageDefaultSemanticEditPolicy extends DefaultSemanticEditPolicy {


	@Override
	public Command getCommand(Request request) {
		if (request instanceof AdoneSetMessageSignatureRequest) {
			return handleSetMessageSignatureRequest((AdoneSetMessageSignatureRequest) request);
		}
		return super.getCommand(request);
	}

	/**
	 * Processes the AdoneSetMessageSignatureRequest to update the message signature. This method extracts the message
	 * from the request's target edit part, verifies it's not null, and then creates a command to update the message's
	 * signature by providing a method selection dialog which pops up at given endPoint position.
	 *
	 * @param request
	 *            The specific request to update a message's signature.
	 * @return A command to execute the signature update or UnexecutableCommand if the message is null.
	 */
	private Command handleSetMessageSignatureRequest(AdoneSetMessageSignatureRequest request) {
		MessageSyncEditPart targetPart = (MessageSyncEditPart) request.getTargetEditPart();
		Object element = ((View) targetPart.getModel()).getElement();

		if (element == null) {
			return UnexecutableCommand.INSTANCE;
		}

		Message msg = (Message) element;
		TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost()).getEditingDomain();
		IFigure figure = targetPart.getFigure();
		Point endPoint = determineEndPoint(figure);
		org.eclipse.swt.graphics.Point targetEndPoint = new org.eclipse.swt.graphics.Point(endPoint.x, endPoint.y);

		AdoneSetMessageSignatureCommand emfCommand = new AdoneSetMessageSignatureCommand(editingDomain, msg, targetEndPoint);
		CompoundCommand newCommand = new CompoundCommand();
		newCommand.add(new GMFtoGEFCommandWrapper(emfCommand));

		return newCommand;
	}

	/**
	 * Determines the end point for the message signature selection dialog based on the figure type.
	 * If the figure is a PolylineConnection, it uses the last point of the connection. Otherwise, it sets a default point.
	 *
	 * @param figure
	 *            The figure from which to derive the end point.
	 * @return The calculated end point.
	 */
	private Point determineEndPoint(IFigure figure) {
		if (figure instanceof PolylineConnection) {
			PolylineConnection connection = (PolylineConnection) figure;
			return connection.getPoints().getLastPoint();
		} else {
			// Set to default value
			return new Point(figure.getBounds().x + 220, figure.getBounds().y + 30);
		}
	}


	/**
	 * Enhances the deletion process for messages by also removing directly associated BehaviorExecutionSpecifications (BES)
	 * and adjusting the positions of related EditParts to maintain diagram integrity. This command addresses the issue where
	 * deleting a message within a CombinedFragment does not automatically remove its corresponding BES, ensuring a cleaner
	 * removal process. Additionally, it considers the adjustment of EditPart positions to avoid layout inconsistencies post-deletion.
	 *
	 * Portions of this method related to resizing lifelines and repositioning EditParts below the deleted elements are
	 * currently under review. These segments have been temporarily commented out to allow for thorough testing and evaluation
	 * before final implementation. This careful approach ensures that any adjustments to the diagram layout do not introduce
	 * errors or unexpected behavior, particularly concerning the positioning of BES and lifelines.
	 */
	@Override
	protected Command getDestroyElementCommand(DestroyElementRequest req) {

		CompoundCommand destroyCompoundCommand = new CompoundCommand();

		Command command = super.getDestroyElementCommand(req);

		if (command != null && command.canExecute()) {
			destroyCompoundCommand.add(command);
		}

		AbstractMessageEditPart targetMsgEp = (AbstractMessageEditPart) getHost();

		Message destroyMsg = (Message) targetMsgEp.resolveSemanticElement();

		// Compensate for the non-deletion of BES when deleting a message within a CF (2023-12-27)
		BehaviorExecutionSpecification bes = AdoneSequenceUtil.getFollowingBehaviorExeSpec(destroyMsg);
		if (bes != null) {
			DestroyElementRequest destroyBesRequest = new DestroyElementRequest(false);
			destroyBesRequest.setElementToDestroy(bes);
			destroyBesRequest.setClientContext(req.getClientContext());
			Command besDestroyCommand = super.getDestroyElementCommand(destroyBesRequest);
			if (besDestroyCommand != null && besDestroyCommand.canExecute()) {
				destroyCompoundCommand.add(besDestroyCommand);
			}

			MessageOccurrenceSpecification start = (MessageOccurrenceSpecification) bes.getStart();
			if (start != null) {
				DestroyElementRequest destroyBesStartOccRequest = new DestroyElementRequest(false);
				destroyBesStartOccRequest.setElementToDestroy(start);
				destroyBesStartOccRequest.setClientContext(req.getClientContext());
				Command besStartOccDestroyCommand = super.getDestroyElementCommand(destroyBesStartOccRequest);
				if (besStartOccDestroyCommand != null) {
					destroyCompoundCommand.add(besStartOccDestroyCommand);
				}
			}

		}

		// The following sections are under review for potential impacts on BES position and overall diagram layout (2024-01-09, 2024-01-23)
		// Future testing will determine their inclusion.

		Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(targetMsgEp, -AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);

		if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
			// Temporarily commented out due to BES position errors when adjusting lifelines (2024-01-09)
			// destroyCompoundCommand.add(resizeLifelineEpCommand);
		}

		Command resizeFirstBesEpHeightCommand = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand(targetMsgEp, -AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);

		if (resizeFirstBesEpHeightCommand != null && resizeFirstBesEpHeightCommand.canExecute()) {
			// Temporarily commented out due to anomalies in adjusting positions of EPs below upon deletion (2024-01-23)
			// destroyCompoundCommand.add(resizeFirstBesEpHeightCommand);
		}

		Point targetLocation = targetMsgEp.getConnectionFigure().getPoints().getFirstPoint().getCopy();

		targetLocation.y += AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT;

		AdoneMoveInteractionFragmentElementRequest moveRequest = new AdoneMoveInteractionFragmentElementRequest();
		moveRequest.setTargetEditPart(targetMsgEp);
		Point moveDelta = new Point();
		moveDelta.setX(0);
		moveDelta.setY(-AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
		moveRequest.setMoveDelta(moveDelta);
		moveRequest.setTargetLocation(targetLocation);

		InteractionInteractionCompartmentEditPart intactionInteractionEp = AdoneSequenceUtil.getInteractionInteractionCompartmentEditPart((GraphicalEditPart) getHost());

		Command moveAllBelowEpCommand = intactionInteractionEp.getCommand(moveRequest);

		if (moveAllBelowEpCommand != null && moveAllBelowEpCommand.canExecute()) {
			// Temporarily commented out due to anomalies in adjusting positions of EPs below upon deletion (2024-01-23)
			// destroyCompoundCommand.add(moveAllBelowEpCommand);
		}

		return destroyCompoundCommand;

	}


}
