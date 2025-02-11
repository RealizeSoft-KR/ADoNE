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
 *   Celine Janssens (ALL4TEC) - Bug 507348
 *
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneChangeElementOrderRequest;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneSetMessageSignatureRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SelectMessagesEditPartTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Message;

public class AdoneDragMessageEditPartTracker extends SelectMessagesEditPartTracker {

	private boolean isDragging = false;

	public AdoneDragMessageEditPartTracker(ConnectionEditPart owner) {
		super(owner);
	}

	@Override
	protected void performSelection() {
		super.performSelection();
		this.handleChangeElementOrderMode();
		return;
	}

	/**
	 * Calculates the appropriate cursor to display based on the current state of the command execution
	 * process. If the command associated with the current action cannot be executed, this method
	 * forcibly sets and returns a "Disabled" cursor, indicating to the user that the action is not
	 * permissible. This functionality enhances user feedback by visually distinguishing between
	 * executable and non-executable actions, improving the overall user experience in interacting
	 * with the diagram.
	 *
	 * @return Cursor The cursor to be displayed, which may be a default cursor or a disabled cursor
	 *         depending on whether the current command can be executed.
	 */
	@Override
	protected Cursor calculateCursor() {

		if (isInState(STATE_TERMINAL)) {
			return null;
		} else if (isInState(STATE_INITIAL) || isInState(STATE_DRAG)) { // 메시지 선택 시 리턴 처리
			// Early return for initial or drag states, typically not requiring cursor change
			return null;
		} else {
			// Placeholder for potential additional checks or operations
		}

		Command command = getCurrentCommand();

		// If a command is present but cannot be executed, set and return a disabled cursor
		if (command != null && !command.canExecute()) {
			// Forcibly set the disabled cursor if not already set (2024-01-13)
			if (getDisabledCursor() == null) {
				setDisabledCursor(Cursors.NO);
			}

			return getDisabledCursor();
		}

		// Return the default cursor if no conditions for a disabled cursor are met
		return getDefaultCursor();
	}

	/**
	 * Executes the specified command and then forcefully exits the change element order mode. This ensures
	 * that after completing an action, such as reordering elements, the diagram returns to its default
	 * interaction state, preventing unintended changes to element order.
	 *
	 * @param command
	 *            The command to be executed.
	 */
	@Override
	protected void executeCommand(Command command) {
		super.executeCommand(command);
		AdoneElementOrderChangeManager.getInstance().setElementOrderChangeMode(false);
	}

	/**
	 * Overrides the getCommand method to ensure that if the retrieved command cannot be executed, an
	 * unexecutable command instance is returned instead. This approach prevents any attempt to execute
	 * a command that is invalid or not permitted in the current context, enhancing the robustness and
	 * reliability of command execution within the editor.
	 *
	 * @return Command The executable command if valid, or an instance of UnexecutableCommand if the
	 *         original command cannot be executed.
	 */
	@Override
	protected Command getCommand() {
		Command command = super.getCommand(); // Retrieve the command from the superclass

		// Check if the command is non-null and cannot be executed
		if (command != null && !command.canExecute()) {
			// Return an unexecutable command to signify that the action is not allowed
			return UnexecutableCommand.INSTANCE;
		}

		// Return the original command if it's executable
		return command;
	}


	/**
	 * Handles the change of element order mode, activated by pressing Ctrl. It sets the mode to allow
	 * reordering of message elements within the diagram, applies visual feedback, and executes
	 * the reordering command if applicable.
	 */
	private void handleChangeElementOrderMode() {

		// Check if Ctrl key is down to activate change mode
		if (getCurrentInput().isControlKeyDown()) {

			// Activate change element order mode
			AdoneElementOrderChangeManager.getInstance().setElementOrderChangeMode(true);

			// Get the current source edit part
			EditPart sourceEditPart = super.getSourceEditPart();
			List<EditPart> sourceEditParts = new ArrayList<>();
			sourceEditParts.add(sourceEditPart);

			// Add visual feedback for the message
			this.addChangeMessageOrderFeedback((GraphicalEditPart) sourceEditPart);

			// Prepare and execute the change order command
			IDiagramEditDomain diagramEditDomain = (IDiagramEditDomain) sourceEditPart.getViewer().getEditDomain();
			TransactionalEditingDomain tranactionalEditingDomain = ((IGraphicalEditPart) sourceEditPart).getEditingDomain();

			AdoneChangeElementOrderRequest request = new AdoneChangeElementOrderRequest(sourceEditParts, diagramEditDomain, tranactionalEditingDomain);
			Command changeOrderCommand = SequenceUtil.getInteractionCompartment(sourceEditPart).getCommand(request);

			// Execute the command if it's not null
			if (changeOrderCommand != null) {
				diagramEditDomain.getDiagramCommandStack().execute(changeOrderCommand);
			}

		}

	}

