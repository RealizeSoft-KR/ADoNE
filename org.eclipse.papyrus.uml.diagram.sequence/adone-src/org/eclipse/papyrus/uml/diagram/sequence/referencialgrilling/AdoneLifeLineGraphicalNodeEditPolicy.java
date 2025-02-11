/*****************************************************************************
 * Copyright (c) 2017 CEA LIST, ALL4TEC, EclipseSource and others.
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
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 519621, 519756, 526191
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 531596
 *   EclipseSource - Bug 536641
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced message creation with constraints and BehaviorExecutionSpecification generation.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.util.StringStatics;
import org.eclipse.gmf.runtime.diagram.core.commands.SetConnectionAnchorsCommand;
import org.eclipse.gmf.runtime.diagram.core.commands.SetConnectionEndsCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.commands.wrappers.GMFtoGEFCommandWrapper;
import org.eclipse.papyrus.infra.gmfdiag.common.commands.CreateViewCommand;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneCreateExecutionSpecificationWithMessage;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneSetConnectionBendpointsCommand;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.routers.AdoneMessageRouter;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneUpdateLocationByNewMessageCreationRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.widgets.Display;

/**
 * Extends the base LifeLineGraphicalNodeEditPolicy to enhance message creation by introducing extensive
 * constraints aimed at minimizing user errors. It expands functionality by also generating BehaviorExecutionSpecification
 * alongside messages, ensuring a cohesive representation of interactions within sequence diagrams.
 * Additionally, this policy adapts the graphical representation of recursive messages to accurately display them
 * through the incorporation of RelativeBendpoints, handled by the AdoneSetConnectionBendpointsCommand.
 * This modification ensures that recursive messages are visually represented correctly, improving the clarity and
 * accuracy of the sequence diagram.
 */
public class AdoneLifeLineGraphicalNodeEditPolicy extends LifeLineGraphicalNodeEditPolicy {

	public static ConnectionRouter adoneMessageRouter = new AdoneMessageRouter();

	private boolean isDialogShown = false;

