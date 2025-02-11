/*****************************************************************************
 * Copyright (c) 2010, 2017-2018 CEA List, ALL4TEC and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA List - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 522228
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 538466
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft -Customized lifeline resizing in sequence diagrams to only
 *   allow height increases, ensuring diagram consistency.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.LifelineMessageDeleteHelper;

/**
 * Defines a custom selection edit policy for lifelines in sequence diagrams, focusing on controlled resizing behavior.
 * This policy enhances lifeline interaction by enforcing specific resizing rules: it allows height increases and width
 * adjustments while strictly preventing height decreases and disallowing any resize attempts in directions other than
 * south (for height) and east (for width). This approach is designed to maintain the diagram's integrity and consistency,
 * especially in scenarios where incoming messages are marked for deletion. By blocking resizing actions that could lead
 * to logical inconsistencies or compromise the structural integrity of the sequence diagram, the policy safeguards against
 * unintended alterations. Through overriding the getResizeCommand method, this policy ensures that all modifications to
 * lifelines are deliberate and contribute positively to the overall modeling process, reinforcing the diagram's clarity
 * and effectiveness.
 */
public class AdoneLifelineSelectionEditPolicy extends LifelineSelectionEditPolicy {

	/**
	 * Overrides getResizeCommand to restrict lifeline resizing within sequence diagrams,
	 * ensuring increases in height and width. It blocks resizing when incoming messages are
	 * marked for deletion or when the request attempts to decrease the lifeline's height,
	 * aligning with diagram integrity and logical consistency.
	 */
	@Override
	protected Command getResizeCommand(final ChangeBoundsRequest request) {
		LifelineEditPart llEditPart = (LifelineEditPart) getHost();

		// Prevent resize if the lifeline has incoming message deletion markers.
		if (!LifelineMessageDeleteHelper.hasIncomingMessageDelete(llEditPart)) {

			final Dimension sizeDelta = request.getSizeDelta();
			final int moveHeight = sizeDelta.height();

			// Allow resize only towards the south (height increase) or east (width increase/decrease).
			if (!(request.getResizeDirection() == PositionConstants.SOUTH || request.getResizeDirection() == PositionConstants.EAST)) {
				return UnexecutableCommand.INSTANCE;
			}

			// Block attempts to decrease the height of the Lifeline.
			if (moveHeight < 0) {
				return UnexecutableCommand.INSTANCE;
			}

			return getOriginalResizeCommand(request);
		}
		return null;
	}

	/**
	 * Bypasses the immediate superclass's resize command implementation to directly invoke the grandparent class's resize logic.
	 * This method copies and repurposes the original command logic from the superclass of the superclass, effectively
	 * neutralizing the intermediate override to retain intended resize behavior. This approach is used to ensure that
	 * the resizing actions align with the original design intentions, bypassing any modifications introduced by the immediate
	 * superclass.
	 */
	protected Command getOriginalResizeCommand(ChangeBoundsRequest request) {
		ChangeBoundsRequest req = new ChangeBoundsRequest(REQ_RESIZE_CHILDREN);
		req.setCenteredResize(request.isCenteredResize());
		req.setConstrainedMove(request.isConstrainedMove());
		req.setConstrainedResize(request.isConstrainedResize());
		req.setSnapToEnabled(request.isSnapToEnabled());
		req.setEditParts(getHost());
		req.setMoveDelta(request.getMoveDelta());
		req.setSizeDelta(request.getSizeDelta());
		req.setLocation(request.getLocation());
		req.setExtendedData(request.getExtendedData());
		req.setResizeDirection(request.getResizeDirection());
		if (getHost().getParent() == null) {
			return null;
		}
		return getHost().getParent().getCommand(req);
	}

}
