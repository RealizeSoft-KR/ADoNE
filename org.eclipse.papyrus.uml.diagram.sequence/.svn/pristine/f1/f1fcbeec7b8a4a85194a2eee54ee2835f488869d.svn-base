/*****************************************************************************
 * Copyright (c) 2016 CEA LIST.
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
 *   Patrick Tessier (CEA LIST) - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - redefines to get better padding management between lifelines and diagram boundary.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.papyrus.uml.diagram.common.figure.node.InteractionRectangleFigure;

/**
 * Extends InteractionRectangleFigure to enhance diagram layout by integrating a customized
 * LayoutManager. This customization aims to ensure adequate padding between the last lifeline
 * and the diagram's boundary, addressing the need for clear spatial organization within
 * sequence diagrams. The approach enhances readability and aesthetic appeal, making it easier
 * to add new lifeline to diagram.
 */
public class AdoneInteractionRectangleFigure extends InteractionRectangleFigure {


	/** The Internal Structure Compartment */
	private final static String INTERACTION_COMPARTMENT = "interactionCompartment";

	/** The List of Compartment */
	private final static List<String> COMPARTMENT = new ArrayList<>() {

		private static final long serialVersionUID = 1L;
		{
			add(INTERACTION_COMPARTMENT);
		}
	};

	/**
	 * Constructor.
	 *
	 */
	public AdoneInteractionRectangleFigure() {
		this(null);
	}

	/**
	 * Create a new Classifier figure with the given tag
	 *
	 * @param tagLabel
	 *            a String that will be displayed at the top of the figure
	 * @since 3.0
	 */
	public AdoneInteractionRectangleFigure(String tagLabel) {
		super(tagLabel);
		// Sets customized LayoutManager to ensure padding between the last lifeline and diagram boundary
		setLayoutManager(new AdoneAutomaticCompartmentLayoutManager());

	}

	/**
	 * Get the attribute's compartment figure
	 *
	 * @return
	 * @since 3.0
	 */
	@Override
	public IFigure getCompartmentFigure() {
		return getCompartment(INTERACTION_COMPARTMENT);
	}


}
