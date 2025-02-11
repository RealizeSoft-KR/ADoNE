/*****************************************************************************
 * Copyright (c) 2018 EclipseSource and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - Initial API and implementation (Bug 533770)
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - improved diagram editing efficiency by automating the movement
 *   of related EditParts during Combined Fragment Separator resize
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.handles.RelativeHandleLocator;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.CompartmentEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IBorderItemEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.MoveSeparatorRequest;
import org.eclipse.papyrus.uml.diagram.sequence.tools.SeparatorResizeTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;

/**
 * Enhances diagram editing efficiency by automating the movement of related EditParts during
 * Combined Fragment Separator resize operations. This class extends the standard Combined Fragment
 * Resize Edit Policy by incorporating logic that ensures when a Combined Fragment's separator is resized,
 * adjacent EditParts are also appropriately moved. This enhancement simplifies the user's interaction with
 * sequence diagrams by reducing manual adjustment needs, thereby improving the overall user experience in
 * modeling complex interactions. The automated adjustment of related EditParts based on separator movements
 * ensures that the diagram remains coherent and accurately represents the intended interactions without
 * requiring additional manual realignment.
 */
public class AdoneCombinedFragmentResizeEditPolicy extends CombinedFragmentResizeEditPolicy {

	private Polyline separatorFeedback;

	@Override
	protected List<Handle> createSelectionHandles() {
		@SuppressWarnings("unchecked")
		List<Handle> handles = super.createSelectionHandles();

		List<GraphicalEditPart> operands = getOperands();
		int separators = operands.size() - 1;

		if (getHost().getSelected() == EditPart.SELECTED_PRIMARY) {
			for (int i = 0; i < separators; i++) {
				handles.add(createSeparatorHandle(i, operands));
			}
		}

		return handles;
	}

	@Override
	public GraphicalEditPart getHost() {
		return super.getHost();
	}

	private Handle createSeparatorHandle(int separatorIndex, List<GraphicalEditPart> operands) {
		GraphicalEditPart resizedOperand = operands.get(separatorIndex + 1);
		Locator locator = new RelativeHandleLocator(resizedOperand.getFigure(), PositionConstants.NORTH);
		Handle handle = new SquareHandle(getHost(), locator, Cursors.SIZENS) {

			@Override
			protected DragTracker createDragTracker() {
				return new SeparatorResizeTracker(AdoneCombinedFragmentResizeEditPolicy.this.getHost(), PositionConstants.NORTH, separatorIndex);
			}

		};
		return handle;
	}

	@Override
	public Command getCommand(Request request) {
		if (request instanceof MoveSeparatorRequest) {
			return getMoveSeparatorCommand((MoveSeparatorRequest) request);
		}
		return super.getCommand(request);
	}

	@Override
	protected Command getResizeCommand(ChangeBoundsRequest request) {
		Command resizeCFCommand = super.getResizeCommand(request);
		if (resizeCFCommand != null && resizeCFCommand.canExecute()) {
			CompoundCommand command = new CompoundCommand(resizeCFCommand.getLabel());
			command.setDebugLabel("Resize CF & Operand");

			@SuppressWarnings("unchecked")
			
			// Added type casting due to changes in the Google API (2024-02-29)
			List<Command> commands = (List<Command>) command.getCommands();
			commands.add(resizeCFCommand);

			ChangeBoundsRequest cbr = request;
			int direction = cbr.getResizeDirection();

			List<GraphicalEditPart> operands = getOperands();
			if (!operands.isEmpty()) {
				ChangeBoundsRequest resizeOperand = new ChangeBoundsRequest();
				GraphicalEditPart operand;
				int firstOrLastOperandResizeDirection;
				if ((direction & PositionConstants.NORTH) != 0) {
					operand = operands.get(0);
					firstOrLastOperandResizeDirection = PositionConstants.NORTH;
				} else {
					operand = operands.get(operands.size() - 1);
					firstOrLastOperandResizeDirection = PositionConstants.SOUTH;
				}

				resizeOperand.setMoveDelta(cbr.getMoveDelta());
				resizeOperand.setLocation(cbr.getLocation());
				resizeOperand.setType(RequestConstants.REQ_RESIZE);

				for (GraphicalEditPart operandPart : operands) {
					resizeOperand.setEditParts(operand);
					if (operandPart == operand) {
						// Give all the delta (Height and width) to either the first or last operand
						resizeOperand.setSizeDelta(new Dimension(cbr.getSizeDelta()));
						resizeOperand.setResizeDirection(firstOrLastOperandResizeDirection);
					} else {
						// Give only the width delta to other operands
						resizeOperand.setSizeDelta(new Dimension(cbr.getSizeDelta().width(), 0));
						resizeOperand.setResizeDirection(PositionConstants.EAST);
					}
					commands.add(operandPart.getCommand(resizeOperand));
				}

				return command;
			}
		}

		return resizeCFCommand;
	}

