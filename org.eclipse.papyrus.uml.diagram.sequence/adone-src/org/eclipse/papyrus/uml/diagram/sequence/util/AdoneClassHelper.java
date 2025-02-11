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
package org.eclipse.papyrus.uml.diagram.sequence.util;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Type;

public class AdoneClassHelper {

	/**
	 * Creates or retrieves an existing operation with the specified name for a given owner, which can be a Class or Interface.
	 * If the operation does not already exist, it is created and added to the owner. This method ensures that operations are
	 * uniquely identified by their name within their owner's context, preventing duplication.
	 *
	 * @param owner
	 *            The Type (Class or Interface) owning the operation.
	 * @param operationName
	 *            The name of the operation to create or retrieve.
	 * @return The existing or newly created Operation, or null if the owner or operationName is null or empty.
	 */
	public static Operation createOperation(Type owner, String operationName) {

		if (owner == null || operationName == null || "".equals(operationName)) {
			return null;
		}

		Operation operation = null;
		if (owner instanceof Class) {
			operation = ((Class) owner).getOperation(operationName, null, null);

			if (operation == null) {
				operation = ((Class) owner).createOwnedOperation(operationName, null, null);
			}
		} else if (owner instanceof Interface) {
			operation = ((Interface) owner).getOperation(operationName, null, null);

			if (operation == null) {
				operation = ((Interface) owner).createOwnedOperation(operationName, null, null);
			}
		}

		return operation;
	}

}