	/**
	 * Enhances the creation of synchronous and asynchronous messages in sequence diagrams by incorporating
	 * extensive pre-validation checks to preemptively address potential issues, ensuring a smoother and more
	 * accurate message creation process. This method reconfigures the original code to simultaneously generate
	 * BehaviorExecutionSpecifications (BES) alongside messages, addressing the need for a cohesive visual and
	 * semantic representation of interactions. Pre-validation includes ensuring messages start and end within
	 * permissible bounds, preventing creation from invalid lifelines or into disallowed regions, and ensuring
	 * the synchronization of message ends with BES boundaries. Additionally, this method employs a strategic
	 * approach to user feedback for invalid actions, aiming to provide clear, contextual reasons for any
	 * restrictions encountered during message creation, thereby enhancing usability and model integrity.
	 */
	@Override
	protected Command getSyncAsyncEdgeCommand(CreateConnectionViewAndElementRequest request, Command cmd) {

		ICommandProxy proxy = (ICommandProxy) request.getStartCommand();
		if (proxy == null) {
			return null;
		}

		GraphicalEditPart sourceEP = (GraphicalEditPart) request.getSourceEditPart();
		GraphicalEditPart targetEP = (GraphicalEditPart) request.getTargetEditPart();

		// Adjust the target edit part based on the connection end
		targetEP = getConnectionCompleteEditPart(request);
		if (targetEP == null) {
			return null;
		}

		// Extract components from the composite command to modify or analyze them
		CompositeCommand cc = (CompositeCommand) proxy.getICommand();
		Iterator commandItr = cc.iterator();
		commandItr.next(); // 0
		SetConnectionEndsCommand sceCommand = (SetConnectionEndsCommand) commandItr.next(); // 1
		SetConnectionAnchorsCommand scaCommand = (SetConnectionAnchorsCommand) commandItr.next(); // 2
		AdoneSetConnectionBendpointsCommand sbbCommand = (AdoneSetConnectionBendpointsCommand) commandItr.next(); // 3

		// Validation to ensure vertical alignment for message ends, crucial for synchronous/asynchronous message logic
		if (!sourceEP.equals(targetEP)) {
			// if (sbbCommand.getSourceRefPoint().y != sbbCommand.getTargetRefPoint().y) {
			// SnapToGrid 해제 적용으로 수정하였으나 절대값이 5 보다 커야 하는 이유에 대해 확인 필요 (2024-02-01)
			if (Math.abs(sbbCommand.getSourceRefPoint().y - sbbCommand.getTargetRefPoint().y) >= 5) {
				return UnexecutableCommand.INSTANCE;
			}
		} else {
			// SnapToGrid 해제 적용으로 수정 (2024-02-01)
			// if (sbbCommand.getSourceRefPoint().y >= sbbCommand.getTargetRefPoint().y) {
			if (sbbCommand.getSourceRefPoint().y > sbbCommand.getTargetRefPoint().y) {
				return UnexecutableCommand.INSTANCE;
			}
		}

		if (isMessageStartingAtBesEnd(sourceEP, request)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (!isValidMessageStartOnFirstOrSecondLifeline(sourceEP, request)) {
			return UnexecutableCommand.INSTANCE;
		}

		if (isSingleMessageAllowedFromFirstLifeline(sourceEP, request)) {
			return UnexecutableCommand.INSTANCE;
		}

		// Check if the target lifeline is a valid target within the operand lifelines
		// If the target is not covered by any operand (i.e., is an invalid message target within the operand lifelines),
		// it is necessary to provide a specific reason to the user. However, implementing a method to show a message
		// dialog directly here can be challenging due to the repetitive nature of this check during drag-and-drop operations.
		// A strategic approach is required to display the reason to the user in a non-intrusive way, ensuring the dialog
		// or notification appears only under appropriate circumstances, such as at the end of a drag operation or when
		// the command is finally executed or rejected. Further investigation and implementation are needed to provide
		// detailed user feedback without disrupting the user experience.
		// if (!isValidMessageTargetWithinOperandLifelines(targetEP, request) && !isDialogShown) {
		if (!isValidMessageTargetWithinOperandLifelines(targetEP, request)) {

			// Code to conditionally show a dialog or notification should be placed here,
			// taking into account the user experience and interaction flow.
			// Implementing a solution that provides specific feedback to the user
			// about why the action is invalid requires careful consideration.

			// showOneTimeMessage();
			isDialogShown = true; // Prevent the dialog from showing again
			return UnexecutableCommand.INSTANCE;

		}

		// If all validations pass, the method proceeds to enhance the original command with additional logic

		Command superSyncAsyncEdgeCommand = super.getSyncAsyncEdgeCommand(request, cmd);

		if (superSyncAsyncEdgeCommand instanceof CompoundCommand) {

			CompoundCommand superCompoundCommand = (CompoundCommand) superSyncAsyncEdgeCommand;

			Object removeTargetCommand = null;

			for (Object child : superCompoundCommand.getCommands()) {
				if (child instanceof GMFtoGEFCommandWrapper) {
					// Identifies a specific command to remove based on criteria not detailed here.
					removeTargetCommand = child;
				}
			}

			if (removeTargetCommand != null) {

				CompoundCommand reConstructedCommand = new CompoundCommand();

				// Reconstruct the command without the targeted removal, and add enhancements such as BES creation
				for (Object child : superCompoundCommand.getCommands()) {
					if (child != removeTargetCommand) {
						reConstructedCommand.add((Command) child);
					}
				}

				AdoneCreateExecutionSpecificationWithMessage createExecutionSpecificationwithMsg = new AdoneCreateExecutionSpecificationWithMessage(getDiagramEditPart(getHost()).getEditingDomain(), request, request.getTargetEditPart());
				reConstructedCommand.add(new GMFtoGEFCommandWrapper(createExecutionSpecificationwithMsg));

				Point msgLocation = new Point();
				msgLocation.setX(request.getLocation().x);
				msgLocation.setY(request.getLocation().y);

				// Prepare the location for the new message to move related editparts accordingly
				AdoneUpdateLocationByNewMessageCreationRequest updateLocationRequest = new AdoneUpdateLocationByNewMessageCreationRequest();
				updateLocationRequest.setTargetEditPart((GraphicalEditPart) request.getTargetEditPart().getParent());
				updateLocationRequest.setLocation(msgLocation);

				// Execute the command to move the graphical representation to the specified location.
				Command moveCommand = ((GraphicalEditPart) request.getTargetEditPart().getParent()).getCommand(updateLocationRequest);

				if (moveCommand != null) {
					reConstructedCommand.add(moveCommand);
				}

				// Generate a command to adjust the height of the first BehaviorExecutionSpecification (BES) element,
				// ensuring it visually represents the start and end of the execution span correctly.
				Command resizeFirstBesEpCommand = AdoneSequenceUtil.getResizeFirstBesEpHeightCommand(targetEP, msgLocation);

				if (resizeFirstBesEpCommand != null) {
					reConstructedCommand.add(resizeFirstBesEpCommand);
				}

				// Returns the enhanced compound command
				return reConstructedCommand;

			}

		}

		return superSyncAsyncEdgeCommand;
	}

	private void showOneTimeMessage() {
		Display.getDefault().asyncExec(() -> {
			MessageDialog.openInformation(
					Display.getDefault().getActiveShell(),
					"Information", // Title
					"Target is not a covered lifeline." // Message
			);
		});
	}


	/**
	 * Validates if the target lifeline for a message creation request within an InteractionOperand is among the covered lifelines.
	 * This ensures that messages created within the bounds of an InteractionOperand are only targeted to lifelines that are
	 * logically part of the operand's scope, as defined by the encompassing CombinedFragment.
	 *
	 * @param targetEp
	 *            The target EditPart for the message, typically expected to be a LifelineEditPart.
	 * @param request
	 *            The creation request containing the location for the new connection view and element.
	 * @return true if the target lifeline is valid for message creation within the operand; false if the target lifeline
	 *         is not covered by the CombinedFragment or if the target is not a LifelineEditPart.
	 */
	private boolean isValidMessageTargetWithinOperandLifelines(EditPart targetEp, CreateConnectionViewAndElementRequest request) {

		if (!(targetEp instanceof LifelineEditPart)) {
			return false;
		}

		// The X and Y coordinates of the message creation location.
		Point location = request.getLocation().getCopy();
		int messageX = location.x;
		int messageY = location.y;

		// Retrieve the edit part registry to search for InteractionOperandEditParts.
		Map<?, ?> editPartRegistry = targetEp.getViewer().getEditPartRegistry();
		for (Object value : editPartRegistry.values()) {
			if (value instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart operand = (InteractionOperandEditPart) value;

				// Get the absolute bounds of the operand.
				Rectangle operandBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(operand);

				// Check if the message creation location is within the bounds of an InteractionOperand.
				if (operandBounds.contains(messageX, messageY)) {
					EditPart parent = operand.getParent().getParent();
					if (parent instanceof CombinedFragmentEditPart) {

						// Retrieve the CombinedFragment associated with the operand.
						CombinedFragmentEditPart combinedFragmentEp = (CombinedFragmentEditPart) parent;

						// List of lifelines covered by the CombinedFragment.
						List<LifelineEditPart> coveredLifelines = AdoneSequenceUtil.getCoveredLifelinesByModel(combinedFragmentEp);

						// Validate if the target lifeline is covered by the CombinedFragment.
						return coveredLifelines.contains(targetEp);
					}
				}
			}
		}

		// If the message does not start within an InteractionOperand, consider the target lifeline valid.
		return true;
	}


	/**
	 * Checks if starting a message from the first lifeline adheres to the rule of allowing only a single message initiation.
	 * This method is designed to enforce a modeling constraint that limits message creation from the first lifeline
	 * to a single instance, ensuring the sequence diagram remains logically coherent and visually uncluttered.
	 *
	 * @param sourceEp
	 *            The source EditPart, typically expected to be a Lifeline, from which the message creation request originates.
	 * @param request
	 *            The request to create a new connection view and element, carrying the location information.
	 * @return true if there is already at least one message starting from the first lifeline; false otherwise.
	 */
	private boolean isSingleMessageAllowedFromFirstLifeline(EditPart sourceEp, CreateConnectionViewAndElementRequest request) {

		// Only proceed if the source EditPart is a Lifeline.
		if (!(sourceEp instanceof LifelineEditPart)) {
			return false;
		}

		Map<?, ?> editPartRegistry = sourceEp.getViewer().getEditPartRegistry();
		LifelineEditPart firstLifeline = null;
		int minX = Integer.MAX_VALUE;

		// Identify the first lifeline based on the minimum X coordinate.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				LifelineEditPart lifeline = (LifelineEditPart) value;
				Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(lifeline);
				if (bounds.x < minX) {
					minX = bounds.x; // Update minX to the lowest found so far.
					firstLifeline = lifeline; // Update the first lifeline.
				}
			}
		}

