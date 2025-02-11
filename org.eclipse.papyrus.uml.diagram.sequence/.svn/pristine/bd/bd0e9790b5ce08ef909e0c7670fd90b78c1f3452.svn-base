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
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**
 * This class is used to offer a dialog for selecting the operation corresponding
 * to a message's signature during its creation. It enables users to define or
 * modify the operation linked to a message's signature, ensuring the message
 * reflects the intended behavior in the model.
 */
public class AdoneSetMessageSignatureRequest extends Request {

	// This field defines the type of request, specifically
	// indicating that the request is for setting or modifying
	// the signature of a message in a sequence diagram or similar modeling context.
	public static final String REQ_SET_MESSAGE_SIGNATURE = "SetMessageSignature";

	protected GraphicalEditPart targetPart;

	public AdoneSetMessageSignatureRequest() {
		setType(RequestConstants.REQ_CREATE);
	}

	public GraphicalEditPart getTargetEditPart() {
		return targetPart;
	}

	public void setTargetEditPart(GraphicalEditPart targetPart) {
		this.targetPart = targetPart;
	}

	@Override
	public Object getType() {
		return REQ_SET_MESSAGE_SIGNATURE;
	}

}
