/*****************************************************************************
 * Copyright (c) 2009 CEA
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
 *   Celine Janssens (ALL4TEC) celine.janssens@all4tec.net - Bug 440230 : Label Margin
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - sets a predefined minimum size to enhance appearance.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;


import org.eclipse.draw2d.geometry.Dimension;

/**
 * Extends CombinedFragmentFigure to set a predefined minimum size,
 * enhancing the default appearance and ensuring consistency across diagrams.
 * The minimum size is defined by static final fields for width and height,
 * facilitating easy adjustments and maintainability.
 */
public class AdoneCombinedFragmentFigure extends CombinedFragmentFigure {

	private static final int MIN_WIDTH = 100;
	private static final int MIN_HEIGHT = 100;

	public AdoneCombinedFragmentFigure() {
		setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	}

}
