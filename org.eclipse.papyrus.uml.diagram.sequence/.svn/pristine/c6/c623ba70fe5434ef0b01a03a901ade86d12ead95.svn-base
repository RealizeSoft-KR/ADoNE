/*****************************************************************************
 * Copyright (c) 2014, 2016 CEA LIST, Christian W. Damus, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Gabriel Pascual (ALL4TEC) gabriel.pascual@all4tec.net - Initial API and implementation
 *  Christian W. Damus - bugs 459566, 463846, 485220
 *
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.util;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;

/**
 * Utility class copied from somewhere to manage delete action in GMF Diagram context.
 */
public final class AdoneDeleteActionUtil {

	private AdoneDeleteActionUtil() {
	}

	/**
	 * Gets the delete from model command.
	 *
	 * @param editPart
	 *            the edit part
	 * @param editingDomain
	 *            the editing domain
	 * @return the delete from model command
	 */
	public static Command getDeleteFromModelCommand(IGraphicalEditPart editPart, TransactionalEditingDomain editingDomain) {
		return editPart.getCommand(new EditCommandRequestWrapper(new DestroyElementRequest(false)));

	}
}
