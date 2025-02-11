/*****************************************************************************
 * Copyright (c) 2016 - 2017 CEA LIST and others.
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
 *   Celine Janssens (ALL4TEC) celine.janssens@all4tec.net - Bug 520154
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced movement and resize for elements like Lifelines
 *   according to element order change mode.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.papyrus.commands.wrappers.GMFtoGEFCommandWrapper;
import org.eclipse.papyrus.uml.diagram.common.editparts.RoundedCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneAddCoveredLifelineToCombinedFragment;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneMessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.GridBasedXYLayoutEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneMoveInteractionFragmentElementRequest;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneUpdateLocationByNewMessageCreationRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneCombinedFragmentEpStatusManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneInteractionHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneOccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Gate;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.OccurrenceSpecification;

/**
 * This class, AdoneMoveResizeXYLayoutEditPolicy, extends GridBasedXYLayoutEditPolicy to redefine
 * the movement and resize functionalities for diagram elements such as Lifelines, Messages,
 * and CombinedFragments within the element order change mode. The modifications are aimed at
 * maximizing user efficiency in diagramming tasks. By adapting the layout policy to better
 * support the reordering and resizing of these elements, users can more intuitively and
 * effectively manage their diagrams. The policy takes into account the specific requirements
 * and behaviors of these elements to ensure a seamless and productive user experience
 * in manipulating diagram layouts.
 *
 * It is anticipated that, following the stabilization of these features, significant
 * refactoring of the method code within this class will be necessary. This future refactoring
 * will focus on enhancing maintainability, improving performance, and ensuring the scalability
 * of the implementation to accommodate further advancements and feature integrations.
 */
public class AdoneMoveResizeXYLayoutEditPolicy extends GridBasedXYLayoutEditPolicy {

	private static final String INSERTION_LOCATION = "insertion_location";
	private static final int MARGIN_BEFORE_FIRST = 20;
	private static final int MARGIN_BETWEEN_LIFELINE = 10;
	protected IFigure layoutFeedbackFigure = null;

	public AdoneMoveResizeXYLayoutEditPolicy() {
		super();
	}

	/**
	 * Overrides the default getCommand method to handle specific move requests.
	 * This implementation specifically checks for AdoneMoveInteractionFragmentElementRequest
	 * types and processes them accordingly, facilitating targeted moves based on the
	 * request's target location.
	 *
	 * @param request
	 *            The request to process, potentially containing move instructions.
	 * @return A command appropriate for the request, either a specialized move command or
	 *         a command from the superclass handling.
	 */
	@Override
	public Command getCommand(Request request) {

		// Check if the request is a special move interaction fragment request.
		if (request instanceof AdoneMoveInteractionFragmentElementRequest) {

			AdoneMoveInteractionFragmentElementRequest moveRequest = (AdoneMoveInteractionFragmentElementRequest) request;

			// If the request specifies a target location, process it for special child moves.
			if (moveRequest.getTargetLocation() != null) {
				return this.getMoveChildrenBySpecialRequest((AdoneMoveInteractionFragmentElementRequest) request);
			}
		}

		// Fall back to the superclass's method for other types of requests.
		return super.getCommand(request);
	}

	/**
	 * Constructs a command to move child EditParts based on a special request.
	 * This includes handling for moving elements under a specific Y position or
	 * resizing parent interaction operands.
	 *
	 * @param request
	 *            The special move request containing move details.
	 * @return A command that moves child EditParts accordingly.
	 */
	private Command getMoveChildrenBySpecialRequest(AdoneMoveInteractionFragmentElementRequest request) {

		CompoundCommand moveCommand = new CompoundCommand();

		int resizeDeltaY = request.getMoveDelta().y;

		GraphicalEditPart moveTargetEp = request.getTargetEditPart();

		if (moveTargetEp == null) {
			// Handles moving of child EditParts below a certain Y position for CF creation.(2024-01-26)
			List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditPartsByYPosition((GraphicalEditPart) getHost(), request.getLocation().y);

			// Exclude nested EditParts from the move.
			List<GraphicalEditPart> netBelowEditParts = this.excludeNestingEditPartsForCfCreeation(request, allBelowEditParts);

			// Process and move the filtered EditParts.
			this.processAndMoveBelowEditParts(request, netBelowEditParts, moveCommand, moveTargetEp);
		} else {
			// Resize the parent interaction operand based on the move and process below EditParts.
			this.resizeParentInteractionOperand(request, request.getTargetEditPart(), moveCommand, resizeDeltaY);

			List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditParts(moveTargetEp, request);

			// Process and move EditParts below the target, considering the move request.
			this.processAndMoveBelowEditParts(request, allBelowEditParts, moveCommand, moveTargetEp);

		}

		return moveCommand;
	}

	/**
	 * Filters EditParts for Combined Fragment (CF) creation, excluding nested parts.
	 *
	 * @param request
	 *            The request containing details for moving interaction fragments.
	 * @param allBelowEditParts
	 *            A list of all EditParts below a certain point.
	 * @return A list of GraphicalEditParts, excluding those nested within the CF creation area.
	 */
	private List<GraphicalEditPart> excludeNestingEditPartsForCfCreeation(AdoneMoveInteractionFragmentElementRequest request, List<GraphicalEditPart> allBelowEditParts) {

		List<GraphicalEditPart> netEditParts = new ArrayList<>();

		// Retrieve the rectangle area for the CF creation from the request's extended data.
		Object obj = request.getExtendedData().get(AdoneMoveInteractionFragmentElementRequest.COMBINEDFRAGMENT_CREATION_RECTANGLE);

		if (obj != null) {

			Rectangle creationRect = (Rectangle) obj;

			// Iterate through all EditParts to filter out those nested within the creation rectangle.
			for (GraphicalEditPart ep : allBelowEditParts) {
				Rectangle epBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ep);

				// Check if the EditPart is outside the creation rectangle; if so, include it in the result.
				if (creationRect.x < epBounds.x &&
						creationRect.y < epBounds.y &&
						creationRect.x + creationRect.width > epBounds.x + epBounds.width &&
						creationRect.y + creationRect.height > epBounds.y + epBounds.height) {
				} else {
					netEditParts.add(ep);
				}
			}

			return netEditParts;// Return the filtered list of EditParts.
		}

