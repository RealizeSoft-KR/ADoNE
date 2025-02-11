/*****************************************************************************
 * Copyright (c) 2010-2017 CEA
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
 *   Atos Origin - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 519408, 525372, 526628
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 531596
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced to move related EditParts concurrently with message
 *   movements and simplifies feedback image code for clearer visual definition.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionBendpointEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.diagram.ui.util.SelectInDiagramHelper;
import org.eclipse.gmf.runtime.draw2d.ui.figures.PolylineConnectionEx;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.gef.ui.internal.editpolicies.LineMode;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.impl.ShapeImpl;
import org.eclipse.papyrus.infra.gmfdiag.common.editpart.NodeEditPart;
import org.eclipse.papyrus.uml.diagram.common.editparts.RoundedCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.CustomMessages;
import org.eclipse.papyrus.uml.diagram.sequence.command.DropDestructionOccurenceSpecification;
import org.eclipse.papyrus.uml.diagram.sequence.command.SetLocationCommand;
import org.eclipse.papyrus.uml.diagram.sequence.command.SetResizeCommand;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.routers.MessageRouter.RouterKind;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneMessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CInteractionEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageAsyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageCreateEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageDeleteEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageFoundEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageLostEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.figures.MessageCreate;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.preferences.CustomDiagramGeneralPreferencePage;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.BoundForEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneMoveInteractionFragmentElementRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneOccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.LifelineEditPartUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.LifelineMessageCreateHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.LifelineMessageDeleteHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.LogOptions;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceRequestConstant;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.uml2.uml.Gate;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.OccurrenceSpecification;

/**
 * The AdoneMessageConnectionLineSegEditPolicy class extends the ConnectionBendpointEditPolicy
 * to enhance message movement functionality within sequence diagrams. This class has been
 * developed by copying and extending the source code of the original MessageConnectionLineSegEditPolicy.
 * Key enhancements include:
 *
 * 1. Ensuring that related EditParts move in conjunction with message movements, providing a
 * cohesive and accurate representation of message interactions within the diagram. This
 * adjustment ensures that when messages are repositioned, all associated diagram components
 * reflect these changes, maintaining the integrity and logical flow of the sequence diagram.
 *
 * 2. Simplifying and clarifying the feedback image provided during message movements. The code
 * responsible for generating visual feedback has been revised to make the feedback more
 * visually intuitive. This simplification aids users in understanding the effects of their
 * actions in real-time, enhancing the user experience by making diagram editing more
 * straightforward and less prone to confusion.
 *
 * These modifications are designed to improve the usability and functionality of editing
 * message connections in sequence diagrams, focusing on user interaction and visual clarity.
 */
public class AdoneMessageConnectionLineSegEditPolicy extends ConnectionBendpointEditPolicy {

	/**
	 * Set up moving LineSeg data for self linked message, the value should be one of MOVED_UP, MOVED_DOWN and MOVED_HORIAONTZL.
	 * See showMoveLineSegFeedback() for self linked message for details.
	 */
	private static final String MOVE_LINE_ORIENTATION_DATA = "Move line orientation";

	private static final String MOVED_UP = "Moved Up";

	private static final String MOVED_DOWN = "Moved Down";

	private static final String MOVED_HORIZONTAL = "Moved Horizontal";

	/** The minimum height of the figure. */
	private static final int LIFELINE_MIN_HEIGHT = 100;

	protected IFigure moveMessageFeedbackConnectionLine;

	private ConnectionRouter router;

	public AdoneMessageConnectionLineSegEditPolicy() {
		super();
	}

