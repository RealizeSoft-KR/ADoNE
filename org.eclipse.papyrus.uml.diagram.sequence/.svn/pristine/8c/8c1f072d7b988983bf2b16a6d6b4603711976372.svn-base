/****************************************************************************
 * Copyright (c) 2009, 2023 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Patrick Tessier (CEA LIST), Thibault Landre (Atos Origin) - Initial API and implementation
 * Vincent Lorenzo (CEA LIST), change layout(IFigure container)
 * Laurent Wouters (CEA LIST), refactoring, cleanup, added layout of labels on top of shapes
 * Vincent LORENZO (CEA LIST) vincent.lorenzo@cea.fr - Bug 581898
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Redefines layout to easily add lifelines with DIAGRAM_PADDING for space management.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.papyrus.uml.diagram.common.figure.node.AutomaticCompartmentLayoutManager;
import org.eclipse.papyrus.uml.diagram.common.figure.node.CompartmentFigure;
import org.eclipse.papyrus.uml.diagram.common.figure.node.RoundedCompartmentFigure;

/**
 * Redefines AutomaticCompartmentLayoutManager to facilitate the addition of lifelines after the last one
 * by incorporating DIAGRAM_PADDING. This adjustment ensures there is adequate space between the last lifeline
 * and the diagram boundary, making it easier to add subsequent lifelines. The introduction of a static final
 * integer DIAGRAM_PADDING enhances the layout's flexibility and maintainability by providing a clear and
 * adjustable definition of the padding used to extend the base width of the diagram.
 */
public class AdoneAutomaticCompartmentLayoutManager extends AutomaticCompartmentLayoutManager {

	// additional space between the last lifeline and diagram boundary
	private static final int DIAGRAM_PADDING = 40;

	@Override
	protected Dimension calculatePreferredSize(IFigure container, int hint, int hint2) {
		collectInformation(container);
		int minimumWith = 0;
		int minimumHeight = 0;
		// CompartementFigure shall be replace by RoundedCompartmentFigure and this condition may be remove
		if (container instanceof CompartmentFigure) {
			CompartmentFigure cf = (CompartmentFigure) container;
			WrappingLabel wl = cf.getNameLabel();
			// display name
			if (wl != null && container.getChildren().contains(wl)) {
				if (wl.getPreferredSize().width > minimumWith) {
					minimumWith = wl.getPreferredSize().width;
				}
			}
		}
		if (container instanceof RoundedCompartmentFigure) {
			RoundedCompartmentFigure cf = (RoundedCompartmentFigure) container;
			WrappingLabel wl = cf.getNameLabel();
			// display name
			if (wl != null && container.getChildren().contains(wl)) {
				int prefNameWidth = wl.getPreferredSize().width;
				if (prefNameWidth > minimumWith) {
					minimumWith = prefNameWidth + 2;
				}
			}
			WrappingLabel stereotypeLabel = cf.getStereotypesLabel();
			if (stereotypeLabel != null && container.getChildren().contains(stereotypeLabel)) {
				int prefStereoWidth = stereotypeLabel.getPreferredSize().width;
				if (prefStereoWidth > minimumWith) {
					minimumWith = prefStereoWidth + 2;
				}
			}
		}
		if (!visibleCompartments.isEmpty()) {
			for (Object o : container.getChildren()) {
				IFigure child = (IFigure) o;
				minimumHeight += child.getPreferredSize().height;
				minimumWith = Math.max(minimumWith, child.getPreferredSize().width);
			}
		} else {
			for (IFigure child : visibleOthers) {
				minimumHeight += child.getPreferredSize().height;
			}
		}
		if (addExtraHeight) {
			minimumHeight += 7;
		}

		// Increase diagram base width by DIAGRAM_PADDING to ensure space between the last lifeline and diagram boundary (2024-02-02)
		return new Dimension(minimumWith + DIAGRAM_PADDING, minimumHeight);
	}
}
