/*****************************************************************************
 * Copyright (c) 2018 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhances InteractionOperand Constraint visibility and
 *   synchronizes UML model and Notation View model for sequence diagrams.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.TreeSearch;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneTopLevelLayerLabelFigure;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.AdoneDeferredModelViewSyncEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.GridManagementEditPolicy;

/**
 * Customizes SequenceDiagramEditPart to enhance the visibility of InteractionOperand's
 * Guard Constraint text by positioning it at the top of the sequence diagram.
 * Introduces a new layer, "TopLevelLayer", dedicated to elevating the Guard Constraint's
 * label for better readability and prominence.
 * Additionally, installs a delayed synchronization EditPolicy to ensure mutual integrity
 * between Model and View information. This approach maintains a dynamic balance between
 * visual Notation view model and UML Semantic model consistency.
 */
public class AdoneSequenceDiagramEditPart extends SequenceDiagramEditPart {

	// New layer name to position Guard Constraint's label text at the top on diagram
	private static final String TOP_LEVEL_LAYER = "TopLevelLayer";

	private IFigure topLevelLayerLabelFigure;


	public AdoneSequenceDiagramEditPart(View view) {
		super(view);
	}

	@Override
	public void activate() {
		super.activate();

		LayerManager lm = (LayerManager) this.getViewer().getEditPartRegistry().get(LayerManager.ID);

		if (lm != null) {

			// TopLevelLayer confirm and creation
			IFigure topLevelLayer = lm.getLayer(TOP_LEVEL_LAYER);

			if (topLevelLayer == null) {
				topLevelLayer = new FreeformLayer() {
					@Override
					public IFigure findFigureAt(int x, int y, TreeSearch search) {

						// Check in the overlay layer first
						// IFigure overlayFigure = super.findFigureAt(x, y, search);
						// if (overlayFigure instanceof AdoneTopLevelLayerLabelFigure) {
						// Find the corresponding figure in the primary layer
						// IFigure primaryLayer = lm.getLayer(LayerConstants.PRIMARY_LAYER);
						// IFigure originFigure = primaryLayer.findFigureAt(x, y, search);
						//
						// if (originFigure instanceof AdoneTopLevelLayerLabelFigure) {
						// return originFigure;
						// }

						return super.findFigureAt(x, y, search);

					}
				};

				topLevelLayer.setLayoutManager(new FreeformLayout());
				IFigure printableLayers = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
				printableLayers.add(topLevelLayer, TOP_LEVEL_LAYER);
				topLevelLayer.setEnabled(true);
				topLevelLayer.setVisible(true);

				this.topLevelLayerLabelFigure = new AdoneTopLevelLayerLabelFigure(this.getFigure(), this);
				topLevelLayer.add(this.topLevelLayerLabelFigure);

			}
		}
	}


	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		installEditPolicy(GridManagementEditPolicy.GRID_MANAGEMENT, new AdoneDeferredModelViewSyncEditPolicy());

	}

	@Override
	public void deactivate() {
		super.deactivate();

		IFigure topLevelLayLayer = this.getLayer(TOP_LEVEL_LAYER);
		if (topLevelLayerLabelFigure != null && topLevelLayLayer != null) {
			topLevelLayLayer.remove(topLevelLayerLabelFigure);
			topLevelLayerLabelFigure = null;
		}
	}

}
