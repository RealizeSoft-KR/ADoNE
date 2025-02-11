/*****************************************************************************
 * Copyright (c) 2010 - 2018 CEA, Christian W. Damus, and others
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
 *   Soyatec - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 519408
 *   Vincent LORENZO (CEA LIST) vincent.lorenzo@cea.fr - Bug 531520
 *   Christian W. Damus - bugs 539373, 536486
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhances layout scalability and lifeline label visibility with AdoneLifeLineLayoutManager.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;
import org.eclipse.papyrus.infra.gmfdiag.common.utils.FigureUtils;

/**
 * This class has been customized to enhance flexibility and visibility within sequence diagrams.
 * By replacing the default layout manager with AdoneLifeLineLayoutManager, it secures enhanced
 * scalability, allowing for more sophisticated layout management tailored to specific needs.
 * Additionally, the height of lifeline labels has been adjusted to improve visibility, ensuring
 * that labels are more easily readable and accessible. This modification addresses common challenges
 * in displaying complex interactions by optimizing the spatial arrangement and presentation of
 * lifelines, thereby facilitating a clearer understanding of the sequence diagram's contents.
 */
public class AdoneLifelineFigure extends LifelineFigure {

	protected static final int HEADER_EXTENSION_HEIGHT = 8;

	public AdoneLifelineFigure() {
		super();
		setLayoutManager(new AdoneLifeLineLayoutManager());
		setTransparency(100);
		createContents();
	}

	private void createContents() {
		lifelineHeaderBoundsFigure = new LifelineHeaderFigure();
		this.add(lifelineHeaderBoundsFigure);
	}

	@Override
	public Dimension getLabelsDimension() {
		final Dimension labelDimension = new Dimension(-1, -1);

		if (null != nameLabel && labelDimension.width < nameLabel.getPreferredSize().width) {
			labelDimension.width = nameLabel.getPreferredSize().width;
		}
		if (null != taggedLabel && labelDimension.width < taggedLabel.getPreferredSize().width) {
			labelDimension.width = taggedLabel.getPreferredSize().width;
		}
		if (null != stereotypesLabel && labelDimension.width < stereotypesLabel.getPreferredSize().width) {
			labelDimension.width = stereotypesLabel.getPreferredSize().width;
		}
		if (null != stereotypePropertiesInBraceContent && labelDimension.width < stereotypePropertiesInBraceContent.getPreferredSize().width) {
			labelDimension.width = stereotypePropertiesInBraceContent.getPreferredSize().width;
		}
		if (null != qualifiedLabel && labelDimension.width < qualifiedLabel.getPreferredSize().width) {
			labelDimension.width = qualifiedLabel.getPreferredSize().width;
		}

		final ResizableCompartmentFigure firstCompartment = FigureUtils.findChildFigureInstance(this, ResizableCompartmentFigure.class);
		if (null != firstCompartment) {

			// Improves visibility by securing additional space to the existing height
			labelDimension.height = firstCompartment.getBounds().y - getBounds().y + HEADER_EXTENSION_HEIGHT;
		}

		return labelDimension;
	}

}
