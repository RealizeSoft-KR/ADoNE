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

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.jface.window.Window;
import org.eclipse.papyrus.uml.diagram.sequence.dialog.AdoneCombinedFragmentOptionSelectionDialog;
import org.eclipse.papyrus.uml.service.types.utils.RequestParameterUtils;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.UMLFactory;

/**
 * Command to create a Combined Fragment in a UML sequence diagram.
 * This class allows for the selection of lifelines to be covered by the new Combined Fragment
 * and the specification of the fragment's interaction operator type (e.g., loop, alternative).
 * The command prompts the user with a dialog to make these selections and then creates
 * the Combined Fragment accordingly within the specified parent (Interaction or InteractionOperand).
 */
public class AdoneCreateCombinedFragment extends AbstractTransactionalCommand {

	// The parent Interaction or InteractionOperand in which the Combined Fragment is created.
	private EObject parent;

	// Lifelines to be covered by the Combined Fragment.
	private List<Lifeline> coveredLifelines;

	// The location at which the command was initiated.
	private Point msgPoint;

	// Request to create the Combined Fragment.
	private CreateElementRequest createElementRequest;

	public AdoneCreateCombinedFragment(TransactionalEditingDomain domain, CreateViewAndElementRequest request, EditPart graphicalContainer) {

		super(domain, null, null);

		CreateElementRequestAdapter requestAdapter = request.getViewAndElementDescriptor().getCreateElementRequestAdapter();
		createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(CreateElementRequest.class);
		parent = createElementRequest.getContainer();
		IEditCommandRequest semanticCreateRequest = (IEditCommandRequest) request.getViewAndElementDescriptor().getCreateElementRequestAdapter().getAdapter(IEditCommandRequest.class);
		Iterable<Lifeline> iterable = RequestParameterUtils.getCoveredLifelines(semanticCreateRequest);
		coveredLifelines = new ArrayList<>();

		if (iterable != null) {
			for (Lifeline lifeline : iterable) {
				coveredLifelines.add(lifeline);
			}
		}

		msgPoint = new Point(request.getLocation().x, request.getLocation().y);

	}


	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		AdoneCombinedFragmentOptionSelectionDialog dialog = new AdoneCombinedFragmentOptionSelectionDialog(shell, coveredLifelines, msgPoint);

		if (dialog.open() == Window.OK) {

			List<Lifeline> confirmCovereedLifelines = dialog.getSelectedLifelines();

			CombinedFragment cf = UMLFactory.eINSTANCE.createCombinedFragment();

			cf.getCovereds().addAll(confirmCovereedLifelines);

			String type = dialog.getSelectedOption();

			// Sets the interaction operator based on the selected option.
			switch (type) {
			case "Loop":
				cf.setInteractionOperator(InteractionOperatorKind.LOOP_LITERAL);
				break;
			case "Alt":
				cf.setInteractionOperator(InteractionOperatorKind.ALT_LITERAL);
				// Additional operands for alternative interactions.
				InteractionOperand opAlt = cf.createOperand("InteractionOperand0");
				opAlt.createGuard("guard");
				break;
			case "Opt":
				cf.setInteractionOperator(InteractionOperatorKind.OPT_LITERAL);
				break;
			case "Assert":
				cf.setInteractionOperator(InteractionOperatorKind.ASSERT_LITERAL);
				break;
			}

			// Additional operand for interactions, setting a default guard.
			InteractionOperand op = cf.createOperand("InteractionOperand1");
			op.createGuard("guard");

			// Sets the parent for the Combined Fragment.
			if (parent instanceof Interaction) {
				cf.setEnclosingInteraction((Interaction) parent);
			} else {
				cf.setEnclosingOperand((InteractionOperand) parent);
			}

			this.createElementRequest.setNewElement(cf);

		}

		return CommandResult.newOKCommandResult();
	}


}