		// If no creation rectangle is defined, return all below EditParts unfiltered.
		return allBelowEditParts;
	}

	@Override
	protected Command getMoveChildrenCommand(Request request) {

		AdoneCombinedFragmentEpStatusManager.getInstance().initialize();

		CompoundCommand moveCompoundCommand = new CompoundCommand();

		if (request instanceof ChangeBoundsRequest) {

			ChangeBoundsRequest changeBoundsRequest = (ChangeBoundsRequest) request;

			List<?> children = changeBoundsRequest.getEditParts();

			// Handle element order change mode specifically.
			if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {

				GraphicalEditPart orderChangeTargetEp = (GraphicalEditPart) children.get(0);

				// Process LifelineEditPart for reordering.
				if (orderChangeTargetEp instanceof LifelineEditPart) {

					// Adjust location if insertion location is specified.
					if (changeBoundsRequest.getExtendedData().containsKey(INSERTION_LOCATION)) {
						int insertLocationX = (int) changeBoundsRequest.getExtendedData().get(INSERTION_LOCATION);
						int originalRequestLocationX = changeBoundsRequest.getLocation().x;

						// Set new X location and adjust move delta accordingly.
						changeBoundsRequest.getLocation().setX(insertLocationX);

						if (insertLocationX > originalRequestLocationX) {
							changeBoundsRequest.getMoveDelta().setX(insertLocationX - originalRequestLocationX);
						} else {
							changeBoundsRequest.getMoveDelta().setX(originalRequestLocationX - insertLocationX);
						}
					}

					// Calculate space between LifelineEditParts.
					int spaceBetweenLifelines = calculateSpaceBetweenLifelines(changeBoundsRequest, (LifelineEditPart) orderChangeTargetEp);

					// Retrieve width of the Lifeline being moved.
					int movingLifelineWidth = getLifelineWidth(orderChangeTargetEp);

					// Check if there is enough space for movement.
					if (spaceBetweenLifelines >= movingLifelineWidth + MARGIN_BETWEEN_LIFELINE * 2) {

						// Find previous Lifeline and calculate the new position based on it.
						LifelineEditPart previousLifeline = AdoneSequenceUtil.getPreviousLifelineEditPartByPosition(changeBoundsRequest, orderChangeTargetEp);
						int previousLifelineRightX = previousLifeline.getFigure().getBounds().getRight().x;

						// Adjust new X coordinate to be in the middle of available space.
						int adjustedX = previousLifelineRightX + (spaceBetweenLifelines / 2) - (movingLifelineWidth / 2);

						Point currentLocation = orderChangeTargetEp.getFigure().getBounds().getLocation();

						Point newLocation = new Point(adjustedX, currentLocation.y);

						// Calculate move delta based on new location.
						Point moveDelta = newLocation.getTranslated(currentLocation.getNegated());
						changeBoundsRequest.setLocation(newLocation);
						changeBoundsRequest.setMoveDelta(moveDelta);

						// Create command for changing Lifeline constraint and add it to compound command.
						Command changeLifelineConstraintCommand = createChangeConstraintCommand(orderChangeTargetEp, translateToModelConstraint(getConstraintFor(changeBoundsRequest, orderChangeTargetEp)));

						if (changeLifelineConstraintCommand != null) {
							moveCompoundCommand.add(changeLifelineConstraintCommand);
						}

						// Get commands for moving child edit parts and add them to the compound command.
						List<Command> moveChildCommand = this.getMoveChildCombinedFragmentEditPart(changeBoundsRequest, (LifelineEditPart) orderChangeTargetEp);

						for (Command command : moveChildCommand) {
							moveCompoundCommand.add(command);
						}


					} else {
						// If there's not enough space, move the target LifelineEditPart to a new location,
						// and push subsequent LifelineEditParts accordingly.

						LifelineEditPart previousLifeline = AdoneSequenceUtil.getPreviousLifelineEditPartByPosition(changeBoundsRequest, orderChangeTargetEp);

						int newLocationX;

						// Determine new X location based on whether a previous lifeline exists.
						if (previousLifeline == null) {
							newLocationX = MARGIN_BEFORE_FIRST; // For the first LifelineEditPart.
						} else {
							// Calculate new location after the previous LifelineEditPart with a specified margin.
							newLocationX = previousLifeline.getFigure().getBounds().getRight().x + MARGIN_BETWEEN_LIFELINE;
						}

						// Retrieve current location of the LifelineEditPart to move.
						Point currentLocation = orderChangeTargetEp.getFigure().getBounds().getLocation();

						// Calculate new location for moving LifelineEditPart.
						Point newLocationForMovingLifeline = new Point(newLocationX, currentLocation.y);

						// Calculate move delta for the new location.
						Point moveDelta = newLocationForMovingLifeline.getTranslated(currentLocation.getNegated());

						// Update ChangeBoundsRequest with the new location and move delta.
						changeBoundsRequest.setLocation(newLocationForMovingLifeline);
						changeBoundsRequest.setMoveDelta(moveDelta);

						// Create and add command to move the target LifelineEditPart to its new location.
						Command moveTargetLifelineCommand = createChangeConstraintCommand(
								orderChangeTargetEp,
								translateToModelConstraint(getConstraintFor(changeBoundsRequest, orderChangeTargetEp)));
						moveCompoundCommand.add(moveTargetLifelineCommand);

						// Get and add commands for moving child CombinedFragmentEditParts.
						List<Command> moveChildCommand = this.getMoveChildCombinedFragmentEditPart(changeBoundsRequest, (LifelineEditPart) orderChangeTargetEp);
						for (Command command : moveChildCommand) {
							moveCompoundCommand.add(command);
						}

						// Move subsequent LifelineEditParts to maintain spacing.
						List<LifelineEditPart> allNextLifelines = AdoneSequenceUtil.getAllNextLifelineEditPart(newLocationForMovingLifeline, orderChangeTargetEp);
						int cumulativeOffset = newLocationX + movingLifelineWidth + MARGIN_BETWEEN_LIFELINE;

						for (LifelineEditPart nextLifeline : allNextLifelines) {

							if (nextLifeline.equals(orderChangeTargetEp)) {
								continue;// Skip if it's the same as the target.
							}

							// Calculate new location for each subsequent LifelineEditPart.
							Point newLocationForNextLifeline = new Point(cumulativeOffset, nextLifeline.getFigure().getBounds().y);
							ChangeBoundsRequest requestForNextLifeline = new ChangeBoundsRequest();
							Point currentLocationForNextLifeline = nextLifeline.getFigure().getBounds().getLocation();
							Point moveDeltaForNextLifeline = newLocationForNextLifeline.getTranslated(currentLocationForNextLifeline.getNegated());
							requestForNextLifeline.setLocation(newLocationForNextLifeline);
							requestForNextLifeline.setEditParts(nextLifeline);
							requestForNextLifeline.setMoveDelta(moveDeltaForNextLifeline);

							// Create and add command to move each subsequent LifelineEditPart.
							Command moveNextLifelineCommand = createChangeConstraintCommand(
									nextLifeline,
									translateToModelConstraint(getConstraintFor(requestForNextLifeline, nextLifeline)));

							// Get and add commands for moving child CombinedFragmentEditParts for each subsequent LifelineEditPart.
							moveCompoundCommand.add(moveNextLifelineCommand);
							List<Command> moveChildCombinedFragmentEpCommand = this.getMoveChildCombinedFragmentEditPart(requestForNextLifeline, nextLifeline);
							for (Command command : moveChildCombinedFragmentEpCommand) {
								moveCompoundCommand.add(command);
							}

							// Update cumulative offset for the next LifelineEditPart.
							cumulativeOffset += getLifelineWidth(nextLifeline) + MARGIN_BETWEEN_LIFELINE;

						}
					}

					// Handle CombinedFragmentEditPart specific movements.
				} else if (orderChangeTargetEp instanceof CombinedFragmentEditPart) {

					// Check if the movement should be skipped based on 'DoNotMoveCheck' flag.
					Object value = changeBoundsRequest.getExtendedData().get("DoNotMoveCheck");
					if (!(value instanceof Boolean && (Boolean) value)) {

						Command command = this.processCombinedFragmentMovementForElementOrderChangeMode(changeBoundsRequest, (CombinedFragmentEditPart) orderChangeTargetEp);
						if (command != null && command == UnexecutableCommand.INSTANCE) {
							return command;
						} else if (command != null && command.canExecute()) {
							moveCompoundCommand.add(command);
						}

						// 아래 Z-Order Command 검토 필요 (반영시 순서 틀어짐) (2004-01-10)

						// AdoneAdjustZOrderCommand zOrderCommand = new AdoneAdjustZOrderCommand(getEditingDomain(), orderChangeTargetEp);
						//
						// if (zOrderCommand.canExecute()) {
						// moveCompoundCommand.add(new ICommandProxy(zOrderCommand));
						// }

						return moveCompoundCommand;

					}

					// Handle AbstractMessageEditPart specific movements.
				} else if (orderChangeTargetEp instanceof AbstractMessageEditPart) {

					// Check if the request is specific to moving interaction fragment elements.
					if (request instanceof AdoneMoveInteractionFragmentElementRequest) {

						// Process message movement in element order change mode.
						Command command = this.processMessageMovementForElementOrderChangeMode(changeBoundsRequest, orderChangeTargetEp);

						if (command != null && command == UnexecutableCommand.INSTANCE) {
							return UnexecutableCommand.INSTANCE;
						} else if (command != null && command.canExecute()) {
							moveCompoundCommand.add(command);
						}

						return moveCompoundCommand;

					}

				} else {
					// Placeholder for handling other types of EditPart not yet defined.
					System.out.println("Other EditPart should be defined");
				}

				// Finalize the command: if there are any commands in moveCompoundCommand, unwrap and return them; otherwise, return null.
				if (moveCompoundCommand.getChildren().length > 0) {
					return moveCompoundCommand.unwrap();
				} else {
					return null;
				}

			} else {

				// Handle specific request types for updating locations during new message creation.
				if (request instanceof AdoneUpdateLocationByNewMessageCreationRequest) {
					return this.getMoveChildrenCommandByCreatingExeSpec((AdoneUpdateLocationByNewMessageCreationRequest) changeBoundsRequest);
				}

				// Process adding whitespace space.

				GraphicalEditPart moveTargetEp = (GraphicalEditPart) children.get(0);

				if (moveTargetEp instanceof LifelineEditPart) {

					// Create and add command to adjust the target Lifeline's constraints.
					Command changeMovingTargetLifelineConstraintCommand = null;
					changeMovingTargetLifelineConstraintCommand = createChangeConstraintCommand(moveTargetEp, translateToModelConstraint(getConstraintFor(changeBoundsRequest, moveTargetEp)));
					if (changeMovingTargetLifelineConstraintCommand != null) {
						moveCompoundCommand.add(changeMovingTargetLifelineConstraintCommand);
					}

					// Process movement of child combined fragments along with the Lifeline.
					List<Command> moveChildCommand = this.getMoveChildCombinedFragmentEditPart(changeBoundsRequest, (LifelineEditPart) moveTargetEp);
					for (Command command : moveChildCommand) {
						moveCompoundCommand.add(command);
					}

					// Adjust the position of subsequent Lifelines to maintain spacing.
					List<LifelineEditPart> allNextLifelineEditPart = AdoneSequenceUtil.getAllNextLifelineEditParts((LifelineEditPart) moveTargetEp);
					for (LifelineEditPart nextLifelineEditPart : allNextLifelineEditPart) {

						Command changeLifelineConstraintCommand = null;

						if (nextLifelineEditPart instanceof LifelineEditPart) {
							changeLifelineConstraintCommand = createChangeConstraintCommand(nextLifelineEditPart, translateToModelConstraint(getConstraintFor(changeBoundsRequest, nextLifelineEditPart)));
						}
						if (changeLifelineConstraintCommand != null) {
							moveCompoundCommand.add(changeLifelineConstraintCommand);
						}

						List<Command> moveCfCommand = this.getMoveChildCombinedFragmentEditPart(changeBoundsRequest, nextLifelineEditPart);

						for (Command command : moveCfCommand) {
							moveCompoundCommand.add(command);
						}

					}

					// Optionally resize diagram width if necessary for the movement.
					Command resizeDiagramWidthCommand = this.getResizeDiagramWidthByMoveLifelineCommand(changeBoundsRequest, allNextLifelineEditPart);
					if (resizeDiagramWidthCommand != null && resizeDiagramWidthCommand.canExecute()) {
						moveChildCommand.add(resizeDiagramWidthCommand);
					}

					// Check for spacing constraints when moving a graphical edit part.
					GraphicalEditPart previousChildEditPart = AdoneSequenceUtil.getPreviousLifelineEditPart(changeBoundsRequest);

					if (previousChildEditPart != null) {

						GraphicalEditPart child = (GraphicalEditPart) children.get(0);

						int minSpacing = AdoneLifeLineEditPart.MIN_LIFELINE_SPACING;

						// Add an unexecutable command if moving violates minimum spacing with the previous part.
						if (((child.getFigure().getBounds().x + changeBoundsRequest.getMoveDelta().x) < (previousChildEditPart.getFigure().getBounds().getRight().x + minSpacing))) {
							moveCompoundCommand.add(UnexecutableCommand.INSTANCE);
						}

					} else {

						// Handle the first Lifeline with specific left margin spacing.
						GraphicalEditPart child = (GraphicalEditPart) children.get(0);
						int minX = AdoneLifeLineEditPart.FIRST_LIFELINE_LEFT_MARGIN;

						// Prevent movement that would place the lifeline before the designated left margin.
						if ((child.getFigure().getBounds().x + changeBoundsRequest.getMoveDelta().x) < minX) {
							moveCompoundCommand.add(UnexecutableCommand.INSTANCE);
						}

					}


				} else {

					// Handle different edit parts and apply specific movement logic.
					if (this.isExceedingUpwardMoveLimit(changeBoundsRequest, moveTargetEp)) {
						// Return unexecutable if the movement exceeds upward limits.
						return UnexecutableCommand.INSTANCE;
					}

					// Calculate vertical movement delta from the request.
					int moveDeltaY = changeBoundsRequest.getMoveDelta().y;

					// Handle movement for CombinedFragmentEditPart.
					if (moveTargetEp instanceof CombinedFragmentEditPart) {

						Command command = this.processCombinedFragmentMovement(changeBoundsRequest, (CombinedFragmentEditPart) moveTargetEp);

						// Check if the command is executable or unexecutable, and add it to the moveCompoundCommand accordingly.
						if (command == UnexecutableCommand.INSTANCE) {
							return command;
						} else if (command != null && command.canExecute()) {
							moveCompoundCommand.add(command);
						}

						// Handle movement for AbstractMessageEditPart.
					} else if (moveTargetEp instanceof AbstractMessageEditPart) {
						Command command = this.processMessageMovement(changeBoundsRequest, (AbstractMessageEditPart) moveTargetEp);
						if (command == UnexecutableCommand.INSTANCE) {
							return command;
						} else if (command != null && command.canExecute()) {
							moveCompoundCommand.add(command);
						}
					}

					// Resize the LifelineEditPart based on the vertical movement.
					Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(moveTargetEp, moveDeltaY);
					if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
						moveCompoundCommand.add(resizeLifelineEpCommand);
					}

					// Resize the first BehaviorExecutionSpecification to adjust its height based on movement.
					Command resizeFirstBesEp = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand(moveTargetEp, moveDeltaY);
					if (resizeFirstBesEp != null && resizeFirstBesEp.canExecute()) {
						moveCompoundCommand.add(resizeFirstBesEp);
					}

					// Adjust the parent interaction operand based on the new position after movement.
					this.resizeParentInteractionOperand(changeBoundsRequest, moveTargetEp, moveCompoundCommand, moveDeltaY);

					// Prevent recursive calls by checking a flag in the extended data of the request.(2024-01-31)
					if (!changeBoundsRequest.getExtendedData().containsKey("DoNotMoveCheck")) {
						List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditParts(moveTargetEp, changeBoundsRequest);
						this.processAndMoveBelowEditParts(changeBoundsRequest, allBelowEditParts, moveCompoundCommand, moveTargetEp);
					}

				}

				// Finalize the command: if there are any executable commands, return the unwrapped compound command; otherwise, return null.
				if (moveCompoundCommand.getChildren().length > 0 && moveCompoundCommand.canExecute()) {
					return moveCompoundCommand.unwrap();
				} else {
					return null;
				}
			}
		} else {
			// Default case: invoke super method for handling other types of requests.
			return super.getMoveChildrenCommand(request);
		}

	}

	/**
	 * Adjusts the diagram width based on the rightmost lifeline position after a move.
	 *
	 * @param changeBoundsRequest
	 *            Details of the bounds change request.
	 * @param allNextLifelineEditPart
	 *            List of all subsequent lifeline edit parts.
	 * @return A command to resize the diagram width or null if no resize is needed.
	 */
	private Command getResizeDiagramWidthByMoveLifelineCommand(ChangeBoundsRequest changeBoundsRequest, List<LifelineEditPart> allNextLifelineEditPart) {

		// Track the maximum right edge across all lifelines.
		int maxRightEdgeX = 0;

		// Iterate over all EditParts to find the rightmost edge of lifelines.
		for (GraphicalEditPart ep : AdoneSequenceUtil.getAllEditParts(getHost())) {

			if (ep instanceof LifelineEditPart) {
				LifelineEditPart llEp = (LifelineEditPart) ep;
				Node llView = (Node) llEp.getNotationView();
				Element e = (Element) llView.getElement();

				if (e instanceof Lifeline) {
					// 라이프라인의 레이아웃 제약조건을 얻어 최대 우측 경계 계산
					Bounds bounds = (Bounds) llView.getLayoutConstraint();
					int rightEdgeX = bounds.getX() + bounds.getWidth();
					if (rightEdgeX > maxRightEdgeX) {
						maxRightEdgeX = rightEdgeX;
					}
				}
			}
		}

		// 가장 우측에 위치한 라이프라인의 우측 경계값이 Default 값 857 보다 Offset 40 적용한 817 이상인 경우 다이어그램 너비
		// 조정
		// Check if the diagram needs to be widened (current max right edge exceeds threshold).
		if (maxRightEdgeX >= 817) {

			int newWidth = maxRightEdgeX + 40; // Calculate new width with margin.

			// Fetch the diagram's compartment edit part to adjust its size.
			InteractionInteractionCompartmentEditPart iicEp = (InteractionInteractionCompartmentEditPart) getHost();
			Node iicEpView = (Node) iicEp.getParent().getModel();

			// 최 우측 라이프라인이 아닌 다른 라이프라인 기준으로 Move 시 다이어그램 사이즈 조정되려면 Dimension 을 초기값으로 세팅해야 함
			// 원하는 형태로 값을 세팅할 경우 다이어그램 사이즈 조정 안됨. 추후 재 검토 요망 (2024-02-02)
			// AdoneInteractionFigure의 Layout Manager 에서 직접 조정 처리
			// Set new dimensions for the diagram, maintaining current height.
			Bounds b = (Bounds) iicEpView.getLayoutConstraint();
			Dimension newDimension = new Dimension(newWidth, b.getHeight());

			// Create and return a command to resize the diagram.
			SetBoundsCommand diagramResizeCommand = new SetBoundsCommand(getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(iicEpView), newDimension);
			return new ICommandProxy(diagramResizeCommand);
		}

		return null;// Return null if no resizing is needed.

	}

	/**
	 * Processes message movement, ensuring it's only vertical.
	 *
	 * @param changeBoundsRequest
	 *            Details of the move request, including delta.
	 * @param moveTargetEp
	 *            The EditPart that represents the moving message.
	 * @return A command that either executes the move or indicates it's unexecutable.
	 */
	private Command processMessageMovement(ChangeBoundsRequest changeBoundsRequest, AbstractMessageEditPart moveTargetEp) {

		CompoundCommand moveCommand = new CompoundCommand();
		Point moveDelta = changeBoundsRequest.getMoveDelta();

		if (moveDelta.x != 0) {
			// Horizontal movement detected; return a command that can't be executed.
			return UnexecutableCommand.INSTANCE;
		} else {
			// No horizontal movement; proceed to process the message's vertical move.
			processAbstractMessageEditPart(moveTargetEp, changeBoundsRequest, moveCommand);
		}
		return moveCommand;
	}

	/**
	 * Processes the movement of a message in the context of changing element order within a sequence diagram.
	 * It validates the move against several constraints to ensure semantic correctness, including checks against
	 * behavior execution specifications and combined fragments. If the move passes all checks, it also handles
	 * the adjustment of following lifelines or behavior specifications as necessary.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the proposed move, including direction and magnitude.
	 * @param orderChangeTargetEp
	 *            The edit part of the message being moved.
	 * @return A compound command that includes all necessary adjustments for the move or UnexecutableCommand.INSTANCE
	 *         if the move violates any constraints.
	 */
	private Command processMessageMovementForElementOrderChangeMode(ChangeBoundsRequest changeBoundsRequest, EditPart orderChangeTargetEp) {

		CompoundCommand moveCompoundCommand = new CompoundCommand();

		AdoneMoveInteractionFragmentElementRequest moveRequest = (AdoneMoveInteractionFragmentElementRequest) changeBoundsRequest;

		// Validate the move against various constraints to ensure it maintains diagram integrity.

		if (!isWithinMainLogicBehaviorExecutionSpecForMessage(moveRequest, orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (isWithinOtherBehaviorExecutionSpecForMessage(moveRequest.getLocation().getCopy(), orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (isWithinCombinedFragmentValidArea(moveRequest.getLocation().getCopy(), orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (!isMessageCovedByCombinedFragment(moveRequest, orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (isSelfMessageMoveOverLowestLimit(moveRequest, orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		// If the message move is valid, process any necessary adjustments to following elements.
		Message msg = (Message) ((AbstractMessageEditPart) orderChangeTargetEp).resolveSemanticElement();
		BehaviorExecutionSpecification followingBes = AdoneSequenceUtil.getFollowingBehaviorExeSpec(msg);

		if (followingBes != null) {

			BehaviorExecutionSpecificationEditPart followingBesEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(orderChangeTargetEp, followingBes);

			// 아래 왜 -10 을 해줘야 하는지 확인 되지 않음 (2024-01-03)
			// Adjust move delta for following elements, needs clarification on the reason for specific adjustments.
			Point newMoveDelta = moveRequest.getMoveDelta().getCopy().setY(moveRequest.getMoveDelta().y - 10);

			// Create a request to adjust the position of the following BES or lifeline.
			ChangeBoundsRequest besMoveRequest = new ChangeBoundsRequest();
			besMoveRequest.setEditParts(followingBesEp);
			besMoveRequest.setMoveDelta(newMoveDelta);

			// Generate and add the command to move the next element.
			Command moveNextLifelineCommand = createChangeConstraintCommand(
					followingBesEp,
					translateToModelConstraint(getConstraintFor(besMoveRequest, followingBesEp)));

			moveCompoundCommand.add(moveNextLifelineCommand);

		}

		// Get the absolute bounds of the message being moved to determine its new location after the move.
		Point moveTargetMessageBounds = SequenceUtil.getAbsoluteEdgeExtremity((ConnectionNodeEditPart) orderChangeTargetEp, false);

		Point moveDelta = moveRequest.getMoveDelta().getCopy();

		// Calculate the new Y position of the message by adding the Y move delta to its current Y position.
		int moveTargetNewY = moveTargetMessageBounds.y + moveDelta.y;

		// Iterate over all EditParts in the diagram to adjust elements affected by the message's move.
		for (Object obj : getHost().getViewer().getEditPartRegistry().values()) {
			if (obj instanceof EditPart) {
				EditPart ep = (EditPart) obj;

				// Skip the current message EditPart to avoid adjusting itself.
				if (orderChangeTargetEp.equals(ep)) {
					continue;
				}

				// Focus on relevant EditParts: Combined Fragments, Messages, and Behavior Execution Specifications.
				if (!(ep instanceof CombinedFragmentEditPart || ep instanceof AbstractMessageEditPart || ep instanceof BehaviorExecutionSpecificationEditPart)) {
					continue;
				}

				Rectangle followingMoveEpBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((GraphicalEditPart) ep);

				// Prepare a request for moving this EditPart, initialized with the type of the original change request.
				ChangeBoundsRequest followingEditPartMoveRequest = new ChangeBoundsRequest();
				followingEditPartMoveRequest.setEditParts(ep);
				followingEditPartMoveRequest.setType(changeBoundsRequest.getType());

				ChangeBoundsRequest followingInteractionOperandResizeRequest = new ChangeBoundsRequest();
				followingInteractionOperandResizeRequest.setEditParts(ep);
				followingInteractionOperandResizeRequest.setType(changeBoundsRequest.getType());

				// Additional logic for adjusting EditParts based on their position relative to the moved message.
				if (moveDelta.y > 0) {// When moving the message downward

					// Check if the EditPart is below the message and adjust its position accordingly.
					if (followingMoveEpBounds.y > moveTargetMessageBounds.y) {
						if (followingMoveEpBounds.y < moveTargetNewY) {
							// If the EditPart is between the message's new and old position, move it upwards.
							// 이동 대상 CF 밑바닥 부터 이동 y 사이에 존재하는 경우 CF 높이만큼 위로(-) 이동 => 추후 필요시 반영

							// Moves EditPart up if between message's new and old position.
							Point epMoveDelta = new Point(0, -AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
							followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

							// Processes different EditPart types with specific adjustments.
							if (ep instanceof AbstractMessageEditPart) {
								processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							} else if (ep instanceof RoundedCompartmentEditPart) {
								processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							}

						} else if (followingMoveEpBounds.y > moveTargetNewY) {
							// If the EditPart is below the message's new position, move it downwards.

							Point epMoveDelta = new Point(0, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
							followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

							// Repeats processing for different EditPart types as above.
							if (ep instanceof AbstractMessageEditPart) {
								processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							} else if (ep instanceof RoundedCompartmentEditPart) {
								processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							}

						}
					}

					// Adjusts parent InteractionOperand size based on move target's new position.
					this.resizeParentInteractionOperandForMoveTarget(changeBoundsRequest, (GraphicalEditPart) orderChangeTargetEp, moveCompoundCommand, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);


				} else if (moveDelta.y < 0) {
					// Upward message move: Adjusts EditParts above the message's initial position.

					if (followingMoveEpBounds.y < moveTargetMessageBounds.y) {

						if (followingMoveEpBounds.y > moveTargetNewY) {
							// 이동 대상 이동 y 사이에 존재하는 경우 BES 높이만큼 아래로 이동
							// Moves EditPart down if it's within target's move range.

							Point epMoveDelta = new Point(0, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT * 2);
							followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

							// Processes different EditPart types for upward move adjustments.
							if (ep instanceof AbstractMessageEditPart) {
								processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							} else if (ep instanceof RoundedCompartmentEditPart) {
								processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							}

						} else if (followingMoveEpBounds.y > moveTargetMessageBounds.y + AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT) {
							// 이동 대상 메시지 아래 존재하는 경우 위로 BES 높이만큼 이동
							// Moves EditPart up if it's below the message's initial top position.
							Point epMoveDelta = new Point(0, -AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
							followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

							// Repeats EditPart processing for upward adjustment as above.
							if (ep instanceof AbstractMessageEditPart) {
								processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							} else if (ep instanceof RoundedCompartmentEditPart) {
								processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							}
						}

						// Adjusts InteractionOperand size for upward move, similar to downward adjustment.
						Point epMoveDelta = new Point(0, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
						followingEditPartMoveRequest.setMoveDelta(epMoveDelta);
						followingInteractionOperandResizeRequest.setMoveDelta(epMoveDelta);
						this.resizeParentInteractionOperandForMoveTarget(changeBoundsRequest, (GraphicalEditPart) orderChangeTargetEp, moveCompoundCommand, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);

					} else {
						// Handles additional upward move adjustments, similar to the above conditions.

						Point epMoveDelta = new Point(0, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT * 2);
						followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

						if (ep instanceof AbstractMessageEditPart) {
							processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
						} else if (ep instanceof RoundedCompartmentEditPart) {
							processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
						}

						followingInteractionOperandResizeRequest.setMoveDelta(epMoveDelta);
						this.resizeParentInteractionOperandForMoveTarget(changeBoundsRequest, (GraphicalEditPart) orderChangeTargetEp, moveCompoundCommand, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);
					}
				}
			}
		}

		return moveCompoundCommand;

	}

	/**
	 * Determines if the movement of a self-message edit part exceeds the lowest boundary limit
	 * established by the first Behavior Execution Specification (BES) associated with the message.
	 * This check ensures that self-messages are not moved beyond an acceptable lower bound,
	 * which could disrupt the logical flow and visual clarity of the sequence diagram.
	 *
	 * @param moveRequest
	 *            The request containing the new location for the self-message.
	 * @param orderChangeTargetEp
	 *            The edit part of the self-message being moved.
	 * @return true if the new location of the self-message is below the lowest allowed limit
	 *         defined by the bottom of the first BES minus a specific threshold; otherwise, false.
	 */
	private boolean isSelfMessageMoveOverLowestLimit(AdoneMoveInteractionFragmentElementRequest moveRequest, EditPart orderChangeTargetEp) {

		Point moveTargetLocation = moveRequest.getLocation().getCopy();

		// Resolve the message and its containing interaction from the target edit part.
		AbstractMessageEditPart msgEp = (AbstractMessageEditPart) orderChangeTargetEp;
		Message msg = (Message) msgEp.resolveSemanticElement();

		Interaction intac = msg.getInteraction();

		// Attempt to find the first BES within the interaction's fragments.
		BehaviorExecutionSpecification firstBes = null;
		BehaviorExecutionSpecificationEditPart firstBesEditPart = null;
		for (InteractionFragment ifg : intac.getFragments()) {
			if (ifg instanceof BehaviorExecutionSpecification) {
				firstBes = (BehaviorExecutionSpecification) ifg;
				break;// Stop at the first BES found.
			}
		}

		// Return false immediately if no BES is associated with the message.
		if (firstBes == null) {
			return false;
		}

		// Get the edit part for the first BES to calculate its bounds.
		firstBesEditPart = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(orderChangeTargetEp, firstBes);
		Rectangle firstBesEpBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(firstBesEditPart);

		// Check if the move location is below the bottom of the first BES minus a threshold.
		if (moveTargetLocation.y > firstBesEpBounds.y + firstBesEpBounds.height - 95) {
			return true; // The move exceeds the lowest limit for self-message placement.
		}

		// The move is within the acceptable bounds.
		return false;
	}

	/**
	 * Checks if a message, represented by the orderChangeTargetEp edit part, remains covered by a Combined Fragment (CF)
	 * after being moved to a new location specified in the moveRequest. This method is essential for ensuring that the
	 * semantic relationships between messages and their enclosing CFs are maintained after movement operations within
	 * a sequence diagram.
	 *
	 * @param moveRequest
	 *            The request containing the new location for the message.
	 * @param orderChangeTargetEp
	 *            The edit part of the message being moved.
	 * @return true if the message is still covered by at least one CF that includes both the sender and receiver lifelines;
	 *         false otherwise.
	 */
	private boolean isMessageCovedByCombinedFragment(AdoneMoveInteractionFragmentElementRequest moveRequest, EditPart orderChangeTargetEp) {

		Point moveTargetLocation = moveRequest.getLocation().getCopy();

		// Resolve the message from the target edit part.
		AbstractMessageEditPart messageEp = (AbstractMessageEditPart) orderChangeTargetEp;
		Message msg = (Message) messageEp.resolveSemanticElement();

		// Get the sender and receiver lifelines for the message.
		Lifeline senderLifeline = AdoneInteractionHelper.getSenderLifeline(msg);
		Lifeline receiverLifeline = AdoneInteractionHelper.getReceiverLifeline(msg);

		// Initially assume the message is not covered by any CF.
		boolean isMessageNotCoveredByCf = true;

		// Retrieve the interaction compartment that contains all the CFs.
		final EditPart interactionCompartment = SequenceUtil.getInteractionCompartment(orderChangeTargetEp);
		if (null != interactionCompartment) {
			// Iterate through all CFs in the interaction compartment.
			for (Object child : interactionCompartment.getChildren()) {
				if (child instanceof CombinedFragmentEditPart) {
					final CombinedFragmentEditPart cfEp = (CombinedFragmentEditPart) child;

					Rectangle cfBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(cfEp);

					// Check if the new location of the message falls within the bounds of the CF.
					if (moveTargetLocation.y >= cfBounds.y && moveTargetLocation.y <= cfBounds.y + cfBounds.height) {
						CombinedFragment cf = (CombinedFragment) cfEp.resolveSemanticElement();

						// Verify if the CF covers both the sender and receiver lifelines of the message.
						if (cf.getCovereds().contains(senderLifeline) && cf.getCovereds().contains(receiverLifeline)) {
							isMessageNotCoveredByCf = true;
						} else {
							// If the message is not covered by this CF, no further checks are necessary.
							isMessageNotCoveredByCf = false;
							break;
						}
					}
				}
			}
		}

		// Return true if the message is covered by a CF after the move; false otherwise.
		return isMessageNotCoveredByCf;
	}


	/**
	 * Determines if the proposed move target location for an edit part is within the valid area of any Combined Fragment (CF)
	 * in the sequence diagram. Specifically, it checks if the move target location falls within the header area of any CF or
	 * just below its bottom edge. This validation helps ensure that message connections and other diagram elements remain logically
	 * placed in relation to the structure defined by Combined Fragments.
	 *
	 * @param moveTargetLocation
	 *            The point representing the proposed new location of the edit part, typically a message.
	 * @param orderChangeTargetEp
	 *            The edit part being considered for movement.
	 * @return true if the move target location is within the header area of any CF or just below any CF's bottom edge; otherwise, false.
	 */
	private boolean isWithinCombinedFragmentValidArea(Point moveTargetLocation, EditPart orderChangeTargetEp) {

		final EditPart interactionCompartment = SequenceUtil.getInteractionCompartment(orderChangeTargetEp);
		if (null != interactionCompartment) {

			// Iterate over all children of the interaction compartment to find Combined Fragment edit parts.
			for (Object child : interactionCompartment.getChildren()) {
				if (child instanceof CombinedFragmentEditPart) {
					final CombinedFragmentEditPart cfEp = (CombinedFragmentEditPart) child;

					Rectangle cfBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(cfEp);

					// Check if the move target location is within the header area of the CF (top 40 pixels of the CF).
					if (moveTargetLocation.y >= cfBounds.y && moveTargetLocation.y <= cfBounds.y + 40) {
						return true;// The move target location is within the CF's header area.
					}

					// Additionally, check if the move target location is just below the CF's bottom edge (within 20 pixels).
					if (moveTargetLocation.y >= cfBounds.y + cfBounds.height && moveTargetLocation.y <= cfBounds.y + cfBounds.height + 20) {
						return true;// The move target location is just below the CF's bottom edge.
					}

				}
			}
		}

		// The move target location is not within the valid area of any Combined Fragment.
		return false;
	}

	/**
	 * Checks if the proposed move location for a message is within the bounds of any Behavior Execution Specification (BES)
	 * associated with lifelines other than the main logic lifeline, excluding the BES immediately following the message if applicable.
	 * This validation ensures that messages do not intersect with unrelated BESs, preserving the logical flow of interactions
	 * within the sequence diagram.
	 *
	 * @param moveTargetLocation
	 *            The point representing the proposed new location of the message.
	 * @param orderChangeTargetEp
	 *            The message edit part being considered for movement.
	 * @return true if the move target location intersects with the bounds of any BES other than those directly associated
	 *         with the main logic lifeline and excluding the BES immediately following the message; otherwise, false.
	 */
	private boolean isWithinOtherBehaviorExecutionSpecForMessage(Point moveTargetLocation, EditPart orderChangeTargetEp) {

		// Retrieve all lifeline edit parts from the diagram, excluding the main logic lifeline.
		List<LifelineEditPart> allLifelineEpList = AdoneSequenceUtil.getAllLifelineEditParts(orderChangeTargetEp);
		LifelineEditPart mainLogicLifelineEp = allLifelineEpList.get(1);

		List<BehaviorExecutionSpecification> otherBesList = new ArrayList<>();

		// Get the BES that follows the target message, if any, to exclude it from checks.
		BehaviorExecutionSpecificationEditPart followingBesEp = AdoneSequenceUtil.getFollowingBehaviorExeSpecEditPart((AdoneMessageSyncEditPart) orderChangeTargetEp);
		BehaviorExecutionSpecification followingBes = (BehaviorExecutionSpecification) followingBesEp.resolveSemanticElement();

		// Iterate over all lifelines to collect BESs, excluding the one immediately following the target message.
		for (LifelineEditPart lfEp : allLifelineEpList) {
			if (lfEp.equals(mainLogicLifelineEp)) {
				continue;// Skip the main logic lifeline.
			}

			Lifeline otherLifeline = (Lifeline) lfEp.resolveSemanticElement();
			for (InteractionFragment frg : otherLifeline.getCoveredBys()) {
				if (frg instanceof BehaviorExecutionSpecification) {

					if (frg.equals(followingBes)) {
						continue;
					}

					otherBesList.add((BehaviorExecutionSpecification) frg);
				}
			}
		}

		// Check if the move target location intersects with the vertical bounds of any collected BES.
		for (BehaviorExecutionSpecification otherBes : otherBesList) {
			BehaviorExecutionSpecificationEditPart otherBesEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(mainLogicLifelineEp, otherBes);
			Rectangle otherBesBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(otherBesEp);

			if (moveTargetLocation.y >= otherBesBounds.y && moveTargetLocation.y <= otherBesBounds.y + otherBesBounds.height) {
				// Move location intersects with a BES of another lifeline.
				return true;
			}
		}

		// No intersection with other BESs detected.
		return false;
	}

	/**
	 * Checks if the specified move target location for a Combined Fragment is within the vertical bounds
	 * of any Behavior Execution Specification (BES) associated with lifelines other than the main logic lifeline.
	 * This validation ensures that the Combined Fragment's movement does not intersect with the execution
	 * specifications of unrelated lifelines, maintaining the semantic integrity of the sequence diagram.
	 *
	 * Note: Anomalies such as null references to BES EditParts (possibly after deletion and undo actions) need
	 * investigation as of 2024-01-15.
	 *
	 * @param moveTargetLocation
	 *            The point representing the proposed new location of the Combined Fragment.
	 * @param orderChangeTargetEp
	 *            The Combined Fragment edit part being considered for movement.
	 * @return true if the move target location intersects with any BES of lifelines other than the main logic lifeline; otherwise, false.
	 */
	private boolean isWithinOtherBehaviorExecutionSpecForCombinedFragment(Point moveTargetLocation, EditPart orderChangeTargetEp) {

		List<LifelineEditPart> allLifelineEpList = AdoneSequenceUtil.getAllLifelineEditParts(orderChangeTargetEp);

		// Identify the main logic lifeline as a reference point.
		LifelineEditPart mainLogicLifelineEp = allLifelineEpList.get(1);

		// Collect BESs from lifelines other than the main logic lifeline.
		List<BehaviorExecutionSpecification> otherBesList = new ArrayList<>();
		for (LifelineEditPart lfEp : allLifelineEpList) {
			if (lfEp.equals(mainLogicLifelineEp)) {
				continue; // Skip the main logic lifeline.
			}

			Lifeline otherLifeline = (Lifeline) lfEp.resolveSemanticElement();
			for (InteractionFragment frg : otherLifeline.getCoveredBys()) {
				if (frg instanceof BehaviorExecutionSpecification) {

					otherBesList.add((BehaviorExecutionSpecification) frg);
				}
			}
		}

		// Check if the move target location intersects with the vertical bounds of any other BES.
		for (BehaviorExecutionSpecification otherBes : otherBesList) {
			BehaviorExecutionSpecificationEditPart otherBesEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(mainLogicLifelineEp, otherBes);

			if (otherBesEp == null) {
				// null 발생하는 원인 검토 필요 (CF 삭제 후 언두 시 발생 ?) (2024-01-15)
				continue; // Skip if the BES EditPart reference is null
			}

			Rectangle otherBesBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(otherBesEp);

			// Determine if the move target location falls within the vertical span of the BES, including a margin.
			if (moveTargetLocation.y >= otherBesBounds.y && moveTargetLocation.y <= otherBesBounds.y + otherBesBounds.height + 20) {
				return true;// Move location intersects with a BES of another lifeline.
			}
		}

		// No intersection with other BESs detected.
		return false;
	}

	/**
	 * Checks if the proposed location for moving a message edit part is within the bounds of the main
	 * logic behavior execution specification (BES) associated with the second lifeline in the sequence diagram.
	 * This ensures that messages are only moved within the execution scope of the main logic, maintaining
	 * the semantic integrity of the sequence diagram.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the proposed move, including the target location.
	 * @param orderChangeTargetEp
	 *            The edit part that is being moved, in this case, representing a message.
	 * @return true if the move location is within the vertical bounds of the main logic BES, with a margin of 20 pixels
	 *         at the top and bottom; otherwise, false.
	 */
	private boolean isWithinMainLogicBehaviorExecutionSpecForMessage(AdoneMoveInteractionFragmentElementRequest changeBoundsRequest, EditPart orderChangeTargetEp) {

		// Retrieve the second lifeline edit part, considered as the main logic lifeline in this context.
		LifelineEditPart mainLogicLifelineEp = AdoneSequenceUtil.getAllLifelineEditParts(orderChangeTargetEp).get(1);

		// Resolve the semantic element to access the main logic lifeline.
		Lifeline mainLogicLifeline = (Lifeline) mainLogicLifelineEp.resolveSemanticElement();

		// Find the first BES covered by the main logic lifeline, indicating the scope of execution.
		BehaviorExecutionSpecification mainBes = null;
		for (InteractionFragment frg : mainLogicLifeline.getCoveredBys()) {
			if (frg instanceof BehaviorExecutionSpecification) {
				mainBes = (BehaviorExecutionSpecification) frg;
				break;
			}
		}

		// Obtain the edit part for the main logic BES to calculate its bounds.
		BehaviorExecutionSpecificationEditPart mainLogicBesEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(mainLogicLifelineEp, mainBes);
		Rectangle mainLogicBesBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(mainLogicBesEp);

		// Get the target location from the change bounds request to check against BES bounds.
		Point moveTargetLocation = changeBoundsRequest.getLocation();

		// Check if the move location is above the top margin or below the bottom margin of the BES.
		if (moveTargetLocation.y < mainLogicBesBounds.y + 20) {
			// Block the move if it's outside the acceptable range of the BES.
			return false;
		}

		// 추후 테스트 요망(2024-01-04)
		if (moveTargetLocation.y > mainLogicBesBounds.y + mainLogicBesBounds.height - 20) {
			// Block the move if it's outside the acceptable range of the BES.
			return false;
		}

		// Allow the move if it's within the BES bounds, considering the specified margins.
		return true;
	}

	/**
	 * Resizes the parent Interaction Operands and their associated Combined Fragments based on the movement
	 * of an order change target EditPart. This method is invoked when a Combined Fragment or its contents
	 * are moved, necessitating a resize of the enclosing Interaction Operands and Combined Fragments to
	 * accommodate the new layout.
	 *
	 * @param request
	 *            The change bounds request triggering the move, containing additional data such as movement restrictions.
	 * @param orderChangeTargetEp
	 *            The graphical edit part that is the target of the order change, typically a Combined Fragment.
	 * @param moveCompoundCommand
	 *            The compound command to which this method will add resize commands.
	 * @param resizeDeltaY
	 *            The amount by which the height of the Interaction Operand and Combined Fragment should be adjusted.
	 */
	private void resizeParentInteractionOperandForMoveTarget(ChangeBoundsRequest request, GraphicalEditPart orderChangeTargetEp, CompoundCommand moveCompoundCommand, int resizeDeltaY) {

		// Check if the move should be ignored based on the request's extended data.
		Object value = request.getExtendedData().get("DoNotMoveCheck");

		if (!(value instanceof Boolean && (Boolean) value)) {

			// Retrieve all parent Interaction Operands based on the location of the order change target.
			List<InteractionOperandEditPart> parentInteractionOperandEpList = AdoneSequenceUtil.getAllParentInteractionOperandEpsByLocation(orderChangeTargetEp, request.getLocation());

			for (InteractionOperandEditPart ioEp : parentInteractionOperandEpList) {
				// Calculate the new bounds for the Interaction Operand, adjusting its height.
				Rectangle ioEpBounds = ioEp.getFigure().getBounds().getCopy();
				ioEpBounds.height += resizeDeltaY;

				// Create and add a resize command for the Interaction Operand to the compound command.
				Command resizeInteractionOperandCommand = createChangeConstraintCommand(
						ioEp,
						translateToModelConstraint(ioEpBounds));
				if (resizeInteractionOperandCommand != null) {
					moveCompoundCommand.add(resizeInteractionOperandCommand);
				}

				// Identify the parent Combined Fragment of the Interaction Operand for resizing.
				CombinedFragmentEditPart parentCbf = ((CombinedFragmentEditPart) ioEp.getParent().getParent());

				// Calculate the new bounds for the Combined Fragment, adjusting its height.
				Rectangle cmbFrgBounds = parentCbf.getFigure().getBounds().getCopy();
				cmbFrgBounds.height += resizeDeltaY;

				// Create and add a resize command for the parent Combined Fragment to the compound command.
				Command resizeCombinedFragmentCommand = createChangeConstraintCommand(
						ioEp.getParent().getParent(),
						translateToModelConstraint(cmbFrgBounds));
				if (resizeCombinedFragmentCommand != null) {
					moveCompoundCommand.add(resizeCombinedFragmentCommand);
				}
			}
		}
	}

	/**
	 * Processes the movement of a Combined Fragment edit part specifically for element order change mode.
	 * It constructs a compound command that includes commands for moving the Combined Fragment and potentially
	 * other related edit parts. This method ensures that horizontal movements are neutralized and only vertical
	 * movements within certain constraints are allowed.
	 *
	 * Note: Modifications were made to address cases where grid alignment is not applied (2024-01-22).
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the proposed move, including direction and magnitude.
	 * @param orderChangeTargetEp
	 *            The Combined Fragment edit part being considered for movement.
	 * @return A compound command for moving the Combined Fragment and any affected nested elements, or null if the move is not possible.
	 */
	private Command processCombinedFragmentMovementForElementOrderChangeMode(ChangeBoundsRequest changeBoundsRequest, CombinedFragmentEditPart orderChangeTargetEp) {

		CompoundCommand moveCompoundCommand = new CompoundCommand();

		Point moveDelta = changeBoundsRequest.getMoveDelta().getCopy();

		// Neutralize horizontal movement to only allow vertical movement within the diagram constraints.
		if (moveDelta.x != 0) {
			// GRID 적용 안된 케이스 대응 수정 (2024-01-22)
			// 수평 이동이 있는 경우, 실행 불가능한 Command를 반환
			// return UnexecutableCommand.INSTANCE;
			moveDelta.x = 0;
		}

		// Check if the proposed move is within the bounds of the behavior execution specification.
		if (!isWithinBehaviorExecutionSpec(changeBoundsRequest, orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		// Check if the combined fragment's new location interferes with another behavior execution specification.
		if (isWithinOtherBehaviorExecutionSpecForCombinedFragment(changeBoundsRequest.getLocation().getCopy(), orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		// Ensure the combined fragment remains within its valid area, considering diagrammatic constraints.
		if (isWithinCombinedFragmentValidArea(changeBoundsRequest.getLocation().getCopy(), orderChangeTargetEp)) {
			return UnexecutableCommand.INSTANCE;
		}

		// Create a command to move the combined fragment, applying the translated constraints.
		Command moveCombinedFragmentCommand = createChangeConstraintCommand(orderChangeTargetEp, translateToModelConstraint(getConstraintFor(changeBoundsRequest, orderChangeTargetEp)));
		if (moveCombinedFragmentCommand != null && moveCombinedFragmentCommand.canExecute()) {
			moveCompoundCommand.add(moveCombinedFragmentCommand);
		}

		// Identify all edit parts contained within the combined fragment's bounds for potential movement.
		Rectangle moveTargetCfBounds = SequenceUtil.getAbsoluteBounds(orderChangeTargetEp);
		List<EditPart> containedEditParts = new ArrayList<>();
		for (Object obj : getHost().getViewer().getEditPartRegistry().values()) {
			if (obj instanceof EditPart) {
				EditPart ep = (EditPart) obj;

				// Skip the combined fragment itself and unrelated edit parts.

				if (orderChangeTargetEp.equals(ep)) {
					continue;
				}

				if (!(ep instanceof CombinedFragmentEditPart || ep instanceof AbstractMessageEditPart || ep instanceof BehaviorExecutionSpecificationEditPart)) {
					continue;
				}

				Rectangle epBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((GraphicalEditPart) ep);
				if (moveTargetCfBounds.contains(epBounds)) {
					// Include edit parts that are graphically contained within the combined fragment.
					containedEditParts.add(ep);
				}
			}
		}

		// Generate move commands for each contained edit part, respecting the overall movement request.
		for (EditPart ep : containedEditParts) {

			if (ep instanceof AbstractMessageEditPart) {
				processAbstractMessageEditPart((GraphicalEditPart) ep, changeBoundsRequest, moveCompoundCommand);
			} else if (ep instanceof RoundedCompartmentEditPart) {
				processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, changeBoundsRequest, moveCompoundCommand);
			}
		}

		// Calculate the new Y position after the move.
		int moveTargetNewY = moveTargetCfBounds.y + moveDelta.y;

		// Ensure the combined fragment's height meets minimum requirements.
		int moveTargetCfBoundsHeight = moveTargetCfBounds.height;
		if (moveTargetCfBoundsHeight < 100) {
			moveTargetCfBoundsHeight = 100; // Enforce a minimum height of 100.
		}

		// Identify and process edit parts located below the combined fragment for potential adjustment.
		for (Object obj : getHost().getViewer().getEditPartRegistry().values()) {

			if (obj instanceof EditPart) {
				EditPart ep = (EditPart) obj;

				// Skip the combined fragment itself and any contained edit parts.

				if (orderChangeTargetEp.equals(ep)) {
					continue;
				}

				if (containedEditParts.contains(ep)) {
					continue;
				}

				// Focus on Combined Fragments, Messages, and BESs only.
				if (!(ep instanceof CombinedFragmentEditPart || ep instanceof AbstractMessageEditPart || ep instanceof BehaviorExecutionSpecificationEditPart)) {
					continue;
				}

				Rectangle followingMoveEpBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((GraphicalEditPart) ep);

				// Prepare requests for moving and resizing following edit parts.
				ChangeBoundsRequest followingEditPartMoveRequest = new ChangeBoundsRequest();
				followingEditPartMoveRequest.setEditParts(ep);
				followingEditPartMoveRequest.setType(changeBoundsRequest.getType());

				ChangeBoundsRequest followingInteractionOperandResizeRequest = new ChangeBoundsRequest();
				followingInteractionOperandResizeRequest.setEditParts(ep);
				followingInteractionOperandResizeRequest.setType(changeBoundsRequest.getType());

				if (moveDelta.y > 0) {
					// Handling movement downwards

					if (followingMoveEpBounds.y > moveTargetCfBounds.bottom()) {
						// If an EditPart is located below the bottom of the CombinedFragment but above the new Y position after the move

						if (followingMoveEpBounds.y < moveTargetNewY) {

							// 이동 대상 CF 밑바닥 부터 이동 y 사이에 존재하는 경우 CF 높이만큼 위로(-) 이동 => 추후 필요시 반영
							// Potential case for future implementation: Move EditParts located between the CombinedFragment's bottom and the new Y position upwards
						} else if (followingMoveEpBounds.y > moveTargetNewY) {
							// For EditParts located below the new Y position, move them downwards to accommodate the CombinedFragment's new position

							Point epMoveDelta = new Point(0, moveTargetCfBoundsHeight);
							followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

							// Process movement for different types of contained EditParts
							if (ep instanceof AbstractMessageEditPart) {
								processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							} else if (ep instanceof RoundedCompartmentEditPart) {
								processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
							}
						}
					}

					// Resize commands for adjusting lifeline and BES height in response to the CombinedFragment's movement
					Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(orderChangeTargetEp, moveTargetCfBoundsHeight);

					if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
						moveCompoundCommand.add(resizeLifelineEpCommand);
					}

					Command resizeFirstBesEp = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand(orderChangeTargetEp, moveTargetCfBoundsHeight);

					if (resizeFirstBesEp != null && resizeFirstBesEp.canExecute()) {
						moveCompoundCommand.add(resizeFirstBesEp);
					}

					// Adjust the parent interaction operand size to accommodate the new position and size of the CombinedFragment
					this.resizeParentInteractionOperandForMoveTarget(changeBoundsRequest, orderChangeTargetEp, moveCompoundCommand, moveTargetCfBoundsHeight);


				} else if (moveDelta.y < 0) {
					// Handling movement upwards

					if (followingMoveEpBounds.y > moveTargetNewY) {
						// 이동 대상 CF 윗면 부터 이동 y 사이에 존재하는 경우 CF 높이만큼 아래로 이동
						// For EditParts located below the new Y position, adjust their position downwards

						Point epMoveDelta = new Point(0, moveTargetCfBoundsHeight);
						followingEditPartMoveRequest.setMoveDelta(epMoveDelta);

						// Process movement for different types of contained EditParts
						if (ep instanceof AbstractMessageEditPart) {
							processAbstractMessageEditPart((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
						} else if (ep instanceof RoundedCompartmentEditPart) {
							processRoundedCompartmentEditPartForInstanceMove((GraphicalEditPart) ep, followingEditPartMoveRequest, moveCompoundCommand);
						}

						// Resize commands for adjusting lifeline and BES height in response to the CombinedFragment's movement
						Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(orderChangeTargetEp, moveTargetCfBoundsHeight);

						if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
							moveCompoundCommand.add(resizeLifelineEpCommand);
						}

						Command resizeFirstBesEp = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand(orderChangeTargetEp, moveTargetCfBoundsHeight);

						if (resizeFirstBesEp != null && resizeFirstBesEp.canExecute()) {
							moveCompoundCommand.add(resizeFirstBesEp);
						}

						// Adjust the parent interaction operand size to better fit the new position and size of the CombinedFragment
						this.resizeParentInteractionOperandForMoveTarget(changeBoundsRequest, orderChangeTargetEp, moveCompoundCommand, moveTargetCfBoundsHeight);

					} else {


					}
				}
			}
		}

		// Return the compound command if it contains any commands, otherwise return null.
		return moveCompoundCommand.isEmpty() ? null : moveCompoundCommand;

	}

	/**
	 * Checks if the proposed location for moving a Combined Fragment edit part is within the bounds of a
	 * Behavior Execution Specification (BES) associated with a specific lifeline. This method identifies the
	 * main logic lifeline and its corresponding BES, then compares the proposed move location against the BES bounds.
	 * It ensures that the Combined Fragment's new location does not move outside the BES's vertical scope,
	 * allowing some margin at the top and bottom.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the proposed move, including the target location.
	 * @param orderChangeTargetEp
	 *            The Combined Fragment edit part being considered for movement.
	 * @return true if the move location is within the vertical bounds of the main BES, with specified margins; otherwise, false.
	 */
	private boolean isWithinBehaviorExecutionSpec(ChangeBoundsRequest changeBoundsRequest, CombinedFragmentEditPart orderChangeTargetEp) {

		LifelineEditPart mainLogicLifelineEp = AdoneSequenceUtil.getAllLifelineEditParts(orderChangeTargetEp).get(1);

		Lifeline mainLogicLifeline = (Lifeline) mainLogicLifelineEp.resolveSemanticElement();

		// Find the first behavior execution specification covered by this lifeline.
		BehaviorExecutionSpecification mainBes = null;
		for (InteractionFragment frg : mainLogicLifeline.getCoveredBys()) {
			if (frg instanceof BehaviorExecutionSpecification) {
				mainBes = (BehaviorExecutionSpecification) frg;
				break;
			}
		}

		BehaviorExecutionSpecificationEditPart mainLogicBesEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(mainLogicLifelineEp, mainBes);

		Rectangle mainLogicBesBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(mainLogicBesEp);

		Point moveTargetLocation = changeBoundsRequest.getLocation();

		// Check if the move location is above the top margin of the BES.
		if (moveTargetLocation.y < mainLogicBesBounds.y + 20) {
			return false;// Move is outside the acceptable range, above the BES.
		}

		// 추후 테스트 요망(2024-01-04)
		// Check if the move location is below the bottom margin of the BES.
		if (moveTargetLocation.y > mainLogicBesBounds.y + mainLogicBesBounds.height - 20) {
			return false;// Move is outside the acceptable range, below the BES.
		}

		// Move is within the BES bounds, considering margins.
		return true;
	}

	/**
	 * Processes the movement of a Combined Fragment edit part based on the provided change bounds request.
	 * This method checks for horizontal movement constraints and only allows vertical movements. If the horizontal
	 * movement exceeds a certain threshold, it returns an unexecutable command to prevent the action. Otherwise,
	 * it constructs and returns a command for the vertical movement of the Combined Fragment. This method is
	 * particularly cautious about horizontal movements to ensure diagram integrity.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the proposed move, including direction and magnitude.
	 * @param moveTargetEp
	 *            The Combined Fragment edit part that is being moved.
	 * @return A Command object that is either executable for valid movements or unexecutable for disallowed movements.
	 */
	private Command processCombinedFragmentMovement(ChangeBoundsRequest changeBoundsRequest, CombinedFragmentEditPart moveTargetEp) {
		Point moveDelta = changeBoundsRequest.getMoveDelta();

		// 아래 내용 오로디에서 생성된 CF 이동시 발생하는 오류 대응 -> 검토 필요 (2024-01-18)

		// Check for horizontal movement and disallow significant horizontal moves.
		if (moveDelta.x > 10) { // Disallow if horizontal movement exceeds 10 units.
			// Return an unexecutable command for significant horizontal movements.
			return UnexecutableCommand.INSTANCE;
		} else {

			// Neutralize horizontal movement for processing.
			moveDelta.x = 0;

			// Construct and return a command for vertical movement if applicable.
			Command moveCombinedFragmentCommand = createChangeConstraintCommand(moveTargetEp, translateToModelConstraint(getConstraintFor(changeBoundsRequest, moveTargetEp)));
			if (moveCombinedFragmentCommand != null && moveCombinedFragmentCommand.canExecute()) {
				return moveCombinedFragmentCommand;
			}
		}

		// Return null if no valid movement command can be constructed.
		return null;
	}


	/**
	 * Checks if moving a graphical edit part upwards in a sequence diagram exceeds certain movement limits.
	 * It considers various constraints based on the type of the edit part being moved and the relationship
	 * between the moving edit part and other elements in the diagram. This includes checking against lifeline
	 * headers, combined fragments, and other messages to ensure the move does not violate diagram semantics
	 * or layout constraints.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the move, including direction and magnitude.
	 * @param moveTargetEp
	 *            The graphical edit part being moved.
	 * @return true if moving the edit part upwards exceeds the defined movement limits; otherwise, false.
	 */
	private boolean isExceedingUpwardMoveLimit(ChangeBoundsRequest changeBoundsRequest, GraphicalEditPart moveTargetEp) {

		// Check for a flag in the request to bypass move checks.
		Object value = changeBoundsRequest.getExtendedData().get("DoNotMoveCheck");
		if (value instanceof Boolean && (Boolean) value) {
			return false;
		}

		// Only process upward movements; downward or no movement is ignored.
		if (changeBoundsRequest.getMoveDelta().y >= 0) {
			return false;
		}

		// Refresh the visual appearance and state of the target edit part.
		refreshEditPart(moveTargetEp);

		// Calculate the current and proposed bounds for the target edit part.
		Rectangle targetBounds;

		if (moveTargetEp instanceof AbstractMessageEditPart) {
			// Use connection figure for messages to determine bounds.
			Connection connection = ((AbstractMessageEditPart) moveTargetEp).getConnectionFigure();
			Point startPoint = connection.getSourceAnchor().getReferencePoint().getCopy();
			Point endPoint = connection.getTargetAnchor().getReferencePoint().getCopy();
			targetBounds = new Rectangle(startPoint, endPoint);

		} else {
			// For other types of edit parts, use the figure's bounds.
			targetBounds = moveTargetEp.getFigure().getBounds().getCopy();
			moveTargetEp.getFigure().translateToAbsolute(targetBounds);
		}

		// Calculate proposed Y position after move.
		int proposedY = targetBounds.y + changeBoundsRequest.getMoveDelta().y;

		Map<?, ?> editPartRegistry = moveTargetEp.getViewer().getEditPartRegistry();
		for (Object obj : editPartRegistry.values()) {
			if (obj instanceof GraphicalEditPart) {
				GraphicalEditPart otherEp = (GraphicalEditPart) obj;

				// Refresh other edit parts to ensure accurate bounds for comparison.
				refreshEditPart(otherEp);

				// Avoid self-comparison.
				if (moveTargetEp.equals(otherEp)) {
					continue;
				}

				// Determine bounds for comparison based on whether the edit part is a message.
				Rectangle otherBounds;
				if (otherEp instanceof AbstractMessageEditPart) {
					Connection connection = ((AbstractMessageEditPart) otherEp).getConnectionFigure();
					Point startPoint = connection.getSourceAnchor().getReferencePoint().getCopy();
					Point endPoint = connection.getTargetAnchor().getReferencePoint().getCopy();
					otherBounds = new Rectangle(startPoint, endPoint);
				} else {
					otherBounds = otherEp.getFigure().getBounds().getCopy();
					otherEp.getFigure().translateToAbsolute(otherBounds);
				}

				// Skip edit parts that are below the target's current position.
				if (otherBounds.y + 1 >= targetBounds.y) {
					continue;
				}

				// Check if the other EditPart is a Lifeline, and apply constraints based on its header bounds.
				if (otherEp instanceof LifelineEditPart) {
					Rectangle headerBounds = calculateLifelineHeaderBounds(otherEp);
					int headerBottomEdge = headerBounds.y + headerBounds.height;
					if (proposedY - 40 <= headerBottomEdge) {
						// Movement violates the upward move limit due to Lifeline header.
						return true;
					}
				}

				// Check if the other EditPart is a Combined Fragment.
				if (otherEp instanceof CombinedFragmentEditPart) {

					// Calculate intersection with the target bounds.
					Rectangle intersection = otherBounds.getCopy().intersect(targetBounds);

					if (intersection.width > 0 && intersection.height > 0) {

						// Find the nearest parent Combined Fragment to the moving target.
						CombinedFragmentEditPart nearestParentFragment = findNearestParentCombinedFragment(moveTargetEp, editPartRegistry);
						if (nearestParentFragment != otherEp) {
							continue; // Ignore if it's not the nearest parent Combined Fragment.
						}

						int upperEdgeOfCombinedFragment = otherBounds.y; // Top edge of the Combined Fragment.

						if (upperEdgeOfCombinedFragment == 0) {
							continue;// Skip if the top edge is not defined (sanity check).
						}

						// Apply different constraints if the moving target is also a Combined Fragment.
						if (moveTargetEp instanceof CombinedFragmentEditPart) {
							if (proposedY < upperEdgeOfCombinedFragment + 40) {
								return true; // Movement violates Combined Fragment constraints.
							}
						} else {
							if (proposedY < upperEdgeOfCombinedFragment + 60) {
								return true; // Movement violates Combined Fragment constraints for non-fragment targets.
							}
						}
						continue;
					}

					// CombinedFragment의 최하단 Y 좌표를 기준으로 이동 제한을 확인
					// Check against the bottom edge of the Combined Fragment for movement constraints.
					int bottomEdgeOfCombinedFragment = otherBounds.y + otherBounds.height;
					if (proposedY - 18 < bottomEdgeOfCombinedFragment) {
						return true; // Movement violates the lower edge constraint of the Combined Fragment.
					}
				}

				// Apply constraints based on the relationship with other EditParts in the diagram.
				if (otherEp instanceof BehaviorExecutionSpecificationEditPart) {

					BehaviorExecutionSpecificationEditPart besEp = (BehaviorExecutionSpecificationEditPart) otherEp;
					BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) besEp.resolveSemanticElement();

					Lifeline coveringLifeline = bes.getCovereds().get(0);

					LifelineEditPart lfEp = (LifelineEditPart) AdoneSequenceUtil.getEditPartFromSemantic(besEp, coveringLifeline);

					if (!AdoneSequenceUtil.isSecondLifelineEp(lfEp)) {
						if (isAboveAndWithinDistance(targetBounds, otherBounds, proposedY, 19)) {
							// Movement violates constraints related to Behavior Execution Specifications.
							return true;
						}
					}

				} else if (otherEp instanceof AbstractMessageEditPart) {
					if (moveTargetEp instanceof AbstractMessageEditPart && isStartOfMessage(targetBounds, otherBounds)) {
						if (isAboveAndWithinDistance(targetBounds, otherBounds, proposedY, 40)) {
							// Movement violates constraints near the start of another message.
							return true; // Message 시작점에서 BehaviorExecutionSpecification 조건 위반
						}

					}

					if (isFirstMessage((AbstractMessageEditPart) otherEp)) {
						if (isAboveAndWithinDistance(targetBounds, otherBounds, proposedY, 38)) {
							// Movement violates constraints for the first message to a Lifeline.
							return true; // Message 시작점에서 BehaviorExecutionSpecification 조건 위반
						}
					}

					if (isAboveAndWithinDistance(targetBounds, otherBounds, proposedY, 19)) {
						return true; // General constraint violation for movement near other messages.
					}
				} else if (moveTargetEp instanceof InteractionOperandEditPart) {
					if (isAboveAndWithinDistance(targetBounds, otherBounds, proposedY, 20)) {
						return true; // Movement violates constraints related to Interaction Operands.
					} else {
					}
				} else {
					// Placeholder for additional checks or to indicate the end of checks.
				}
			}
		}

		return false;// No constraints violated, allowing upward movement.
	}

	/**
	 * Determines if the specified message edit part represents the first message sent to a lifeline
	 * in a sequence diagram. This determination is based on the message's receive event and the
	 * covered lifeline. It specifically checks if the covered lifeline is the second lifeline in the
	 * sequence diagram, implying the message is the first one received by that lifeline.
	 *
	 * @param otherEp
	 *            The AbstractMessageEditPart instance representing the message to check.
	 * @return true if the message is the first received by its covered lifeline; otherwise, false.
	 */
	private boolean isFirstMessage(AbstractMessageEditPart otherEp) {
		// Resolve the semantic element to access the message's details.
		Message msg = (Message) otherEp.resolveSemanticElement();
		MessageOccurrenceSpecification receiveMos = (MessageOccurrenceSpecification) msg.getReceiveEvent();
		Lifeline coveredLifeline = receiveMos.getCovered();

		// Find the edit part corresponding to the covered lifeline.
		LifelineEditPart coveredLifelineEditPart = (LifelineEditPart) AdoneSequenceUtil.getEditPartFromSemantic(otherEp, coveredLifeline);

		// Determine if the message is the first received based on the lifeline's position.
		if (AdoneSequenceUtil.isSecondLifelineEp(coveredLifelineEditPart)) {
			return true;
		}
		return false;
	}

	/**
	 * Finds the nearest parent combined fragment edit part for a given target edit part within a diagram.
	 * This method iterates through all combined fragment edit parts in the edit part registry, excluding
	 * the target edit part itself, and determines the closest parent based on graphical containment and
	 * proximity. The "nearest" parent is defined as the one that graphically contains the target and is closest
	 * in terms of the area, indicating a tighter nesting relationship.
	 *
	 * @param targetEp
	 *            The target graphical edit part for which to find the nearest parent combined fragment.
	 * @param editPartRegistry
	 *            The registry containing all edit parts in the diagram.
	 * @return The nearest parent combined fragment edit part, or null if no suitable parent is found.
	 */
	private CombinedFragmentEditPart findNearestParentCombinedFragment(GraphicalEditPart targetEp, Map<?, ?> editPartRegistry) {
		CombinedFragmentEditPart nearestParentFragment = null;
		Rectangle targetBounds = getAbsoluteBoundsForMessageEp(targetEp);

		// Iterate through all edit parts to find the nearest parent combined fragment.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof CombinedFragmentEditPart && value != targetEp) {
				CombinedFragmentEditPart combinedFragmentEditPart = (CombinedFragmentEditPart) value;
				Rectangle parentBounds = getAbsoluteBoundsForMessageEp(combinedFragmentEditPart);

				// Check for graphical parentage and closer proximity.
				if (isGraphicalParent(targetBounds, parentBounds)) {
					if (nearestParentFragment == null || isCloserParent(nearestParentFragment, combinedFragmentEditPart, targetBounds)) {
						nearestParentFragment = combinedFragmentEditPart;
					}
				}
			}
		}

		return nearestParentFragment;
	}

	/**
	 * Determines if a candidate combined fragment edit part is a "closer" graphical parent to the target
	 * compared to the current nearest combined fragment edit part. "Closer" in this context means that the
	 * candidate's bounds not only contain the target bounds but also have a smaller area than the current
	 * nearest, indicating a tighter graphical grouping or nesting.
	 *
	 * @param currentNearest
	 *            The current nearest combined fragment edit part to the target.
	 * @param candidate
	 *            The candidate combined fragment edit part being considered as a closer parent.
	 * @param targetBounds
	 *            The bounds of the target for which a closer parent is sought.
	 * @return true if the candidate is a closer parent to the target; otherwise, false.
	 */
	private boolean isCloserParent(CombinedFragmentEditPart currentNearest, CombinedFragmentEditPart candidate, Rectangle targetBounds) {

		// Calculate absolute bounds for comparison.
		Rectangle currentNearestBounds = getAbsoluteBoundsForMessageEp(currentNearest);
		Rectangle candidateBounds = getAbsoluteBoundsForMessageEp(candidate);

		// Determine if candidate is a closer parent based on containment and smaller area.
		return candidateBounds.contains(targetBounds) && candidateBounds.width * candidateBounds.height < currentNearestBounds.width * currentNearestBounds.height;
	}

	/**
	 * Determines if the given parent bounds graphically contain the child bounds.
	 * This method checks if the entirety of the child's bounds is within the parent's bounds,
	 * effectively determining if the parent is the graphical container of the child.
	 *
	 * @param childBounds
	 *            The bounds of the child element.
	 * @param parentBounds
	 *            The bounds of the parent element.
	 * @return true if the parent bounds contain the child bounds; otherwise, false.
	 */
	private boolean isGraphicalParent(Rectangle childBounds, Rectangle parentBounds) {
		// Check if parent bounds contain child bounds.
		return parentBounds.contains(childBounds);
	}


	/**
	 * Calculates the absolute bounds of a graphical edit part representing a message in a sequence diagram.
	 * For message edit parts, it computes the bounds based on the start and end points of its connection figure,
	 * ensuring the bounds encompass the entire message path. For other edit parts, it simply returns the absolute
	 * bounds of the edit part's figure. This method is useful for positioning and collision detection in sequence diagrams.
	 *
	 * @param editPart
	 *            The graphical edit part for which to calculate absolute bounds.
	 * @return A rectangle representing the absolute bounds of the edit part.
	 */
	private Rectangle getAbsoluteBoundsForMessageEp(GraphicalEditPart editPart) {
		if (editPart instanceof AbstractMessageEditPart) {
			// Calculate bounds based on connection figure for messages.
			Connection connection = ((AbstractMessageEditPart) editPart).getConnectionFigure();
			PointList points = connection.getPoints();
			Point start = points.getFirstPoint().getCopy();
			Point end = points.getLastPoint().getCopy();

			// Convert start and end points to absolute coordinates.
			connection.translateToAbsolute(start);
			connection.translateToAbsolute(end);

			// Create a rectangle that encompasses the start and end points.
			return new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y).union(new Rectangle(end.x, end.y, 0, 0));

		} else {
			// For other edit parts, return the absolute bounds of the figure.
			Rectangle bounds = editPart.getFigure().getBounds().getCopy();
			editPart.getFigure().translateToAbsolute(bounds);
			return bounds;
		}
	}

	/**
	 * Requests the refresh of a graphical edit part and its figure. This ensures that any changes
	 * to the model or visual representation are updated in the diagram. It first calls the refresh
	 * method on the edit part, which updates its visual state and model if necessary. Then, it validates
	 * the figure to ensure that all visual changes are correctly displayed.
	 *
	 * @param editPart
	 *            The graphical edit part to be refreshed.
	 */
	private void refreshEditPart(GraphicalEditPart editPart) {
		// Request refresh of the EditPart and its figure.
		editPart.refresh();
		// Uncomment the next line to invalidate the figure before validation if needed.
		// editPart.getFigure().invalidate();
		editPart.getFigure().validate(); // Ensures the figure's layout and visuals are up-to-date.
	}


	/**
	 * Calculates the bounds of the header area of a lifeline in a sequence diagram.
	 * This area is generally at the top part of the lifeline and may vary depending on specific implementations.
	 * For instance, the top 20 pixels of the lifeline could be considered as the header area.
	 * This method uses an absolute position calculation to ensure consistency across different diagrams.
	 *
	 * @param lifelineEditPart
	 *            The graphical edit part representing the lifeline.
	 * @return A rectangle representing the bounds of the lifeline's header area.
	 */
	private Rectangle calculateLifelineHeaderBounds(GraphicalEditPart lifelineEditPart) {
		// Calculate the header area of the LifelineEditPart.
		Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(lifelineEditPart);
		return new Rectangle(bounds.x, bounds.y, bounds.width, 20); // Example: Top 20 pixels as header area.
	}

	/**
	 * Determines if the "other" rectangle is above the "target" rectangle and within a specified vertical distance
	 * from a proposed Y coordinate. It takes into account the bottom edges of both rectangles to assess
	 * their vertical positioning and proximity based on the proposed Y coordinate and distance.
	 *
	 * @param target
	 *            The rectangle of the target edit part.
	 * @param other
	 *            The rectangle of the other edit part to compare with.
	 * @param proposedY
	 *            The proposed Y coordinate to compare against.
	 * @param distance
	 *            The maximum allowed vertical distance from the proposed Y.
	 * @return true if "other" is above "target" and within the specified distance from the proposed Y coordinate.
	 */
	private boolean isAboveAndWithinDistance(Rectangle target, Rectangle other, int proposedY, int distance) {

		// Calculate the bottom edge of both rectangles to determine vertical positioning
		int targetBottomEdge = target.y + target.height;
		int otherBottomEdge = other.y + other.height;

		// Check if "other" is above "target" and within the specified distance from proposedY
		if (otherBottomEdge > targetBottomEdge) { // Check if "other" is taller
			return other.y < target.y && proposedY - distance < other.y;// Ensure "other" is above and within distance
		} else {
			return other.y < target.y && proposedY - distance < otherBottomEdge;// Check against "other"'s bottom edge
		}
	}

	/**
	 * Checks if the start point of a message overlaps with another edit part based on their Y coordinates.
	 * The start of the message is determined by its Y coordinate.
	 *
	 * @param message
	 *            The rectangle representing the message's bounds.
	 * @param other
	 *            The rectangle representing the other edit part's bounds.
	 * @return true if the Y coordinate of the message's start matches the other's Y coordinate.
	 */
	private boolean isStartOfMessage(Rectangle message, Rectangle other) {
		// Check if the start point of the message overlaps with another edit part.
		return message.y == other.y;
	}


	/**
	 * Iterates through all edit parts below a specific point in the diagram and processes each
	 * for movement based on the provided change bounds request. Supports different edit part types
	 * including abstract messages and rounded compartments.
	 *
	 * @param changeBoundsRequest
	 *            The request detailing the move or resize action.
	 * @param allBelowEditParts
	 *            A list of all graphical edit parts located below a certain point.
	 * @param moveCompoundCommand
	 *            A compound command that accumulates all move commands.
	 * @param moveTargetEp
	 *            The target edit part that is being moved.
	 */
	private void processAndMoveBelowEditParts(ChangeBoundsRequest changeBoundsRequest, List<GraphicalEditPart> allBelowEditParts, CompoundCommand moveCompoundCommand, GraphicalEditPart moveTargetEp) {

		for (GraphicalEditPart nextBelowEditPart : allBelowEditParts) {

			if (nextBelowEditPart instanceof AbstractMessageEditPart) {
				// Skip processing the target edit part itself
				if (nextBelowEditPart.equals(moveTargetEp)) {
					continue;
				}
				// Process AbstractMessageEditPart instances for movement
				processAbstractMessageEditPart(nextBelowEditPart, changeBoundsRequest, moveCompoundCommand);
			} else if (nextBelowEditPart instanceof RoundedCompartmentEditPart) {
				// Process RoundedCompartmentEditPart instances for movement
				processRoundedCompartmentEditPart(nextBelowEditPart, changeBoundsRequest, moveCompoundCommand);
			} else {
				// Log unidentifiable edit parts
				System.out.println("NextBelowEditPart Not Identified :" + nextBelowEditPart.getClass().getSimpleName());
			}
		}
	}

	/**
	 * Processes the movement and resizing of an abstract message edit part within a diagram.
	 * This involves calculating new source and target locations based on the provided request,
	 * and creating commands to move these endpoints accordingly.
	 *
	 * @param nextBelowEditPart
	 *            The graphical edit part that is being moved or resized.
	 * @param changeBoundsRequest
	 *            The request containing details about the move or resize action.
	 * @param moveCompoundCommand
	 *            A compound command that will contain all the generated move commands.
	 */
	private void processAbstractMessageEditPart(GraphicalEditPart nextBelowEditPart, ChangeBoundsRequest changeBoundsRequest, CompoundCommand moveCompoundCommand) {

		// Initializes connection and calculates source and target points.
		AbstractMessageEditPart connectionPart = (AbstractMessageEditPart) nextBelowEditPart;
		Connection connection = (Connection) connectionPart.getFigure();
		PointList points = connection.getPoints().getCopy();

		// Retrieve source and target edit parts for the connection.
		EditPart srcPart = connectionPart.getSource();
		EditPart tgtPart = connectionPart.getTarget();

		// Calculate absolute source location and adjust for snap-to-grid.
		Point sourceLocation = points.getFirstPoint().getCopy();
		connection.translateToAbsolute(sourceLocation);
		sourceLocation = SequenceUtil.getSnappedLocation(getHost(), sourceLocation);

		// Adjust source location based on request type (resize/move).
		if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
			sourceLocation.y += changeBoundsRequest.getSizeDelta().height; // 크기 변경에 대한 y 축 변화
		} else {
			sourceLocation.y += changeBoundsRequest.getMoveDelta().y; // 이동에 대한 y 축 변화
		}

		// Calculate absolute target location and adjust for snap-to-grid.
		Point targetLocation = points.getLastPoint().getCopy();
		connection.translateToAbsolute(targetLocation);
		targetLocation = SequenceUtil.getSnappedLocation(getHost(), targetLocation);

		// Adjust target location based on request type (resize/move).
		if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
			targetLocation.y += changeBoundsRequest.getSizeDelta().height; // 크기 변경에 대한 y 축 변화
		} else {
			targetLocation.y += changeBoundsRequest.getMoveDelta().y; // 이동에 대한 y 축 변화
		}

		// Generate and add command to move source endpoint.
		ReconnectRequest sourceReq = new ReconnectRequest(REQ_RECONNECT_SOURCE);
		sourceReq.setConnectionEditPart(connectionPart);
		sourceReq.setLocation(sourceLocation);
		sourceReq.setTargetEditPart(srcPart);
		sourceReq.getExtendedData().put(SequenceUtil.DO_NOT_CHECK_HORIZONTALITY, true);
		sourceReq.getExtendedData().put("DoNotMoveCheck", true);
		Command moveSourceCommand = srcPart.getCommand(sourceReq);

		if (moveSourceCommand != null && moveSourceCommand.canExecute()) {
			moveCompoundCommand.add(moveSourceCommand);
		}

		// Generate and add command to move target endpoint.
		ReconnectRequest targetReq = new ReconnectRequest(REQ_RECONNECT_TARGET);
		targetReq.setConnectionEditPart(connectionPart);
		targetReq.setLocation(targetLocation);
		targetReq.setTargetEditPart(tgtPart);
		targetReq.getExtendedData().put(SequenceUtil.DO_NOT_CHECK_HORIZONTALITY, true);
		sourceReq.getExtendedData().put("DoNotMoveCheck", true);
		Command moveTargetCommand = tgtPart.getCommand(targetReq);

		if (moveTargetCommand != null && moveTargetCommand.canExecute()) {
			moveCompoundCommand.add(moveTargetCommand);
		}
	}

	/**
	 * Processes a RoundedCompartmentEditPart or a BehaviorExecutionSpecificationEditPart for moving based on a ChangeBoundsRequest.
	 * This method constructs and adds the appropriate move command to a compound command. For BehaviorExecutionSpecificationEditParts,
	 * it specifically prevents recursive processing. For other types of RoundedCompartmentEditParts, it adjusts their Y position based
	 * on the request details and constructs a move command.
	 *
	 * @param nextBelowEditPart
	 *            The next edit part below the one being processed, either a BehaviorExecutionSpecificationEditPart or a RoundedCompartmentEditPart.
	 * @param changeBoundsRequest
	 *            The request that contains details about the move or resize action.
	 * @param moveCompoundCommand
	 *            The compound command to which the move command will be added.
	 */
	private void processRoundedCompartmentEditPart(GraphicalEditPart nextBelowEditPart, ChangeBoundsRequest changeBoundsRequest, CompoundCommand moveCompoundCommand) {

		// Prevent recursive processing. (2024-01-31)
		if (changeBoundsRequest.getExtendedData().containsKey("DoNotMoveCheck")) {
			return;
		}

		if (nextBelowEditPart instanceof BehaviorExecutionSpecificationEditPart) {

			ChangeBoundsRequest resizeReq = new ChangeBoundsRequest();

			// 이동 시 상단 제약 회피하기 위한 정보 추가 (2024-01-31)
			// Avoid top constraint during movement.
			resizeReq.getExtendedData().put("DoNotMoveCheck", true);
			resizeReq.getExtendedData().put("GroupMove", resizeReq);
			resizeReq.setEditParts(nextBelowEditPart);

			// Adjust move delta based on the request type.
			if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
				resizeReq.setMoveDelta(new Point(0, changeBoundsRequest.getSizeDelta().getCopy().height));
			} else {
				resizeReq.setMoveDelta(changeBoundsRequest.getMoveDelta());
			}

			// Prepare a move request for the RoundedCompartmentEditPart.
			resizeReq.setLocation(changeBoundsRequest.getLocation());
			resizeReq.setType(RequestConstants.REQ_MOVE);
			// resizeReq.setSizeDelta(new Dimension(0, changeBoundsRequest.getMoveDelta().y));
			// resizeReq.setResizeDirection(resizeReq.getResizeDirection());
			// req.setMoveDelta(request.getMoveDelta());
			// req.setSizeDelta(request.getSizeDelta());
			// req.setLocation(request.getLocation());
			// req.setExtendedData(request.getExtendedData());

			Command moveBesCommand = nextBelowEditPart.getCommand(resizeReq);
			// Command moveBesCommand = createChangeConstraintCommand(nextBelowEditPart, translateToModelConstraint(getConstraintFor(changeBoundsRequest, nextBelowEditPart)));

			if (moveBesCommand != null && moveBesCommand.canExecute()) {
				moveCompoundCommand.add(moveBesCommand);
			}

		} else {

			RoundedCompartmentEditPart roundedEditPart = (RoundedCompartmentEditPart) nextBelowEditPart;
			Point location = roundedEditPart.getLocation().getCopy();
			roundedEditPart.getFigure().translateToAbsolute(location);

			// Move command for RoundedCompartmentEditPart
			ChangeBoundsRequest requestForMove = new ChangeBoundsRequest(REQ_MOVE);
			requestForMove.setEditParts(roundedEditPart);

			// Adjust the Y location based on the request type.
			if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
				location.y += changeBoundsRequest.getSizeDelta().getCopy().height; // 크기 변경에 대한 y 축 변화
				requestForMove.setMoveDelta(new Point(0, changeBoundsRequest.getSizeDelta().getCopy().height));
			} else {
				location.y += changeBoundsRequest.getMoveDelta().y; // 이동에 대한 y 축 변화
				requestForMove.setMoveDelta(new Point(0, changeBoundsRequest.getMoveDelta().y));
			}

			requestForMove.getExtendedData().put("DoNotMoveCheck", true); // Prevent recursive adjustments.
			requestForMove.setLocation(location);

			// BES 위치 정보 틀려지는 현상 확인 필요 (2024-01-08) => 순환 호출로 인해 직접 Command 생성하는 방식으로 수정 (2024-01-31)
			// Command moveCommand = roundedEditPart.getCommand(requestForMove);
			// if (moveCommand != null && moveCommand.canExecute()) {
			// moveCompoundCommand.add(moveCommand);
			// }

			// Create a command to move the RoundedCompartmentEditPart and add it to the compound command.
			Command moveInteractionFragmentCommand = createChangeConstraintCommand(nextBelowEditPart, translateToModelConstraint(getConstraintFor(requestForMove, nextBelowEditPart)));

			if (moveInteractionFragmentCommand != null) {
				moveCompoundCommand.add(moveInteractionFragmentCommand);
			}

		}

	}

	/**
	 * Processes a RoundedCompartmentEditPart for movement based on a ChangeBoundsRequest, and adds the
	 * resulting move command to a compound command. This method adjusts the Y position of the RoundedCompartmentEditPart
	 * based on whether the request is for resizing or moving. It then constructs a move command for the
	 * RoundedCompartmentEditPart and adds it to the given compound command.
	 *
	 * @param nextBelowEditPart
	 *            The RoundedCompartmentEditPart to be moved.
	 * @param changeBoundsRequest
	 *            The request that triggered the move, containing details about the move or resize.
	 * @param moveCompoundCommand
	 *            The compound command to which the move command will be added.
	 */
	private void processRoundedCompartmentEditPartForInstanceMove(GraphicalEditPart nextBelowEditPart, ChangeBoundsRequest changeBoundsRequest, CompoundCommand moveCompoundCommand) {

		RoundedCompartmentEditPart roundedEditPart = (RoundedCompartmentEditPart) nextBelowEditPart;
		Point location = roundedEditPart.getLocation().getCopy();
		roundedEditPart.getFigure().translateToAbsolute(location);

		// Prepare a move request for the RoundedCompartmentEditPart.
		ChangeBoundsRequest requestForMove = new ChangeBoundsRequest(REQ_MOVE);
		requestForMove.setEditParts(roundedEditPart);

		// Adjust the Y location based on the type of request (resize or move).
		if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
			location.y += changeBoundsRequest.getSizeDelta().height; // 크기 변경에 대한 y 축 변화
			requestForMove.setMoveDelta(new Point(0, changeBoundsRequest.getSizeDelta().height));
		} else {
			location.y += changeBoundsRequest.getMoveDelta().y; // 이동에 대한 y 축 변화
			requestForMove.setMoveDelta(new Point(0, changeBoundsRequest.getMoveDelta().y));
		}

		// Indicate that this move should not be checked again to prevent recursive adjustments.
		requestForMove.getExtendedData().put("DoNotMoveCheck", true);
		requestForMove.setLocation(location);

		// Create a command to move the RoundedCompartmentEditPart and add it to the compound command.
		Command moveInteractionFragmentCommand = createChangeConstraintCommand(nextBelowEditPart, translateToModelConstraint(getConstraintFor(changeBoundsRequest, nextBelowEditPart)));

		if (moveInteractionFragmentCommand != null) {
			moveCompoundCommand.add(moveInteractionFragmentCommand);
		}

	}

	/**
	 * Creates a command to move a message end to a new location on a lifeline. This method handles both
	 * OccurrenceSpecifications and Gates. For OccurrenceSpecifications, it delegates to a helper to create the
	 * move command. For Gates, it creates a reconnect request for either the source or target end of the message,
	 * depending on whether the end is a send event or receive event, respectively.
	 *
	 * @param message
	 *            The message whose end is to be moved.
	 * @param endEditPart
	 *            The edit part of the message end.
	 * @param end
	 *            The message end (either an OccurrenceSpecification or a Gate).
	 * @param yLocation
	 *            The new Y location for the message end.
	 * @param lifeline
	 *            The lifeline edit part on which the message end is located.
	 * @param request
	 *            The original request, for context.
	 * @return A command to move the message end, or UnexecutableCommand if the operation is not supported.
	 */
	protected Command createMoveMessageEndCommand(Message message, EditPart endEditPart, MessageEnd end, int yLocation, LifelineEditPart lifeline, Request request) {
		if (end instanceof OccurrenceSpecification) {
			List<EditPart> empty = Collections.emptyList();
			// Delegate the move of OccurrenceSpecifications to a helper class.
			return AdoneOccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification) end, null, yLocation, -1, lifeline, empty, request);
		} else if (end instanceof Gate) {
			// Determine if the end is the source (send event) or target (receive event) of the message.
			boolean isSource = (end == message.getSendEvent());
			ConnectionNodeEditPart connection = (ConnectionNodeEditPart) getHost();
			if (isSource) {
				ReconnectRequest req = new ReconnectRequest(REQ_RECONNECT_SOURCE);
				req.getExtendedData().put(SequenceUtil.DO_NOT_CHECK_HORIZONTALITY, true);
				req.setConnectionEditPart(connection);
				req.setTargetEditPart(endEditPart);
				Point location = SequenceUtil.getAbsoluteEdgeExtremity(connection, true);
				location.setY(yLocation);
				req.setLocation(location);
				Command command = endEditPart.getCommand(req);
				return command;
			} else {
				ReconnectRequest req = new ReconnectRequest(REQ_RECONNECT_TARGET);
				req.getExtendedData().put(SequenceUtil.DO_NOT_CHECK_HORIZONTALITY, true);
				req.setConnectionEditPart(connection);
				req.setTargetEditPart(endEditPart);
				Point location = SequenceUtil.getAbsoluteEdgeExtremity(connection, false);
				location.setY(yLocation);
				req.setLocation(location);
				Command command = endEditPart.getCommand(req);
				return command;
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Generates a command to reposition child edit parts upon the creation of a new BehaviorExecutionSpecification.
	 * It resizes parent containers and moves edit parts below the insertion point to maintain diagram coherence.
	 *
	 * @param changeBoundsRequest
	 *            Request with details about the new execution spec creation, including location.
	 * @return A Command to move relevant child edit parts, or null if no action is required.
	 */
	private Command getMoveChildrenCommandByCreatingExeSpec(AdoneUpdateLocationByNewMessageCreationRequest changeBoundsRequest) {

		CompoundCommand moveCompoundCommand = new CompoundCommand();

		GraphicalEditPart interactionCompartmentEp = changeBoundsRequest.getTargetEditPart();

		// Calculate the delta Y based on the default height of a BehaviorExecutionSpecification.
		int resizeDeltaY = AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT;

		// Resize parent InteractionOperands and CombinedFragments to accommodate the new execution spec.
		this.resizeParentInteractionOperandForCreatingExeSpec(changeBoundsRequest, interactionCompartmentEp, moveCompoundCommand, resizeDeltaY);

		// Find all edit parts located below the point of execution spec creation.
		List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditPartsByLocation(interactionCompartmentEp, changeBoundsRequest);

		// Create a move delta to shift the below edit parts downwards.
		Point moveDelta = new Point(0, AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT);

		// Apply the move delta to the change bounds request.
		changeBoundsRequest.setMoveDelta(moveDelta);

		// Process and move below edit parts to create space for the new execution spec.
		this.processAndMoveBelowEditParts(changeBoundsRequest, allBelowEditParts, moveCompoundCommand, interactionCompartmentEp);

		if (moveCompoundCommand.getChildren().length > 0) {
			return moveCompoundCommand.unwrap();
		} else {
			return null;
		}
	}

	/**
	 * Resizes parent InteractionOperand and CombinedFragment elements to accommodate a new
	 * BehaviorExecutionSpecification being created, ensuring that these elements have sufficient
	 * space to visually contain the new specification. This method is called during the process
	 * of creating a new BehaviorExecutionSpecification, typically in response to a user action
	 * such as drawing a new execution specification on a sequence diagram.
	 *
	 * The method checks for a "DoNotMoveCheck" flag in the request's extended data to prevent
	 * recursive resizing when moving child elements. It then retrieves all parent InteractionOperands
	 * at the location of the new execution specification and resizes them and their parent CombinedFragments
	 * by the specified delta amount. This ensures the logical containment and visual coherence
	 * of the sequence diagram are maintained.
	 *
	 * @param request
	 *            The request object containing information about the new
	 *            message creation, including location and extended data.
	 * @param interactionCompartmentEp
	 *            The graphical edit part representing the interaction compartment
	 *            in which the new execution specification is being created.
	 * @param command
	 *            The compound command to which resize commands are added, to be
	 *            executed as a single transactional operation.
	 * @param resizeDeltaY
	 *            The amount by which the height of the InteractionOperand and
	 *            CombinedFragment should be increased.
	 */
	private void resizeParentInteractionOperandForCreatingExeSpec(AdoneUpdateLocationByNewMessageCreationRequest request, GraphicalEditPart interactionCompartmentEp, CompoundCommand command, int resizeDeltaY) {

		// Check for the "DoNotMoveCheck" flag to prevent recursive updates. (2023-12-23)
		Object value = request.getExtendedData().get("DoNotMoveCheck");

		if (!(value instanceof Boolean && (Boolean) value)) {

			// Retrieve all parent InteractionOperands based on the location of the new execution specification.
			List<InteractionOperandEditPart> parentInteractionOperandEpList = AdoneSequenceUtil.getAllParentInteractionOperandEpsByLocation(interactionCompartmentEp, request.getLocation());

			for (InteractionOperandEditPart ioEp : parentInteractionOperandEpList) {

				// Calculate and apply the new height for the InteractionOperand.
				Rectangle ioEpBounds = ioEp.getFigure().getBounds().getCopy();
				ioEpBounds.height += resizeDeltaY;

				Command resizeInteractionOperandCommand = createChangeConstraintCommand(
						ioEp,
						translateToModelConstraint(ioEpBounds));
				if (resizeInteractionOperandCommand != null) {
					command.add(resizeInteractionOperandCommand);
				}

				// Apply the same height adjustment to the parent CombinedFragment.
				CombinedFragmentEditPart parentCbf = ((CombinedFragmentEditPart) ioEp.getParent().getParent());

				Rectangle cmbFrgBounds = parentCbf.getFigure().getBounds().getCopy();
				cmbFrgBounds.height += resizeDeltaY;

				Command resizeCombinedFragmentCommand = createChangeConstraintCommand(
						ioEp.getParent().getParent(),
						translateToModelConstraint(cmbFrgBounds));
				if (resizeCombinedFragmentCommand != null) {
					command.add(resizeCombinedFragmentCommand);
				}
			}
		}
	}

	/**
	 * Determines whether the specified point is located below a given GraphicalEditPart in the diagram.
	 * This method calculates the absolute Y position of the edit part and compares it with the Y coordinate
	 * of the point. It effectively checks if the point lies below the bottom edge of the edit part's figure,
	 * which is useful for spatial analysis in layout management or determining drag-and-drop target areas.
	 *
	 * @param point
	 *            The point in question, whose Y position is to be compared.
	 * @param editPart
	 *            The GraphicalEditPart to compare against the point's Y position.
	 * @return true if the point is located below the edit part, false otherwise.
	 */
	private boolean isYPositionLocatedBelow(Point point, GraphicalEditPart editPart) {
		if (point == null || editPart == null) {
			return false;
		}

		// Calculate the Y position of the edit part in absolute coordinates.
		int editPartYPosition = getAbsoluteYPosition(editPart);

		// Check if the point's Y position is greater than the edit part's Y position, indicating it is below.
		if (editPartYPosition > point.y) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Calculates the absolute Y position of a given GraphicalEditPart within the diagram.
	 * This method takes into account the hierarchical structure of figures by converting
	 * the local coordinate system of the figure to the absolute coordinate system of the diagram.
	 * This is essential for accurately determining the vertical position of elements in the context
	 * of the entire diagram, particularly useful in layout algorithms and when positioning feedback or markers.
	 *
	 * @param editPart
	 *            The GraphicalEditPart for which the absolute Y position is to be calculated.
	 * @return The absolute Y position of the edit part within the diagram.
	 */
	private int getAbsoluteYPosition(GraphicalEditPart editPart) {
		IFigure figure = editPart.getFigure();
		Rectangle bounds = figure.getBounds().getCopy();

		// Convert the figure's local coordinates to the absolute coordinate system of the diagram.
		figure.translateToAbsolute(bounds);

		// Return the Y position in the absolute coordinate system.
		return bounds.y;
	}

	/**
	 * Generates a list of commands to move or resize child CombinedFragmentEditParts associated with a specific LifelineEditPart.
	 * This method differentiates between the first covered lifeline, where the CombinedFragment's position is merely shifted,
	 * and subsequent lifelines, where the CombinedFragment's width is adjusted to reflect changes in the lifeline's position.
	 *
	 * @param changeBoundsRequest
	 *            The request that contains the details of the move or resize operation, such as the move delta.
	 * @param parentLifelineEp
	 *            The parent LifelineEditPart that the CombinedFragments are associated with.
	 * @return A list of Command objects that, when executed, will move or resize the covered CombinedFragmentEditParts appropriately.
	 */
	private List<Command> getMoveChildCombinedFragmentEditPart(ChangeBoundsRequest changeBoundsRequest, LifelineEditPart parentLifelineEp) {

		List<Command> compoundCommandList = new ArrayList<>();

		// Find all CombinedFragmentEditParts that are covered by the parent lifeline.
		// List<GraphicalEditPart> coveredCmbFrgEpList = findCoveredCombinedFragments(parentLifelineEp);
		List<GraphicalEditPart> coveredCmbFrgEpList = findGraphicallyCoveredCombinedFragments(parentLifelineEp);

		for (GraphicalEditPart moveTargetCmbFrgEp : coveredCmbFrgEpList) {

			// Check if the target CombinedFragment is the first covered by the parent lifeline.
			if (isFirstCoveredLifelineEditPart((CombinedFragmentEditPart) moveTargetCmbFrgEp, parentLifelineEp)) {

				// For the first lifeline, only the position of the CombinedFragment is changed.

				ChangeBoundsRequest requestForCombinedFrgEp = new ChangeBoundsRequest();
				requestForCombinedFrgEp.setEditParts(moveTargetCmbFrgEp);
				requestForCombinedFrgEp.setMoveDelta(changeBoundsRequest.getMoveDelta());
				requestForCombinedFrgEp.setType(RequestConstants.REQ_MOVE_CHILDREN);

				Command moveCombinedFragmentCommand = createChangeConstraintCommand(moveTargetCmbFrgEp, translateToModelConstraint(getConstraintFor(requestForCombinedFrgEp, moveTargetCmbFrgEp)));

				if (moveCombinedFragmentCommand != null) {
					compoundCommandList.add(moveCombinedFragmentCommand);
				}

				// Register the move operation for status management.
				AdoneCombinedFragmentEpStatusManager.getInstance().registerMoveMode((CombinedFragmentEditPart) moveTargetCmbFrgEp);

			} else {

				// For other lifelines, adjust the width of the CombinedFragment.
				if (!AdoneCombinedFragmentEpStatusManager.getInstance().isRegistered((CombinedFragmentEditPart) moveTargetCmbFrgEp)) {

					Rectangle cmbFrgBounds = moveTargetCmbFrgEp.getFigure().getBounds().getCopy();

					// Adjust the width without changing the X position.
					cmbFrgBounds.width += changeBoundsRequest.getMoveDelta().x;

					Command resizeCombinedFragmentCommand = createChangeConstraintCommand(
							moveTargetCmbFrgEp,
							translateToModelConstraint(cmbFrgBounds));
					if (resizeCombinedFragmentCommand != null) {
						compoundCommandList.add(resizeCombinedFragmentCommand);
					}

				}

			}
		}

		return compoundCommandList;
	}

	/**
	 * Creates a list of commands to resize child CombinedFragmentEditParts based on a given ChangeBoundsRequest for a LifelineEditPart.
	 * This method focuses on resizing or moving CombinedFragmentEditParts that are graphically related to the target lifeline.
	 *
	 * @param changeBoundsRequest
	 *            The request that contains information about how the lifeline is being resized or moved.
	 * @param targetLifelineEp
	 *            The target lifeline edit part that is being resized or moved.
	 * @return A list of Command objects that, when executed, will resize or move the child CombinedFragmentEditParts accordingly.
	 */
	private List<Command> getResizeChildCombinedFragmentEditPart(ChangeBoundsRequest changeBoundsRequest, LifelineEditPart targetLifelineEp) {
		List<Command> compoundCommandList = new ArrayList<>();

		// Find all CombinedFragmentEditParts that are covered by the parent lifeline.
		List<GraphicalEditPart> coveredCmbFrgEpList = findGraphicallyCoveredCombinedFragments(targetLifelineEp);

		// Process already covered Combined Fragments for resizing
		for (GraphicalEditPart moveTargetCmbFrgEp : coveredCmbFrgEpList) {
			Rectangle cmbFrgBounds = moveTargetCmbFrgEp.getFigure().getBounds().getCopy();
			cmbFrgBounds.width += changeBoundsRequest.getMoveDelta().x; // Adjust the width without changing the X position.
			Command resizeCombinedFragmentCommand = createChangeConstraintCommand(moveTargetCmbFrgEp, translateToModelConstraint(cmbFrgBounds));
			if (resizeCombinedFragmentCommand != null) {
				compoundCommandList.add(resizeCombinedFragmentCommand);
			}
		}

		// Retrieve the center x position of the target lifeline
		Rectangle lifelineBounds = targetLifelineEp.getFigure().getBounds().getCopy();
		int lifelineCenterX = lifelineBounds.x + (lifelineBounds.width / 2);

		// Process Combined Fragments that start from the right side of the lifeline center
		for (GraphicalEditPart ep : AdoneSequenceUtil.getAllEditParts(targetLifelineEp)) {
			if (ep instanceof CombinedFragmentEditPart && !coveredCmbFrgEpList.contains(ep)) {
				Rectangle epBounds = ep.getFigure().getBounds().getCopy();
				// Check if the CombinedFragment starts from the right of the lifeline's center
				if (epBounds.x >= lifelineCenterX) {
					ChangeBoundsRequest requestForCombinedFrgEp = new ChangeBoundsRequest();
					requestForCombinedFrgEp.setEditParts(ep);
					requestForCombinedFrgEp.setMoveDelta(changeBoundsRequest.getMoveDelta());
					requestForCombinedFrgEp.setType(RequestConstants.REQ_MOVE_CHILDREN);

					Command moveCombinedFragmentCommand = createChangeConstraintCommand(ep, translateToModelConstraint(getConstraintFor(requestForCombinedFrgEp, ep)));
					if (moveCombinedFragmentCommand != null) {
						compoundCommandList.add(moveCombinedFragmentCommand);
					}
				}
			}
		}

		return compoundCommandList;
	}


	/**
	 * @param parentLifelineEp
	 * @return
	 */
	private List<GraphicalEditPart> findGraphicallyCoveredCombinedFragments(LifelineEditPart targetLifelineEp) {
		List<GraphicalEditPart> relatedCombinedFragments = new ArrayList<>();

		// Retrieve the root EditPart of the diagram.
		EditPart root = targetLifelineEp.getRoot();

		// Find all CombinedFragmentEditParts in the diagram.
		List<GraphicalEditPart> allCombinedFragments = findAllInteractionFragmentEditParts(root);

		// Retrieve the graphical bounds of the target lifeline and create a narrow rectangle along its center line
		IFigure lifelineFigure = targetLifelineEp.getFigure();
		Rectangle lifelineBounds = lifelineFigure.getBounds().getCopy();
		lifelineFigure.translateToAbsolute(lifelineBounds);

		// Create a narrow rectangle to represent the center line of the lifeline
		Rectangle centerLine = new Rectangle(
				lifelineBounds.getCenter().x, lifelineBounds.y,
				1, lifelineBounds.height);

		// Check each combined fragment to see if it graphically intersects with the lifeline's center line.
		for (GraphicalEditPart combinedFragment : allCombinedFragments) {
			IFigure fragmentFigure = combinedFragment.getFigure();
			Rectangle fragmentBounds = fragmentFigure.getBounds().getCopy();
			fragmentFigure.translateToAbsolute(fragmentBounds);

			if (centerLine.intersects(fragmentBounds)) {
				relatedCombinedFragments.add(combinedFragment);
			}
		}

		return relatedCombinedFragments;
	}



	/**
	 * Retrieves the width of a specified lifeline within a sequence diagram. This method is crucial for understanding
	 * the spatial characteristics of lifelines and aids in layout calculations, collision detection, and rendering optimizations.
	 *
	 * @param lifelineEditPart
	 *            The graphical edit part representing the lifeline whose width is to be determined.
	 * @return The width of the lifeline, derived from the graphical bounds of the lifeline's figure. This value is used
	 *         in various layout calculations to ensure that lifelines are properly spaced and aligned within the diagram.
	 */
	private int getLifelineWidth(GraphicalEditPart lifelineEditPart) {
		// Obtain the bounds of the lifeline's figure.
		Rectangle bounds = lifelineEditPart.getFigure().getBounds();
		// Return the width from the lifeline's bounds.
		return bounds.width;
	}

	/**
	 * Calculates the available space for moving a lifeline within a sequence diagram, taking into account the positions
	 * of adjacent lifelines. This method ensures that the lifeline movement does not overlap with other lifelines and maintains
	 * appropriate spacing, contributing to the visual clarity and organization of the diagram.
	 *
	 * @param changeBoundsRequest
	 *            The request containing the target location for the lifeline movement.
	 * @param orderChangeTargetEp
	 *            The LifelineEditPart that is being moved.
	 * @return The calculated space available for the movement. If the target lifeline is moving towards the beginning of the diagram,
	 *         it returns the space from the diagram's start offset to the moving target. If it's moving towards the end, or between two lifelines,
	 *         it calculates the space between adjacent lifelines. Returns Integer.MAX_VALUE if there is ample space without specific constraints.
	 */
	private int calculateSpaceBetweenLifelines(ChangeBoundsRequest changeBoundsRequest, LifelineEditPart orderChangeTargetEp) {

		// Retrieve the target x-coordinate for movement.
		int movingX = changeBoundsRequest.getLocation().x;

		// Initial offset where the first LifelineEditPart starts, relative to the parent.
		int startOffset = 30;

		// Find the closest lifelines to the moving target's x-coordinate.
		LifelineEditPart closestPreviousLifeline = null;
		int closestPreviousDistance = Integer.MAX_VALUE;
		LifelineEditPart closestNextLifeline = null;
		int closestNextDistance = Integer.MAX_VALUE;

		List<LifelineEditPart> lifelines = AdoneSequenceUtil.getAllLifelineEditParts(orderChangeTargetEp);

		for (LifelineEditPart lifelineEp : lifelines) {

			if (lifelineEp.equals(orderChangeTargetEp)) {
				continue; // Skip the moving target itself.
			}

			Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(lifelineEp);

			int distance = Math.abs(bounds.x - movingX);

			// Determine the closest previous and next lifelines based on their distance to the moving x-coordinate.
			if (bounds.left() < movingX && distance < closestPreviousDistance) {
				closestPreviousLifeline = lifelineEp;
				closestPreviousDistance = distance;
			} else if (bounds.x > movingX && distance < closestNextDistance) {
				closestNextLifeline = lifelineEp;
				closestNextDistance = distance;
			}
		}

		// Calculate the space between the closest previous and next lifelines, or to the diagram boundaries.
		if (closestPreviousLifeline != null && closestNextLifeline != null) {
			return calculateSpaceBetweenLifelines(closestPreviousLifeline, closestNextLifeline);
		} else if (closestPreviousLifeline == null && closestNextLifeline != null) {
			// 맨 앞의 경우, 첫 번째 LifelineEditPart가 시작하는 위치를 반환합니다.
			// If there's no previous lifeline, calculate space from the start offset to the moving target.
			return startOffset - movingX;
		} else if (closestPreviousLifeline != null) {
			// previousLifeline이 있고 nextLifeline이 없는 경우, previousLifeline과의 거리를 반환합니다.
			// If there's no next lifeline, calculate space from the moving target to the closest previous lifeline.
			return movingX - closestPreviousLifeline.getFigure().getBounds().right();
		}

		// Assume ample space if there are no specific constraints (e.g., last in the sequence).
		return Integer.MAX_VALUE;
	}

	/**
	 * Calculates the space between two adjacent lifelines in a sequence diagram. This method is crucial for
	 * managing the layout and ensuring appropriate spacing between lifelines, which is essential for readability
	 * and clarity in sequence diagrams. By determining the distance between the right edge of a previous lifeline
	 * and the left edge of the next lifeline, this method provides the exact spacing that separates them.
	 *
	 * @param previousLifeline
	 *            The LifelineEditPart representing the lifeline immediately before the next lifeline.
	 * @param nextLifeline
	 *            The LifelineEditPart representing the lifeline immediately after the previous lifeline.
	 * @return The calculated space (in pixels) between the right edge of the previous lifeline and the left edge of the next lifeline.
	 */
	private int calculateSpaceBetweenLifelines(LifelineEditPart previousLifeline, LifelineEditPart nextLifeline) {
		// Retrieve the right boundary of the previous lifeline.
		Rectangle previousBounds = previousLifeline.getFigure().getBounds();
		int previousRightEdge = previousBounds.right();

		// Retrieve the left boundary of the next lifeline.
		Rectangle nextBounds = nextLifeline.getFigure().getBounds();
		int nextLeftEdge = nextBounds.x;

		// Calculate and return the space between the two lifelines.
		return nextLeftEdge - previousRightEdge;
	}


	/**
	 * Determines if a given lifeline edit part is the first covered lifeline within a combined fragment.
	 * This method iterates through all lifelines covered by a specified combined fragment and identifies
	 * the one positioned most to the left within the diagram. The position is determined based on the X coordinate
	 * of the lifeline's bounds, ensuring an accurate representation of the visual layout.
	 *
	 * The comparison between the given lifeline edit part and the identified first covered lifeline edit part
	 * allows us to ascertain whether the specified lifeline edit part is indeed the first lifeline that the
	 * combined fragment covers, from a visual perspective. This method is crucial for understanding the
	 * sequencing and relationships of lifelines within combined fragments, which is fundamental to analyzing
	 * interactions in sequence diagrams.
	 *
	 * @param cmbFrgEp
	 *            The CombinedFragmentEditPart representing the combined fragment in question.
	 * @param coveredLifelineEditPart
	 *            The LifelineEditPart to check if it is the first covered lifeline.
	 * @return true if the specified lifeline edit part is the first covered lifeline, false otherwise.
	 */
	private boolean isFirstCoveredLifelineEditPart(CombinedFragmentEditPart cmbFrgEp, LifelineEditPart coveredLifelineEditPart) {
		// Retrieve all lifeline edit parts covered by the combined fragment.
		List<LifelineEditPart> coveredLifelines = AdoneSequenceUtil.getCoveredLifelinesByModel(cmbFrgEp);

		// Find the lifeline edit part located most to the left.
		LifelineEditPart firstLifeline = null;
		int minX = Integer.MAX_VALUE;

		for (LifelineEditPart lifeline : coveredLifelines) {
			Rectangle bounds = lifeline.getFigure().getBounds();
			if (bounds.x < minX) {
				minX = bounds.x;
				firstLifeline = lifeline;
			}
		}

		// Check if the given lifeline edit part is the first covered lifeline.
		return firstLifeline == coveredLifelineEditPart;
	}

	/**
	 * Identifies and collects all CombinedFragmentEditParts that cover a specified lifeline within a sequence diagram.
	 * This method traverses the entire diagram, starting from the root edit part, to find all instances of
	 * CombinedFragmentEditParts. It then determines which of these combined fragments semantically cover the given
	 * lifeline, leveraging the UML model's relationships. The determination of coverage is based on the semantic
	 * relationship between the combined fragment and the lifeline, ensuring accurate reflection of the UML model's
	 * intent and structure.
	 *
	 * @param nextLifelineEditPart
	 *            The lifeline edit part for which covered combined fragments are being sought.
	 * @return A list of GraphicalEditParts representing combined fragments that cover the specified lifeline.
	 */
	private List<GraphicalEditPart> findCoveredCombinedFragments(LifelineEditPart nextLifelineEditPart) {

		List<GraphicalEditPart> relatedCombinedFragments = new ArrayList<>();

		// Retrieve the root EditPart of the diagram.
		EditPart root = nextLifelineEditPart.getRoot();

		// Find all CombinedFragmentEditParts in the diagram.
		List<GraphicalEditPart> allCombinedFragments = findAllInteractionFragmentEditParts(root);

		// Check each combined fragment to see if it covers the specified lifeline based on semantic relationships.
		for (GraphicalEditPart combinedFragment : allCombinedFragments) {
			if (isCoveringBySemanticRelationship((CombinedFragmentEditPart) combinedFragment, nextLifelineEditPart)) {
				relatedCombinedFragments.add(combinedFragment);
			}
		}

		return relatedCombinedFragments;
	}

	/**
	 * Determines whether a given lifeline is covered by a combined fragment based on their semantic relationships.
	 * This method checks if the lifeline, represented by the specified LifelineEditPart, is included in the list
	 * of lifelines (covereds) that the combined fragment, represented by the specified CombinedFragmentEditPart,
	 * interacts with. The semantic elements of both the combined fragment and the lifeline are resolved to check
	 * their direct relationship, specifically if the lifeline is one of the elements covered by the combined fragment.
	 *
	 * @param combinedFragmentEp
	 *            The edit part of the combined fragment whose coverage is being checked.
	 * @param nextLifelineEditPart
	 *            The edit part of the lifeline that might be covered by the combined fragment.
	 * @return true if the lifeline is covered by the combined fragment according to their semantic relationship,
	 *         otherwise false.
	 */
	private boolean isCoveringBySemanticRelationship(CombinedFragmentEditPart combinedFragmentEp, LifelineEditPart nextLifelineEditPart) {

		// Resolve the semantic elements for the combined fragment and the lifeline.
		CombinedFragment cf = (CombinedFragment) combinedFragmentEp.resolveSemanticElement();
		Lifeline lf = (Lifeline) nextLifelineEditPart.resolveSemanticElement();

		// Check if the lifeline is included in the list of covereds by the combined fragment.
		if (cf.getCovereds().contains(lf)) {
			return true;
		}

		return false;
	}

	/**
	 * Recursively searches for and collects all interaction fragment edit parts within a given root edit part.
	 * This method focuses on identifying combined fragments, which represent interaction fragments in UML sequence diagrams.
	 * By exploring the hierarchy of edit parts starting from the specified root, it gathers a comprehensive list of
	 * combined fragment edit parts that are critical for representing interaction constructs such as alternative,
	 * option, and loop constructs in a sequence diagram.
	 *
	 * @param root
	 *            The starting point for the search, typically the root of the sequence diagram or a specific container
	 *            within the diagram from which to begin the search.
	 * @return A list of graphical edit parts representing all found interaction fragments within the specified root's
	 *         hierarchy. This list primarily includes combined fragments, which are key to structuring complex interactions
	 *         in sequence diagrams.
	 */
	private List<GraphicalEditPart> findAllInteractionFragmentEditParts(EditPart root) {
		List<GraphicalEditPart> allInteractionFragments = new ArrayList<>();

		// Directly add the root if it's an instance of CombinedFragmentEditPart,
		// indicating it represents an interaction fragment.
		if (root instanceof CombinedFragmentEditPart) {
			allInteractionFragments.add((GraphicalEditPart) root);
		}

		// Recursively explore and add all interaction fragments found within the children of the root.
		for (Object child : root.getChildren()) {
			if (child instanceof EditPart) {
				allInteractionFragments.addAll(findAllInteractionFragmentEditParts((EditPart) child));
			}
		}

		return allInteractionFragments;
	}

	/**
	 * Recursively finds all relevant graphical edit parts within a sequence diagram that could potentially
	 * be targets or containers for creating new Behavior Execution Specifications. This includes lifelines,
	 * combined fragments, and existing behavior executions, capturing the structural elements where
	 * behaviors could logically occur or be initiated.
	 *
	 * @param root
	 *            The starting point edit part from which to begin the search. This could be the root of
	 *            the diagram or a substructure like a lifeline or a combined fragment.
	 * @return A list of graphical edit parts that are relevant for the creation of new behavior executions.
	 *         This list includes lifelines, combined fragments, behavior executions, and messages, as these
	 *         elements can host or initiate new behavior executions.
	 */
	private List<GraphicalEditPart> findAllEditPartsForCreatingBehExe(EditPart root) {
		List<GraphicalEditPart> allInteractionFragments = new ArrayList<>();

		// Add the current edit part if it's a CombinedFragmentEditPart or a LifelineEditPart,
		// capturing the primary elements where behavior executions can be directly associated.
		if (root instanceof CombinedFragmentEditPart) {
			allInteractionFragments.add((GraphicalEditPart) root);
		} else if (root instanceof LifelineEditPart) {

			// For lifelines, include both the lifeline itself and any connected messages,
			// as behavior executions often result from interactions represented by messages.
			LifelineEditPart lifeline = (LifelineEditPart) root;
			for (Object graphicalEditPart : lifeline.getSourceConnections()) {
				if (graphicalEditPart instanceof AbstractMessageEditPart) {
					allInteractionFragments.add((GraphicalEditPart) graphicalEditPart);
				}
			}

			allInteractionFragments.add((GraphicalEditPart) root);

		} else if (root instanceof BehaviorExecutionSpecificationEditPart) {
			// Directly add behavior execution specifications, as new executions may be related or nested.
			allInteractionFragments.add((GraphicalEditPart) root);
		}

		// Recursively process child edit parts to find all potential targets for new behavior executions.
		for (Object child : root.getChildren()) {
			if (child instanceof EditPart) {
				allInteractionFragments.addAll(findAllEditPartsForCreatingBehExe((EditPart) child));
			}
		}

		return allInteractionFragments;
	}

	/**
	 * Displays visual feedback during layout adjustments within a sequence diagram, specifically when moving a lifeline.
	 * This method is invoked in response to a change bounds request and shows a feedback figure at the closest
	 * insertion point to indicate where the lifeline or combined fragment will be placed if the move operation is completed.
	 *
	 * @param request
	 *            The request triggering the layout feedback, typically a change bounds request for moving elements.
	 */
	@Override
	protected void showLayoutTargetFeedback(final Request request) {

		if (request instanceof ChangeBoundsRequest) {

			// Check if element order change mode is active
			if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {

				final ChangeBoundsRequest changeBoundsRequest = (ChangeBoundsRequest) request;
				EditPart targetEditPart = (EditPart) changeBoundsRequest.getEditParts().get(0);

				// Provide feedback for moving a lifeline
				if (targetEditPart instanceof LifelineEditPart) {

					// Remove any existing layout feedback
					eraseLayoutTargetFeedback();

					// Compute the closest insertion point to the current location
					final LifelineMoveInsertionPoint insertionPoint = computeClosestInsertionPoint(changeBoundsRequest.getLocation().x);

					// Exit if no suitable insertion point is found
					if (insertionPoint == null) {
						return;
					}

					// If the insertion point location differs from the current location, show feedback
					if (insertionPoint.location != changeBoundsRequest.getLocation().x) {

						// Get the bounds of the target edit part and translate to absolute coordinates
						final Rectangle containerBounds = new Rectangle(((LifelineEditPart) targetEditPart).getFigure().getBounds()).getCopy();
						getHostFigure().translateToAbsolute(containerBounds);
						final int layoutFeedbackHeight = containerBounds.height;

						// Create and display a feedback figure at the insertion point
						final Rectangle feedbackBounds = new Rectangle(insertionPoint.getLocation(), containerBounds.y, 3, layoutFeedbackHeight);
						this.layoutFeedbackFigure = createLayoutTargetFeedbackFigure(feedbackBounds);

						// Adjust the move delta and store the insertion location for later use
						changeBoundsRequest.setMoveDelta(new Point(changeBoundsRequest.getMoveDelta().x, 0));
						changeBoundsRequest.getExtendedData().put(INSERTION_LOCATION, insertionPoint.location);
						return;
					}

				} else if (targetEditPart instanceof CombinedFragmentEditPart) {
					// Future support for combined fragment feedback can be implemented here
				}
			}
		}

		// Call the superclass implementation for other cases
		super.showLayoutTargetFeedback(request);

	}

	/**
	 * Computes the closest vertical insertion point for placing or moving a combined fragment or message relative to a target edit part
	 * in a sequence diagram. This method evaluates the positions of existing combined fragments and messages to determine the most suitable
	 * insertion point based on the specified vertical offset (Y position).
	 *
	 * @param targetEditPart
	 *            The target edit part around which the insertion point is to be calculated. This can be a combined fragment or a message.
	 * @param offsetY
	 *            The vertical offset (Y position) within the sequence diagram where the new insertion point is sought.
	 * @return A {@link LifelineMoveInsertionPoint} representing the closest insertion point to the specified offset, or null if no suitable point is found.
	 *         The method calculates this by analyzing the vertical positions of existing elements and identifying the point with the minimum distance
	 *         to the specified offsetY.
	 */
	protected LifelineMoveInsertionPoint computeClosestInsertionPointForCombinedFragment(EditPart targetEditPart, final int offsetY) {
		List<GraphicalEditPart> relevantEditParts = new ArrayList<>();

		// Convert the bounds of the target edit part to absolute coordinates to determine its position within the diagram.
		Rectangle targetBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((GraphicalEditPart) targetEditPart);

		// Filter out relevant edit parts from the edit part registry that are not contained within the target edit part's bounds.
		for (Object value : getHost().getViewer().getEditPartRegistry().values()) {
			if (value instanceof CombinedFragmentEditPart || value instanceof AbstractMessageEditPart) {
				GraphicalEditPart editPart = (GraphicalEditPart) value;

				if (editPart.equals(targetEditPart)) {
					continue; // Skip the same edit part.
				}

				// Convert the bounds of each edit part to absolute coordinates.
				Rectangle editPartBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(editPart);

				// Add to the list if not contained within the target edit part's bounds.
				if (!targetBounds.contains(editPartBounds)) {
					relevantEditParts.add(editPart);
				}
			}
		}

		// If no relevant edit parts are found, return null.
		if (relevantEditParts.isEmpty()) {
			return null;
		}

		// Calculate insertion points for the collected relevant edit parts.
		final List<LifelineMoveInsertionPoint> insertionPoints = computeInsertionPointsForCombinedFragment(relevantEditParts);

		// Find the closest insertion point to the specified offsetY by comparing distances.
		final TreeMap<Integer, LifelineMoveInsertionPoint> distanceMap = new TreeMap<>();
		for (final LifelineMoveInsertionPoint insertionPoint : insertionPoints) {
			final int distance = Math.abs(offsetY - insertionPoint.getLocation());
			distanceMap.put(Integer.valueOf(distance), insertionPoint);
		}
		// Return the insertion point with the minimum distance to offsetY.
		return distanceMap.values().iterator().next();
	}


	/**
	 * Computes potential vertical insertion points for a combined fragment within a sequence diagram based on the layout of
	 * existing graphical edit parts. This method identifies the optimal positions for inserting new combined fragments or
	 * other elements vertically by analyzing the space between existing elements.
	 *
	 * @param editParts
	 *            A list of {@link GraphicalEditPart} objects representing the children elements within the combined fragment's scope.
	 * @return A list of {@link LifelineMoveInsertionPoint} objects representing the calculated vertical insertion points.
	 *         Each insertion point is determined based on the vertical midpoints between consecutive edit parts, as well as positions
	 *         before the first and after the last edit parts to accommodate new insertions.
	 */
	protected List<LifelineMoveInsertionPoint> computeInsertionPointsForCombinedFragment(final List<GraphicalEditPart> editParts) {
		final int size = editParts.size();
		final List<LifelineMoveInsertionPoint> insertionPoints = new ArrayList<>();
		Rectangle previousBounds = null;
		for (int i = 0; i < size; i++) {
			final GraphicalEditPart editPart = editParts.get(i);
			// Calculate the absolute bounds for each edit part to determine its position within the diagram.
			final Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(editPart);

			if (previousBounds != null) {
				// Calculate the midpoint between the bottom of the previous edit part and the top of the current one.
				int middleY = previousBounds.bottom() + (bounds.y - previousBounds.bottom()) / 2;
				// Add an insertion point at the calculated midpoint.
				insertionPoints.add(new LifelineMoveInsertionPoint(i, middleY));
			} else {
				// For the first edit part, calculate an insertion point just before it.
				int beforeFirstY = bounds.y - (bounds.height / 4);
				insertionPoints.add(new LifelineMoveInsertionPoint(i, beforeFirstY));
			}

			// For the last edit part, calculate an insertion point just after it.
			if (i == size - 1) {
				int afterLastY = bounds.bottom() + (bounds.height / 4);
				insertionPoints.add(new LifelineMoveInsertionPoint(i + 1, afterLastY));
			}

			previousBounds = bounds;
		}
		return insertionPoints;
	}


	/**
	 * Computes the closest insertion point relative to a given offset within the sequence diagram.
	 * This method is essential for determining the most appropriate position for inserting or moving a lifeline
	 * based on the user's cursor location during drag-and-drop operations. It leverages the layout of existing
	 * lifelines to calculate potential insertion points and then identifies which of these points is closest to
	 * the specified offset, optimizing the placement of elements for user interactions.
	 *
	 * @param offset
	 *            The horizontal offset position (typically, the X coordinate) where the user intends to insert or move a lifeline.
	 * @return A {@link LifelineMoveInsertionPoint} object representing the closest potential insertion point to the given offset,
	 *         or null if there are no children (lifelines) from which to compute insertion points.
	 */
	protected LifelineMoveInsertionPoint computeClosestInsertionPoint(final int offset) {

		List<GraphicalEditPart> childrenObject = new ArrayList<>();

		// Collect all lifeline edit parts from the sequence diagram.
		for (final Object child : AdoneSequenceUtil.getAllLifelineEditParts(getHost())) {

			if (child instanceof LifelineEditPart) {
				childrenObject.add((GraphicalEditPart) child);
			}
		}

		final List<GraphicalEditPart> children = childrenObject;
		// Return null if there are no lifelines to compute insertion points from.
		if (children.isEmpty()) {
			return null;
		}

		// Compute potential insertion points based on the current layout of lifelines.
		final List<LifelineMoveInsertionPoint> insertionPoints = computeInsertionPoints(children);

		// Use a TreeMap to sort insertion points by their distance to the given offset, facilitating the search for the closest one.
		final TreeMap<Integer, LifelineMoveInsertionPoint> distanceMap = new TreeMap<>();
		for (final LifelineMoveInsertionPoint insertionPoint : insertionPoints) {
			final int distance = Math.abs(offset - insertionPoint.getLocation());
			distanceMap.put(Integer.valueOf(distance), insertionPoint);
		}

		// Return the insertion point with the smallest distance to the given offset, i.e., the closest one.
		return distanceMap.values().iterator().next();
	}

	/**
	 * Erases visual feedback for layout target based on the given request.
	 * This method delegates the task to a more general eraseLayoutTargetFeedback method without parameters,
	 * ensuring that any visual feedback related to layout targeting is removed once it's no longer needed,
	 * such as after a drag-and-drop operation or upon completion of a resize action.
	 *
	 * @param request
	 *            The request for which layout target feedback should be erased. The specific type of request
	 *            can dictate different feedback mechanisms, but this implementation generalizes feedback removal.
	 */
	@Override
	protected void eraseLayoutTargetFeedback(final Request request) {
		// Call the generalized method to erase layout target feedback.
		eraseLayoutTargetFeedback();
	}


	/**
	 * Erases the visual feedback for layout target areas within the sequence diagram editor.
	 * This method is called when the drag-and-drop operation is completed or cancelled,
	 * ensuring that the temporary visual cues provided to assist the user are removed,
	 * returning the diagram to its original state without the feedback figure.
	 */
	protected void eraseLayoutTargetFeedback() {
		if (this.layoutFeedbackFigure != null) {
			removeFeedback(this.layoutFeedbackFigure);
			this.layoutFeedbackFigure = null;
		}
	}

	/**
	 * Creates a visual feedback figure for layout target areas within the sequence diagram editor.
	 * This feedback figure is represented by a rectangle that highlights the area where an element
	 * (e.g., a lifeline or a combined fragment) could be placed or moved to. The visual feedback assists users
	 * in understanding the potential drop locations during drag-and-drop operations and enhances the user experience
	 * by providing clear visual cues.
	 *
	 * @param bounds
	 *            The rectangle bounds that define the size and position of the feedback figure on the diagram.
	 * @return The created {@link IFigure}, which is a {@link RectangleFigure} styled to visually indicate the target area.
	 */
	protected IFigure createLayoutTargetFeedbackFigure(final Rectangle bounds) {
		final RectangleFigure rFigure = new RectangleFigure();
		rFigure.setForegroundColor(ColorConstants.darkBlue);
		rFigure.setBackgroundColor(ColorConstants.lightBlue);
		rFigure.setBounds(bounds);
		rFigure.validate();
		addFeedback(rFigure);

		return rFigure;
	}

	/**
	 * Computes potential insertion points between lifelines based on the graphical layout of child edit parts within a sequence diagram.
	 * It calculates insertion points by determining the midpoints between adjacent lifelines and also includes insertion points at the
	 * beginning and end of the lifeline sequence. This is critical for enabling accurate drop locations during drag-and-drop operations.
	 *
	 * @param children
	 *            The list of graphical edit parts representing lifelines in the sequence diagram.
	 * @return A list of {@link LifelineMoveInsertionPoint} objects, each representing a potential insertion point within the sequence of lifelines.
	 */
	protected List<LifelineMoveInsertionPoint> computeInsertionPoints(final List<GraphicalEditPart> children) {
		final int size = children.size();
		final List<LifelineMoveInsertionPoint> insertionPoints = new ArrayList<>();
		Rectangle previousBounds = null;
		for (int i = 0; i < size; i++) {
			final GraphicalEditPart childEditPart = children.get(i);

			Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(childEditPart);

			// Adjust bounds for any potential scroll offset, ensuring insertion points are accurately placed within the viewport. (2024-02-02)
			if (getHost().getViewer().getControl() instanceof FigureCanvas) {
				FigureCanvas canvas = (FigureCanvas) getHost().getViewer().getControl();
				Point scrollOffset = canvas.getViewport().getViewLocation();
				bounds = bounds.getTranslated(scrollOffset);
			}

			if (previousBounds != null) {
				// -1 을 왜 해줘야 정 중앙에 위치하는데 이유를 모르겠음. 아마도 스크롤바의 영향인듯 ? (2024-02-02)
				// Calculate the midpoint between the right edge of the previous figure and the left edge of the current figure.
				int middleX = previousBounds.right() + (bounds.x - previousBounds.right()) / 2 - 1;
				insertionPoints.add(new LifelineMoveInsertionPoint(i, middleX));
			} else {
				// For the first figure, calculate the insertion point to the left of it.
				int beforeFirstX = bounds.x - 10; // A fixed offset is used to slightly move the insertion point to the left.
				insertionPoints.add(new LifelineMoveInsertionPoint(i, beforeFirstX));
			}

			// For the last figure, calculate the insertion point to the right of it.
			if (i == size - 1) {
				// A fixed offset is used to slightly move the insertion point to the right.
				int afterLastX = bounds.right() + 10;
				insertionPoints.add(new LifelineMoveInsertionPoint(i + 1, afterLastX));
			}

			previousBounds = bounds;// Update previousBounds for the next iteration.
		}
		return insertionPoints;
	}

	/**
	 * Constructs a command to resize selected children within a sequence diagram. This method supports resizing
	 * for Lifeline and Combined Fragment edit parts, applying specific constraints and validations to ensure
	 * the resize operation is permissible. For Combined Fragments, it considers directional constraints and checks
	 * against exceeding boundary limits to prevent overlapping or misalignment within the diagram.
	 *
	 * @param request
	 *            The change bounds request containing details of the resize operation, including direction and size delta.
	 * @return A command to execute the resize operation, or null if the operation is not executable due to constraints.
	 */
	@Override
	protected Command getResizeChildrenCommand(ChangeBoundsRequest request) {

		CompoundCommand resizeCompoundCommand = new CompoundCommand();
		List children = request.getEditParts();
		if (children.size() != 1) {
			return null;
		}

		GraphicalEditPart resizeTargetEp = (GraphicalEditPart) children.get(0);
		Command resizeTargetCommand = null;

		if (resizeTargetEp instanceof LifelineEditPart) {

			// Check if the target edit part is a Lifeline to apply specific resize logic.

			if (request.getResizeDirection() == PositionConstants.SOUTH) {
				// If resizing towards the south, adjust the Lifeline's height accordingly.
				resizeTargetCommand = createChangeConstraintCommand(resizeTargetEp, translateToModelConstraint(getConstraintFor(request, resizeTargetEp)));
			} else if (request.getResizeDirection() == PositionConstants.EAST || request.getResizeDirection() == PositionConstants.WEST) {
				// If resizing east or west, adjust the Lifeline's width and possibly reposition related elements.
				resizeTargetCommand = createLifelineWidthResizeCommand(resizeTargetEp, request);
			}

		} else if (resizeTargetEp instanceof AdoneCombinedFragmentEditPart) {
			// Combined Fragment specific constraints and checks.

			// Check for valid resize directions: West, East, or South.
			if (!(request.getResizeDirection() == PositionConstants.WEST || request.getResizeDirection() == PositionConstants.EAST || request.getResizeDirection() == PositionConstants.SOUTH)) {
				return UnexecutableCommand.INSTANCE;
			}

			// Handle resizing towards the West.
			if (request.getResizeDirection() == PositionConstants.WEST) {

				if (isExceedingLeftResizeLimit(request, resizeTargetEp)) {
					return UnexecutableCommand.INSTANCE;
				}

				if (isExceedingLeftRightResizeLimit(request, resizeTargetEp)) {
					return UnexecutableCommand.INSTANCE;
				}

			}

			// Generate the resize command based on the new constraints.
			resizeTargetCommand = createChangeConstraintCommand(resizeTargetEp, translateToModelConstraint(getConstraintFor(request, resizeTargetEp)));

			// Handle resizing towards the East.
			if (request.getResizeDirection() == PositionConstants.EAST) {

				// Block if resizing east exceeds the right limit.
				if (isExceedingRightResizeLimit(request, resizeTargetEp)) {
					return UnexecutableCommand.INSTANCE;
				}

				// Adjust covered lifelines if needed.
				List<Lifeline> changedCoveredLiflines = this.getAddedLifelinesToResizeCombinedFragment(request, resizeTargetEp);

				if (!changedCoveredLiflines.isEmpty()) {
					// Add commands for adjusting covered lifelines.
					AdoneAddCoveredLifelineToCombinedFragment addCoveredLifelineCommand = new AdoneAddCoveredLifelineToCombinedFragment(((IGraphicalEditPart) getHost()).getEditingDomain(), request, resizeTargetEp, changedCoveredLiflines);
					resizeCompoundCommand.add(new GMFtoGEFCommandWrapper(addCoveredLifelineCommand));
				}

			} else if (request.getResizeDirection() == PositionConstants.SOUTH) {
				// Handle specific case where the target is an Interaction Operand.

				if (request.getExtendedData().get("ResizeTargetInteractionOperand") != null) {

					InteractionOperandEditPart resizeTargetOperand = (InteractionOperandEditPart) request.getExtendedData().get("ResizeTargetInteractionOperand");

					if (isExceedingUpperResizeLimit(request, resizeTargetOperand)) {
						// Block if resizing south exceeds the upper limit for an Interaction Operand.
						return UnexecutableCommand.INSTANCE;
					}

				} else {

					if (isExceedingUpperResizeLimit(request, resizeTargetEp)) {
						// Block if resizing south exceeds the upper limit for the Combined Fragment.
						return UnexecutableCommand.INSTANCE;
					}
				}

				// Check for minimum height requirement.
				Rectangle r = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(resizeTargetEp);

				if (r.height + request.getSizeDelta().height < 90) {
					// Block if the new height is less than the minimum required height.
					return UnexecutableCommand.INSTANCE;
				}

				// Calculate the delta for the Y-axis resize.
				int resizeDeltaY = request.getSizeDelta().height;

				// Generate and add commands for resizing Lifeline and first BES as needed.
				Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(resizeTargetEp, resizeDeltaY);

				if (resizeLifelineEpCommand != null) {
					resizeCompoundCommand.add(resizeLifelineEpCommand);
				}

				Command resizeFirstBesEp = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand(resizeTargetEp, resizeDeltaY);

				if (resizeFirstBesEp != null) {
					resizeCompoundCommand.add(resizeFirstBesEp);
				}

				// Adjust the parent Interaction Operand and move below edit parts as necessary.

				this.resizeParentInteractionOperand(request, resizeTargetEp, resizeCompoundCommand, resizeDeltaY);

				if (request.getExtendedData().get("ResizeTargetInteractionOperand") != null) {
					InteractionOperandEditPart resizeTargetOperand = (InteractionOperandEditPart) request.getExtendedData().get("ResizeTargetInteractionOperand");
					List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditParts(resizeTargetOperand, request);
					processAndMoveBelowEditParts(request, allBelowEditParts, resizeCompoundCommand, resizeTargetOperand);
				} else {
					List<GraphicalEditPart> allBelowEditParts = AdoneSequenceUtil.getAllBelowEditParts(resizeTargetEp, request);
					processAndMoveBelowEditParts(request, allBelowEditParts, resizeCompoundCommand, resizeTargetEp);
				}

			}

		} else if (resizeTargetEp instanceof AdoneBehaviorExecutionSpecificationEditPart) {
			// Future support for Behavior Execution Specification resize handling can be added here.
		} else {
			// do nothing
		}

		// If a valid resize command is created and can be executed, add it to the compound command.
		if (resizeTargetCommand != null && resizeTargetCommand.canExecute()) {
			resizeCompoundCommand.add(resizeTargetCommand);
			return resizeCompoundCommand.unwrap();
		} else {
			return null;
		}
	}

	/**
	 * Creates a command to resize the width of a lifeline and adjust the diagram accordingly.
	 * This includes resizing the targeted lifeline, moving child combined fragments, adjusting subsequent lifelines,
	 * and possibly resizing the diagram width to accommodate the changes.
	 *
	 * @param resizeTargetEp
	 *            The graphical edit part of the lifeline to be resized.
	 * @param request
	 *            The request containing the size delta and other information for the resize operation.
	 * @return A command that, when executed, will apply all necessary changes to resize the lifeline and adjust related components.
	 */
	private Command createLifelineWidthResizeCommand(GraphicalEditPart resizeTargetEp, ChangeBoundsRequest request) {

		CompoundCommand resizeCompoundCommand = new CompoundCommand();

		// Create command for changing Lifeline constraint and add it to compound command.
		Command changeLifelineConstraintCommand = createChangeConstraintCommand(resizeTargetEp, translateToModelConstraint(getConstraintFor(request, resizeTargetEp)));

		if (changeLifelineConstraintCommand != null) {
			resizeCompoundCommand.add(changeLifelineConstraintCommand);
		}

		ChangeBoundsRequest moveRequest = new ChangeBoundsRequest();
		moveRequest.setEditParts(request.getEditParts());
		moveRequest.setLocation(request.getLocation());
		Point moveDelta = new Point(request.getSizeDelta());
		moveRequest.setMoveDelta(moveDelta);
		moveRequest.setType(RequestConstants.REQ_MOVE_CHILDREN);

		// Process movement of child combined fragments along with the Lifeline.
		List<Command> moveChildCommand = this.getResizeChildCombinedFragmentEditPart(moveRequest, (LifelineEditPart) resizeTargetEp);
		for (Command command : moveChildCommand) {
			resizeCompoundCommand.add(command);
		}

		// Adjust the position of subsequent Lifelines to maintain spacing.
		List<LifelineEditPart> allNextLifelineEditPart = AdoneSequenceUtil.getAllNextLifelineEditParts((LifelineEditPart) resizeTargetEp);
		for (LifelineEditPart nextLifelineEditPart : allNextLifelineEditPart) {

			Command changeNextLifelineConstraintCommand = null;

			if (nextLifelineEditPart instanceof LifelineEditPart) {
				changeNextLifelineConstraintCommand = createChangeConstraintCommand(nextLifelineEditPart, translateToModelConstraint(getConstraintFor(moveRequest, nextLifelineEditPart)));
			}
			if (changeNextLifelineConstraintCommand != null) {
				resizeCompoundCommand.add(changeNextLifelineConstraintCommand);
			}

		}

		// Optionally resize diagram width if necessary for the movement.
		Command resizeDiagramWidthCommand = this.getResizeDiagramWidthByMoveLifelineCommand(moveRequest, allNextLifelineEditPart);
		if (resizeDiagramWidthCommand != null && resizeDiagramWidthCommand.canExecute()) {
			moveChildCommand.add(resizeDiagramWidthCommand);
		}

		return resizeCompoundCommand.unwrap();
	}

	/**
	 * Evaluates whether resizing a Combined Fragment exceeds left or right boundary limits based on the positions of
	 * covered lifelines and nested Combined Fragments within it. This method ensures that Combined Fragments do not
	 * resize beyond the extent of their covered elements, maintaining diagram integrity and spatial coherence.
	 *
	 * @param request
	 *            The change bounds request detailing the proposed resize dimensions.
	 * @param resizeTargetEp
	 *            The Combined Fragment edit part being resized.
	 * @return true if the resizing operation causes the Combined Fragment to exceed the left or right boundary limits
	 *         of covered lifelines or nested Combined Fragments, otherwise false.
	 */
	private boolean isExceedingLeftRightResizeLimit(ChangeBoundsRequest request, GraphicalEditPart resizeTargetEp) {

		if (!(resizeTargetEp instanceof CombinedFragmentEditPart)) {
			return false;
		}

		Map<?, ?> editPartRegistry = resizeTargetEp.getViewer().getEditPartRegistry();
		Rectangle targetBounds = getAbsoluteBounds(resizeTargetEp);

		Dimension resizeDimension = request.getSizeDelta();
		Rectangle newBounds = targetBounds.getCopy();
		newBounds.width += resizeDimension.width; // 너비 변경을 반영
		newBounds.x -= resizeDimension.width;

		// Find the center X coordinate of the leftmost lifeline covered by the Combined Fragment.
		int leftmostLifelineCenterX = Integer.MAX_VALUE;
		int leftmostChildCombinedFragmentX = Integer.MAX_VALUE;

		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				LifelineEditPart lifelinePart = (LifelineEditPart) value;
				// Covered된 LifelineEditPart만 확인
				if (AdoneSequenceUtil.isLifelineCoveredByCombinedFragment(lifelinePart, (CombinedFragmentEditPart) resizeTargetEp)) {
					Rectangle lifelineBounds = getAbsoluteBounds(lifelinePart);
					int centerX = lifelineBounds.x + lifelineBounds.width / 2;
					leftmostLifelineCenterX = Math.min(leftmostLifelineCenterX, centerX);
				}

			}

			if (value instanceof CombinedFragmentEditPart && value != resizeTargetEp) {
				CombinedFragmentEditPart childFragment = (CombinedFragmentEditPart) value;
				if (targetBounds.contains(getAbsoluteBounds(childFragment))) {
					Rectangle childBounds = getAbsoluteBounds(childFragment);
					leftmostChildCombinedFragmentX = Math.min(leftmostChildCombinedFragmentX, childBounds.x);
				}
			}
		}

		// 여유 공간을 -20으로 설정하여 왼쪽으로의 리사이즈 제한
		// Check against the leftmost lifeline and child Combined Fragment positions with a buffer margin.
		if (leftmostLifelineCenterX != Integer.MAX_VALUE && newBounds.x > leftmostLifelineCenterX - 20) {
			return true;
		}

		if (leftmostLifelineCenterX != Integer.MAX_VALUE && newBounds.x > leftmostChildCombinedFragmentX - 5) {
			return true;
		}

		return false;
	}

	/**
	 * Determines if the leftward resize operation on a Combined Fragment exceeds the left boundary limit.
	 * This method calculates the new bounds of the Combined Fragment considering the requested resize dimensions
	 * and checks these against the left boundary of the nearest parent Combined Fragment or the diagram's leftmost lifeline.
	 * It aims to prevent the Combined Fragment from overlapping or extending beyond permissible diagram boundaries or parent constraints.
	 *
	 * @param request
	 *            The change bounds request detailing the proposed resize dimensions.
	 * @param resizeTargetEp
	 *            The Combined Fragment edit part being resized.
	 * @return true if the leftward resizing action causes the target to exceed the left boundary limit of its nearest parent Combined Fragment
	 *         or the leftmost lifeline, otherwise false.
	 */
	private boolean isExceedingLeftResizeLimit(ChangeBoundsRequest request, GraphicalEditPart resizeTargetEp) {

		if (!(resizeTargetEp instanceof CombinedFragmentEditPart)) {
			return false;
		}

		Map<?, ?> editPartRegistry = resizeTargetEp.getViewer().getEditPartRegistry();
		CombinedFragmentEditPart nearestParentCombinedFragment = null;
		Rectangle targetBounds = getAbsoluteBounds(resizeTargetEp);

		Dimension resizeDimension = request.getSizeDelta();
		Rectangle newBounds = targetBounds.getCopy();
		newBounds.width += resizeDimension.width; // 너비 변경을 반영
		newBounds.x -= resizeDimension.width;

		// Check for the nearest parent Combined Fragment.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof CombinedFragmentEditPart && value != resizeTargetEp) {
				CombinedFragmentEditPart combinedFragmentEditPart = (CombinedFragmentEditPart) value;
				Rectangle parentBounds = getAbsoluteBounds(combinedFragmentEditPart);

				if (parentBounds.contains(targetBounds)) {
					if (nearestParentCombinedFragment == null || getAbsoluteBounds(nearestParentCombinedFragment).contains(parentBounds)) {
						nearestParentCombinedFragment = combinedFragmentEditPart;
					}
				}
			}
		}

		if (nearestParentCombinedFragment == null) {
			// Determine the leftmost lifeline's center X coordinate.
			int leftmostLifelineCenterX = Integer.MAX_VALUE;
			for (Object value : editPartRegistry.values()) {
				if (value instanceof LifelineEditPart) {
					Rectangle lifelineBounds = getAbsoluteBounds((GraphicalEditPart) value);
					int centerX = lifelineBounds.x + lifelineBounds.width / 2;
					leftmostLifelineCenterX = Math.min(leftmostLifelineCenterX, centerX);
				}
			}

			if (leftmostLifelineCenterX != Integer.MAX_VALUE && newBounds.x < leftmostLifelineCenterX + 5) {
				return true;
			} else {
				return false;
			}
		}

		Rectangle parentBounds = getAbsoluteBounds(nearestParentCombinedFragment);

		// 상위 CombinedFragment의 오른쪽 경계에서 최소 여백 5을 고려하여 넘는지 확인
		// Check against the nearest parent Combined Fragment's left boundary.
		int LeftResizeLimit = parentBounds.left();

		if (newBounds.left() < LeftResizeLimit + 5) { // Consider a minimal margin.
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Determines if the upper resize limit is exceeded for a Combined Fragment or Interaction Operand during a resize operation.
	 * This involves calculating the new bounds post-resize and checking against all child edit parts to ensure that the resized
	 * element does not encroach upon the space required by its children. The method considers additional margins for certain types
	 * of child elements to maintain clear visual separation and structural integrity within sequence diagrams.
	 *
	 * @param request
	 *            The change bounds request that includes details of the resize operation.
	 * @param resizeTargetEp
	 *            The Combined Fragment or Interaction Operand being resized.
	 * @return true if the resizing operation causes the target to exceed the upper boundary limit by overlapping with child elements, otherwise false.
	 */
	private boolean isExceedingUpperResizeLimit(ChangeBoundsRequest request, GraphicalEditPart resizeTargetEp) {

		if (!(resizeTargetEp instanceof CombinedFragmentEditPart || resizeTargetEp instanceof InteractionOperandEditPart)) {
			return false;
		}

		// Calculate the resized bounds in absolute coordinates.
		Rectangle resizeBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(resizeTargetEp);


		// Traverse all child edit parts within the resize bounds to check for potential overlap.
		for (Object child : AdoneSequenceUtil.getCoveredInteractionFragmentEditParts(resizeBounds, resizeTargetEp, false, null)) {
			if (child instanceof GraphicalEditPart) {
				GraphicalEditPart childEp = (GraphicalEditPart) child;

				if (!(childEp instanceof CombinedFragmentEditPart || childEp instanceof BehaviorExecutionSpecificationEditPart)) {
					continue;
				}

				Rectangle childBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(childEp);

				// Additional margin to prevent visual overlap.
				int additionalMargin = 20;

				// Check if the bottom edge of any child, plus an additional margin, exceeds the resized bounds.
				if (childBounds.y + childBounds.height + additionalMargin > resizeBounds.y + resizeBounds.height + request.getSizeDelta().height) {
					return true; // 한 개라도 조건에 부합하면 true 반환
				}
			}
		}

		// No overlaps detected, within upper resize limit.
		return false;
	}

	/**
	 * Determines if the resizing of a Combined Fragment exceeds the right boundary limit of its nearest parent Combined Fragment.
	 * This method is crucial for maintaining the structural integrity of sequence diagrams by ensuring that child fragments do not
	 * resize beyond the confines of their parent fragments. A minimal margin is considered on the right side to ensure there is
	 * always a buffer space between the child and parent boundaries.
	 *
	 * @param request
	 *            The request containing details about how the target edit part is being resized.
	 * @param resizeTargetEp
	 *            The Combined Fragment being resized.
	 * @return true if the resizing action causes the target to exceed the right boundary limit of its nearest parent Combined Fragment, otherwise false.
	 */
	private boolean isExceedingRightResizeLimit(ChangeBoundsRequest request, GraphicalEditPart resizeTargetEp) {

		if (!(resizeTargetEp instanceof CombinedFragmentEditPart)) {
			return false;
		}

		Map<?, ?> editPartRegistry = resizeTargetEp.getViewer().getEditPartRegistry();
		CombinedFragmentEditPart nearestParentCombinedFragment = null;
		Rectangle targetBounds = getAbsoluteBounds(resizeTargetEp);

		// Iterate through all Combined Fragment edit parts to find the nearest parent of the target.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof CombinedFragmentEditPart && value != resizeTargetEp) {
				CombinedFragmentEditPart combinedFragmentEditPart = (CombinedFragmentEditPart) value;
				Rectangle parentBounds = getAbsoluteBounds(combinedFragmentEditPart);

				// Check if the parent bounds contain the target bounds.
				if (parentBounds.contains(targetBounds)) {
					if (nearestParentCombinedFragment == null || getAbsoluteBounds(nearestParentCombinedFragment).contains(parentBounds)) {
						nearestParentCombinedFragment = combinedFragmentEditPart;
					}
				}
			}
		}

		if (nearestParentCombinedFragment == null) {
			// No limiting parent found, so no limit exceeded.
			return false;
		}

		Rectangle parentBounds = getAbsoluteBounds(nearestParentCombinedFragment);
		Dimension resizeDimension = request.getSizeDelta();
		Rectangle newBounds = targetBounds.getCopy();
		newBounds.width += resizeDimension.width; // 너비 변경을 반영

		// 상위 CombinedFragment의 오른쪽 경계에서 최소 여백 5을 고려하여 넘는지 확인
		// Calculate the right move limit considering a minimal margin from the parent's right boundary.
		int rightMoveLimit = parentBounds.right() - 5;

		// Check if the new right boundary of the target exceeds this limit.
		if (newBounds.right() > rightMoveLimit) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Calculates and returns the absolute bounds of a given graphical edit part.
	 * This method converts the local bounds of the edit part's figure to absolute coordinates,
	 * accounting for any nested positioning within the diagram. Absolute bounds are essential for
	 * operations that require precise positioning or dimensions of elements within the overall diagram context.
	 *
	 * @param editPart
	 *            The graphical edit part whose absolute bounds are to be calculated.
	 * @return A Rectangle representing the absolute bounds of the edit part's figure.
	 */
	private Rectangle getAbsoluteBounds(GraphicalEditPart editPart) {
		Rectangle bounds = editPart.getFigure().getBounds().getCopy();
		editPart.getFigure().translateToAbsolute(bounds);
		return bounds;
	}

	/**
	 * Resizes parent Interaction Operands and their parent Combined Fragments based on a specified resize operation.
	 * This method applies a vertical resize delta to Interaction Operands and their enclosing Combined Fragments
	 * identified as parents of a target edit part. It constructs and adds the necessary resize commands to a compound command,
	 * which is executed to apply the changes. This method includes a filtering condition to prevent recursive resizing when moving
	 * nested edit parts.
	 *
	 * @param request
	 *            The change bounds request that triggered the resize operation, containing details such as the resize delta.
	 * @param targetEp
	 *            The target graphical edit part that initiated the resize request or is affected by it.
	 * @param command
	 *            The compound command to which the resize commands are added for execution.
	 * @param resizeDeltaY
	 *            The vertical resize delta to apply to the parent Interaction Operand and Combined Fragment.
	 */
	private void resizeParentInteractionOperand(ChangeBoundsRequest request, GraphicalEditPart targetEp, CompoundCommand command, int resizeDeltaY) {

		// Prevent recursive resizing actions when moving nested edit parts. (2023-12-23)
		Object value = request.getExtendedData().get("DoNotMoveCheck");

		if (!(value instanceof Boolean && (Boolean) value)) {
			List<InteractionOperandEditPart> parentInteractionOperandEpList = AdoneSequenceUtil.getAllParentInteractionOperandEps(targetEp);

			for (InteractionOperandEditPart ioEp : parentInteractionOperandEpList) {

				// Adjust the height of Interaction Operand Edit Parts.
				Rectangle ioEpBounds = ioEp.getFigure().getBounds().getCopy();
				ioEpBounds.height += resizeDeltaY;

				Command resizeInteractionOperandCommand = createChangeConstraintCommand(
						ioEp,
						translateToModelConstraint(ioEpBounds));
				if (resizeInteractionOperandCommand != null && resizeInteractionOperandCommand.canExecute()) {
					command.add(resizeInteractionOperandCommand);
				}

				// Adjust the height of the parent Combined Fragment Edit Parts accordingly.
				CombinedFragmentEditPart parentCbf = ((CombinedFragmentEditPart) ioEp.getParent().getParent());
				Rectangle cmbFrgBounds = parentCbf.getFigure().getBounds().getCopy();
				cmbFrgBounds.height += resizeDeltaY;

				Command resizeCombinedFragmentCommand = createChangeConstraintCommand(
						ioEp.getParent().getParent(),
						translateToModelConstraint(cmbFrgBounds));
				if (resizeCombinedFragmentCommand != null && resizeCombinedFragmentCommand.canExecute()) {
					command.add(resizeCombinedFragmentCommand);
				}
			}
		}
	}

	/**
	 * Determines the lifelines that are newly included or excluded from a combined fragment's bounds
	 * after a resizing operation. This method compares the original and new bounds of the combined fragment
	 * to identify lifelines that have moved into or out of these bounds as a result of the change.
	 *
	 * @param request
	 *            The change bounds request detailing the proposed move and resize operations.
	 * @param combinedFragmentEp
	 *            The graphical edit part representing the combined fragment being resized.
	 * @return A list of lifelines affected by the resizing of the combined fragment, either by being newly
	 *         included within its bounds or by being excluded from it.
	 */
	private List<Lifeline> getAddedLifelinesToResizeCombinedFragment(ChangeBoundsRequest request, GraphicalEditPart combinedFragmentEp) {

		List<Lifeline> affectedLifelines = new ArrayList<>();

		// Convert the current bounds of the CombinedFragmentEditPart to absolute coordinates.
		Rectangle originalBounds = combinedFragmentEp.getFigure().getBounds().getCopy();
		combinedFragmentEp.getFigure().translateToAbsolute(originalBounds);

		// Calculate the new bounds in absolute coordinates based on the requested changes.
		Rectangle newBounds = originalBounds.getCopy().translate(request.getMoveDelta()).resize(request.getSizeDelta());
		combinedFragmentEp.getFigure().getParent().translateToAbsolute(newBounds);

		Map<?, ?> editPartRegistry = combinedFragmentEp.getViewer().getEditPartRegistry();

		// Traverse all LifelineEditParts to identify lifelines that need to be newly included or excluded.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				LifelineEditPart lifelineEp = (LifelineEditPart) value;
				Rectangle lifelineBounds = lifelineEp.getFigure().getBounds().getCopy();
				lifelineEp.getFigure().translateToAbsolute(lifelineBounds);

				// Calculate the midpoint of the lifeline.
				int lifelineCenterX = lifelineBounds.x + lifelineBounds.width / 2;

				// Check if the lifeline's midpoint falls within the original and new bounds of the CombinedFragment.
				boolean isInOriginalBounds = originalBounds.x <= lifelineCenterX && lifelineCenterX <= originalBounds.x + originalBounds.width;
				boolean isInNewBounds = newBounds.x <= lifelineCenterX && lifelineCenterX <= newBounds.x + newBounds.width;

				// Include lifelines that have moved into or out of the combined fragment's bounds due to the resize.
				if (!isInOriginalBounds && isInNewBounds) {
					affectedLifelines.add((Lifeline) lifelineEp.resolveSemanticElement());
				} else if (isInOriginalBounds && !isInNewBounds) {
					affectedLifelines.add((Lifeline) lifelineEp.resolveSemanticElement());
				}
			}
		}

		return affectedLifelines;
	}

	/**
	 * Represents the insertion point for moving a lifeline, encapsulating both the index and the pixel location.
	 * This class is used to manage and convey where a lifeline should be inserted during reordering operations
	 * within a sequence diagram, ensuring that lifelines are positioned accurately relative to each other.
	 */
	protected class LifelineMoveInsertionPoint {

		// Index within a collection where the lifeline is to be inserted.
		int locationIndex;

		// Pixel location within the diagram where the lifeline is to be inserted.
		int location;

		/**
		 * Constructs a new instance of LifelineMoveInsertionPoint with specified index and location.
		 *
		 * @param index
		 *            The index within a collection or list indicating where the lifeline should be inserted.
		 * @param location
		 *            The pixel location within the diagram indicating where the lifeline should be placed.
		 */
		public LifelineMoveInsertionPoint(final int index, final int location) {
			this.locationIndex = index;
			this.location = location;
		}

		/**
		 * Gets the index of the insertion point.
		 *
		 * @return The index indicating where the lifeline should be inserted within a collection or list.
		 */
		public int getIndex() {
			return this.locationIndex;
		}

		/**
		 * Gets the pixel location of the insertion point within the diagram.
		 *
		 * @return The pixel location indicating where the lifeline should be positioned within the diagram.
		 */
		public int getLocation() {
			return this.location;
		}
	}

}