	/**
	 * Redefines the operation for moving a CombinedFragment's separator to ensure collective movement
	 * of the parent CombinedFragment and all graphically lower EditParts. This method facilitates a cohesive
	 * visual adjustment when resizing CF separators, accommodating the dynamic layout changes within sequence diagrams.
	 * It calculates the necessary resize requests for the operand above the separator and the parent CombinedFragment,
	 * executing these adjustments in a compound command to maintain diagram integrity and visual coherence.
	 *
	 * @param request
	 *            The move separator request containing details about the separator movement.
	 * @return A command that executes the collective movement and resizing of related diagram components.
	 */
	@Override
	protected Command getMoveSeparatorCommand(MoveSeparatorRequest request) {

		// Validate the separator index is within the valid range
		int separatorIndex = request.getSeparatorIndex();
		if (separatorIndex < 0 || separatorIndex > getOperands().size() - 1) {
			return UnexecutableCommand.INSTANCE;
		}

		// Calculate the move distance to determine if the operation should proceed
		double moveDistance = request.getMoveDelta().getDistance(new Point(0, 0));
		if (moveDistance < 1) {
			return UnexecutableCommand.INSTANCE;
		}

		// Generate a request to resize the operand above the separator
		ChangeBoundsRequest requestAbove = getResizeAboveRequest(request);

		// Initialize the command to move the separator
		CompoundCommand moveSeparatorCommand = new CompoundCommand("Move Operands Separator");
		moveSeparatorCommand.add(getOperandAbove(request).getCommand(requestAbove));

		// Batch process the movement for the parent CF and all EditParts located below graphically during CF separator resize
		ChangeBoundsRequest parentCfResizeRequest = new ChangeBoundsRequest();
		parentCfResizeRequest.setEditParts(getHost());
		Rectangle cmbFrgBounds = getHost().getFigure().getBounds().getCopy();
		cmbFrgBounds.height += request.getMoveDelta().y;
		parentCfResizeRequest.setSizeDelta(new Dimension(0, request.getMoveDelta().y));
		parentCfResizeRequest.setResizeDirection(PositionConstants.SOUTH);
		parentCfResizeRequest.setType(RequestConstants.REQ_RESIZE);

		// Indicate the target operand for the resize request
		parentCfResizeRequest.getExtendedData().put("ResizeTargetInteractionOperand", getOperandAbove(request));

		// Generate the command to resize the parent CombinedFragment
		Command parentCfResizeCommand = this.getResizeCfCommandForSaperator(parentCfResizeRequest);

		if (parentCfResizeCommand != null) {
			moveSeparatorCommand.add(parentCfResizeCommand);
		}
		return moveSeparatorCommand;
	}

	protected Command getResizeCfCommandForSaperator(ChangeBoundsRequest request) {
		ChangeBoundsRequest req = new ChangeBoundsRequest(REQ_RESIZE_CHILDREN);
		req.setEditParts(getHost());
		req.setCenteredResize(request.isCenteredResize());
		req.setConstrainedMove(request.isConstrainedMove());
		req.setConstrainedResize(request.isConstrainedResize());
		req.setSnapToEnabled(request.isSnapToEnabled());
		req.setMoveDelta(request.getMoveDelta());
		req.setSizeDelta(request.getSizeDelta());
		req.setLocation(request.getLocation());
		req.setExtendedData(request.getExtendedData());
		req.setResizeDirection(request.getResizeDirection());
		return getHost().getParent().getCommand(req);
	}

	@Override
	protected GraphicalEditPart getOperandAbove(MoveSeparatorRequest request) {
		return getOperandAbove(request.getSeparatorIndex());
	}

	@Override
	protected GraphicalEditPart getOperandAbove(int separatorIndex) {
		return getOperands().get(separatorIndex);
	}

	@Override
	protected GraphicalEditPart getOperandBelow(MoveSeparatorRequest request) {
		return getOperandBelow(request.getSeparatorIndex());
	}

	@Override
	protected GraphicalEditPart getOperandBelow(int separatorIndex) {
		return getOperands().get(separatorIndex + 1);
	}

	@Override
	protected ChangeBoundsRequest getResizeAboveRequest(MoveSeparatorRequest request) {
		ChangeBoundsRequest requestAbove = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
		requestAbove.setMoveDelta(new Point(0, 0));
		requestAbove.setSizeDelta(new Dimension(request.getMoveDelta().x, request.getMoveDelta().y));
		requestAbove.setResizeDirection(PositionConstants.SOUTH);
		requestAbove.setLocation(request.getLocation());
		requestAbove.setEditParts(getOperandAbove(request.getSeparatorIndex()));
		return requestAbove;
	}

	@Override
	protected ChangeBoundsRequest getResizeBelowRequest(MoveSeparatorRequest request) {
		ChangeBoundsRequest requestBelow = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
		Point sizeDelta = request.getMoveDelta().getNegated();
		requestBelow.setSizeDelta(new Dimension(sizeDelta.x, sizeDelta.y));
		requestBelow.setMoveDelta(request.getMoveDelta().getCopy());
		requestBelow.setResizeDirection(PositionConstants.NORTH);
		requestBelow.setLocation(request.getLocation());
		requestBelow.setEditParts(getOperandBelow(request.getSeparatorIndex()));
		return requestBelow;
	}

