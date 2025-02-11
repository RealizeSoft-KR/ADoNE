/*****************************************************************************
 * Copyright (c) 2018 CEA LIST, Christian W. Damus, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Christian W. Damus - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.commands.CommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.RefreshConnectionsRequest;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.commands.wrappers.GMFtoGEFCommandWrapper;
import org.eclipse.papyrus.uml.diagram.common.Activator;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneAdjustZOrderCommand;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneCreateCombinedFragment;
import org.eclipse.papyrus.uml.diagram.sequence.command.AdoneCreateLifelineCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneMoveInteractionFragmentElementRequest;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.service.types.element.UMLDIElementTypes;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;
import org.eclipse.papyrus.uml.service.types.helper.advice.InteractionContainerDeletionContext;
import org.eclipse.papyrus.uml.service.types.utils.ElementUtil;
import org.eclipse.papyrus.uml.service.types.utils.RequestParameterUtils;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Custom creation edit policy for containers of {@link InteractionFragment}s, primarily
 * for the creation of such fragments.
 *
 * @since 5.0
 */
public class AdoneInteractionFragmentContainerCreationEditPolicy extends InteractionFragmentContainerCreationEditPolicy {

	public AdoneInteractionFragmentContainerCreationEditPolicy() {
		super();
	}

	/**
	 * Generates commands for creating UML elements and their corresponding views based on the request.
	 * This method specifically handles the creation of Lifelines, Combined Fragments, and Interaction Operands
	 * with tailored logic for each. For Lifelines and Combined Fragments, it includes custom commands for resizing
	 * and positioning. For Interaction Operands, it includes validation against the creation context to ensure
	 * they are only created under appropriate conditions (e.g., within an ALT Combined Fragment). It also incorporates
	 * additional logic to prevent the creation of Interaction Operands in non-ALT Combined Fragments and addresses
	 * specific layout adjustments required for the newly created elements to maintain diagram integrity.
	 *
	 * @param request
	 *            The creation request containing details about the type of element and its intended view.
	 * @return A command to create the element and view, or UnexecutableCommand if the creation is not valid or supported.
	 */
	@Override
	protected Command getCreateElementAndViewCommand(CreateViewAndElementRequest request) {

		IElementType typeToCreate = request.getViewAndElementDescriptor().getElementAdapter().getAdapter(IElementType.class);

		// Handle creation of Lifeline elements with custom command.
		if (ElementUtil.isTypeOf(typeToCreate, UMLElementTypes.LIFELINE)) {

			return getCreateElementAndViewCommandForLifelineWithUndo(request);

			// Handle creation of Combined Fragment elements with custom logic.
		} else if (ElementUtil.isTypeOf(typeToCreate, UMLElementTypes.COMBINED_FRAGMENT)) {

			// Extract the semantic create request to adjust covered lifelines and validate creation.
			IEditCommandRequest semanticCreateRequest = (IEditCommandRequest) request.getViewAndElementDescriptor().getCreateElementRequestAdapter().getAdapter(IEditCommandRequest.class);
			if (semanticCreateRequest != null) {
				Rectangle rectangle = getCreationRectangle(request);
				Set<Lifeline> covered = AdoneSequenceUtil.getCoveredLifelines(rectangle, getHost());
				RequestParameterUtils.setCoveredLifelines(semanticCreateRequest, covered);

				// Validate the starting point of the Combined Fragment on a valid Lifeline.
				if (!isCombinedFragmentStartingFromValidLifeline(rectangle, request)) {
					return UnexecutableCommand.INSTANCE;
				}

				CompoundCommand CfCompoundCommand = new CompoundCommand();

				// Add commands to resize lifelines and BEs as necessary.
				Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand((GraphicalEditPart) getHost(), rectangle.height);
				if (resizeLifelineEpCommand != null) {
					CfCompoundCommand.add(resizeLifelineEpCommand);
				}

				Command resizeFirstBesEpCommand = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand((GraphicalEditPart) getHost(), rectangle.height);
				if (resizeFirstBesEpCommand != null) {
					CfCompoundCommand.add(resizeFirstBesEpCommand);
				}

				Command command = getCreateElementAndViewCommandForCombinedFragmentWithUndo(request);
				if (command != null) {
					CfCompoundCommand.add(command);
				}

				return CfCompoundCommand;
			}

			// Handle creation of Interaction Operand elements with custom logic.
		} else if (ElementUtil.isTypeOf(typeToCreate, UMLElementTypes.INTERACTION_OPERAND)) {

			CompoundCommand operandCompoundCommand = new CompoundCommand();

			// Add commands to resize lifelines and BEs to a default height for Interaction Operand.

			Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand((GraphicalEditPart) getHost(), AdoneInteractionOperandEditPart.DEFAULT_HEIGHT);
			if (resizeLifelineEpCommand != null) {
				operandCompoundCommand.add(resizeLifelineEpCommand);
			}

			Command resizeFirstBesEpCommand = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand((GraphicalEditPart) getHost(), AdoneInteractionOperandEditPart.DEFAULT_HEIGHT);
			if (resizeFirstBesEpCommand != null) {
				operandCompoundCommand.add(resizeFirstBesEpCommand);
			}

			Command command = super.getCreateElementAndViewCommand(request);
			if (command != null) {
				operandCompoundCommand.add(command);
			}

			// Prevent creation of Interaction Operands under non-ALT Combined Fragments. (2023-12-06)
			// Alt 하위에 Opt 등이 있는 경우 Opt 에서 InteractionOperand 가 생성되는 현상 발생 -> 추후 조치 필요 (2024-02-07)

			// Retrieve the element creation request adapter from the view and element descriptor.
			final CreateElementRequestAdapter requestAdapter = request.getViewAndElementDescriptor().getCreateElementRequestAdapter();

			// Convert the adapter to a CreateElementRequest to access specific request details.
			final CreateElementRequest createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(CreateElementRequest.class);

			// Determine the container in which the new element should be created.
			EObject container = createElementRequest.getContainer();

			// Check if the container is a CombinedFragmentEditPart, indicating a combined fragment context.
			if (container instanceof CombinedFragmentEditPart) {
				CombinedFragmentEditPart parentCfEp = (CombinedFragmentEditPart) container;

				// Resolve the semantic element to access its properties.
				CombinedFragment parentCf = (CombinedFragment) parentCfEp.resolveSemanticElement();

				// If the interaction operator is not ALT, the command is not executable.
				if (!parentCf.getInteractionOperator().equals(InteractionOperatorKind.ALT_LITERAL)) {
					return UnexecutableCommand.INSTANCE;
				}
			}

			// If no container is specified, attempt to find a CombinedFragmentEditPart based on the location.
			if (container == null) {
				Point point = request.getLocation();

				// Utilize a utility method to find the nearest CombinedFragmentEditPart at the given point.
				CombinedFragmentEditPart parentCfEp = AdoneSequenceUtil.getCombinedFragmentEp(getHost().getRoot(), point);

				// If a combined fragment is found, check the interaction operator.
				if (parentCfEp != null) {
					CombinedFragment parentCf = (CombinedFragment) parentCfEp.resolveSemanticElement();

					// Commands to create elements within non-ALT combined fragments are deemed not executable.
					if (!parentCf.getInteractionOperator().equals(InteractionOperatorKind.ALT_LITERAL)) {
						return UnexecutableCommand.INSTANCE;
					}
				}
			}
		}

		// Proceed with the default element and view creation command if none of the above conditions apply.
		return super.getCreateElementAndViewCommand(request);

	}

