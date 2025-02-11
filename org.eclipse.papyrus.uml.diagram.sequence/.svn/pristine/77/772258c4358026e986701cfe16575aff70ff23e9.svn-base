/*****************************************************************************
 * Copyright (c) 2019 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced drag functionality for Lifeline in Sequence diagrams
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.tools;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.papyrus.infra.gmfdiag.common.snap.PapyrusDragEditPartsTrackerEx;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneChangeElementOrderRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;

/**
 * This class enhances the dragging functionality for Lifelines in Sequence Diagrams when Ctrl is held
 * down. It changes the Lifeline's line color to blue and activates Change Element Order Mode, allowing the
 * Lifeline to be repositioned horizontally across other Lifelines but restricts vertical movement.
 * In Change Element Mode, a Lifeline can bypass the positions of other Lifelines, offering greater
 * flexibility in reorganizing diagram elements. Without this mode, moving a Lifeline also shifts
 * adjacent Lifelines to the right, maintaining diagram structure. Movement is exclusively allowed along
 * the X-axis to adhere to design constraints. Additionally, dynamic assessment of Lifeline positions
 * during movement introduces a blue insertion point bar for enhanced visual guidance and convenience in
 * positioning.
 */
public class AdoneDragLifelineEditPartsTracker extends PapyrusDragEditPartsTrackerEx {


	private boolean isDragging = false;

	public AdoneDragLifelineEditPartsTracker(final EditPart sourceEditPart) {
		super(sourceEditPart);
	}

	@Override
	protected void performSelection() {
		super.performSelection();
		this.handleChangeElementOrderMode();
		return;
	}

	/**
	 * Handles the change of element order mode, activated by pressing Ctrl. It sets the mode to allow
	 * reordering of Lifeline elements within the diagram, applies visual feedback, and executes
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

			// Add visual feedback for the combined fragment
			this.addChangeLifelineOrderFeedback(sourceEditPart);

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
	 * Adds visual feedback to the selected Lifeline by changing its outline color. This feedback
	 * helps users identify the Combined Fragment currently being reordered.
	 *
	 * @param sourceEditPart
	 *            The source EditPart representing the Lifeline.
	 */
	private void addChangeLifelineOrderFeedback(EditPart sourceEp) {

		if (sourceEp != null) {

			IGraphicalEditPart gEp = (IGraphicalEditPart) sourceEp;
			IFigure figure = null;

			if (gEp instanceof LifelineEditPart) {
				LifelineEditPart lEp = (LifelineEditPart) gEp;
				figure = lEp.getContentPane();
			} else {
				figure = gEp.getFigure();
			}

			// If the figure is not null, add it to the selected figures for visual feedback
			if (figure != null) {
				AdoneElementOrderChangeManager.getInstance().addSelectedFigure(figure);
			}

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
			this.showChangeOrderFeedback();
			isDragging = true;
		}
		return super.handleDragInProgress();
	}


	/**
	 * Displays visual feedback for all figures currently selected for reordering. This method iterates
	 * through the list of selected figures and applies feedback to each, visually indicating that they
	 * are part of an ongoing reordering process.
	 */
	private void showChangeOrderFeedback() {
		for (IFigure movingFigure : AdoneElementOrderChangeManager.getInstance().getSelectedFigureList()) {
			// Apply visual feedback to each moving figure
			addFeedback(movingFigure);
		}
	}


	/**
	 * Handles the mouse button release event, marking the end of a drag operation. This method disables
	 * dragging mode and the change element order mode, ensuring the system returns to its default state
	 * after a drag is completed. Additionally, it reveals the source edit part in the viewer, ensuring
	 * the dragged element remains visible, potentially addressing unintended scroll movements.
	 *
	 * @param button
	 *            The mouse button number that was released.
	 * @return boolean Returns the result of the superclass's handleButtonUp method.
	 */
	@Override
	protected boolean handleButtonUp(int button) {

		if (isDragging) {
			// Execute only once when dragging ends
			isDragging = false; // Reset dragging state
			AdoneElementOrderChangeManager.getInstance().setElementOrderChangeMode(false);
		}

		boolean result = super.handleButtonUp(button);
		// Reveal the source edit part to keep it in view, addressing any unintended scrolling (2023-11-03)
		if (getSourceEditPart() != null) {
			getCurrentViewer().reveal(AdoneSequenceUtil.getSequenceDiagramEditPart(getSourceEditPart()));
		}

		return result;

	}


	@Override
	protected void eraseTargetFeedback() {
		super.eraseTargetFeedback();
		this.eraseChangeOrderFeedback();
	}


	/**
	 * Removes visual feedback from figures involved in a reordering process once the operation is completed
	 * or canceled. It ensures that all temporary visual cues are cleared, maintaining the integrity
	 * of the diagram's visual representation.
	 */
	private void eraseChangeOrderFeedback() {

		for (IFigure figure : AdoneElementOrderChangeManager.getInstance().getSelectedFigureList()) {
			removeFeedback(figure);
		}

		AdoneElementOrderChangeManager.getInstance().clear();
	}

	/**
	 * Removes the specified figure from the feedback layer, effectively clearing any visual feedback that
	 * was applied to it. This method is crucial for maintaining the visual clarity of the diagram by
	 * ensuring that temporary feedback is properly cleaned up after interactions such as dragging or
	 * reordering are concluded.
	 *
	 * @param figure
	 *            The figure for which feedback needs to be removed.
	 */
	@Override
	protected void removeFeedback(IFigure figure) {

		// Access the layer manager to retrieve the feedback layer
		LayerManager lm = (LayerManager) getCurrentViewer().getEditPartRegistry().get(LayerManager.ID);
		if (lm == null) {
			return;
		}

		IFigure feedbackLayer = lm.getLayer(LayerConstants.FEEDBACK_LAYER);

		// Check if the figure is actually present in the feedback layer
		if (feedbackLayer != null && feedbackLayer.getChildren().contains(figure)) {
			feedbackLayer.remove(figure);
		} else {
			// Optional: log a warning if the figure was not found in the feedback layer
			// System.out.println("Warning: Attempted to remove a figure that is not in the feedback layer.");
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
	 * Customizes the location retrieval for drag operations to restrict Lifeline movement
	 * to the vertical axis only. This override fixes the Y coordinate to the start location's Y value,
	 * allowing the X coordinate to follow the mouse's current position. This ensures Lifelines can only be moved
	 * left or right, aligning with design constraints that prohibit vertical movement.
	 *
	 * @return Point A point representing the new location with a fixed Y coordinate and a dynamic X coordinate based on the current mouse position.
	 */
	@Override
	protected Point getLocation() {
		// Retrieve the current mouse location
		Point mouseLocation = getCurrentInput().getMouseLocation();
		// Return a new point with the original X and current Y, restricting movement to the Y-axis
		return new Point(mouseLocation.x, getStartLocation().y);
	}

}