	@Override
	public void showSourceFeedback(Request request) {
		if (request instanceof MoveSeparatorRequest) {
			showMoveSeparatorFeedback((MoveSeparatorRequest) request);
		}
		super.showSourceFeedback(request);
	}

	@Override
	protected void showMoveSeparatorFeedback(MoveSeparatorRequest request) {
		Polyline feedback = getMoveSeparatorFeedbackFigure();
		GraphicalEditPart operandPart = getOperandBelow(request.getSeparatorIndex());
		IFigure operandBelowFigure = operandPart.getFigure();
		IFigure operandAboveFigure = getOperandAbove(request.getSeparatorIndex()).getFigure();

		PrecisionRectangle location = new PrecisionRectangle(operandBelowFigure.getBounds());

		Point newPosition = location.getTopLeft();
		if (operandBelowFigure.containsPoint(newPosition) || operandAboveFigure.containsPoint(newPosition)) {
			feedback.setVisible(true);
		} else {
			// We're leaving the valid area; hide the feedback
			feedback.setVisible(false);
		}

		operandBelowFigure.translateToAbsolute(location);
		feedback.translateToRelative(location);
		location.translate(0., request.getMoveDelta().preciseY());

		feedback.setPoint(location.getTopLeft(), 0);
		feedback.setPoint(location.getTopRight(), 1);

		feedback.validate();
	}

	@Override
	protected Polyline getMoveSeparatorFeedbackFigure() {
		if (separatorFeedback == null) {
			separatorFeedback = createSeparatorFeedbackFigure();
		}
		return separatorFeedback;
	}

	@Override
	public void eraseSourceFeedback(Request request) {
		if (request instanceof MoveSeparatorRequest) {
			eraseMoveSeparatorFeedback((MoveSeparatorRequest) request);
		}
		super.eraseSourceFeedback(request);
	}

	@Override
	protected void eraseMoveSeparatorFeedback(MoveSeparatorRequest request) {
		if (separatorFeedback != null) {
			removeFeedback(separatorFeedback);
		}
		separatorFeedback = null;
	}

	@Override
	protected Polyline createSeparatorFeedbackFigure() {
		Polyline l = new Polyline() {
			@Override
			public void paint(Graphics graphics) {
				super.paint(graphics);
			}
		};
		l.setLineStyle(Graphics.LINE_DASH);
		l.setForegroundColor(ColorConstants.darkGray);
		l.addPoint(new Point(0, 0));
		l.addPoint(new Point(0, 50));
		l.setBounds(getHostFigure().getBounds());
		l.validate();
		addFeedback(l);
		return l;
	}

	private List<GraphicalEditPart> getOperands() {
		List<?> children = getHost().getChildren();

		CompartmentEditPart cfCompartment = children.stream()
				.filter(CombinedFragmentCombinedFragmentCompartmentEditPart.class::isInstance)
				.map(CombinedFragmentCombinedFragmentCompartmentEditPart.class::cast)
				.findFirst().orElse(null);

		if (cfCompartment == null) {
			return Collections.emptyList();
		}

		List<?> compartmentChildren = cfCompartment.getChildren();

		return compartmentChildren.stream()
				.filter(part -> !(part instanceof IBorderItemEditPart))
				.filter(GraphicalEditPart.class::isInstance)
				.map(GraphicalEditPart.class::cast)
				.collect(Collectors.toList());
	}

	/**
	 * This method improves the drag source feedback figure's visibility by adjusting its properties when in Element Order Change Mode.
	 * It switches the line style to solid and increases the line width for better visibility. The color is set to orange, indicating
	 * the mode's activation, and will change to blue to reflect the current state. These adjustments provide users with clear visual cues,
	 * facilitating an intuitive and efficient element reordering process within the diagram.
	 *
	 * @override
	 * @return A semi-transparent rectangle figure used as drag feedback, with enhanced visibility settings for Element Order Change Mode.
	 */
	@Override
	protected IFigure createDragSourceFeedbackFigure() {

		RectangleFigure r = new RectangleFigure();

		// Use a semi-transparent rectangle as drag feedback
		FigureUtilities.makeGhostShape(r);

		// Check if in element order change mode to adjust feedback appearance
		if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			r.setLineStyle(Graphics.LINE_SOLID);// Solid line for clarity in order change mode
			r.setLineWidth(3); // Increase line width for visibility
			r.setForegroundColor(ColorConstants.orange); // Initially set to orange, changes to blue on activation
		} else {
			r.setLineStyle(Graphics.LINE_DOT);
			r.setForegroundColor(ColorConstants.white);
		}

		r.setBounds(getInitialFeedbackBounds());
		r.validate();
		addFeedback(r);
		return r;

	}

}

