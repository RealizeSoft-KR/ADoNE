/*****************************************************************************
 * Copyright (c) 2017, 2019 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 542802
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Added message signature setting code & delete return message related code
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest.ConnectionViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequestFactory;
import org.eclipse.gmf.runtime.emf.commands.core.command.CompositeTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.papyrus.uml.diagram.sequence.dialog.AdoneOperationFilteredSelectionDialog;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.preferences.CustomDiagramGeneralPreferencePage;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneInteractionHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceDeleteHelper;
import org.eclipse.papyrus.uml.service.types.element.UMLDIElementTypes;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.OccurrenceSpecification;
import org.eclipse.uml2.uml.Operation;

/**
 * Extends the functionality for creating an execution specification in a UML sequence diagram
 * with additional features for setting the message signature and deletion of reply messages.
 * This class provides a specialized command that integrates with the existing sequence diagram editing infrastructure
 * to allow for enhanced interaction creation, including the automatic configuration of message signatures
 * based on selected operations and the smart management of related diagram elements.
 */
public class AdoneCreateExecutionSpecificationWithMessage extends CreateExecutionSpecificationWithMessage {

	private ExecutionSpecification createdExecutionSpecification;
	protected Operation targetOperation = null;

	public AdoneCreateExecutionSpecificationWithMessage(TransactionalEditingDomain domain, CreateConnectionViewAndElementRequest request, EditPart graphicalContainer) {
		super(domain, request, graphicalContainer);
	}

	@Override
	protected CommandResult doExecuteWithResult(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		// 1. look for the message triggering the creation of the execution specification
		Message message = getMessage();
		if (message == null) {
			throw new ExecutionException("null message"); //$NON-NLS-1$
		}
		// 2. retrieve preferences to apply
		// according to the message sort
		retrievePreferences();
		if (null == type && null == preference) {
			throw new ExecutionException("undefined preference"); //$NON-NLS-1$
		}

		// Create the ExecutionSpecification only if needed
		if (null != type && !CustomDiagramGeneralPreferencePage.CHOICE_NONE.equals(preference)) {
			// 3. create execution specification at target
			createExecutionSpecification();
		}

		// Creates a dialog for selecting an operation to set as the message signature.
		Message createdMessage = getMessage();
		Lifeline recvLifeline = AdoneInteractionHelper.getReceiverLifeline(createdMessage);
		if (recvLifeline.getRepresents() != null && recvLifeline.getRepresents().getType() != null) {
			Classifier lifelineType = (Classifier) recvLifeline.getRepresents().getType();
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			Point msgLocation = getMessageLocation();
			org.eclipse.swt.graphics.Point swtMsgPoint = new org.eclipse.swt.graphics.Point(msgLocation.x, msgLocation.y);
			AdoneOperationFilteredSelectionDialog dialog = new AdoneOperationFilteredSelectionDialog(shell, lifelineType,
					lifelineType.getOperations(), swtMsgPoint);
			if (dialog.open() == Dialog.OK) {
				targetOperation = dialog.getSelectedOperation();
			} else {
				targetOperation = null;
			}
			if (targetOperation != null) {
				createdMessage.setSignature(targetOperation);
			}
		}

		return CommandResult.newOKCommandResult();
	}

	/**
	 * @return
	 */
	private Point getMessageLocation() {
		Point pt = new Point(this.request.getLocation().x, this.request.getLocation().y);
		return pt;
	}


	private Message getMessage() {
		Message message = null;
		ConnectionViewAndElementDescriptor connectionViewAndElementDescriptor = request.getConnectionViewAndElementDescriptor();
		if (connectionViewAndElementDescriptor != null) {
			CreateElementRequestAdapter createElementRequestAdapter = connectionViewAndElementDescriptor.getCreateElementRequestAdapter();
			message = (Message) createElementRequestAdapter.getAdapter(Message.class);
		}
		return message;
	}

	private void retrievePreferences() {
		this.type = null;
		IPreferenceStore store = UMLDiagramEditorPlugin.getInstance().getPreferenceStore();
		if (request.getConnectionViewAndElementDescriptor().getSemanticHint().equals(UMLDIElementTypes.MESSAGE_ASYNCH_EDGE.getSemanticHint())) {
			// for asynchronous messages
			this.preference = store.getString(CustomDiagramGeneralPreferencePage.PREF_EXECUTION_SPECIFICATION_ASYNC_MSG);
		}
		if (request.getConnectionViewAndElementDescriptor().getSemanticHint().equals(UMLDIElementTypes.MESSAGE_SYNCH_EDGE.getSemanticHint())) {
			// for synchronous messages
			this.preference = store.getString(CustomDiagramGeneralPreferencePage.PREF_EXECUTION_SPECIFICATION_SYNC_MSG);
		}
		// case where a behavior execution specification must be created at target
		if (CustomDiagramGeneralPreferencePage.CHOICE_BEHAVIOR.equals(preference) || CustomDiagramGeneralPreferencePage.CHOICE_BEHAVIOR_AND_REPLY.equals(preference)) {
			this.type = UMLDIElementTypes.BEHAVIOR_EXECUTION_SPECIFICATION_SHAPE;
		}
		// case where an action execution specification must be created at target
		if (CustomDiagramGeneralPreferencePage.CHOICE_ACTION.equals(preference) || CustomDiagramGeneralPreferencePage.CHOICE_ACTION_AND_REPLY.equals(preference)) {
			this.type = UMLDIElementTypes.ACTION_EXECUTION_SPECIFICATION_SHAPE;
		}
		// case where a message reply must also be created
		if (CustomDiagramGeneralPreferencePage.CHOICE_BEHAVIOR_AND_REPLY.equals(preference) || CustomDiagramGeneralPreferencePage.CHOICE_ACTION_AND_REPLY.equals(preference)) {
			this.createReply = true;
		}
	}

