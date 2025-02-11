/******************************************************************************
 * Copyright (c) 2002, 2006 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - enhanced the modeling experience by enforcing intuitive
 *   resizing constraints for BehaviorExecutionSpecifications
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ResizableShapeEditPolicy;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.BoundForEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.CoordinateReferentialUtils;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Lifeline;

/**
 * Extends ResizableShapeEditPolicy to refine resizing behaviors for BehaviorExecutionSpecifications in sequence diagrams.
 * It enforces intuitive and straightforward modeling by limiting resizing movements and ensuring visual coherence.
 * This policy prevents overlaps and maintains diagram integrity by checking against upper resize limits and
 * adjusting feedback during resizing operations. Designed to simplify user interaction, it enhances the modeling
 * experience by providing clear constraints and feedback mechanisms.
 */
public class AdoneBehaviorExecutionSpecResizePolicy extends ResizableShapeEditPolicy {


	/**
	 * Enhances the modeling process by limiting the movement of BehaviorExecutionSpecifications, making it more intuitive and straightforward.
	 * This method applies constraints to the resizing of BehaviorExecutionSpecifications within a sequence diagram, ensuring that their
	 * placement and adjustments align with the diagram's logical structure and layout requirements. The goal is to simplify the user's
	 * modeling efforts by providing a clear and manageable way to adjust these elements, thereby supporting a more efficient and
	 * effective diagramming experience.
	 *
	 * @param request
	 *            The request to resize an element within the diagram.
	 * @return A command that executes the resize operation, adhering to the specified constraints and enhancements.
	 */
	@Override
	protected Command getResizeCommand(ChangeBoundsRequest request) {

		CompoundCommand resizeCompoundCommand = new CompoundCommand();
		List children = request.getEditParts();
		// Validate there's only one child to resize
		if (children == null || children.size() != 1) {
			return null;
		}

		GraphicalEditPart resizeTargetEp = (GraphicalEditPart) children.get(0);
		Command resizeTargetCommand = null;

		// Only process resizing for BehaviorExecutionSpecification elements
		if (resizeTargetEp instanceof AdoneBehaviorExecutionSpecificationEditPart) {

			AdoneBehaviorExecutionSpecificationEditPart besEp = (AdoneBehaviorExecutionSpecificationEditPart) resizeTargetEp;

			if (besEp.resolveSemanticElement() == null) {
				return UnexecutableCommand.INSTANCE;
			}

			// Check if resizing exceeds the upper limit
			if (isExceedingUpperResizeLimit(request, resizeTargetEp)) {
				return UnexecutableCommand.INSTANCE;
			}

			BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) ((AdoneBehaviorExecutionSpecificationEditPart) resizeTargetEp).resolveSemanticElement();
			Lifeline ll = bes.getCovereds().get(0);

			LifelineEditPart llEp = (LifelineEditPart) AdoneSequenceUtil.getEditPartFromSemantic(besEp, ll);

			// Restrict resizing if it's not related to the second lifeline
			if (!AdoneSequenceUtil.isSecondLifelineEp(llEp)) {
				return UnexecutableCommand.INSTANCE;
			}

			resizeTargetCommand = super.getResizeCommand(request);

			int resizeDeltaY = request.getSizeDelta().height;

			// Generate command to resize the lifeline height accordingly
			Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand(resizeTargetEp, resizeDeltaY);

			if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
				resizeCompoundCommand.add(resizeLifelineEpCommand);
			}

			resizeCompoundCommand.add(resizeTargetCommand);

