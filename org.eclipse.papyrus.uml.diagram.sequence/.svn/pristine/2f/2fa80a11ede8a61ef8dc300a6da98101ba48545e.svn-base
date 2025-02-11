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

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**
 * Utilized to adjust the location of all EditParts below a newly created message. Upon message creation,
 * this class ensures that all EditParts positioned beneath the message's Y-coordinate are moved downwards
 * by the default height of 20 units which represents newly created BehaviorExecutionSpecificationEditPart together with the message
 * This facilitates maintaining the visual integrity and order of elements within sequence diagrams or similar
 * modeling scenarios, ensuring that the introduction of new messages and their associated execution specifications
 * do not overlap or clutter the diagram.
 */
public class AdoneUpdateLocationByNewMessageCreationRequest extends ChangeBoundsRequest {

	protected GraphicalEditPart targetEditPart;

	public GraphicalEditPart getTargetEditPart() {
		return targetEditPart;
	}

	public void setTargetEditPart(GraphicalEditPart targetPart) {
		this.targetEditPart = targetPart;
	}

	@Override
	public Object getType() {
		return RequestConstants.REQ_MOVE_CHILDREN;
	}

}
