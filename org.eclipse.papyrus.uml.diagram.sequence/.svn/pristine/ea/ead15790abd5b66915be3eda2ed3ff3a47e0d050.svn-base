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

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.uml.diagram.sequence.dialog.AdoneAddCoveredLifelineSelectionDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Lifeline;

/**
 * Command to add or remove lifelines covered by a Combined Fragment in a UML sequence diagram.
 * This class extends AbstractTransactionalCommand to modify the model within a transactional editing domain.
 * It uses a dialog to allow users to select which lifelines to add or remove from the coverage of a Combined Fragment.
 */
public class AdoneAddCoveredLifelineToCombinedFragment extends AbstractTransactionalCommand {

	// The Combined Fragment being modified.
	private CombinedFragment cf;

	// Lifelines to potentially add to the Combined Fragment.
	private List<Lifeline> addedLifelines;

	// The location where the Add/Remove Lifeline selection dialog appears.
	private Point msgPoint;

	/**
	 * Constructs the command for modifying covered lifelines of a Combined Fragment.
	 *
	 * @param domain
	 *            The editing domain in which the command will execute.
	 * @param request
	 *            The request containing the location for the dialog.
	 * @param cbfEp
	 *            The graphical edit part representing the Combined Fragment.
	 * @param addedLifelines
	 *            Lifelines considered for addition to the Combined Fragment.
	 */
	public AdoneAddCoveredLifelineToCombinedFragment(TransactionalEditingDomain domain, ChangeBoundsRequest request, GraphicalEditPart cbfEp, List<Lifeline> addedLifelines) {
		super(domain, null, null);
		this.addedLifelines = addedLifelines;
		this.msgPoint = new Point(request.getLocation().x, request.getLocation().y);
		// Extracts the Combined Fragment from the graphical edit part's model.
		if (cbfEp.getModel() instanceof View) {
			View view = (View) cbfEp.getModel();
			EObject semanticElement = view.getElement();
			if (semanticElement instanceof CombinedFragment) {
				this.cf = (CombinedFragment) semanticElement;
			}
		}
	}

	/**
	 * Executes the command to show the selection dialog and update the Combined Fragment's covered lifelines.
	 *
	 * @param monitor
	 *            Progress monitor to report progress.
	 * @param info
	 *            Adaptable for obtaining additional context (unused).
	 * @return The result of the command execution.
	 * @throws ExecutionException
	 *             If an error occurs during command execution.
	 */
	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		// Only proceed if there are lifelines to add.
		if (!addedLifelines.isEmpty()) {

			List<Lifeline> coveredLifelines = cf.getCovereds();

			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

			AdoneAddCoveredLifelineSelectionDialog dialog = new AdoneAddCoveredLifelineSelectionDialog(shell, coveredLifelines, addedLifelines, msgPoint);

			if (dialog.open() == Window.OK) {

				List<Lifeline> toAdd = dialog.getAddedLifelines();
				List<Lifeline> toRemove = dialog.getRemovedLifelines();
				cf.getCovereds().addAll(toAdd);
				cf.getCovereds().removeAll(toRemove);

			}

		}

		return CommandResult.newOKCommandResult();
	}


}
