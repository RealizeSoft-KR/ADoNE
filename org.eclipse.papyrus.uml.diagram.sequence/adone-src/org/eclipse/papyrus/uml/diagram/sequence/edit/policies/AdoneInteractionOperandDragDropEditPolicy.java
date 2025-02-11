/*****************************************************************************
 * Copyright (c) 2009, 2018 Atos Origin, EclipseSource and others.
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
 *   Atos Origin - Initial API and implementation
 *   EclipseSource - Bug 533770
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - prepared for future extension.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;

/**
 * A preparatory extension of InteractionOperandDragDropEditPolicy, designed for future enhancements and feature
 * additions in drag and drop functionalities within interaction operands. While it currently leverages existing
 * functionality by overriding the getCommand method with its superclass implementation, this class is strategically
 * positioned for the introduction of more sophisticated drag and drop interactions, aiming to enhance the modeling
 * experience with advanced user interaction capabilities. This foundation anticipates the development of innovative
 * features that will be integrated into the interaction operand's drag and drop mechanism.
 */
public class AdoneInteractionOperandDragDropEditPolicy extends InteractionOperandDragDropEditPolicy {

	@Override
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

}
