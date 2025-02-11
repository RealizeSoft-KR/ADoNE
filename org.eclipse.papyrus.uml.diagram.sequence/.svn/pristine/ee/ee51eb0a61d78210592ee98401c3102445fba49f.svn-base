/******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - customized drag behavior to prevent movement of BehaviorExecutionSpecification
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.papyrus.infra.gmfdiag.common.snap.PapyrusDragEditPartsTrackerEx;
import org.eclipse.swt.events.MouseEvent;

/**
 * Extends PapyrusDragEditPartsTrackerEx to customize drag behavior for BehaviorExecutionSpecification
 * EditParts. This class modifies the standard drag functionality to prevent movement actions for
 * BehaviorExecutionSpecification elements, ensuring they remain static during drag operations. This
 * approach is adopted to maintain the integrity of sequence diagrams by preventing inadvertent
 * repositioning of these critical components.
 */
public class AdoneBehaviorExecutionDragEditPartsTracker extends PapyrusDragEditPartsTrackerEx {

	public AdoneBehaviorExecutionDragEditPartsTracker(EditPart sourceEditPart) {
		super(sourceEditPart);
	}

	@Override
	public void mouseDrag(MouseEvent me, EditPartViewer viewer) {
		if (!isViewerImportant(viewer)) {
			return;
		}
		setViewer(viewer);
		boolean wasDragging = movedPastThreshold();
		getCurrentInput().setInput(me);
		handleDrag();
		if (movedPastThreshold()) {
			if (!wasDragging) {
				handleDragStarted();
			}
			// Disabling MOVE action for BehaviorExecutionSpecification to prevent dragging. (2023-12-25)
			// handleDragInProgress();
		}
	}

}