	/**
	 * Adds visual feedback to the selected message and following behaviorExecutionSpecification
	 * by changing its outline color. This feedback helps users identify the message currently being reordered.
	 *
	 * @param msgEp
	 *            The source EditPart representing the message.
	 */
	private void addChangeMessageOrderFeedback(GraphicalEditPart msgEp) {

		if (msgEp != null) {
			IFigure figure = msgEp.getFigure();
			if (figure != null) {
				AdoneElementOrderChangeManager.getInstance().addSelectedFigure(figure);
			}

			// get the connected behaviorExecutionSpec at message received event and add to selectedFigures
			Message msg = (Message) ((AbstractMessageEditPart) msgEp).resolveSemanticElement();
			BehaviorExecutionSpecification bes = AdoneSequenceUtil.getFollowingBehaviorExeSpec(msg);

			if (bes != null) {
				if (AdoneSequenceUtil.getEditPartFromSemantic(msgEp, bes) != null) {
					BehaviorExecutionSpecificationEditPart besEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(msgEp, bes);
					figure = besEp.getContentPane();
					if (figure != null) {
						AdoneElementOrderChangeManager.getInstance().addSelectedFigure(figure);
					}
				}
			}
		}
	}

	/**
	 * Overrides the double-click event handling to prevent any action from being taken on a double-click.
	 * This customization is intended to disable the default behavior associated with double-clicking within
	 * the context of this tracker, enhancing control over user interactions and potentially avoiding unintended
	 * actions.
	 *
	 * @param button
	 *            The mouse button number that was double-clicked.
	 * @return boolean Always returns true to indicate that the double-click event has been handled and no
	 *         further action is required.
	 */
	@Override
	protected boolean handleDoubleClick(int button) {
		// Disable default double-click behavior
		return true; // Indicate that the double-click has been handled
	}

	/**
	 * Initiates the process to change the message signature by displaying a dialog for the user to select
	 * a new operation signature for the message. This method constructs and issues a request for changing
	 * the message signature based on the user's selection. If the command generated from the request is
	 * valid and executable, it executes the command, effectively updating the message's signature.
	 */
	protected void setMessageSignature() {

		EditPart selectedEditParts = getSourceEditPart();

		if (selectedEditParts != null) {

			IGraphicalEditPart msgEditPart = (IGraphicalEditPart) selectedEditParts;

			// Create a new request specifically for setting the message signature
			AdoneSetMessageSignatureRequest connectionRequest = new AdoneSetMessageSignatureRequest();
			connectionRequest.setTargetEditPart(msgEditPart);
			connectionRequest.setType(AdoneSetMessageSignatureRequest.REQ_SET_MESSAGE_SIGNATURE);

			Command command = msgEditPart.getCommand(connectionRequest);

			if (command != null) {
				setCurrentCommand(command);
				executeCurrentCommand();
				// selectAddedObject(getCurrentViewer(), DiagramCommandStack
				// .getReturnValues(command));
			}

			// Deactivate this tracker after the command execution
			deactivate();

		}

	}

	/**
	 * Handles the drag-in-progress event by initiating visual feedback for change order mode the first time
	 * it is called during a drag operation. This method ensures that feedback is shown only once at the start
	 * of a drag, avoiding repetitive actions and enhancing performance.
	 *
	 * @return boolean Returns the result of the superclass's handleDragInProgress method.
	 */
	@Override
	protected boolean handleDragInProgress() {
		if (!isDragging) {
			// Execute once at the start of dragging
			// this.showChangeOrderFeedback();
			isDragging = true;
		}
		return super.handleDragInProgress();
	}

	/**
	 * Overrides the performDirectEdit method to prevent direct editing functionality. This method is
	 * intentionally left empty to disable the default direct edit behavior, ensuring that direct editing
	 * actions do not trigger in response to user interactions that would normally initiate such edits.
	 */
	@Override
	protected void performDirectEdit() {
		// Intentionally left empty to disable direct editing
	}

	/**
	 * Overrides the performOpen method to disable the default behavior that typically handles opening or
	 * double-click actions. By leaving this method empty, we prevent any unintended actions from being
	 * executed when an open or double-click event occurs, ensuring a controlled user experience.
	 */
	@Override
	protected void performOpen() {
		// Intentionally left empty to disable the open action
	}

}