	@Override
	protected List<?> createManualHandles() {
		RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());
		if (kind == RouterKind.SELF || kind == RouterKind.HORIZONTAL || getConnection() instanceof MessageCreate) {
			// Removed the handles for self message.
			return Collections.emptyList();
		}
		return super.createManualHandles();
	}

	@Override
	public Command getCommand(Request request) {
		// get the command in case of deletion of a message
		if (request instanceof EditCommandRequestWrapper
				&& (getHost() instanceof AbstractMessageEditPart)
				&& !(getHost() instanceof MessageDeleteEditPart)
				&& !(getHost() instanceof MessageCreateEditPart)) {

			// Check that this is a delete command, in this case, we have to recalculate the other execution specification positions
			final IEditCommandRequest editCommandRequest = ((EditCommandRequestWrapper) request).getEditCommandRequest();
			if (editCommandRequest instanceof DestroyElementRequest
					&& ((DestroyElementRequest) editCommandRequest).getElementToDestroy() instanceof Message) {
				return getUpdateWeakRefForMessageDelete((EditCommandRequestWrapper) request);
			}
		}

		RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());
		if (kind == RouterKind.SELF || kind == RouterKind.HORIZONTAL || kind == RouterKind.OBLIQUE || getConnection() instanceof MessageCreate) {
			return super.getCommand(request);
		} else if (request instanceof BendpointRequest) {
			return getMoveMessageCommand((BendpointRequest) request);
		}
		return null;
	}

	@Override
	protected Connection getConnection() {
		return (Connection) ((ConnectionEditPart) getHost()).getFigure();
	}

	private Command getUpdateWeakRefForMessageDelete(final EditCommandRequestWrapper request) {
		CompoundCommand command = null;
		ConnectionEditPart hostConnectionEditPart = (ConnectionEditPart) getHost();

		// compute Delta
		Point moveDelta = new Point(0, -UpdateWeakReferenceEditPolicy.deltaMoveAtCreationAndDeletion);

		if (moveDelta.y < 0) {
			// get the edit policy of references
			if (hostConnectionEditPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE) != null) {
				SequenceReferenceEditPolicy references = (SequenceReferenceEditPolicy) hostConnectionEditPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);
				if (!SenderRequestUtils.isASender(request, getHost())) {
					CompoundCommand compoundCommand = new CompoundCommand();

					// Gets weak references
					List<EditPart> weakReferences = new ArrayList<>();
					HashMap<EditPart, String> allWeakReferences = references.getWeakReferences();

					allWeakReferences.forEach((editPart, value) -> {
						if (SequenceReferenceEditPolicy.ROLE_FINISH != value) {// Do not take into account finish event of ecexution specification
							weakReferences.add(editPart);
						}
					});

					// for each weak reference move it
					for (Iterator<EditPart> iterator = weakReferences.iterator(); iterator.hasNext();) {
						EditPart editPart = iterator.next();
						if (!hostConnectionEditPart.equals(editPart) && !SenderRequestUtils.isASender(request, editPart)) {// avoid loop
							UMLDiagramEditorPlugin.log.trace(LogOptions.SEQUENCE_DEBUG, "+--> try to Move " + editPart);//$NON-NLS-1$
							ArrayList<EditPart> senderList = SenderRequestUtils.getSenders(request);
							if (editPart instanceof ConnectionEditPart) {
								ConnectionEditPart connectionEditPart = (ConnectionEditPart) editPart;
								// move up, source must be moved before
								UpdateWeakReferenceEditPolicy.moveSourceConnectionEditPart(hostConnectionEditPart, moveDelta, compoundCommand, connectionEditPart, senderList);
								UpdateWeakReferenceEditPolicy.moveTargetConnectionEditPart(hostConnectionEditPart, moveDelta, compoundCommand, connectionEditPart, senderList);
							}
							if (editPart instanceof RoundedCompartmentEditPart) {
								UpdateWeakReferenceEditPolicy.moveRoundedEditPart(hostConnectionEditPart, moveDelta, compoundCommand, editPart, senderList);
							}
						}
						if (!compoundCommand.isEmpty()) {
							command = compoundCommand;
						}
					}
				}
			}
		}
		return null != command && command.canExecute() ? command : null; // Don't return unexecutive command just null
	}

	protected Command getMoveMessageCommand(BendpointRequest request) {
		if (getHost() instanceof MessageLostEditPart || getHost() instanceof MessageFoundEditPart) {
			PointList points = getConnection().getPoints().getCopy();
			CompoundCommand command = new CompoundCommand("Move");//$NON-NLS-1$
			AbstractMessageEditPart messageEditPart = (AbstractMessageEditPart) getHost();

			// Before to do any move, we need to check if the life lines need to be resized
			// Get the source and target location
			Point sourceLocation = points.getFirstPoint().getCopy();
			getConnection().translateToAbsolute(sourceLocation);
			sourceLocation = SequenceUtil.getSnappedLocation(getHost(), sourceLocation);
			Point targetLocation = points.getLastPoint().getCopy();
			getConnection().translateToAbsolute(targetLocation);
			targetLocation = SequenceUtil.getSnappedLocation(getHost(), targetLocation);

			// Get the life lines
			final EditPart source = messageEditPart.getSource();
			final EditPart target = messageEditPart.getTarget();
			final Collection<EditPart> editParts = new HashSet<>(2);
			editParts.add(source);
			editParts.add(target);

			// This field determine the max y position for the life lines
			// If this one is equals to '-1', there is no resize needed
			int maxY = -1;

			// Loop on possible source and target edit parts to check if this is needed to resize life lines
			for (final EditPart editPart : editParts) {
				final LifelineEditPart lifeLineEditPart = SequenceUtil.getParentLifelinePart(editPart);

				if (null != lifeLineEditPart) {
					if (lifeLineEditPart.getModel() instanceof Shape) {
						final Shape view = (ShapeImpl) lifeLineEditPart.getModel();
						final Bounds bounds = BoundForEditPart.getBounds(view);

						if (sourceLocation.y > (bounds.getY() + bounds.getHeight())) {
							maxY = sourceLocation.y;
						}
						if (targetLocation.y > (bounds.getY() + bounds.getHeight())) {
							maxY = targetLocation.y;
						}
					}
				}
			}

			// If the max y position is greater than '-1', resize is needed for life lines
			if (maxY > -1) {
				LifelineEditPartUtil.resizeAllLifeLines(command, messageEditPart, maxY, null);
			}

			// move source
			ReconnectRequest sourceReq = new ReconnectRequest(REQ_RECONNECT_SOURCE);
			sourceReq.setConnectionEditPart(messageEditPart);
			sourceReq.setLocation(sourceLocation);
			sourceReq.setTargetEditPart(source);
			Command moveSourceCommand = source.getCommand(sourceReq);
			command.add(moveSourceCommand);
			// move target
			ReconnectRequest targetReq = new ReconnectRequest(REQ_RECONNECT_TARGET);
			targetReq.setConnectionEditPart(messageEditPart);
			targetReq.setLocation(targetLocation);
			targetReq.setTargetEditPart(target);
			Command moveTargetCommand = target.getCommand(targetReq);
			command.add(moveTargetCommand);
			return command.unwrap();
		}
		return null;
	}

	/**
	 * Generates a command to change bendpoints, incorporating logic to handle the movement of message ends.
	 * This method has been updated to ensure that after moving message ends, related EditParts are also moved accordingly.
	 * This enhancement facilitates the accurate representation of message flows and interactions within the diagram,
	 * particularly in scenarios involving complex routing or interaction sequences.
	 *
	 * @param request
	 *            The bendpoint request containing details of the move operation.
	 * @return The command to execute the bendpoint change, or UnexecutableCommand if the operation cannot be performed.
	 */
	@Override
	protected Command getBendpointsChangedCommand(BendpointRequest request) {
		// snap to grid the location request
		request.setLocation(SequenceUtil.getSnappedLocation(getHost(), request.getLocation()));
		if ((getHost().getViewer() instanceof ScrollingGraphicalViewer) && (getHost().getViewer().getControl() instanceof FigureCanvas)) {
			SelectInDiagramHelper.exposeLocation((FigureCanvas) getHost().getViewer().getControl(), request.getLocation().getCopy());
		}
		if (getHost() instanceof ConnectionNodeEditPart) {
			ConnectionNodeEditPart connectionPart = (ConnectionNodeEditPart) getHost();
			EObject message = connectionPart.resolveSemanticElement();
			if (message instanceof Message) {
				MessageEnd send = ((Message) message).getSendEvent();
				MessageEnd rcv = ((Message) message).getReceiveEvent();
				EditPart srcPart = connectionPart.getSource();
				CLifeLineEditPart srcLifelinePart = (CLifeLineEditPart) SequenceUtil.getParentLifelinePart(srcPart);
				EditPart tgtPart = connectionPart.getTarget();
				CLifeLineEditPart targetLifelinePart = (CLifeLineEditPart) SequenceUtil.getParentLifelinePart(tgtPart);
				if (/* send instanceof OccurrenceSpecification && rcv instanceof OccurrenceSpecification && */srcLifelinePart != null && targetLifelinePart != null) {
					RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());
					if ((getHost() instanceof MessageSyncEditPart || getHost() instanceof MessageAsyncEditPart) && kind == RouterKind.SELF) {
						// TODO_MIA Test it
						return getSelfLinkMoveCommand(request, connectionPart, send, rcv, srcLifelinePart);
					} else if (getHost() instanceof MessageCreateEditPart) {
						// Move message End
						int y = request.getLocation().y;
						Command srcCmd = createMoveMessageEndCommand((Message) message, srcPart, send, y, srcLifelinePart, request);
						Command tgtCmd = createMoveMessageEndCommand((Message) message, tgtPart, rcv, y, targetLifelinePart, request);

						CompoundCommand compoudCmd = new CompoundCommand(CustomMessages.MoveMessageCommand_Label);
						Point oldLocation = SequenceUtil.getAbsoluteEdgeExtremity(connectionPart, true);
						if (oldLocation != null) {
							int oldY = oldLocation.y;

							// Calculate if this is needed to enlarge the lifelines
							final int yMoveDelta = y - oldY;
							final Command updateLifeLinesBounds = getUpdateLifeLinesBoundsCommand(request, connectionPart, yMoveDelta);

							if (null != updateLifeLinesBounds) {
								compoudCmd.add(updateLifeLinesBounds);
							}
							if (oldY < y) {
								compoudCmd.add(tgtCmd);
								compoudCmd.add(srcCmd);
							} else {
								compoudCmd.add(srcCmd);
								compoudCmd.add(tgtCmd);
							}
							return compoudCmd;
						}
					} else if (getHost() instanceof MessageDeleteEditPart) {
						// Reposition lifeline
						IFigure targetFigure = targetLifelinePart.getPrimaryShape();

						Point refPoint = SequenceUtil.getSnappedLocation(targetLifelinePart, request.getLocation().getCopy());
						targetFigure.getParent().translateToRelative(refPoint);
						Bounds bounds = ((Bounds) ((Node) targetLifelinePart.getModel()).getLayoutConstraint());

						ICommand setSizeCommand = new SetResizeCommand(targetLifelinePart.getEditingDomain(), "Size LifeLine", new EObjectAdapter(((GraphicalEditPart) targetLifelinePart).getNotationView()), //$NON-NLS-1$
								new Dimension(bounds.getWidth(), refPoint.y - bounds.getY()));

						CompoundCommand compoudCmd = new CompoundCommand(CustomMessages.MoveMessageCommand_Label);
						if (kind == RouterKind.SELF) {
							// Only resize for down moved
							if (MOVED_DOWN.equals(request.getExtendedData().get(MOVE_LINE_ORIENTATION_DATA))) {
								compoudCmd.add(new ICommandProxy(setSizeCommand));
							}
						} else {
							// Move message End
							int y = request.getLocation().y;
							Command srcCmd = createMoveMessageEndCommand((Message) message, srcPart, send, y, srcLifelinePart, request);
							Command tgtCmd = createMoveMessageEndCommand((Message) message, tgtPart, rcv, y, targetLifelinePart, request);
							DropDestructionOccurenceSpecification dropDestructionOccurenceSpecification = new DropDestructionOccurenceSpecification(((ConnectionEditPart) getHost()).getEditingDomain(), request, targetLifelinePart,
									request.getLocation().getCopy());

							Point oldLocation = SequenceUtil.getAbsoluteEdgeExtremity(connectionPart, true);
							if (oldLocation != null) {
								int oldY = oldLocation.y;

								// Calculate if this is needed to enlarge the lifelines
								final int yMoveDelta = y - oldY;
								final Command updateLifeLinesBounds = getUpdateLifeLinesBoundsCommand(request, connectionPart, yMoveDelta);

								if (null != updateLifeLinesBounds) {
									compoudCmd.add(updateLifeLinesBounds);
								}
								if (oldY < y) {// down
									compoudCmd.add(tgtCmd);
									compoudCmd.add(srcCmd);
									compoudCmd.add(new ICommandProxy(dropDestructionOccurenceSpecification));
								} else {// up
									compoudCmd.add(srcCmd);
									compoudCmd.add(tgtCmd);
									compoudCmd.add(new ICommandProxy(dropDestructionOccurenceSpecification));
								}
							}
						}
						return compoudCmd;
					} else {

						// Determine the Y-coordinate for the current request's location.
						int y = request.getLocation().y;
						int yDelta = 0;

						// Retrieve the connection figure and calculate positions of source and target anchors.
						PolylineConnectionEx polyline = (PolylineConnectionEx) connectionPart.getFigure();
						Point sourceAnchorPosition = polyline.getSourceAnchor().getReferencePoint();
						Point targetAnchorPosition = polyline.getTargetAnchor().getReferencePoint();

						// For oblique connections, calculate the vertical difference between source and target anchors.
						if (kind == RouterKind.OBLIQUE) {
							yDelta = targetAnchorPosition.y - sourceAnchorPosition.y;
						}

						// Log the operation for debugging purposes if the message has a signature.
						if (((Message) message).getSignature() != null) {
							UMLDiagramEditorPlugin.log.trace(LogOptions.SEQUENCE_DEBUG,
									"AdoneMessageConnectionLineSegEditPolicy.getBendpointsChangedCommand()" + "+Starting Message " + ((Message) message).getSignature().getQualifiedName());//$NON-NLS-1$
						}

						// Create commands to move the message ends based on the new Y-coordinate and calculated Y-delta.
						Command srcCmd = createMoveMessageEndCommand((Message) message, srcPart, send, y, srcLifelinePart, request);
						Command tgtCmd = createMoveMessageEndCommand((Message) message, tgtPart, rcv, y + yDelta, targetLifelinePart, request);

						// Initialize a compound command to combine multiple commands into a single undoable operation.
						CompoundCommand compoudCmd = new CompoundCommand(CustomMessages.MoveMessageCommand_Label);

						/*
						 * Take care of the order of commands, to make sure target is always bellow the source.
						 * Otherwise, moving the target above the source would cause order conflict with existing CF.
						 */
						Point oldLocation = SequenceUtil.getAbsoluteEdgeExtremity(connectionPart, true);
						if (oldLocation != null) {
							int oldY = oldLocation.y; // Store the Y-coordinate of the old location for comparison.

							// Calculate the vertical movement delta based on the new Y-coordinate.
							final int yMoveDelta = y - oldY;

							// Generate a command to update lifelines' bounds if the movement requires it.
							final Command updateLifeLinesBoundsCommand = getUpdateLifeLinesBoundsCommand(request, connectionPart, yMoveDelta);

							if (null != updateLifeLinesBoundsCommand) {
								compoudCmd.add(updateLifeLinesBoundsCommand);
							}

							// Prepare a request for moving interaction fragment elements along with the connection.
							AdoneMoveInteractionFragmentElementRequest moveRequest = new AdoneMoveInteractionFragmentElementRequest();
							moveRequest.setEditParts(connectionPart);
							Point moveDelta = new Point();
							moveDelta.setX(0);
							moveDelta.setY(yMoveDelta);
							moveRequest.setMoveDelta(moveDelta);
							moveRequest.setLocation(request.getLocation().getCopy());

							// Obtain and add the command to move all affected EditParts below the connection.
							Command moveAllBelowEpCommand = getInteractionInteractionCompartmentEditPart().getCommand(moveRequest);
							if (moveAllBelowEpCommand != null && moveAllBelowEpCommand.canExecute()) {
								compoudCmd.add(moveAllBelowEpCommand);

								if (oldY < y) { // If moving downwards, target command is added first.
									compoudCmd.add(tgtCmd);
									compoudCmd.add(srcCmd);
								} else { // If moving upwards, source command is added first.
									compoudCmd.add(srcCmd);
									compoudCmd.add(tgtCmd);
								}

								// Return the compound command to execute all movements.
								return compoudCmd;

							}
						}
					}
				} else
				// Handle cases specifically for found and lost messages.
				if ((srcLifelinePart == null) && (targetLifelinePart != null) || (srcLifelinePart != null && targetLifelinePart == null)) {
					return getMoveMessageCommand(request);
				}
			}
		}

		// Return an unexecutable command if none of the conditions are met, indicating no action should be taken.
		return UnexecutableCommand.INSTANCE;
	}

	/**
	 * Retrieves the edit part for an interaction compartment within the Adone messaging system. This method
	 * traverses the semantic elements starting from the host edit part to find the associated interaction
	 * compartment. If found, it returns the corresponding edit part; otherwise, returns null.
	 *
	 * The search process involves resolving the semantic element of the host and iterating through the edit part
	 * registry of the host's viewer to locate the InteractionInteractionCompartmentEditPart that matches the
	 * resolved semantic element.
	 */
	private EditPart getInteractionInteractionCompartmentEditPart() {

		AdoneMessageSyncEditPart msgEp = (AdoneMessageSyncEditPart) getHost();

		if (msgEp != null) {
			EObject element = msgEp.resolveSemanticElement();

			// Loop to ascend the model hierarchy until an Interaction or no parent is found.
			while (element != null && !(element instanceof Interaction)) {
				element = element.eContainer();
			}

			// Check if the element is an instance of Interaction.
			if (element instanceof Interaction) {
				Map registry = msgEp.getViewer().getEditPartRegistry();
				for (Object part : registry.values()) {
					if (part instanceof InteractionInteractionCompartmentEditPart) {
						InteractionInteractionCompartmentEditPart interactionPart = (InteractionInteractionCompartmentEditPart) part;

						// Check if the edit part corresponds to the Interaction element found earlier.
						if (interactionPart.resolveSemanticElement().equals(element)) {
							return interactionPart;
						}
					}
				}
			}
		}


		return null;
	}


	/**
	 * This allows to updates life lines height if necessary.
	 *
	 * @param request
	 *            The initial request.
	 * @param hostConnectionEditPart
	 *            The connection edit part corresponding to the moved message.
	 * @param yMoveDelta
	 *            The height of the move.
	 * @return The command to update life lines or <code>null</code>.
	 * @since 5.0
	 */
	protected Command getUpdateLifeLinesBoundsCommand(final BendpointRequest request, final ConnectionEditPart hostConnectionEditPart, final int yMoveDelta) {
		CompoundCommand command = null;
		UMLDiagramEditorPlugin.log.trace(LogOptions.SEQUENCE_DEBUG, "+ Calculate lifelines height modification for " + hostConnectionEditPart.getClass().getName());//$NON-NLS-1$

		boolean mustMoveBelowAtMovingDown = UMLDiagramEditorPlugin.getInstance().getPreferenceStore().getBoolean(CustomDiagramGeneralPreferencePage.PREF_MOVE_BELOW_ELEMENTS_AT_MESSAGE_DOWN);

		if (yMoveDelta != 0) {
			if (yMoveDelta > 0 && mustMoveBelowAtMovingDown && hostConnectionEditPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE) != null) {
				final SequenceReferenceEditPolicy references = (SequenceReferenceEditPolicy) hostConnectionEditPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);
				if (!SenderRequestUtils.isASender(request, getHost())) {

					// Gets weak references
					final List<EditPart> weakAndStrongReferences = new ArrayList<>();
					weakAndStrongReferences.addAll(references.getWeakReferences().keySet());
					weakAndStrongReferences.addAll(references.getStrongReferences().keySet());

					// The needed y position and heights
					// Get the initial source and target positions of the message
					final PolylineConnectionEx polyline = (PolylineConnectionEx) hostConnectionEditPart.getFigure();
					final Point initialSourcePosition = polyline.getSourceAnchor().getReferencePoint();
					final Point initialTargetPosition = polyline.getTargetAnchor().getReferencePoint();

					final Set<LifelineEditPart> lifelineEditParts = new HashSet<>();
					if (hostConnectionEditPart.getSource() instanceof LifelineEditPart) {
						lifelineEditParts.add((LifelineEditPart) hostConnectionEditPart.getSource());
					}
					if (hostConnectionEditPart.getTarget() instanceof LifelineEditPart) {
						lifelineEditParts.add((LifelineEditPart) hostConnectionEditPart.getTarget());
					}

					// This variable store the maximum y position depending to the message moved
					int maxY = initialTargetPosition.y > initialSourcePosition.y ? initialTargetPosition.y + yMoveDelta : initialTargetPosition.y + yMoveDelta;

					// Loop on each references to get the maximum y and to watch if it is needed to resize lifelines
					for (int index = 0; index < weakAndStrongReferences.size(); index++) {
						final EditPart editPart = weakAndStrongReferences.get(index);
						if (!SenderRequestUtils.isASender(request, editPart)) {
							if (editPart instanceof ConnectionEditPart) {
								ConnectionEditPart connectionEditPart = (ConnectionEditPart) editPart;

								// create the request
								if (yMoveDelta > 0) {

									// Calculate the anchor target Y
									final PolylineConnectionEx subPolyline = (PolylineConnectionEx) connectionEditPart.getFigure();
									final Point targetPositionOnScreen = subPolyline.getTargetAnchor().getReferencePoint();
									final int newYTargetPoint = targetPositionOnScreen.y + yMoveDelta;
									final Point sourcePositionOnScreen = subPolyline.getSourceAnchor().getReferencePoint();
									final int newYSourcePoint = sourcePositionOnScreen.y + yMoveDelta;

									// Get the max y
									if (maxY < newYTargetPoint) {
										maxY = newYTargetPoint;
									}
									if (maxY < newYSourcePoint) {
										maxY = newYSourcePoint;
									}

									if (connectionEditPart.getSource() instanceof LifelineEditPart) {
										lifelineEditParts.add((LifelineEditPart) connectionEditPart.getSource());
									}
									if (connectionEditPart.getTarget() instanceof LifelineEditPart) {
										lifelineEditParts.add((LifelineEditPart) connectionEditPart.getTarget());
									}
								}
							} else if (editPart instanceof IGraphicalEditPart) {
								if (editPart.getModel() instanceof Node) {
									final Rectangle absoluteBounds = SequenceUtil.getAbsoluteBounds((IGraphicalEditPart) editPart);
									if (absoluteBounds.height == -1) {
										absoluteBounds.setHeight(BoundForEditPart.getDefaultHeightFromView((Node) editPart.getModel()));
									}

									final LifelineEditPart parentLifeline = SequenceUtil.getParentLifelinePart(editPart);
									if (null != parentLifeline) {
										lifelineEditParts.add(parentLifeline);
									}

									if (maxY < absoluteBounds.y + absoluteBounds.height + yMoveDelta) {
										maxY = absoluteBounds.y + absoluteBounds.height + yMoveDelta;
									}
								}
							}

							// Get the weak and strong references of the current edit part
							if (editPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE) != null) {
								final SequenceReferenceEditPolicy subReferences = (SequenceReferenceEditPolicy) editPart.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);
								for (final EditPart refEditPart : subReferences.getWeakReferences().keySet()) {
									if (!weakAndStrongReferences.contains(refEditPart)) {
										weakAndStrongReferences.add(refEditPart);
									}
								}
								for (final EditPart refEditPart : subReferences.getStrongReferences().keySet()) {
									if (!weakAndStrongReferences.contains(refEditPart)) {
										weakAndStrongReferences.add(refEditPart);
									}
								}
							}
						}
					}

					// Manage the lifelines to resize if needed
					if (!weakAndStrongReferences.isEmpty() || !(hostConnectionEditPart instanceof MessageCreateEditPart || hostConnectionEditPart instanceof MessageDeleteEditPart)) {
						// Get all the life lines in the model to resize them if needed
						final Set<LifelineEditPart> lifeLinesToResize = SequenceUtil.getLifeLinesFromEditPart(hostConnectionEditPart);

						final CompoundCommand compoundCommand = new CompoundCommand();

						// Loop on each life line
						for (final LifelineEditPart lifeLineEP : lifeLinesToResize) {
							if (lifeLineEP.getModel() instanceof Shape) {
								final Shape view = (ShapeImpl) lifeLineEP.getModel();

								// Check if there is message delete on life line
								final boolean hasIncomingMessageDelete = LifelineMessageDeleteHelper.hasIncomingMessageDelete(lifeLineEP);

								// Check if there is message create on life line located after the moved message
								final List<?> incomingMessagesCreate = LifelineMessageCreateHelper.getIncomingMessageCreate(lifeLineEP);
								boolean hasIncomingMessageCreate = incomingMessagesCreate.size() > 0;
								if (hasIncomingMessageCreate) {
									hasIncomingMessageCreate = false;

									final Iterator<?> incomingMessagesCreateIt = incomingMessagesCreate.iterator();
									while (incomingMessagesCreateIt.hasNext() && !hasIncomingMessageCreate) {
										final Object incomingMessageCreate = incomingMessagesCreateIt.next();
										if (incomingMessageCreate instanceof ConnectionEditPart) {
											final PolylineConnectionEx subPolyline = (PolylineConnectionEx) ((ConnectionEditPart) incomingMessageCreate).getFigure();
											final Point targetPositionOnScreen = subPolyline.getTargetAnchor().getReferencePoint();
											final Point sourcePositionOnScreen = subPolyline.getSourceAnchor().getReferencePoint();

											// If the source position is located after the moved source message, in this case, consider the message create as after
											// As same, if the target position is located after the moved target message, in this case, consider the message create as after
											if (sourcePositionOnScreen.y >= initialSourcePosition.y || targetPositionOnScreen.y >= initialTargetPosition.y) {
												hasIncomingMessageCreate = true;
											}
										}
									}
								}

								final Rectangle absoluteBounds = SequenceUtil.getAbsoluteBounds(lifeLineEP);
								if (absoluteBounds.height == -1) {
									// absoluteBounds.setHeight(CLifeLineEditPart.DEFAUT_HEIGHT);
									absoluteBounds.setHeight(AdoneLifeLineEditPart.DEFAUT_HEIGHT);
								}

								// If there is no Message delete, resize the lifeline if needed
								if (!hasIncomingMessageDelete && !hasIncomingMessageCreate) {
									// Check if this is needed to resize the life line
									if (maxY > absoluteBounds.y + absoluteBounds.height) {
										if (view.getLayoutConstraint() instanceof Bounds) {
											// Create the command to change bounds
											final Bounds bounds = (Bounds) view.getLayoutConstraint();
											final Point newLocation = new Point(bounds.getX(), bounds.getY());
											final Dimension newDimension = new Dimension(bounds.getWidth(), maxY - absoluteBounds.y);

											final ICommand heightCommand = new SetResizeCommand(lifeLineEP.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newDimension);
											compoundCommand.add(new ICommandProxy(heightCommand));
											final ICommand locationCommand = new SetLocationCommand(lifeLineEP.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newLocation);
											compoundCommand.add(new ICommandProxy(locationCommand));
										}
									}
								} else if (lifelineEditParts.contains(lifeLineEP)) {

									if (view.getLayoutConstraint() instanceof Bounds) {
										// Create the command to change bounds
										final Bounds bounds = (Bounds) view.getLayoutConstraint();

										// We need to calculate the new y and the new height for the life line
										int newY = bounds.getY();
										int newHeight = bounds.getHeight();

										// If a message create exists, move the life line with yDelta
										if (hasIncomingMessageCreate) {
											newY = bounds.getY() + yMoveDelta;
											// If a message create and no message delete exists, calculate the new height of the life line depending to the current height and to the new maximum Y position
											if (!hasIncomingMessageDelete) {
												newHeight = (absoluteBounds.y + absoluteBounds.height) - newY;
												if (maxY > (newY + newHeight)) {
													newHeight = maxY - newY;
												}
											}
										} else if (hasIncomingMessageDelete) {
											newHeight = absoluteBounds.height + yMoveDelta;
										}

										final Point newLocation = new Point(bounds.getX(), newY);
										final Dimension newDimension = new Dimension(bounds.getWidth(), newHeight);

										final ICommand heightCommand = new SetResizeCommand(lifeLineEP.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newDimension);
										compoundCommand.add(new ICommandProxy(heightCommand));
										final ICommand locationCommand = new SetLocationCommand(lifeLineEP.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newLocation);
										compoundCommand.add(new ICommandProxy(locationCommand));
									}
								}
							}
						}

						if (!compoundCommand.isEmpty()) {
							command = compoundCommand;
						}
					}
				}
			}

			// If the command is null and the connection is a message create or a message delete, we need to recalculate the target lifeline bounds
			if (null == command) {
				if (hostConnectionEditPart instanceof MessageCreateEditPart) {
					final CompoundCommand updateLifeLinesBoundsForOnlyOneMessageCreate = getUpdateLifeLinesBoundsForOnlyOneMessageCreate(request, hostConnectionEditPart, yMoveDelta);

					if (!updateLifeLinesBoundsForOnlyOneMessageCreate.isEmpty()) {
						command = updateLifeLinesBoundsForOnlyOneMessageCreate;
					}
				} else if (hostConnectionEditPart instanceof MessageDeleteEditPart) {
					final CompoundCommand updateLifeLinesBoundsForOnlyOneMessageDelete = getUpdateLifeLinesBoundsForOnlyOneMessageDelete(request, hostConnectionEditPart, yMoveDelta);

					if (!updateLifeLinesBoundsForOnlyOneMessageDelete.isEmpty()) {
						command = updateLifeLinesBoundsForOnlyOneMessageDelete;
					}
				}
			}
		}

		return command;
	}

	/**
	 * This allows to get the command for bounds of life lines corresponding to the initial request when only one message create is modifying life lines.
	 *
	 * @param request
	 *            The initial request.
	 * @param hostConnectionEditPart
	 *            The connection edit part corresponding to the moved message.
	 * @param yMoveDelta
	 *            The height of the move.
	 * @return The compound command to update life lines bounds or <code>null</code>.
	 * @since 5.0
	 */
	protected CompoundCommand getUpdateLifeLinesBoundsForOnlyOneMessageCreate(final BendpointRequest request, final ConnectionEditPart hostConnectionEditPart, final int yMoveDelta) {
		final CompoundCommand compoundCommand = new CompoundCommand();

		if (hostConnectionEditPart.getTarget() instanceof LifelineEditPart) {
			final LifelineEditPart targetLifeLine = (LifelineEditPart) hostConnectionEditPart.getTarget();
			if (targetLifeLine.getModel() instanceof Shape) {
				final Shape view = (ShapeImpl) targetLifeLine.getModel();
				// Create the command to change bounds
				final Bounds bounds = (Bounds) view.getLayoutConstraint();
				final Point newLocation = new Point(bounds.getX(), bounds.getY() + yMoveDelta);
				final int initialHeight = bounds.getHeight() == -1 ? BoundForEditPart.getDefaultHeightFromView(view) : bounds.getHeight();
				Dimension newDimension = new Dimension(bounds.getWidth(), initialHeight - yMoveDelta);

				// Check here if this is needed to resize other life lines
				if (yMoveDelta > 0 && initialHeight < yMoveDelta) {
					newDimension.height = LIFELINE_MIN_HEIGHT;
					final int maxY = newLocation.y + newDimension.height;

					final Collection<LifelineEditPart> lifeLineEditPartsToSkip = new HashSet<>(1);
					lifeLineEditPartsToSkip.add(targetLifeLine);
					LifelineEditPartUtil.resizeAllLifeLines(compoundCommand, hostConnectionEditPart, maxY, lifeLineEditPartsToSkip);
				}

				final ICommand heightCommand = new SetResizeCommand(targetLifeLine.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newDimension);
				compoundCommand.add(new ICommandProxy(heightCommand));
				final ICommand locationCommand = new SetLocationCommand(targetLifeLine.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newLocation);
				compoundCommand.add(new ICommandProxy(locationCommand));
			}
		}

		return compoundCommand;
	}

	/**
	 * This allows to get the command for bounds of life lines corresponding to the initial request when only one message delete is modifying life lines.
	 *
	 * @param request
	 *            The initial request.
	 * @param hostConnectionEditPart
	 *            The connection edit part corresponding to the moved message.
	 * @param yMoveDelta
	 *            The height of the move.
	 * @return The compound command to update life lines bounds or <code>null</code>.
	 * @since 5.0
	 */
	protected CompoundCommand getUpdateLifeLinesBoundsForOnlyOneMessageDelete(final BendpointRequest request, final ConnectionEditPart hostConnectionEditPart, final int yMoveDelta) {
		final CompoundCommand compoundCommand = new CompoundCommand();

		if (hostConnectionEditPart.getTarget() instanceof LifelineEditPart) {
			final LifelineEditPart targetLifeLine = (LifelineEditPart) hostConnectionEditPart.getTarget();
			if (targetLifeLine.getModel() instanceof Shape) {
				final Shape view = (ShapeImpl) targetLifeLine.getModel();
				final Bounds bounds = (Bounds) view.getLayoutConstraint();
				Dimension newDimension = new Dimension(bounds.getWidth(), bounds.getHeight() + yMoveDelta);

				if (yMoveDelta > 0) {
					// get the max Y of the current life line with the delta
					final int maxY = bounds.getY() + newDimension.height();

					// We need to check if this is needed to resize other life lines
					final Collection<LifelineEditPart> lifeLineEditPartsToSkip = new HashSet<>(1);
					lifeLineEditPartsToSkip.add(targetLifeLine);
					LifelineEditPartUtil.resizeAllLifeLines(compoundCommand, hostConnectionEditPart, maxY, lifeLineEditPartsToSkip);
				}

				final ICommand boundsCommand = new SetResizeCommand(targetLifeLine.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newDimension);
				compoundCommand.add(new ICommandProxy(boundsCommand));
			}
		}

		return compoundCommand;
	}

	/**
	 * Add impossible to move the anchor connected inside of CoRegion
	 *
	 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=402970
	 */
	protected Command createMoveMessageEndCommand(Message message, EditPart endEditPart, MessageEnd end, int yLocation, LifelineEditPart lifeline, Request request) {
		if (end instanceof OccurrenceSpecification) {
			List<EditPart> empty = Collections.emptyList();
			return AdoneOccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification) end, null, yLocation, -1, lifeline, empty, request);
		} else if (end instanceof Gate) {
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

	protected Command getSelfLinkMoveCommand(BendpointRequest request, ConnectionNodeEditPart connectionPart, MessageEnd send, MessageEnd rcv, LifelineEditPart srcLifelinePart) {
		// Just do it, checking was finished by showing feedback.
		Object moveData = request.getExtendedData().get(MOVE_LINE_ORIENTATION_DATA);
		CompoundCommand compoudCmd = new CompoundCommand(CustomMessages.MoveMessageCommand_Label);
		// And make sure the self linked message can be customized by using bendpoints.
		compoudCmd.add(super.getBendpointsChangedCommand(request));
		PointList points = getConnection().getPoints();
		if (MOVED_UP.equals(moveData)) {
			Point sourceRefPoint = points.getFirstPoint();
			getConnection().translateToAbsolute(sourceRefPoint);
			Command srcCmd = getReconnectCommand(connectionPart, connectionPart.getSource(), sourceRefPoint, RequestConstants.REQ_RECONNECT_SOURCE);
			compoudCmd.add(srcCmd);
		} else if (MOVED_DOWN.equals(moveData)) {
			Point targetRefPoint = points.getLastPoint();
			getConnection().translateToAbsolute(targetRefPoint);
			// Self message not always has same source and target, such as MessageSync.
			Command tgtCmd = getReconnectCommand(connectionPart, connectionPart.getTarget(), targetRefPoint, RequestConstants.REQ_RECONNECT_TARGET);
			compoudCmd.add(tgtCmd);
		}
		return compoudCmd.unwrap();
	}

	protected Command getReconnectCommand(ConnectionNodeEditPart connectionPart, EditPart targetPart, Point location, String requestType) {
		// Create and set the properties of the request
		ReconnectRequest reconnReq = new ReconnectRequest();
		reconnReq.setConnectionEditPart(connectionPart);
		reconnReq.setLocation(location);
		reconnReq.setTargetEditPart(targetPart);
		reconnReq.setType(requestType);
		// add a parameter to bypass the move impact to avoid infinite loop
		reconnReq.getExtendedData().put(SequenceRequestConstant.DO_NOT_MOVE_EDIT_PARTS, true);
		Command cmd = targetPart.getCommand(reconnReq);
		return cmd;
	}

	/**
	 * Simplifies the feedback image for diagram element manipulation based on the type of request and the connection's routing kind.
	 * This method determines the appropriate feedback mechanism for various scenarios such as moving bendpoints or adjusting message
	 * connections. Feedback is tailored to the context, ensuring that visual cues are both meaningful and minimally intrusive.
	 * Special handling is implemented for message elements and based on the routing constraints to address specific user interactions.
	 *
	 * @param request
	 *            The request triggering the feedback, potentially a BendpointRequest for moving bendpoints or adjusting connections.
	 */
	@Override
	public void showSourceFeedback(Request request) {
		if (request instanceof BendpointRequest) {
			RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());
			if (getHost() instanceof MessageFoundEditPart || getHost() instanceof MessageLostEditPart) {
				showMoveLineSegFeedback((BendpointRequest) request);
			} else if (kind == RouterKind.SELF || kind == RouterKind.HORIZONTAL || kind == RouterKind.OBLIQUE || getConnection() instanceof MessageCreate) {
				if (getLineSegMode() != LineMode.OBLIQUE && REQ_MOVE_BENDPOINT.equals(request.getType())) {
					// Fixed bug about show feedback for moving bendpoints, make sure at least 3 points.
					List constraint = (List) getConnection().getRoutingConstraint();
					if (constraint.size() > 2) {
						super.showSourceFeedback(request);
					}
				} else {

					// 이동 모드인 경우에만 피드백 이미지 보이도록 설정 (2024-02-02)
					// 피드백 적용될 때 있고 안될 때 있다...ㅠㅠ
					// Feedback visibility conditioned on move mode, addressing inconsistencies in feedback application.
					showMoveMessageFeedback((BendpointRequest) request);

				}
				if (getLineSegMode() != LineMode.OBLIQUE && REQ_MOVE_BENDPOINT.equals(request.getType())) {
					showMoveLineSegFeedback((BendpointRequest) request);
				}
			}
		}
	}

	/**
	 * Displays visual feedback for a message move operation within a diagram.
	 *
	 * @param request
	 *            The bendpoint request containing the new location for the message.
	 */
	private void showMoveMessageFeedback(BendpointRequest request) {

		// Erase any previous feedback
		this.eraseTargetFeedback(request);

		// Copy current connection points for feedback calculation
		PointList linkPoints = getConnection().getPoints().getCopy();
		Point ptLoc = new Point(request.getLocation());
		getConnection().translateToRelative(ptLoc); // Adjust point to diagram coordinates

		int size = 0; // Initialize size for feedback bounds calculation

		// Determine connection routing kind
		RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());


		if (kind == RouterKind.OBLIQUE) {// self
			// Calculate size for oblique (self-referencing) connections
			size = linkPoints.getPoint(1).x - linkPoints.getPoint(0).x;
		} else {
			// Calculate size for straight or rectilinear connections
			size = linkPoints.getLastPoint().x - linkPoints.getFirstPoint().x;
		}

		// Calculate feedback bounds
		final Rectangle feedbackBounds = new Rectangle(linkPoints.getFirstPoint().x, ptLoc.y, size, 2);

		// Create and display the feedback figure based on the calculated bounds and routing kind
		createMoveMessageFeedbackFigure(feedbackBounds, kind); // 피드백 Figure 생성

	}

	/**
	 * Creates a visual feedback figure for message movement based on the provided bounds and router kind.
	 * This method dynamically adjusts the feedback figure's color and positioning based on the current
	 * editing mode, specifically targeting the Element Order Change Mode for enhanced user interaction.
	 * Future enhancements include adding a directional arrow to the feedback figure to indicate movement
	 * direction more clearly.
	 *
	 * @param feedbackBounds
	 *            The bounds within which the feedback figure should be displayed.
	 * @param kind
	 *            The type of router, affecting the feedback's visual presentation.
	 */
	private void createMoveMessageFeedbackFigure(Rectangle feedbackBounds, RouterKind kind) {

		// Initialize the feedback line figure with default properties.
		moveMessageFeedbackConnectionLine = new RectangleFigure();

		// Set feedback line color based on Element Order Change Mode activation.
		if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			moveMessageFeedbackConnectionLine.setBackgroundColor(ColorConstants.blue);
			moveMessageFeedbackConnectionLine.setForegroundColor(ColorConstants.blue);
		} else {
			moveMessageFeedbackConnectionLine.setForegroundColor(ColorConstants.gray);
		}

		// Apply the specified bounds to the feedback figure.
		moveMessageFeedbackConnectionLine.setBounds(feedbackBounds);

		// Add the feedback figure to the diagram for display.
		addFeedback(moveMessageFeedbackConnectionLine);

		// 추후 피드백 라인에 삼각 화살표 추가 요망(2024-02-05)
		// 삼각형을 생성하고 위치 설정

		// Note for future implementation: Add a triangular arrow to indicate movement direction.
		// The arrow should be dynamically positioned based on the feedback line's location and router kind.
		// Implementation details for creating and positioning the arrow are commented out and will be
		// developed in the future, including color adjustments based on the editing mode.

		// moveMessageFeedbackConnectionArrow = new PolygonShape();
		// 삼각형의 세 꼭짓점을 정의합니다.
		// PointList arrowPoints = new PointList();
		// arrowPoints.addPoint(10, 0); // 화살표 끝점 (우측)
		// arrowPoints.addPoint(0, 3); // 화살표 꼬리의 아래쪽
		// arrowPoints.addPoint(0, -3); // 화살표 꼬리의 위쪽
		// arrowPoints.addPoint(10, 0); // 화살표 끝점으로 복귀하여 폐곡선을 형성

		// arrowPoints.addPoint(new Point(0, 0)); // 화살표 끝점 (우측)
		// arrowPoints.addPoint(new Point(-10, -6)); // 화살표 끝점 (우측)
		// arrowPoints.addPoint(new Point(-10, 6)); // 화살표 끝점 (우측)
		// arrowPoints.addPoint(new Point(0, 0)); // 화살표 끝점 (우측)

		// arrowPoints.addPoint(new Point(10, 0)); // 화살표 끝점 (우측)
		// arrowPoints.addPoint(new Point(0, -6)); // 화살표 꼬리의 아래쪽
		// arrowPoints.addPoint(new Point(0, 6)); // 화살표 꼬리의 위쪽
		// arrowPoints.addPoint(new Point(10, 0)); // 화살표 끝점으로 복귀하여 폐곡선을 형성


		// moveMessageFeedbackConnectionArrow.setPoints(arrowPoints);
		//
		// if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
		// moveMessageFeedbackConnectionArrow.setForegroundColor(ColorConstants.darkBlue); // 삼각형의 선 색상 설정
		// moveMessageFeedbackConnectionArrow.setBackgroundColor(ColorConstants.darkBlue); // 삼각형의 내부 색상 설정
		// } else {
		// moveMessageFeedbackConnectionArrow.setForegroundColor(ColorConstants.gray); // 삼각형의 선 색상 설정
		// moveMessageFeedbackConnectionArrow.setBackgroundColor(ColorConstants.gray); // 삼각형의 내부 색상 설정
		// }
		//
		// moveMessageFeedbackConnectionArrow.setFill(true); // 삼각형 내부를 채움

		// RectangleFigure의 우측 끝 지점에 삼각형을 배치
		// int arrowWidth = 10; // Width of the arrow
		// int arrowHeight = 12; // Height of the arrow
		// RectangleFigure의 우측 끝 지점을 계산
		// int xPosition = feedbackBounds.x + feedbackBounds.width - arrowWidth; // Aligning arrow tip to the right end of the line
		// int yPosition = feedbackBounds.y - (arrowHeight / 2); // Centering arrow vertically
		// int yPosition = feedbackBounds.y; // Centering arrow vertically

		// 삼각형의 위치 및 크기 설정
		// moveMessageFeedbackConnectionArrow.setBounds(new Rectangle(xPosition, yPosition, arrowWidth, arrowHeight));

		// 삼각형을 RectangleFigure에 추가
		// moveMessageFeedbackConnectionLine.add(moveMessageFeedbackConnectionArrow);


		// moveMessageFeedbackConnectionLine.validate();

		// if (kind == RouterKind.OBLIQUE) {// self
		// self 인 경우 화살표 표시 하지 않음
		// addFeedback(moveMessageFeedbackConnectionArrow);
		// } else {
		// addFeedback(moveMessageFeedbackConnectionArrow);
		// }

		// return moveMessageFeedbackConnectionLine;

	}

	/**
	 * Provides visual feedback specifically for line segment movements within connection figures
	 * during the Element Order Change Mode. This enhanced functionality focuses on improving user
	 * interaction by offering immediate and clear visual cues for element reordering operations
	 * within diagrams. It ensures that feedback is only applied when the Element Order Change Mode
	 * is active, optimizing performance and user experience by avoiding unnecessary feedback in
	 * other modes. Special handling is included for self-connections and oblique connections, as well
	 * as specific message types where movement might not be applicable or desired.
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void showMoveLineSegFeedback(BendpointRequest request) {

		// Only applies feedback in ElementOrderChange mode to prevent misplaced feedback. (2024-02-01)
		if (!AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			return;
		}

		RouterKind kind = RouterKind.getKind(getConnection(), getConnection().getPoints());
		if (((getHost() instanceof MessageSyncEditPart || getHost() instanceof MessageAsyncEditPart || getHost() instanceof MessageDeleteEditPart) && kind == RouterKind.SELF) || kind == RouterKind.OBLIQUE) {
			if (router == null) {
				router = getConnection().getConnectionRouter();
				getConnection().setConnectionRouter(new DummyRouter());
			}
			PointList linkPoints = getConnection().getPoints().getCopy();
			Point ptLoc = SequenceUtil.getSnappedLocation(getHost(), request.getLocation());

			getConnection().translateToRelative(ptLoc);
			int dy = 0;
			int dx = 0;
			int from = 0, to = 0;
			int index = request.getIndex();
			if (index == 0) {
				dy = ptLoc.y - linkPoints.getFirstPoint().y;
				from = 0;
				to = 1;
				request.getExtendedData().put(MOVE_LINE_ORIENTATION_DATA, MOVED_UP);
			} else if (index == 1) {
				dx = ptLoc.x - linkPoints.getMidpoint().x;
				from = 1;
				to = 2;
				request.getExtendedData().put(MOVE_LINE_ORIENTATION_DATA, MOVED_HORIZONTAL);
			} else if (index == 2) {
				dy = ptLoc.y - linkPoints.getLastPoint().y;
				from = 2;
				to = 3;
				request.getExtendedData().put(MOVE_LINE_ORIENTATION_DATA, MOVED_DOWN);
			}
			if (getHost() instanceof MessageSyncEditPart && index > 1) {
				dy = 0;
			}
			// move points on link
			int size = linkPoints.size();
			if (from >= 0 && from < size && to >= 0 && to < size && from <= to) {
				for (int i = from; i <= to; i++) {
					Point p = linkPoints.getPoint(i);
					p.translate(dx, dy);
					linkPoints.setPoint(p, i);
				}
			}
			// link should not exceed lifeline bounds
			getConnection().setPoints(linkPoints);
			getConnection().getLayoutManager().layout(getConnection());
			return;
		}
		// Add impossible to dragging MessageLost and MessageFound. See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=403138
		if (getHost() instanceof MessageCreateEditPart || getHost() instanceof MessageDeleteEditPart || getHost() instanceof MessageLostEditPart || getHost() instanceof MessageFoundEditPart) {
			if (router == null) {
				router = getConnection().getConnectionRouter();
				getConnection().setConnectionRouter(new DummyRouter());
			}
			PointList linkPoints = getConnection().getPoints().getCopy();
			Point ptLoc = new Point(request.getLocation());
			ptLoc = SequenceUtil.getSnappedLocation(getHost(), ptLoc);
			getConnection().translateToRelative(ptLoc);

			int dy = ptLoc.y - linkPoints.getFirstPoint().y;
			int size = linkPoints.size();
			for (int i = 0; i < size; i++) {
				Point p = linkPoints.getPoint(i).translate(0, dy);
				linkPoints.setPoint(p, i);
			}
			if (checkBounds(linkPoints)) {
				getConnection().setPoints(linkPoints);
				getConnection().getLayoutManager().layout(getConnection());
			}
			return;
		}
		super.showMoveLineSegFeedback(request);
	}

	protected boolean checkBounds(PointList linkPoints) {
		if (linkPoints.getFirstPoint().y > linkPoints.getLastPoint().y) {
			return false;
		}
		EditPart sourcePart = ((ConnectionNodeEditPart) getHost()).getSource();
		if (sourcePart instanceof CInteractionEditPart && getHost() instanceof MessageFoundEditPart) {
			sourcePart = ((ConnectionNodeEditPart) getHost()).getTarget();
		}
		if (sourcePart instanceof CLifeLineEditPart) {
			CLifeLineEditPart sourceLifelineEditPart = (CLifeLineEditPart) sourcePart;
			NodeFigure sourceFigure = sourceLifelineEditPart.getPrimaryShape();
			Rectangle boundsToMatch = sourceFigure.getBounds().getCopy();
			// The bounds to match must be the top of the dashline of the source lifeline and the bottom of the target or source lifeline.
			// TODO_MIA case where target is not a lifeline(AbstractExecutionSpecificationEditPart)
			EditPart targetPart = ((ConnectionNodeEditPart) getHost()).getTarget();
			if (sourceLifelineEditPart.getStickerHeight() != -1) {
				boundsToMatch.setHeight(boundsToMatch.height - sourceLifelineEditPart.getStickerHeight());
				boundsToMatch.setY(boundsToMatch.y + sourceLifelineEditPart.getStickerHeight());
			}

			if (getHost() instanceof MessageCreateEditPart && targetPart instanceof CLifeLineEditPart) {
				NodeFigure targetFigure = (NodeFigure) ((NodeEditPart) targetPart).getPrimaryShape();
				// If the bottom of the target is higher
				int bottom = targetFigure.getBounds().bottom();
				if (bottom < boundsToMatch.bottom()) {
					int delta = boundsToMatch.bottom() - bottom + LIFELINE_MIN_HEIGHT;
					boundsToMatch.setHeight(boundsToMatch.height - delta);
				}
			}
			sourceFigure.translateToAbsolute(boundsToMatch);
			Rectangle boundsToCheck = linkPoints.getBounds();
			getConnection().translateToAbsolute(boundsToCheck);
			// check top y limit
			if (boundsToCheck.getTop().y <= boundsToMatch.getTop().y) {
				return false;
			}
		}
		// It seems the self message can be created on ES, too
		else if (sourcePart instanceof AbstractExecutionSpecificationEditPart) {
			AbstractExecutionSpecificationEditPart esep = (AbstractExecutionSpecificationEditPart) sourcePart;
			IFigure fig = esep.getFigure();
			Rectangle bounds = fig.getBounds().getCopy();
			fig.translateToAbsolute(bounds);
			Rectangle conBounds = linkPoints.getBounds().getCopy();
			getConnection().translateToAbsolute(conBounds);
			if (getHost() instanceof MessageSyncEditPart) {// Sync message is linked between two executions.
				if (conBounds.width < 2 || conBounds.height < 2
				// check top and bottom y limit
						|| conBounds.y <= bounds.y) {
					return false;
				}
			} else if ( // Don't change the orientation of self message.
			bounds.intersects(conBounds.getShrinked(1, 1))
					// make sure the line is not closest.
					|| conBounds.width < 2 || conBounds.height < 2
					// check top and bottom y limit
					|| conBounds.y <= bounds.y || conBounds.getBottom().y >= bounds.getBottom().y) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Restores the connection visibility and optionally resets its router after feedback has been erased.
	 * This method ensures that the connection's original visibility is restored and, if specified,
	 * the connection router is reset to its previous state. This is typically called after a bendpoint move
	 * operation to clean up any feedback visuals and restore the connection's routing.
	 *
	 * @param request
	 *            The bendpoint request triggering the feedback erasure.
	 * @param removeFeedbackFigure
	 *            A flag indicating whether to remove the feedback figure as part of the cleanup.
	 */
	@Override
	protected void eraseConnectionFeedback(BendpointRequest request, boolean removeFeedbackFigure) {
		// Ensure the connection is visible after feedback is erased.
		getConnection().setVisible(true);

		// Call superclass method to handle additional feedback erasure logic.
		super.eraseConnectionFeedback(request, removeFeedbackFigure);

		// Reset the connection router to its original if it was altered during the feedback.
		if (router != null) {
			getConnection().setConnectionRouter(router);
		}

		// Clear the router reference to indicate it's no longer in use.
		router = null;
	}

	/**
	 * Removes the visual feedback for a message move operation.
	 * This method checks if a feedback connection line exists and removes it,
	 * ensuring the diagram is cleaned up after a move operation.
	 *
	 * @param request
	 *            The request causing the feedback removal, not directly used but passed to super.
	 */
	@Override
	public void eraseTargetFeedback(Request request) {

		// Remove the move message feedback line if it exists.
		if (this.moveMessageFeedbackConnectionLine != null) {
			removeFeedback(this.moveMessageFeedbackConnectionLine);
			this.moveMessageFeedbackConnectionLine = null;
		}

		// Call the superclass implementation for any additional cleanup.
		super.eraseTargetFeedback(request);
	}


	/**
	 * A no-op (no operation) router implementation that extends AbstractRouter.
	 * This router is used in scenarios where routing behavior needs to be temporarily overridden
	 * or disabled without affecting the original routing logic of a Connection object. It essentially
	 * performs no routing, leaving the Connection's points as they are. This can be particularly useful
	 * in situations like providing visual feedback for diagram editing operations where the actual routing
	 * logic is not to be disturbed.
	 */
	static class DummyRouter extends AbstractRouter {

		/**
		 * Overrides the route method to perform no routing.
		 * This implementation simply does nothing, preserving the existing points of the Connection
		 * without any modification. It is intended to be used temporarily until the actual router
		 * is restored to the Connection.
		 *
		 * @param conn
		 *            The Connection object whose route is being calculated. In this case, the Connection
		 *            is left unchanged.
		 */
		@Override
		public void route(Connection conn) {
			// Intentionally left blank to avoid altering the Connection's routing.
		}
	}

}
