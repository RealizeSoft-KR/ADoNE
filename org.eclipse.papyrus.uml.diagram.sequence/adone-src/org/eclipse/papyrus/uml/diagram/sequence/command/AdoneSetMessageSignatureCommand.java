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
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.papyrus.uml.diagram.sequence.dialog.AdoneOperationFilteredSelectionDialog;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneInteractionHelper;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Operation;

/**
 * A command to set the signature of a UML message to a selected operation.
 * It enables the selection of an operation as the message signature based on the type
 * of the receiving lifeline's represented classifier. This command facilitates the
 * precise modeling of message interactions in UML sequence diagrams by allowing the
 * assignment of specific operations to messages.
 */
public class AdoneSetMessageSignatureCommand extends AbstractTransactionalCommand {

	// The message whose signature is to be set.
	private Message msg;

	// The location on the diagram where the command is triggered.
	private Point location;

	// The operation to be set as the message signature.
	private Operation targetOperation;

	public AdoneSetMessageSignatureCommand(final TransactionalEditingDomain editingDomain, final Message targetMsg, final Point location) {
		super(editingDomain, null, null);
		Assert.isNotNull(targetMsg, "Message cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(location, "location cannot be null"); //$NON-NLS-1$
		this.msg = targetMsg;
		this.location = location;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info)
			throws ExecutionException {

		Lifeline recvLifeline = AdoneInteractionHelper.getReceiverLifeline(this.msg);

		if (recvLifeline.getRepresents() != null && recvLifeline.getRepresents().getType() != null) {

			Classifier lifelineType = (Classifier) recvLifeline.getRepresents().getType();

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

			// Opens a dialog to select an operation from those available to the lifeline's type.
			AdoneOperationFilteredSelectionDialog dialog = new AdoneOperationFilteredSelectionDialog(shell, lifelineType,
					lifelineType.getOperations(), this.location);

			if (dialog.open() == Dialog.OK) {
				this.targetOperation = dialog.getSelectedOperation();
			}

			// Sets the selected operation as the signature of the message.
			if (this.targetOperation != null) {
				this.msg.setSignature(this.targetOperation);
			}
		}

		return CommandResult.newOKCommandResult();
	}

}
