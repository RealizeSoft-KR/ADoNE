/*****************************************************************************
 * Copyright (c) 2016-2017 CEA LIST and others.
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
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 521312
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - replaces node shape with AdoneInteractionRectangleFigure
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.common.figure.node.InteractionRectangleFigure;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneInteractionRectangleFigure;

/**
 * Extends CInteractionEditPart to customize the node shape with AdoneInteractionRectangleFigure.
 * Overrides createNodeShape to specify the custom figure for enhanced visual representation.
 * getPrimaryShape method ensures consistent use of this custom figure across the application.
 */
public class AdoneInteractionEditPart extends CInteractionEditPart {

	public AdoneInteractionEditPart(View view) {
		super(view);
	}

	@Override
	protected IFigure createNodeShape() {
		return primaryShape = new AdoneInteractionRectangleFigure();
	}

	@Override
	public InteractionRectangleFigure getPrimaryShape() {
		return (AdoneInteractionRectangleFigure) primaryShape;
	}

}
