/*****************************************************************************
 * Copyright (c) 2024 RealizeSoft and others.
 *
 * All rights reserved. This file is part of a software program that is made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   RealizeSoft - Extends Figure to prominently display label text
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;

/**
 * Provides a custom figure wrapper to ensure that labels are rendered at the top layer of a diagram,
 * enhancing visibility and clarity, especially for labels associated with InteractionOperands' Guard Constraints.
 * This implementation draws inspiration from the Eclipse Sirius project. The main functionality includes adjusting
 * label rendering based on the diagram's current viewport and scroll position, ensuring labels remain visible and correctly
 * positioned regardless of user navigation. This approach allows for dynamic visibility management and
 * ensures that critical information is always accessible to the user, leveraging the RENDER_LABEL flag
 * for conditional rendering and incorporating scroll offset calculations to maintain label positions
 * relative to the diagram's visible area.
 */
public final class AdoneTopLevelLayerLabelFigure extends Figure {

	// Fields to store root figure and associated graphical edit part.
	private final IFigure root;
	private final IGraphicalEditPart part;

	public AdoneTopLevelLayerLabelFigure(IFigure root, IGraphicalEditPart part) {
		this.root = root;
		this.part = part;
	}

	@Override
	public Rectangle getBounds() {
		return this.root.getBounds();
	}

	@Override
	public IFigure findFigureAt(int x, int y, TreeSearch search) {
		return null;
	}

	/**
	 * Paints the figure, enabling label rendering temporarily for high visibility before reverting.
	 *
	 * @param graphics
	 *            The graphics context used for painting.
	 */
	@Override
	public void paint(Graphics graphics) {
		try {
			AdoneTopLevelLayerLabel.RENDER_LABEL.set(true);
			paintLabels(graphics, root);
		} finally {
			AdoneTopLevelLayerLabel.RENDER_LABEL.set(false);
		}
	}

	/**
	 * Recursively paints labels at the topmost layer of the diagram.
	 *
	 * @param graphics
	 *            The graphics context used for painting.
	 * @param figure
	 *            The figure whose labels are to be painted.
	 */
	private void paintLabels(Graphics graphics, IFigure figure) {
		if (figure instanceof AdoneTopLevelLayerLabel) {
			paintTopLevelLayerLabel(graphics, (AdoneTopLevelLayerLabel) figure);
		} else {
			List<Object> children = new ArrayList<>(figure.getChildren());
			for (Object child : children) {
				paintLabels(graphics, (IFigure) child);
			}
		}
	}

	/**
	 * Paints the AdoneTopLevelLayerLabel at its intended position on the diagram, ensuring
	 * it appears at the topmost layer regardless of scroll position. This method adjusts
	 * for the current scroll offset to maintain the label's correct screen position.
	 *
	 * @param graphics
	 *            The graphics context used to draw the label.
	 * @param label
	 *            The AdoneTopLevelLayerLabel to be painted.
	 */
	private void paintTopLevelLayerLabel(Graphics graphics, AdoneTopLevelLayerLabel label) {

		// Retrieve the label's bounds within the diagram.
		Rectangle bounds = label.getBounds();

		// Create a copy of the top-left corner of the label's bounds.
		Point delta = bounds.getTopLeft().getCopy();

		// Convert the label's location to absolute coordinates within the diagram.
		label.translateToAbsolute(delta);

		// Check if the diagram viewer's control is a FigureCanvas, which supports scrolling.
		if (part.getViewer().getControl() instanceof FigureCanvas) {

			FigureCanvas canvas = (FigureCanvas) part.getViewer().getControl();

			// Retrieve the current scroll offset of the canvas viewport.
			Point scrollOffset = canvas.getViewport().getViewLocation();

			// Adjust the delta by the scroll offset to calculate the label's position relative to the visible diagram area.
			delta = delta.getTranslated(scrollOffset);
		}

		// Adjust the delta to align with the label's top-left corner, negating any previous position to start drawing from (0,0).
		delta.translate(bounds.getTopLeft().getNegated());

		// Apply the calculated delta to the graphics context, effectively moving the drawing starting point.
		graphics.translate(delta.x, delta.y);

		// Paint the label using its own paint method, now correctly positioned regardless of scrolling.
		label.paint(graphics);

		// Revert the graphics context translation to avoid affecting subsequent drawings.
		graphics.translate(-delta.x, -delta.y);
	}

}