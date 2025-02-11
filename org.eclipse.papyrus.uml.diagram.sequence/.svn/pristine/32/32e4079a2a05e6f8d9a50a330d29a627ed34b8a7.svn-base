/*****************************************************************************
 * Copyright (c) 2018 CEA LIST and others.
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
 *
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;
import org.eclipse.papyrus.infra.gmfdiag.common.figure.node.IPapyrusNodeFigure;
import org.eclipse.papyrus.uml.diagram.common.draw2d.CenterLayout;
import org.eclipse.papyrus.uml.diagram.common.figure.node.CenteredWrappedLabel;
import org.eclipse.swt.graphics.Color;

public class ContinuationFigure extends RoundedRectangle implements IPapyrusNodeFigure {

	private CenteredWrappedLabel fFigureContinuationNameLabel;

	public ContinuationFigure() {
		IMapMode mapMode = SequenceMapModeUtil.getMapModel(this);
		CenterLayout layoutThis = new CenterLayout();
		this.setLayoutManager(layoutThis);
		this.setCornerDimensions(new Dimension(mapMode.DPtoLP(50), mapMode.DPtoLP(50)));
		createContents();
	}

	private void createContents() {
		fFigureContinuationNameLabel = new CenteredWrappedLabel();
		this.add(fFigureContinuationNameLabel);
	}

	public CenteredWrappedLabel getFigureContinuationNameLabel() {
		return fFigureContinuationNameLabel;
	}

	public IFigure getInvariantFigure() {
		return this;
	}

	@Override
	public Color getBorderColor() {
		return null;
	}

	@Override
	public boolean isShadow() {
		return false;
	}

	@Override
	public void setBorderColor(Color borderColor) {
	}

	@Override
	public void setShadow(boolean shadow) {
	}

	@Override
	public void setTransparency(int transparency) {
	}

	@Override
	public void setGradientData(int gradientColor1, int gradientColor2, int gradientStyle) {
	}

	@Override
	public void setIsUsingGradient(boolean b) {
	}
}