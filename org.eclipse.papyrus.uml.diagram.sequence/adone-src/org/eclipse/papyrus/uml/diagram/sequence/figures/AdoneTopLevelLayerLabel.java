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
 *   RealizeSoft - Extends the WrappingLabel to prominently display label text
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;

/**
 * Extends the WrappingLabel to ensure the InteractionOperand's Guard Constraint Label text is
 * prominently displayed at the top layer of the diagram for enhanced visibility. This class
 * is inspired by concepts from the Eclipse Sirius project, adapting them to apply conditional
 * rendering and transparency adjustments based on visibility mismatches. By toggling the
 * RENDER_LABEL flag, it controls whether the label should be rendered, applying semi-transparency
 * to elements with visibility discrepancies to indicate their state clearly.
 */
public class AdoneTopLevelLayerLabel extends WrappingLabel {

	private final View view;

	// Controls whether the label is rendered, allowing dynamic visibility adjustments.
	public static final AtomicBoolean RENDER_LABEL = new AtomicBoolean(false);

	public AdoneTopLevelLayerLabel(View view) {
		this.view = view;
	}

	/**
	 * Customizes the painting process to conditionally render based on the RENDER_LABEL flag and
	 * apply transparency settings for elements with mismatched visibility states. It leverages the
	 * superclass's paint method for the actual drawing while ensuring graphics state is correctly
	 * managed before and after the operation, supporting both cases where the view context is present
	 * or absent.
	 *
	 * @param graphics
	 *            The graphics context used for drawing the label.
	 */
	@Override
	public void paint(Graphics graphics) {

		// Only proceed with painting if rendering is enabled.
		if (!RENDER_LABEL.get()) {
			return;
		}

		// Apply custom graphics settings for both visible and invisible elements.
		if (view != null) {

			adjustGraphicsOpacity(this, graphics, view);

			try {
				// Perform actual painting using superclass implementation.
				super.paint(graphics);
				graphics.restoreState();
			} finally {
				// Ensure graphics state is restored to avoid affecting subsequent renderings.
				graphics.popState();
			}
		} else {
			// Default painting process when view context is absent.
			super.paint(graphics);
		}
	}

	/**
	 * Adjusts the opacity of graphics based on the visibility state of the figure compared to its
	 * corresponding view. If the figure's visibility does not match the view's visibility,
	 * it applies a semi-transparent effect by setting the graphics' alpha value to 50,
	 * indicating a visibility discrepancy. This method ensures that the rendering
	 * respects the visibility settings, enhancing the user interface's intuitiveness.
	 *
	 * @param figure
	 *            The figure whose visibility is being adjusted.
	 * @param graphics
	 *            The graphics context used for painting.
	 * @param view
	 *            The view associated with the figure, used to determine visibility state.
	 */
	private void adjustGraphicsOpacity(IFigure figure, Graphics graphics, View view) {
		try {
			graphics.pushState();
			if (figure.isVisible() != view.isVisible()) {
				// Apply semi-transparency to elements whose visibility doesn't match the view.
				graphics.setAlpha(50);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

}
