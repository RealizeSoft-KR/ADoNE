/*****************************************************************************
 * Copyright (c) 2017 CEA LIST, ALL4TEC and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Modified DEFAULT_SCALE values for enhanced arrow visibility
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.anchors;

import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.papyrus.infra.gmfdiag.common.decoration.SolidArrowFilledConnectionDecoration;

public class AdoneSolidArrowFilledConnectionDecoration extends SolidArrowFilledConnectionDecoration {

	/** Default X scale value. */
	private static final double DEFAULT_SCALE_X = 10;

	/** Default Y scale value. */
	private static final double DEFAULT_SCALE_Y = 3.5;


	/**
	 * Constructor.
	 */
	public AdoneSolidArrowFilledConnectionDecoration() {
		init();
	}

	/**
	 * Initializes the decoration with default scale values and a triangle tip.
	 * Overrides to apply specific scale settings for this subclass.
	 */
	@Override
	protected void init() {
		setTemplate(PolygonDecoration.TRIANGLE_TIP);
		setScale(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
		setFill(true);
	}

	/**
	 * Adjusts the scale based on the line width for improved visual representation.
	 * Maintains the superclass's behavior while applying subclass-specific scales.
	 *
	 * @param w
	 *            the line width
	 */
	@Override
	public void setLineWidth(final int w) {
		setScale(DEFAULT_SCALE_X + w, DEFAULT_SCALE_Y + w);
		super.setLineWidth(w);
	}

}