		// Check if the source EditPart is the first lifeline identified.
		if (sourceEp != firstLifeline) {
			return false;
		}

		// Count the number of messages originating from the first lifeline.
		int messageCount = 0;
		for (Object value : editPartRegistry.values()) {
			if (value instanceof AbstractMessageEditPart) {
				AbstractMessageEditPart message = (AbstractMessageEditPart) value;
				if (message.getSource() == firstLifeline) {
					messageCount++; // Increment for each message starting from the first lifeline.
				}
			}
		}

		// If there is at least one message starting from the first lifeline, return true.
		return messageCount > 0;
	}


	/**
	 * Checks if a message creation request is valid based on its starting position being on the first or second lifeline.
	 * This method ensures that messages are only started from valid positions to maintain the logical flow of the sequence diagram.
	 * Specifically, it prevents messages from being incorrectly initiated from within multiple Behavior Execution Specifications (BES)
	 * or outside the first two lifelines, which could represent invalid or unintended interactions.
	 *
	 * @param sourceEp
	 *            The source EditPart, typically a Lifeline, where the connection creation request is initiated.
	 * @param request
	 *            The request to create a new connection view and element, carrying the location information.
	 * @return true if the message starts from a valid location on the first or second lifeline; false otherwise.
	 */
	private boolean isValidMessageStartOnFirstOrSecondLifeline(EditPart sourceEp, CreateConnectionViewAndElementRequest request) {

		// Only proceed if the source EditPart is a Lifeline. (2024-01-03)
		if (!(sourceEp instanceof LifelineEditPart)) {
			return false;
		}

		// The Y-coordinate of the message start location.
		Point location = request.getLocation().getCopy();
		int messageY = location.y;

		Map<?, ?> editPartRegistry = sourceEp.getViewer().getEditPartRegistry();

		// Counter for BES that encompass the message start location.
		int besCount = 0;

		// Count BES that include the message start location.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof BehaviorExecutionSpecificationEditPart) {
				BehaviorExecutionSpecificationEditPart bes = (BehaviorExecutionSpecificationEditPart) value;
				Rectangle besBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(bes);
				if (messageY >= besBounds.y && messageY <= (besBounds.y + besBounds.height)) {
					besCount++;
				}
			}
		}

		// If the message starts within multiple BES, prevent starting from a recursive message's BES
		if (besCount > 1) {
			return false;
		}

		// Collect and sort the bounds of all lifelines.
		List<Rectangle> lifelineBounds = new ArrayList<>();
		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				LifelineEditPart lifeline = (LifelineEditPart) value;
				Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(lifeline);
				lifelineBounds.add(bounds);
			}
		}
		lifelineBounds.sort(Comparator.comparingInt(r -> r.x));

		// Determine if the message starts on the first or second lifeline.
		Rectangle sourceBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((LifelineEditPart) sourceEp);

		if (lifelineBounds.size() >= 2 &&
				!sourceBounds.equals(lifelineBounds.get(0)) &&
				!sourceBounds.equals(lifelineBounds.get(1))) {
			return false;
		}

		// Check if the message start is within any BES.
		boolean isWithinBES = false;
		for (Object value : editPartRegistry.values()) {
			if (value instanceof BehaviorExecutionSpecificationEditPart) {
				BehaviorExecutionSpecificationEditPart bes = (BehaviorExecutionSpecificationEditPart) value;
				Rectangle besBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(bes);

				if (messageY >= besBounds.y && messageY <= (besBounds.y + besBounds.height)) {
					isWithinBES = true;
					break;
				}
			}
		}

		// For messages starting on the second lifeline, ensure it is not within a BES.
		if (lifelineBounds.size() > 1) {
			if (!isWithinBES && sourceBounds.equals(lifelineBounds.get(1))) {
				return false;
			}
		}

		// The message start is considered valid.
		return true;
	}

	/**
	 * Determines if a message starts exactly at the end of a Behavior Execution Specification (BES).
	 * This method is crucial for preventing cases where the BES's getFinish is deleted and the message's
	 * ReceiveEvent is incorrectly assigned to the end of the BES, which can lead to inconsistencies in the model.
	 * It checks if the message creation location is exactly at or within a small threshold of the end of a BES,
	 * indicating a potential for such incorrect assignment. Future enhancements should include verifying the
	 * correctness of the message's start point relative to the BES.
	 *
	 * The method is designed to prevent incorrect model manipulations that could arise when messages are
	 * created at the boundaries of execution specifications, ensuring the integrity of sequence diagrams.
	 *
	 * @param sourceEp
	 *            The source EditPart, typically a Lifeline or an Execution Specification, where the connection creation request is initiated.
	 * @param request
	 *            The request to create a new connection view and element, carrying the location information where the message starts.
	 * @return true if the message starts at the exact end of a BES or within a small threshold; false otherwise.
	 */
	private boolean isMessageStartingAtBesEnd(EditPart sourceEp, CreateConnectionViewAndElementRequest request) {

		// 메시지가 BES의 끝 지점에서 시작하는 경우 해당 BES 의 getFinish 가 삭제되고 메시지의 ReceiveEvent 로 잘 못 세팅되는 케이스 방지(
		// 추후 메시지의 Start 지점 Bes 가 맞는지 확인하는 로직 보강 필요 (2024-01-03)

		// Copy the location to avoid modifying the original request.
		Point location = request.getLocation().getCopy();

		// Y-coordinate where the message creation is requested.
		int messageY = location.y;

		// Iterate through all registered edit parts to find Behavior Execution Specification EditParts.
		Map<?, ?> editPartRegistry = sourceEp.getViewer().getEditPartRegistry();
		for (Object value : editPartRegistry.values()) {
			if (value instanceof BehaviorExecutionSpecificationEditPart) {
				BehaviorExecutionSpecificationEditPart besEditPart = (BehaviorExecutionSpecificationEditPart) value;
				Rectangle r = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(besEditPart);
				int besY = r.y + r.height;

				if (messageY == besY) {
					return true;
				}

				// Check if the message start location matches or is very close to the BES end.
				if (Math.abs(messageY - besY) <= 5) {
					// Log the proximity of the message start to the BES end for debugging.
					System.out.println("messageY: " + messageY + ", besY: " + besY + ", difference: " + Math.abs(messageY - besY));
					return true;
				}
			}
		}

		// No matching BES end found near the message start location.
		return false;
	}

	@Override
	protected ConnectionRouter getDummyConnectionRouter(CreateConnectionRequest req) {
		return adoneMessageRouter;
	}

	/**
	 * Initiates the command to create a new connection in response to a user's request, customized for sequence diagrams.
	 * This method ensures that connections snap to grid and initializes with necessary commands for creating the view,
	 * setting connection ends, specifying anchors, and configuring bendpoints. It utilizes a customized
	 * AdoneSetConnectionBendpointsCommand to ensure recursive messages are graphically displayed correctly,
	 * addressing specific visual representation requirements for sequence diagrams.
	 *
	 * The process involves creating a view for the new connection, setting the source and target ends,
	 * specifying the connection anchors, and configuring bendpoints for accurate visual alignment.
	 * This comprehensive approach ensures the integrity and clarity of connections, particularly for recursive messages,
	 * within sequence diagrams.
	 *
	 * @param request
	 *            The user's request to create a new connection.
	 * @return A command that, when executed, will create the new connection with appropriate visual representation.
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {

		// Adjust the request's location to adhere to the diagram's snap-to-grid settings for precision.
		request.setLocation(SequenceUtil.getSnappedLocation(getHost(), request.getLocation()));

		if (!(request instanceof CreateConnectionViewRequest)) {
			return null; // Ensure the request is the correct type for creating connection views.
		}
		CreateConnectionViewRequest req = (CreateConnectionViewRequest) request;

		CompositeCommand cc = new CompositeCommand(DiagramUIMessages.Commands_CreateCommand_Connection_Label);
		Diagram diagramView = ((View) getHost().getModel()).getDiagram();

		// Obtain the editing domain from the host edit part to execute model changes.
		TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost()).getEditingDomain();

		// Create the command to create the view for the new connection in the diagram.
		CreateCommand createCommand = new CreateViewCommand(editingDomain, req.getConnectionViewDescriptor(), diagramView.getDiagram());
		setViewAdapter((IAdaptable) createCommand.getCommandResult().getReturnValue());

		SetConnectionEndsCommand sceCommand = new SetConnectionEndsCommand(editingDomain, StringStatics.BLANK);
		sceCommand.setEdgeAdaptor(getViewAdapter());
		sceCommand.setNewSourceAdaptor(new EObjectAdapter(getView()));
		ConnectionAnchor sourceAnchor = getConnectableEditPart().getSourceConnectionAnchor(request);
		SetConnectionAnchorsCommand scaCommand = new SetConnectionAnchorsCommand(editingDomain, StringStatics.BLANK);
		scaCommand.setEdgeAdaptor(getViewAdapter());
		scaCommand.setNewSourceTerminal(getConnectableEditPart().mapConnectionAnchorToTerminal(sourceAnchor));

		// Initialize the command to configure the bendpoints of the connection for visual representation.
		AdoneSetConnectionBendpointsCommand sbbCommand = new AdoneSetConnectionBendpointsCommand(editingDomain);
		sbbCommand.setEdgeAdapter(getViewAdapter());

		// Compose all the commands into the composite command to form the complete creation process.
		cc.compose(createCommand);
		cc.compose(sceCommand);
		cc.compose(scaCommand);
		cc.compose(sbbCommand);

		// Wrap the composite command into a GEF command and set it as the start command for the request.
		Command c = new ICommandProxy(cc);
		request.setStartCommand(c);

		// Return the composite command ready for execution.
		return c;

	}

	/**
	 * Completes the command for creating a connection once the target endpoint is determined.
	 * This method sets the target endpoint for the connection, calculates the appropriate
	 * connection anchors based on the source and target nodes, and establishes the bendpoints
	 * for the connection path. It ensures the connection visually connects the source and target
	 * nodes accurately within the diagram.
	 *
	 * @param request
	 *            The user's request to create a new connection, including the source and target information.
	 * @return A command that, when executed, finalizes the creation of the connection between the source and target nodes.
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {

		ICommandProxy proxy = (ICommandProxy) request.getStartCommand();
		if (proxy == null) {
			return null;
		}

		// reset the target edit-part for the request
		INodeEditPart targetEP = getConnectionCompleteEditPart(request);
		if (targetEP == null) {
			return null;
		}

		CompositeCommand cc = (CompositeCommand) proxy.getICommand();
		ConnectionAnchor targetAnchor = targetEP.getTargetConnectionAnchor(request);
		Iterator commandItr = cc.iterator();

		commandItr.next(); // Skip to the relevant command.

		// Command to set connection ends.
		SetConnectionEndsCommand sceCommand = (SetConnectionEndsCommand) commandItr.next(); // 1
		sceCommand.setNewTargetAdaptor(new EObjectAdapter(((IGraphicalEditPart) targetEP).getNotationView()));

		// Set the connection anchor terminal for the target.
		SetConnectionAnchorsCommand scaCommand = (SetConnectionAnchorsCommand) commandItr.next(); // 2
		scaCommand.setNewTargetTerminal(targetEP.mapConnectionAnchorToTerminal(targetAnchor));

		// Cache the adapter for the connection view for further operations.
		setViewAdapter(sceCommand.getEdgeAdaptor());

		// Calculate the bendpoints for the connection path based on the source and target locations.
		INodeEditPart sourceEditPart = (INodeEditPart) request.getSourceEditPart();
		ConnectionAnchor sourceAnchor = sourceEditPart.mapTerminalToConnectionAnchor(scaCommand.getNewSourceTerminal());

		PointList pointList = new PointList();
		if (request.getLocation() == null) {
			pointList.addPoint(sourceAnchor.getLocation(targetAnchor.getReferencePoint()));
			pointList.addPoint(targetAnchor.getLocation(sourceAnchor.getReferencePoint()));
		} else {
			pointList.addPoint(sourceAnchor.getLocation(request.getLocation()));
			pointList.addPoint(targetAnchor.getLocation(request.getLocation()));
		}

		// Finalize the bendpoints configuration for the connection.
		// For recursive messages, it is necessary to set RelativeBendpoints, and the detailed logic for this
		// is handled within the AdoneSetConnectionBendpointsCommand.
		AdoneSetConnectionBendpointsCommand sbbCommand = (AdoneSetConnectionBendpointsCommand) commandItr.next(); // 3
		sbbCommand.setNewPointList(pointList, sourceAnchor.getReferencePoint(), targetAnchor.getReferencePoint());

		// Return the initial command to finalize the connection creation process.
		return request.getStartCommand();
	}


}
