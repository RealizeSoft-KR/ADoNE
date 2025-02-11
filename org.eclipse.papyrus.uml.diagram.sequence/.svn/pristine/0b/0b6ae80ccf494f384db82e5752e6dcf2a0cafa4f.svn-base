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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.uml.diagram.sequence.dialog.AdoneUMLElementTreeSelectorDialog;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneUMLCommonHelper;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

/**
 * A command to create a lifeline in a UML interaction within a transactional editing domain.
 * It allows for the specification of the lifeline's type through a dialog selection process
 * and integrates the newly created lifeline into the interaction model.
 */
public class AdoneCreateLifelineCommand extends AbstractTransactionalCommand {

	// The parent interaction for the new lifeline.
	private EObject parent;

	// The point where the lifeline creation was initiated.
	private Point msgPoint;

	// The request to create a new lifeline element.
	private CreateElementRequest createElementRequest;

	// The lifeline created by this command.
	private Lifeline createdLifeline = null;


	public AdoneCreateLifelineCommand(TransactionalEditingDomain domain, CreateViewAndElementRequest request, EditPart graphicalContainer) {
		super(domain, null, null);
		CreateElementRequestAdapter requestAdapter = request.getViewAndElementDescriptor().getCreateElementRequestAdapter();
		this.createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(CreateElementRequest.class);
		this.parent = createElementRequest.getContainer();
		this.msgPoint = new Point(request.getLocation().x, request.getLocation().y);
	}


	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		// Create a new lifeline with a unique name within the parent interaction.
		Interaction intac = (Interaction) this.parent;
		this.createdLifeline = intac.createLifeline(AdoneUMLCommonHelper.getUniqueName(intac, "lifeline"));

		Property representProperty = null;

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		AdoneUMLElementTreeSelectorDialog dialog = new AdoneUMLElementTreeSelectorDialog(shell, this.msgPoint);

		Type lifelineType = null;

		if (dialog.open() == Window.OK) {
			lifelineType = dialog.getSelectedType();
		}

		// Find an existing owned attribute that matches the selected type or create a new one.
		if (lifelineType != null) {
			for (Property pty : intac.getOwnedAttributes()) {
				if (pty.getType() == null) {
					continue;
				}
				if (pty.getType().equals(lifelineType)) {
					representProperty = pty;
				}
			}
		}

		if (representProperty == null) {
			representProperty = intac.createOwnedAttribute("", lifelineType);
		}

		this.createdLifeline.setRepresents(representProperty);
		this.createElementRequest.setNewElement(this.createdLifeline);

		return CommandResult.newOKCommandResult();
	}

	public Lifeline getCreatedLifeline() {
		return this.createdLifeline;
	}

}
