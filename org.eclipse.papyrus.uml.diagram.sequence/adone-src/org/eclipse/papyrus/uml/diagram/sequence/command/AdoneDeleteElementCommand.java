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
 *   RealizeSoft - Original development and implementation.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.uml2.uml.Element;

/**
 * This class is pre-created for future use, designed to handle the deletion of UML elements
 * within a transactional editing domain. It encapsulates the functionality to destroy a specified
 * element, ensuring that deletions are performed in a controlled and undoable manner.
 */
public class AdoneDeleteElementCommand extends AbstractTransactionalCommand {

	private Element deleteElement;

	public AdoneDeleteElementCommand(TransactionalEditingDomain domain, EditCommandRequestWrapper request, IGraphicalEditPart host) {
		super(domain, null, null);
		final IEditCommandRequest editCommandRequest = request.getEditCommandRequest();
		if (editCommandRequest instanceof DestroyElementRequest) {
			this.deleteElement = (Element) ((DestroyElementRequest) editCommandRequest).getElementToDestroy();
			if (this.deleteElement == null) {
				this.deleteElement = (Element) host.resolveSemanticElement();
			}
		}
	}

	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		if (deleteElement != null) {
			deleteElement.destroy();
		}

		return CommandResult.newOKCommandResult();
	}

}
