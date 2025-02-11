/*****************************************************************************
 * Copyright (c) 2009 CEA
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
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Customized for improved InteractionConstraint label
 *   visibility via AdoneTopLevelLayerLabel.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;

/**
 * This class has been customized to incorporate the redefined createContents method,
 * specifically to enhance the InteractionConstraint label's visibility by positioning it
 * at the top of the diagram. By utilizing the AdoneTopLevelLayerLabel class, it ensures
 * that the label is prominently displayed within a dedicated layer, addressing the need
 * for clear and effective communication of constraints in the diagram's context.
 */
public class AdoneInteractionOperandFigure extends InteractionOperandFigure {

	/**
	 * True if line separator is printed
	 */
	private boolean lineSeparator = true;

	/**
	 * True if this Edit Part is the first Operand of his CombinedFragment's parent
	 */
	private boolean firstOperand = false;

	private WrappingLabel fInteractionConstraintLabel;

	public AdoneInteractionOperandFigure() {
		this.setLayoutManager(new XYLayout());
		this.setLineStyle(Graphics.LINE_DASH);
		this.setBorder(null);
		this.setLineSeparator(!firstOperand);
		createContents();
	}

	@Override
	public WrappingLabel getInteractionConstraintLabel() {
		return fInteractionConstraintLabel;
	}

	/**
	 * @param fInteractionConstraintLabel
	 *            the fInteractionConstraintLabel to set
	 */
	public void setfInteractionConstraintLabel(WrappingLabel fInteractionConstraintLabel) {
		this.fInteractionConstraintLabel = fInteractionConstraintLabel;
	}

	@Override
	public void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		if (lineSeparator) {
			graphics.setLineStyle(getLineStyle());
			graphics.setLineWidth(getLineWidth());
			graphics.drawLine(getBounds().getTopLeft(), getBounds().getTopRight());
		}
	}

	/**
	 * Set the line separator
	 *
	 * @param lineSeparator
	 */
	@Override
	public void setLineSeparator(boolean lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Initializes the contents by setting up the InteractionConstraint label with
	 * the AdoneTopLevelLayerLabel class. This ensures its placement at the top of
	 * the diagram for enhanced visibility. The label is positioned within a dedicated
	 * layer, making it clear and prominently displayed.
	 */
	private void createContents() {
		fInteractionConstraintLabel = new AdoneTopLevelLayerLabel(null);
		fInteractionConstraintLabel.setText("");
		this.add(fInteractionConstraintLabel, new Rectangle(10, 10, -1, -1));
	}

	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {

		Dimension dim = super.getMinimumSize(wHint, hHint);
		// look for combinedFragmentFigure
		IFigure cfFigure = getParent();
		while (!(cfFigure instanceof CombinedFragmentFigure)) {
			cfFigure = cfFigure.getParent();
		}
		if (cfFigure == null) {
			return dim;
		}
		Rectangle ccfbound = cfFigure.getBounds();
		if (ccfbound.width != -1) {
			return new Dimension(ccfbound.width, dim.height);
		}
		return dim;
	}
}
