/*****************************************************************************
 * Copyright (c) 2024 RealizeSoft and others.
 *
 * All rights reserved. This file is part of a software program that is made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   RealizeSoft - initial API and implementation
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.requests;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * A specialized request class used during the creation of CombinedFragments to adjust the
 * positioning of existing EditParts in the sequence diagram. This class ensures that when a
 * new CombinedFragment is created, all EditParts below its intended Y-coordinate are moved
 * downwards by the height of the new CombinedFragment. However, EditParts that fall within the
 * boundaries of the new CombinedFragment are exempt from this move operation and remain in their
 * original positions. This behavior facilitates seamless insertion of CombinedFragments without
 * disturbing the layout of encapsulated or overlapping sequence diagram elements.
 */
public class AdoneMoveInteractionFragmentElementRequest extends ChangeBoundsRequest {

	// Key for storing the size (Rectangle) of the newly created CombinedFragment in the ExtendedData Map.
	public static String COMBINEDFRAGMENT_CREATION_RECTANGLE = "CombinedFragmentCreationRectangle";

	protected GraphicalEditPart targetPart;
	protected int moveDelta;
	protected Point targetLocation;

	public Point getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(Point targetLocation) {
		this.targetLocation = targetLocation;
	}

	public GraphicalEditPart getTargetEditPart() {
		return targetPart;
	}

	public void setTargetEditPart(GraphicalEditPart targetPart) {
		this.targetPart = targetPart;
	}

	@Override
	public Object getType() {
		return RequestConstants.REQ_MOVE_CHILDREN;
	}

}
