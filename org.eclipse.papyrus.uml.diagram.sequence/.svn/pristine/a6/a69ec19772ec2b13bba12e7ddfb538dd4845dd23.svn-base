/*****************************************************************************
 * Copyright (c) 2019 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced selection of operation for the message's signature
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.tools.DragEditPartsTrackerEx;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneSetMessageSignatureRequest;

/**
 * Extends the drag functionality for message labels in Sequence diagrams by providing a dialog to
 * select an operation for the message's signature upon double-clicking the label. This allows for
 * easy modification of the message signature directly from the diagram, enhancing the user's ability
 * to quickly update the communication between objects without navigating away from the visual representation.
 */
public class AdoneDragMessageNameEditPartsTracker extends DragEditPartsTrackerEx {

	public AdoneDragMessageNameEditPartsTracker(EditPart sourceEditPart) {
		super(sourceEditPart);
	}

	/**
	 * Handles double-click events on the message label to initiate the signature change process.
	 *
	 * @param button
	 *            The mouse button number that was double-clicked.
	 * @return boolean Always returns true to indicate the double-click event has been handled.
	 */
	@Override
	protected boolean handleDoubleClick(int button) {
		setMessageSignature();
		return true;
	}

	/**
	 * Opens a dialog for the user to select a new operation for the message's signature. Applies the
	 * selected signature to the message if a valid choice is made.
	 */
	protected void setMessageSignature() {
		EditPart selectedEditParts = getSourceEditPart();
		if (selectedEditParts != null) {
			IGraphicalEditPart msgEditPart = (IGraphicalEditPart) selectedEditParts.getParent();

			// Creates and configures a request to change the message signature
			AdoneSetMessageSignatureRequest connectionRequest = new AdoneSetMessageSignatureRequest();
			connectionRequest.setTargetEditPart(msgEditPart);
			connectionRequest.setType(AdoneSetMessageSignatureRequest.REQ_SET_MESSAGE_SIGNATURE);

			// Retrieves and executes the command to apply the new signature
			Command command = msgEditPart.getCommand(connectionRequest);
			if (command != null) {
				setCurrentCommand(command);
				executeCurrentCommand();
			}

			// Deactivate tracker after operation completion
			deactivate();
		}
	}

}