	private void createExecutionSpecification() {
		LifelineEditPart lifelineEditPart = (LifelineEditPart) graphicalContainer;
		CreateViewRequest requestcreation = CreateViewRequestFactory.getCreateShapeRequest(type, lifelineEditPart.getDiagramPreferencesHint());
		Point point = request.getLocation().getCopy();
		requestcreation.setLocation(point);
		Command command = lifelineEditPart.getCommand(requestcreation);

		command.execute();
		// Save the created execution specification for the possible undo
		createdExecutionSpecification = getCreatedElement(command, ExecutionSpecification.class);
	}

	@SuppressWarnings("unchecked")
	private <T> T getCreatedElement(final Command command, final Class<T> type) {
		T element = null;
		try {
			// extract the semantic create command from compound command
			SemanticCreateCommand semanticCreateCommand = getAllCommands(command)
					.filter(SemanticCreateCommand.class::isInstance)
					.map(SemanticCreateCommand.class::cast).findFirst().get();
			// get the return value of the command
			CommandResult commandResult = semanticCreateCommand.getCommandResult();
			if (null != commandResult && commandResult.getReturnValue() instanceof CreateElementRequestAdapter) {
				// Get the created element
				element = (T) ((CreateElementRequestAdapter) commandResult.getReturnValue()).getAdapter(type);
			}
		} catch (NoSuchElementException e) {
			// Do nothing null is return
		}

		return element;
	}

	@SuppressWarnings("unchecked")
	private Stream<Object> getAllCommands(final Object parent) {
		Object command = null;
		if (parent instanceof ICommandProxy) {
			// get the inner command in case of proxy
			command = ((ICommandProxy) parent).getICommand();
		} else {
			command = parent;
		}

		if (command instanceof CompoundCommand) {
			return ((CompoundCommand) command).getCommands().stream()
					.flatMap(childNode -> getAllCommands(childNode));
		} else if (command instanceof CompositeCommand) {
			return StreamSupport.stream(Spliterators.spliteratorUnknownSize(((CompositeCommand) command).iterator(), Spliterator.ORDERED), false)
					.flatMap(childNode -> getAllCommands(childNode));
		} else {
			return Stream.of(command);
		}
	}


	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doUndo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doUndo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {

		// Remove the execution specification
		if (null != createdExecutionSpecification) {
			// First delete its start and finish
			final OccurrenceSpecification start = createdExecutionSpecification.getStart();
			final OccurrenceSpecification finish = createdExecutionSpecification.getFinish();
			final CompoundCommand compoundCommand = new CompoundCommand();

			if (null != start) {
				DestroyElementRequest delStart = new DestroyElementRequest(getEditingDomain(), start, false);
				compoundCommand.add(new ICommandProxy(new DestroyElementCommand(delStart)));
			}
			if (null != finish) {
				DestroyElementRequest delEnd = new DestroyElementRequest(getEditingDomain(), finish, false);
				compoundCommand.add(new ICommandProxy(new DestroyElementCommand(delEnd)));
			}
			if (!compoundCommand.isEmpty() && compoundCommand.canExecute()) {
				compoundCommand.execute();
			}

			// Destroy the graphical representation first
			final CompositeTransactionalCommand compositeCommand = new CompositeTransactionalCommand(getEditingDomain(), "Remove execution specification view"); //$NON-NLS-1$
			SequenceDeleteHelper.deleteView(compositeCommand, createdExecutionSpecification, getEditingDomain());
			compositeCommand.execute(monitor, info);
			// Remove the execution specification
			final EObject container = createdExecutionSpecification.eContainer();
			if (container instanceof Interaction) {
				((Interaction) container).getFragments().remove(createdExecutionSpecification);
			} else if (container instanceof InteractionOperand) {
				((InteractionOperand) container).getFragments().remove(createdExecutionSpecification);
			}
		}

		// Clear the stored values because the redo will fill this fields if needed
		createdExecutionSpecification = null;

		setResult(new CommandResult(Status.OK_STATUS));
		return Status.OK_STATUS;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand#doRedo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	protected IStatus doRedo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
		// Only re-execute the initial process
		final CommandResult result = doExecuteWithResult(monitor, info);

		setResult(result);
		return result.getStatus();
	}

}