			if (resizeTargetCommand != null && resizeTargetCommand.canExecute()) {
				resizeCompoundCommand.add(resizeTargetCommand);
			}

		}

		return resizeCompoundCommand.unwrap();

	}


	@Override
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		request.getMoveDelta().x = 0; // reset offset
		IFigure feedback = getDragSourceFeedbackFigure();
		PrecisionRectangle rect = new PrecisionRectangle(getInitialFeedbackBounds().getCopy());
		getHostFigure().translateToAbsolute(rect);
		IFigure f = getHostFigure();
		Dimension min = f.getMinimumSize().getCopy();
		Dimension max = f.getMaximumSize().getCopy();
		IMapMode mmode = MapModeUtil.getMapMode(f);
		min.height = mmode.LPtoDP(min.height);
		min.width = mmode.LPtoDP(min.width);
		max.height = mmode.LPtoDP(max.height);
		max.width = mmode.LPtoDP(max.width);
		Rectangle originalBounds = rect.getCopy();
		rect.translate(request.getMoveDelta());
		rect.resize(request.getSizeDelta());
		if (min.width > rect.width) {
			rect.width = min.width;
		} else if (max.width < rect.width) {
			rect.width = max.width;
		}
		if (min.height > rect.height) {
			rect.height = min.height;
		} else if (max.height < rect.height) {
			rect.height = max.height;
		}
		if (rect.height == min.height && request.getSizeDelta().height < 0 && request.getMoveDelta().y > 0) { // shrink at north
			Point loc = rect.getLocation();
			loc.y = originalBounds.getBottom().y - min.height;
			rect.setLocation(loc);
			request.getSizeDelta().height = min.height - originalBounds.height;
			request.getMoveDelta().y = loc.y - originalBounds.y;
		}
		if (request.getSizeDelta().height == 0) { // moving
			// moveExecutionSpecificationFeedback(request, AbstractExecutionSpecificationEditPart.this, rect, originalBounds);
			moveExecutionSpecificationFeedback(request, (AbstractExecutionSpecificationEditPart) getHost(), rect, originalBounds);
		}
		feedback.translateToRelative(rect);
		feedback.setBounds(rect);
	}

	protected void moveExecutionSpecificationFeedback(ChangeBoundsRequest request, AbstractExecutionSpecificationEditPart movedPart, PrecisionRectangle rect, Rectangle originalBounds) {

		// If this is a move to the top, the execution specification cannot be moved upper than the life line y position
		if (request.getMoveDelta().y < 0) {
			EditPart parent = getHost().getParent();
			if (parent instanceof CLifeLineEditPart) {

				Point locationOnDiagram = CoordinateReferentialUtils.transformPointFromScreenToDiagramReferential(originalBounds.getCopy().getLocation(), (GraphicalViewer) movedPart.getViewer());
				Bounds parentBounds = BoundForEditPart.getBounds((Node) ((CLifeLineEditPart) parent).getModel());

				// This magic delta is needed to be at the bottom of the life line name
				if ((locationOnDiagram.y + request.getMoveDelta().y) < (parentBounds.getY() + 50)) {
					Point loc = locationOnDiagram.getCopy();
					loc.y = parentBounds.getY() + 50;
					rect.setLocation(loc);
					request.getMoveDelta().y = parentBounds.getY() + 50 - locationOnDiagram.y;
				}
			}
		}
	}

	/**
	 * Checks if resizing a graphical edit part exceeds the upper limit imposed by the positions of surrounding edit parts.
	 * This method ensures that the resized element does not overlap or infringe upon the space reserved for other elements
	 * above it in the sequence diagram, maintaining visual clarity and diagram integrity. It particularly handles cases
	 * where elements are being moved as part of a group to avoid false positives by excluding those movements from this check.
	 *
	 * @param request
	 *            The change bounds request containing the proposed resizing details.
	 * @param resizeTargetEp
	 *            The graphical edit part being resized.
	 * @return true if the resizing exceeds the upper limit, false otherwise.
	 */
	private boolean isExceedingUpperResizeLimit(ChangeBoundsRequest request, GraphicalEditPart resizeTargetEp) {

		// If the movement is part of a group move (e.g., parent edit part being moved), bypass the check. (2024-01-31)
		if (request.getExtendedData().containsKey("GroupMove")) {
			return false;
		}

		// Calculate the absolute bounds of the resize target in the diagram.
		Rectangle resizeBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(resizeTargetEp);

		Map<?, ?> registry = resizeTargetEp.getRoot().getViewer().getEditPartRegistry();

		// Iterate over all edit parts from the registry.
		for (Object child : registry.values()) {

			if (child instanceof GraphicalEditPart) {
				GraphicalEditPart childEp = (GraphicalEditPart) child;

				if (!(childEp instanceof CombinedFragmentEditPart || childEp instanceof BehaviorExecutionSpecificationEditPart)) {
					continue;
				}

				if (childEp.equals(resizeTargetEp)) {
					continue;
				}

				Rectangle childBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(childEp);

				int additionalMargin = 20;

				// Check if any child edit part's bottom Y coordinate is less than the new bottom Y coordinate of the resize target.
				if (childBounds.y + childBounds.height + additionalMargin > resizeBounds.y + resizeBounds.height + request.getSizeDelta().height) {
					return true; // Return true if at least one condition is met.
				}
			}
		}

		return false;
	}

}
