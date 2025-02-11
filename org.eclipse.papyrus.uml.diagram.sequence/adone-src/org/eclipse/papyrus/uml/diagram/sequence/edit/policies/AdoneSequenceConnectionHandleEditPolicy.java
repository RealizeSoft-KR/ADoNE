/*****************************************************************************
 * Copyright (c) 2010 CEA
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
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Customized connection handle tooltips to enhance clarity
 *   and prevent user confusion
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle.HandleDirection;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandleLocator;

/**
 * Enhances the SequenceConnectionHandleEditPolicy by specifically tailoring connection handle tooltips to improve user experience.
 * This class prevents confusion by removing tooltips for incoming connections, focusing solely on outgoing connection guidance.
 * By simplifying the interaction cues, it aims to reduce errors and streamline the diagram's connection handling process,
 * ensuring users are provided with only the most pertinent information for their diagramming activities.
 */
public class AdoneSequenceConnectionHandleEditPolicy extends SequenceConnectionHandleEditPolicy {

	@Override
	protected ConnectionHandleLocator getConnectionHandleLocator(Point referencePoint) {
		return new AdoneConnectionHandleLocator(getHostFigure(), referencePoint);
	}

	/**
	 * Removes unnecessary tooltips for HandleDirection.INCOMING to prevent user confusion. By excluding INCOMING direction
	 * tooltips, this method focuses on providing clear guidance for OUTGOING connections only, thereby enhancing user
	 * experience by simplifying interaction cues and reducing the potential for errors. This selective tooltip presentation
	 * ensures that users receive only relevant information, streamlining the connection handling process within the diagram.
	 */
	@Override
	protected List getHandleFigures() {
		List list = new ArrayList(2);

		String tooltip;

		// Excluding HandleDirection.INCOMING tooltips (2024-01-24)

		tooltip = buildTooltip(HandleDirection.OUTGOING);
		if (tooltip != null) {
			list.add(new ConnectionHandle((IGraphicalEditPart) getHost(),
					HandleDirection.OUTGOING, tooltip));
		}

		return list;
	}

}