	/**
	 * Checks if the starting point of a Combined Fragment is on a valid lifeline within the sequence diagram.
	 * Specifically, it verifies if the Combined Fragment is starting from the second lifeline and does not overlap
	 * with any Behavior Execution Specifications (BES) on that lifeline.
	 *
	 * @param creationRectangle
	 *            The rectangle representing the area of the Combined Fragment creation.
	 * @param request
	 *            The creation request with details about the view and element to be created.
	 * @return true if the Combined Fragment starts from a valid lifeline and position; false otherwise.
	 */
	private boolean isCombinedFragmentStartingFromValidLifeline(Rectangle creationRectangle, CreateViewAndElementRequest request) {

		// Retrieve all lifelines from the EditPartRegistry and sort them by their x-coordinate.
		Map<?, ?> editPartRegistry = getHost().getViewer().getEditPartRegistry();
		List<LifelineEditPart> lifelines = new ArrayList<>();
		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				lifelines.add((LifelineEditPart) value);
			}
		}
		lifelines.sort(Comparator.comparingInt(l -> l.getFigure().getBounds().x));

		// Find lifelines that overlap with the creation rectangle.
		Set<LifelineEditPart> coveredLifelineEp = AdoneSequenceUtil.getCoveredLifelineEditParts(creationRectangle, getHost());

		// Identify the leftmost lifeline that is covered by the creation rectangle.
		LifelineEditPart leftmostCoveredLifeline = coveredLifelineEp.stream()
				.min(Comparator.comparingInt(l -> l.getFigure().getBounds().x))
				.orElse(null);

		// Check if the identified lifeline is the second lifeline in the sequence diagram.
		if (lifelines.size() > 1 && leftmostCoveredLifeline == lifelines.get(1)) {

			// Iterate through all BES to find their boundaries.
			for (Object value : editPartRegistry.values()) {
				if (value instanceof BehaviorExecutionSpecificationEditPart) {
					BehaviorExecutionSpecificationEditPart bes = (BehaviorExecutionSpecificationEditPart) value;

					// Calculate the absolute bounds for the BES.
					Rectangle besBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(bes);

					// Verify if the Combined Fragment is entirely within the bounds of a BES.
					if (creationRectangle.y > besBounds.y && creationRectangle.y + creationRectangle.height < besBounds.y + besBounds.height) {
						return true;
					}
				}
			}
		}

		// If no valid starting lifeline is found, return false.
		return false;
	}

	/**
	 * Generates a command for creating a lifeline with the ability to undo and redo the creation. This custom command
	 * caters to the specific requirement of undoing the creation of a lifeline, handling exceptions and ensuring
	 * that the lifeline can be correctly removed and re-added to the model. It also includes additional logic
	 * for dealing with potential casting issues during undo operations as noted for future resolution.
	 *
	 * @param request
	 *            The request to create a lifeline and its corresponding view.
	 * @return A command that supports undoing and redoing the lifeline creation.
	 */
	private Command getCreateElementAndViewCommandForLifelineWithUndo(CreateViewAndElementRequest request) {

		// Obtain the editing domain from the host edit part.
		final TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost()).getEditingDomain();

		// Define a custom undo command for lifeline creation.
		final Command undoCommand = new Command() {

			// Holds the reference to the created lifeline for undo/redo operations.
			private Lifeline createdLifeline = null;

			// The container of the lifeline (typically an Interaction).
			private EObject container = null;

			private EStructuralFeature feature = null;

			/**
			 * Undo operation for lifeline creation.
			 * Removes the newly created lifeline from its container.
			 */
			@Override
			public void undo() {

				// Exception handling note: Address class cast exception issues related to lifeline handling.
				// Retrieve the newly created lifeline from the request. (2024-01-01)
				// class java.util.Collections$SingletonList cannot be cast to class org.eclipse.uml2.uml.Lifeline (java.util.Collections$SingletonList is in module java.base of loader 'bootstrap'; org.eclipse.uml2.uml.Lifeline is in unnamed module of loader
				// org.eclipse.osgi.internal.loader.EquinoxClassLoader @30dd0f78)

				final Lifeline newLifeline = getLifeline(request.getNewObject());

				if (null != newLifeline) {

					// Store references to the created lifeline and its container for undo operation.
					createdLifeline = newLifeline;
					container = newLifeline.eContainer();

					// Execute the command to remove the lifeline from its container.
					final AbstractTransactionalCommand abstractTransactionalCommand = new AbstractTransactionalCommand(editingDomain, "Remove Lifeline", Collections.singletonList(newLifeline.eResource())) { //$NON-NLS-1$

						@Override
						protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
							EcoreUtil.remove(newLifeline);
							return CommandResult.newOKCommandResult();
						}
					};
					try {
						abstractTransactionalCommand.execute(new NullProgressMonitor(), null);
					} catch (ExecutionException e) {
						Activator.log.error(e);
					}
				}

			}

			/**
			 * Redo operation for lifeline creation.
			 * Re-adds the lifeline to its original container.
			 */
			@Override
			public void redo() {

				// Redo operation requires re-adding the lifeline to the Interaction.
				final AbstractTransactionalCommand abstractTransactionalCommand = new AbstractTransactionalCommand(editingDomain, "Remove CombinedFragment", null) { //$NON-NLS-1$

					@Override
					protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
						if (null != container && null != createdLifeline) {

							Interaction intac = (Interaction) container;
							intac.getLifelines().add(createdLifeline);

						}
						return CommandResult.newOKCommandResult();
					}
				};
				try {
					abstractTransactionalCommand.execute(new NullProgressMonitor(), null);
				} catch (ExecutionException e) {
					Activator.log.error(e);
				}
			}

		};

		// Chain the custom undo command with the standard command for creating a lifeline.
		return undoCommand.chain(getCreateElementAndViewCommandForLifeline(request));
	}

	/**
	 * Creates a command to create a lifeline and its view. This method prepares the necessary semantic and view commands
	 * to ensure both the UML element and its graphical representation are created in sync. It includes setting up the
	 * container for the new element based on the host's semantic context, executing the creation command, and then
	 * refreshing connections to reflect any changes in the diagram.
	 *
	 * The command is prepared to handle cases where the host element's semantic context is not directly resolvable,
	 * preventing the creation of elements in unresolved or ambiguous locations within the model. Additional logic
	 * is included to ensure the command is only executed if valid, with custom undo and redo actions defined to
	 * maintain model integrity.
	 *
	 * @param request
	 *            The creation request containing the details and specifications for the new lifeline to be created.
	 * @return A command that, when executed, will create a new lifeline and its corresponding view in the diagram,
	 *         or null if the creation context is not properly resolved.
	 */
	protected Command getCreateElementAndViewCommandForLifeline(CreateViewAndElementRequest request) {
		// get the element descriptor
		CreateElementRequestAdapter requestAdapter = request.getViewAndElementDescriptor().getCreateElementRequestAdapter();

		// get the semantic request
		CreateElementRequest createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(
				CreateElementRequest.class);

		if (createElementRequest.getContainer() == null) {
			// complete the semantic request by filling in the host's semantic
			// element as the context
			View view = (View) getHost().getModel();
			EObject hostElement = ViewUtil.resolveSemanticElement(view);

			if (hostElement == null && view.getElement() == null) {
				hostElement = view;
			}

			// Returns null if host is unresolvable so that trying to create a
			// new element in an unresolved shape will not be allowed.
			if (hostElement == null) {
				return null;
			}
			createElementRequest.setContainer(hostElement);
		}

		AdoneCreateLifelineCommand createLifelineCommand = new AdoneCreateLifelineCommand(((IGraphicalEditPart) getHost()).getEditingDomain(), request, null);
		Command createElementCommand = new GMFtoGEFCommandWrapper(createLifelineCommand);

		if (!createElementCommand.canExecute()) {
			return createElementCommand;
		}

		// create the semantic create wrapper command
		SemanticCreateCommand semanticCommand = new SemanticCreateCommand(requestAdapter, createElementCommand) {

			@Override
			protected CommandResult doUndoWithResult(final IProgressMonitor progressMonitor, final IAdaptable info) throws ExecutionException {
				// We need to do nothing
				return CommandResult.newOKCommandResult();
			}

			@Override
			protected CommandResult doRedoWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
				// We need to do nothing
				return CommandResult.newOKCommandResult();
			}

			@Override
			protected CommandResult doExecuteWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
				CommandResult result = super.doExecuteWithResult(progressMonitor, info);
				return result;
			}

		};

		Command viewCommand = getCreateCommand(request);
		Command refreshConnectionCommand = getHost().getCommand(new RefreshConnectionsRequest(((List) request.getNewObject())));

		// form the compound command and return
		CompositeCommand cc = new CompositeCommand(semanticCommand.getLabel());
		cc.compose(semanticCommand);
		cc.compose(new CommandProxy(viewCommand));
		if (refreshConnectionCommand != null) {
			cc.compose(new CommandProxy(refreshConnectionCommand));
		}

		return new ICommandProxy(cc);
	}

	/**
	 * Generates a command to set the bounds of a newly created view, with specific adjustments for Lifeline elements.
	 * This method overrides the default behavior to specifically handle the case of Lifeline creation, ensuring
	 * that Lifelines are initialized with a default width and height. This adjustment was made in response to
	 * issues with message placement arising from incorrect Lifeline sizes. If the element being created is identified
	 * as a Lifeline based on its semantic hint, this method constructs and returns a command that sets the Lifeline's
	 * bounds to these predefined dimensions. For other types of elements, it defers to the superclass implementation.
	 *
	 * @param request
	 *            The create view request containing details about the creation, including the location.
	 * @param descriptor
	 *            The view descriptor for the element being created, which includes the semantic hint used to identify the type of element.
	 * @return An ICommand to set the bounds of the created view, customized for Lifelines or default for other elements.
	 */
	@Override
	protected ICommand getSetBoundsCommand(CreateViewRequest request, ViewDescriptor descriptor) {
		// LifelineEditPart 기본 크기 조정 (2024-01-01) => 메시지 위치 오류 발생하여 원래 크기로 원복
		// Adjusts LifelineEditPart's default size to address issues with message placement.
		if (UMLDIElementTypes.LIFELINE_SHAPE.getSemanticHint().equals(descriptor.getSemanticHint())) {
			Point location = request.getLocation().getCopy();
			Dimension size = new Dimension(AdoneLifeLineEditPart.DEFAUT_WIDTH, AdoneLifeLineEditPart.DEFAUT_HEIGHT);
			return new SetBoundsCommand(((IGraphicalEditPart) getHost()).getEditingDomain(), DiagramUIMessages.Commands_MoveElement, descriptor, new Rectangle(location, size));
		}
		return super.getSetBoundsCommand(request, descriptor);
	}

	/**
	 * Creates a command for adding a Combined Fragment to the diagram with support for undo and redo operations.
	 * This command specifically includes logic to manage the deletion and re-addition of the Combined Fragment
	 * and its contained InteractionOperands and InteractionFragments, ensuring the model's integrity is maintained
	 * throughout undo and redo actions.
	 *
	 * @param request
	 *            The creation request for a Combined Fragment, including view and element information.
	 * @return A command that allows for the creation of a Combined Fragment with undo and redo capabilities.
	 */
	private Command getCreateElementAndViewCommandForCombinedFragmentWithUndo(final CreateViewAndElementRequest request) {
		final TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost()).getEditingDomain();
		final Command undoCommand = new Command() {

			private CombinedFragment createdCombinedFragment = null;

			private EObject container = null;

			private EStructuralFeature feature = null;

			/**
			 * @see org.eclipse.gef.commands.Command#undo()
			 */
			@Override
			public void undo() {
				// We need to remove objects in InteractionOperand before deleting the created CombinedFragment

				// First step, get the created CombinedFragment
				final CombinedFragment combinedFragment = getCombinedFragment(request.getNewObject());

				// If the CombinedFragment is found, continue process
				if (null != combinedFragment) {

					createdCombinedFragment = combinedFragment;

					// Only one operand must be available
					final InteractionOperand interactionOperand = combinedFragment.getOperands().get(0);

					final CompositeCommand compositeCommand = new CompositeCommand("Move needed fragments"); //$NON-NLS-1$
					// Loop on each contained fragments to move it to super container (calculated automatically)
					for (final InteractionFragment interactionFragment : interactionOperand.getFragments()) {
						final DestroyElementRequest request = new DestroyElementRequest(editingDomain, interactionOperand, false);
						final Optional<InteractionContainerDeletionContext> context = InteractionContainerDeletionContext.get(request);
						final Optional<ICommand> result = context.map(ctx -> ctx.getDestroyCommand(interactionFragment));

						final ICommand undoCommand = result.get();
						if (null != undoCommand && undoCommand.canExecute()) {
							compositeCommand.add(undoCommand);
						}
					}

					// If there is something to move, move it
					if (null != compositeCommand && !compositeCommand.isEmpty() && compositeCommand.canExecute()) {
						try {
							compositeCommand.execute(new NullProgressMonitor(), null);
						} catch (ExecutionException e) {
							Activator.log.error(e);
						}
					}

					// We need to remove manually the combined fragment because it is not at the same position of the contained feature
					container = combinedFragment.eContainer();
					feature = container instanceof Interaction ? UMLPackage.eINSTANCE.getInteraction_Fragment() : container instanceof InteractionOperand ? UMLPackage.eINSTANCE.getInteractionOperand_Fragment() : null;
					final AbstractTransactionalCommand abstractTransactionalCommand = new AbstractTransactionalCommand(editingDomain, "Remove CombinedFragment", Collections.singletonList(combinedFragment.eResource())) { //$NON-NLS-1$

						@Override
						protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
							final Object value = container.eGet(feature);
							if (value instanceof List) {
								((List) value).remove(combinedFragment);
							}
							return CommandResult.newOKCommandResult();
						}
					};
					try {
						abstractTransactionalCommand.execute(new NullProgressMonitor(), null);
					} catch (ExecutionException e) {
						Activator.log.error(e);
					}
				}
			}

			/**
			 * @see org.eclipse.gef.commands.Command#redo()
			 */
			@Override
			public void redo() {
				// We need to manage the redo (only re-add the combined fragment)
				final AbstractTransactionalCommand abstractTransactionalCommand = new AbstractTransactionalCommand(editingDomain, "Remove CombinedFragment", null) { //$NON-NLS-1$

					@Override
					protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
						if (null != container && null != feature && null != createdCombinedFragment) {
							final Object value = container.eGet(feature);
							if (value instanceof List) {
								((List) value).add(createdCombinedFragment);
							}
						}
						return CommandResult.newOKCommandResult();
					}
				};
				try {
					abstractTransactionalCommand.execute(new NullProgressMonitor(), null);
				} catch (ExecutionException e) {
					Activator.log.error(e);
				}
			}

		};

		return undoCommand.chain(getCreateElementAndViewCommandForCombinedFragment(request));
	}

	/**
	 * Constructs a command to create a Combined Fragment element and its view, including undo and redo functionality.
	 * This command encapsulates several steps: setting up the semantic request, creating the Combined Fragment with specified properties,
	 * and adjusting diagram elements (like Lifelines and Behavior Execution Specifications) as necessary to accommodate the new fragment.
	 *
	 * @param request
	 *            The creation request containing details about the Combined Fragment and its location.
	 * @return A Command that, when executed, will create a Combined Fragment and adjust diagram elements as necessary.
	 */
	private Command getCreateElementAndViewCommandForCombinedFragment(final CreateViewAndElementRequest request) {
		// get the element descriptor
		final CreateElementRequestAdapter requestAdapter = request.getViewAndElementDescriptor().getCreateElementRequestAdapter();

		// get the semantic request
		final CreateElementRequest createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(CreateElementRequest.class);

		if (createElementRequest.getContainer() == null) {
			// complete the semantic request by filling in the host's semantic element as the context
			final View view = (View) getHost().getModel();
			EObject hostElement = ViewUtil.resolveSemanticElement(view);

			if (hostElement == null && view.getElement() == null) {
				hostElement = view;
			}

			// Returns null if host is unresolvable so that trying to create a
			// new element in an unresolved shape will not be allowed.
			if (hostElement == null) {
				return null;
			}
			createElementRequest.setContainer(hostElement);
		}

		AdoneCreateCombinedFragment createCombinedFragment = new AdoneCreateCombinedFragment(((IGraphicalEditPart) getHost()).getEditingDomain(), request, null);
		Command createElementCommand = new GMFtoGEFCommandWrapper(createCombinedFragment);

		if (!createElementCommand.canExecute()) {
			return createElementCommand;
		}
		// create the semantic create wrapper command
		final SemanticCreateCommand semanticCommand = new SemanticCreateCommand(requestAdapter, createElementCommand) {

			/**
			 * @see org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand#doUndoWithResult(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
			 *
			 * @param progressMonitor
			 * @param info
			 * @return
			 * @throws ExecutionException
			 */
			@Override
			protected CommandResult doUndoWithResult(final IProgressMonitor progressMonitor, final IAdaptable info) throws ExecutionException {
				// We need to do nothing
				return CommandResult.newOKCommandResult();
			}

			/**
			 * @see org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand#doRedoWithResult(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
			 *
			 * @param progressMonitor
			 * @param info
			 * @return
			 * @throws ExecutionException
			 */
			@Override
			protected CommandResult doRedoWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
				// We need to do nothing
				return CommandResult.newOKCommandResult();
			}

			/**
			 * @see org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand#doExecuteWithResult(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
			 *
			 * @param progressMonitor
			 * @param info
			 * @return
			 * @throws ExecutionException
			 */

			@Override
			protected CommandResult doExecuteWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
				// TODO Auto-generated method stub

				CommandResult result = super.doExecuteWithResult(progressMonitor, info);

				return result;
			}

		};

		// Obtain a command to create the view for the new element.
		final Command viewCommand = getCreateCommand(request);

		// Obtain a command to refresh connections, ensuring any diagram connections are updated to reflect the new element.
		final Command refreshConnectionCommand = getHost().getCommand(
				new RefreshConnectionsRequest(((List<?>) request.getNewObject())));

		// Initialize a composite command to aggregate multiple commands into a single executable unit.
		final CompositeCommand cc = new CompositeCommand(semanticCommand.getLabel());
		cc.compose(semanticCommand);
		cc.compose(new CommandProxy(viewCommand));

		// Add a command to adjust the Z-order of elements as necessary, ensuring visual consistency. (2024-01-10)
		final AdoneAdjustZOrderCommand zOrderCommand = new AdoneAdjustZOrderCommand(((IGraphicalEditPart) getHost()).getEditingDomain(), getHost());

		if (zOrderCommand.canExecute()) {
			cc.compose(zOrderCommand);
		}

		// Call a method to potentially resize the parent Interaction Operand based on the new Combined Fragment's requirements.
		this.resizeParentInteractionOperand(request, cc);

		// CF 생성 시 하단에 위치한 모든 EP 들 MOVE 처리 추가 (2024-01-26)

		// Calculate necessary adjustments for diagram elements based on the new Combined Fragment's size.
		Rectangle rectangle = getCreationRectangle(request);
		int moveDeltaY = rectangle.height;

		// Ensure a minimum height for adjustments.
		if (moveDeltaY < AdoneCombinedFragmentEditPart.DEFAULT_HEIGHT + 40) {
			moveDeltaY = AdoneCombinedFragmentEditPart.DEFAULT_HEIGHT + 40;
		}

		// Add commands to resize Lifelines and Behavior Execution Specifications as needed to accommodate the new Combined Fragment.

		Command resizeLifelineEpCommand = AdoneSequenceUtil.getResizeLifelineHeightCommand((GraphicalEditPart) getHost(), moveDeltaY);
		if (resizeLifelineEpCommand != null && resizeLifelineEpCommand.canExecute()) {
			cc.compose(new CommandProxy(resizeLifelineEpCommand));
		}

		Command resizeFirstBesEpCommand = AdoneSequenceUtil.getResizeFirstBesEpHeightForMoveCommand((GraphicalEditPart) getHost(), moveDeltaY);
		if (resizeFirstBesEpCommand != null && resizeFirstBesEpCommand.canExecute()) {
			cc.compose(new CommandProxy(resizeFirstBesEpCommand));
		}

		// Prepare a request to move diagram elements below the new Combined Fragment, avoiding overlaps.
		AdoneMoveInteractionFragmentElementRequest moveRequest = new AdoneMoveInteractionFragmentElementRequest();

		// Mark the creation area to prevent moving elements within it. (2024-01-26)
		moveRequest.getExtendedData().put(AdoneMoveInteractionFragmentElementRequest.COMBINEDFRAGMENT_CREATION_RECTANGLE, rectangle);

		Point moveDelta = new Point();
		moveDelta.setX(0);
		moveDelta.setY(rectangle.y);
		moveRequest.setMoveDelta(new Point(0, moveDeltaY));
		moveRequest.setLocation(request.getLocation().getCopy());
		moveRequest.setTargetLocation(request.getLocation().getCopy());

		// Obtain and conditionally add a command to move all elements below the new Combined Fragment.
		Command moveAllBelowEpCommand = getHost().getCommand(moveRequest);
		if (moveAllBelowEpCommand != null && moveAllBelowEpCommand.canExecute()) {
			cc.compose(new CommandProxy(moveAllBelowEpCommand));
		}

		// Add the command to refresh connections if applicable.
		if (refreshConnectionCommand != null) {
			cc.compose(new CommandProxy(refreshConnectionCommand));
		}

		// Return the composite command wrapped in a proxy for execution within the GEF framework.
		return new ICommandProxy(cc);
	}

	/**
	 * Resizes parent Interaction Operands and their containing Combined Fragments based on the size of the newly created element.
	 * This method ensures that parent Interaction Operands and Combined Fragments are sufficiently large to contain
	 * the new element, adjusting their sizes as necessary.
	 *
	 * @param request
	 *            The creation request for the new element, containing size and location information.
	 * @param cc
	 *            The CompositeCommand to which resize commands are added, allowing for execution as part of a larger operation.
	 */
	private void resizeParentInteractionOperand(CreateViewAndElementRequest request, CompositeCommand cc) {

		// Early return if size or location is not specified in the request.
		if (request.getSize() == null || request.getLocation() == null) {
			return;
		}

		// Copy the location and size from the request to avoid modifying the original request.
		Point p = request.getLocation().getCopy();
		Dimension d = request.getSize().getCopy();
		int resizeDeltaY = d.height;

		// Ensure a minimum resize amount; this value should eventually be replaced with a constant for clarity and maintainability.
		if (resizeDeltaY < 100) {
			resizeDeltaY = 100;
		}

		// Calculate the rectangle representing the creation area.
		Rectangle createRectangle = new Rectangle(p, d);

		// Retrieve a list of parent Interaction Operand Edit Parts that intersect with the creation area.
		List<InteractionOperandEditPart> parentInteractionOperandEpList = AdoneSequenceUtil.getAllParentInteractionOperandEps(getHost(), createRectangle);

		// Iterate through each parent Interaction Operand to resize them and their parent Combined Fragments.
		for (InteractionOperandEditPart ioEp : parentInteractionOperandEpList) {

			// Prepare a request to resize the Interaction Operand.
			ChangeBoundsRequest parentIoResizeRequest = new ChangeBoundsRequest();
			parentIoResizeRequest.setEditParts(ioEp);
			Rectangle ioFrgBounds = ioEp.getFigure().getBounds().getCopy();
			ioFrgBounds.height += resizeDeltaY;
			parentIoResizeRequest.setSizeDelta(new Dimension(0, resizeDeltaY));
			parentIoResizeRequest.setResizeDirection(PositionConstants.SOUTH);
			parentIoResizeRequest.setType(RequestConstants.REQ_RESIZE_CHILDREN);

			// Obtain and add the resize command for the Interaction Operand to the composite command.
			Command ioResizeCommand = ioEp.getParent().getCommand(parentIoResizeRequest);
			if (ioResizeCommand != null && ioResizeCommand.canExecute()) {
				cc.compose(new CommandProxy(ioResizeCommand));
			}

			// Also resize the parent Combined Fragment of the Interaction Operand.
			CombinedFragmentEditPart parentCfEp = ((CombinedFragmentEditPart) ioEp.getParent().getParent());
			ChangeBoundsRequest parentCfResizeRequest = new ChangeBoundsRequest();
			parentCfResizeRequest.setEditParts(parentCfEp);
			Rectangle cmbFrgBounds = parentCfEp.getFigure().getBounds().getCopy();
			cmbFrgBounds.height += resizeDeltaY;
			parentCfResizeRequest.setSizeDelta(new Dimension(0, resizeDeltaY));
			parentCfResizeRequest.setResizeDirection(PositionConstants.SOUTH);
			parentCfResizeRequest.setType(RequestConstants.REQ_RESIZE_CHILDREN);

			// Obtain and add the resize command for the parent Combined Fragment to the composite command.
			Command cfResizeCommand = parentCfEp.getParent().getCommand(parentCfResizeRequest);
			if (cfResizeCommand != null && cfResizeCommand.canExecute()) {
				cc.compose(new CommandProxy(cfResizeCommand));
			}
		}
	}

	/**
	 * Recursively retrieves a CombinedFragment instance from the given object. This method is capable of handling
	 * different types of inputs, including collections of objects, single CombinedFragment instances, or ViewAndElementDescriptors
	 * that indirectly reference a CombinedFragment through a createElement request.
	 *
	 * @param newObject
	 *            The object from which to extract a CombinedFragment instance. This can be a direct CombinedFragment instance,
	 *            a collection containing a CombinedFragment, or a ViewAndElementDescriptor linked to a CombinedFragment.
	 * @return The first CombinedFragment instance found within the input object, or null if no CombinedFragment can be extracted.
	 */
	private CombinedFragment getCombinedFragment(final Object newObject) {
		CombinedFragment result = null;

		// If the input is a collection, iterate through its elements to find a CombinedFragment.
		if (newObject instanceof Collection) {
			final Iterator<?> collectionIt = ((Collection<?>) newObject).iterator();
			while (collectionIt.hasNext() && null == result) {
				final Object next = collectionIt.next();
				// Recursively call this method in case the collection contains nested collections or descriptors.
				result = getCombinedFragment(next);
			}

			// Directly return the object if it is already a CombinedFragment.
		} else if (newObject instanceof CombinedFragment) {
			result = (CombinedFragment) newObject;

			// Handle the case where the object is a ViewAndElementDescriptor, which may indirectly reference a CombinedFragment.
		} else if (newObject instanceof ViewAndElementDescriptor && null != ((ViewAndElementDescriptor) newObject).getCreateElementRequestAdapter()) {

			// Extract the createElement request to find the new CombinedFragment element.
			final CreateElementRequest createElementRequest = (CreateElementRequest) ((ViewAndElementDescriptor) newObject).getCreateElementRequestAdapter().getAdapter(CreateElementRequest.class);
			if (null != createElementRequest) {
				final EObject eObject = createElementRequest.getNewElement();
				if (eObject instanceof CombinedFragment) {
					result = (CombinedFragment) eObject;
				}
			}
		}

		// Return the found CombinedFragment or null if not found.
		return result;
	}

	private Lifeline getLifeline(final Object newObject) {
		Lifeline result = null;

		if (newObject instanceof Collection) {
			final Iterator<?> collectionIt = ((Collection<?>) newObject).iterator();
			while (collectionIt.hasNext() && null == result) {
				final Object next = collectionIt.next();
				result = getLifeline(next);
			}
		} else if (newObject instanceof CombinedFragment) {
			result = (Lifeline) newObject;
		} else if (newObject instanceof ViewAndElementDescriptor && null != ((ViewAndElementDescriptor) newObject).getCreateElementRequestAdapter()) {
			final CreateElementRequest createElementRequest = (CreateElementRequest) ((ViewAndElementDescriptor) newObject).getCreateElementRequestAdapter().getAdapter(CreateElementRequest.class);
			if (null != createElementRequest) {
				final EObject eObject = createElementRequest.getNewElement();
				if (eObject instanceof Lifeline) {
					result = (Lifeline) eObject;
				}
			}
		}

		return result;
	}

	/**
	 * Calculates the rectangle for the creation of a new diagram element based on the location and size
	 * specified in the request. If the size is not specified, defaults to the Combined Fragment's default
	 * height and width to ensure the new element has a valid initial size.
	 *
	 * @param request
	 *            The creation request containing the location and optionally the size for the new element.
	 * @return A Rectangle object representing the area where the new element should be created.
	 */
	@Override
	protected Rectangle getCreationRectangle(CreateViewAndElementRequest request) {
		Point location = request.getLocation();
		Dimension size = request.getSize();

		// Use default size for Combined Fragment if the size is not specified in the request.
		if (size == null) {
			// Note: Potential error with hardcoded sizes to be reviewed. (2024-01-26)
			// return new Rectangle(location.x(), location.y(), 1, 1);
			return new Rectangle(location.x(), location.y(), AdoneCombinedFragmentEditPart.DEFAULT_HEIGHT, AdoneCombinedFragmentEditPart.DEFAULT_WIDTH);
		}

		// Return the rectangle based on the provided location and size.
		return new Rectangle(location, size);
	}

}
