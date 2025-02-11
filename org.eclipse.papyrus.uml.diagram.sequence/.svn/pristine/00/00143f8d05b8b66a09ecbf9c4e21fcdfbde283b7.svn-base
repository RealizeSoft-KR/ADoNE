/*****************************************************************************
 * Copyright (c) 2016, 2018 CEA LIST, Christian W. Damus, and others.
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
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 531596
 *   Christian W. Damus - bug 539373
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Prepares for future extension
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.BaseSlidableAnchor;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.papyrus.infra.gmfdiag.common.figure.node.LinkLFSVGNodePlateFigure;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.BoundForEditPart;

/**
 * This class is a preparatory extension of the LifelineNodePlate, initially duplicating the source code
 * from its parent class. It has been created to facilitate future feature enhancements and modifications
 * specific to our needs. The current implementation serves as a foundational setup, with plans to redefine
 * and extend its functionalities to better suit the requirements of advanced lifeline manipulations in
 * sequence diagrams.
 */
public class AdoneLifelineNodePlate extends LinkLFSVGNodePlateFigure {

	public AdoneLifelineNodePlate(org.eclipse.gef.GraphicalEditPart hostEP, int width, int height) {
		super(hostEP, width, height);
		withLinkLFEnabled();
		followSVGPapyrusPath = true;
	}

	@Override
	public PointList getPolygonPoints() {
		return getLifelineFigure().getPolygonPoints();
	}

	protected NodeFigure getLifelineFigure() {
		return (NodeFigure) this.getChildren().get(0);
	}

	@Override
	protected ConnectionAnchor createAnchor(PrecisionPoint p) {
		p.setPreciseX(0.5);// a changer
		return super.createAnchor(p);
	}

	@Override
	protected ConnectionAnchor createConnectionAnchor(Point p) {
		if (p == null) {
			return getConnectionAnchor(szAnchor);
		} else {
			Point temp = p.getCopy();
			translateToRelative(temp);

			// This allows to calculate the bounds corresponding to the node instead of the figure bounds
			final Bounds bounds = BoundForEditPart.getBounds((Node) getGraphicalEditPart().getModel());
			final Rectangle rectangle = new Rectangle(new Point(bounds.getX(), bounds.getY()), new Dimension(bounds.getWidth(), bounds.getHeight()));

			PrecisionPoint pt = BaseSlidableAnchor.getAnchorRelativeLocation(temp, rectangle);
			return createAnchor(pt);
		}
	}

	@Override
	protected boolean isDefaultAnchorArea(PrecisionPoint p) {
		return false;
	}

	@Override
	public boolean containsPoint(int x, int y) {
		return getLifelineFigure().containsPoint(x, y);
	}

	@Override
	public final IFigure findFigureAt(int x, int y, TreeSearch search) {
		NodeFigure lifeline = getLifelineFigure();
		return lifeline.findFigureAt(x, y, search);
	}

}
