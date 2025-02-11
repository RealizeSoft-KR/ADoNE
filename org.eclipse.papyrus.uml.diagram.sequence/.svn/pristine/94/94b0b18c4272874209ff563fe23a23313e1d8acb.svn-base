/*****************************************************************************
 * Copyright (c) 2011 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *
 *		CEA LIST - Initial API and implementation
 *		Bonnabesse Fanch (ALL4TEC) fanch.bonnabesse@alltec.net - Bug 476872
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - enhanced automatically resizing ALT CombinedFragments to
 *   include new InteractionOperands, resizing constraints for BehaviorExecutionSpecifications
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultCreationEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;
import org.eclipse.papyrus.uml.service.types.utils.ElementUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionOperatorKind;

/**
 * Extends the DefaultCreationEditPolicy to specially handle the creation and resizing of CombinedFragments within sequence diagrams.
 * This policy ensures that ALT CombinedFragments are automatically resized to accommodate new InteractionOperands, maintaining the
 * visual coherence and logical structure of the diagram. It includes checks to only resize for ALT CombinedFragments and constructs
 * appropriate commands for resizing operations. This approach facilitates the accurate representation of alternative flows by adjusting
 * the diagram elements to reflect their interactions and dependencies properly.
 */
public class AdoneCombinedCreationEditPolicy extends DefaultCreationEditPolicy {

	/**
	 * Returns the appropriate command based on the given request, specifically handling the creation of new elements within a sequence diagram.
	 * This method adeptly manages requests to create new InteractionOperands, ensuring that the containing CombinedFragment is properly resized
	 * to accommodate the new operand. It does so by determining the type of element to be created and, if necessary, invoking a specialized method
	 * to adjust the size of the CombinedFragment. This approach maintains the integrity and visual coherence of sequence diagrams, particularly when
	 * adding elements that alter the diagram's structure.
	 *
	 * @param request
	 *            The request triggering the command generation, which may involve creating new diagram elements.
	 * @return A Command that, when executed, performs the action specified by the request, including any necessary adjustments to the diagram's layout.
	 */
	@Override
	public Command getCommand(Request request) {

		if (request instanceof CreateViewAndElementRequest) {
			// Handle CreateViewAndElementRequest requests

			CreateViewAndElementRequest createRequest = (CreateViewAndElementRequest) request;

			// Retrieve the type of element to be created from the request
			IElementType typeToCreate = createRequest.getViewAndElementDescriptor().getElementAdapter().getAdapter(IElementType.class);

			if (ElementUtil.isTypeOf(typeToCreate, UMLElementTypes.INTERACTION_OPERAND)) {
				// If the element to be created is an INTERACTION_OPERAND

				final CreateElementRequestAdapter requestAdapter = createRequest.getViewAndElementDescriptor().getCreateElementRequestAdapter();
				final CreateElementRequest createElementRequest = (CreateElementRequest) requestAdapter.getAdapter(CreateElementRequest.class);

				EObject container = createElementRequest.getContainer();

				CompoundCommand newCommand = new CompoundCommand();
				Command command = super.getCommand(request);
				if (command != null) {
					newCommand.add(command);
				}

				if (container instanceof CombinedFragmentEditPart) {
					// If the container is of type CombinedFragmentEditPart
					return processCombinedFragmentResize((CombinedFragmentEditPart) container, newCommand, false, createRequest);
				}

				if (container == null || !(container instanceof CombinedFragmentEditPart)) {
					// If the container is null or not of type CombinedFragmentEditPart
					Point point = createRequest.getLocation();
					CombinedFragmentEditPart parentCfEp = AdoneSequenceUtil.getCombinedFragmentEp(getHost().getRoot(), point);

					if (parentCfEp != null) {
						// If a CombinedFragmentEditPart is found
						return processCombinedFragmentResize(parentCfEp, newCommand, true, createRequest);
					} else {
						System.out.println("createRequest.getLocation() -> Null : " + point);
					}
				}
			}
		}
		return super.getCommand(request);
	}

	/**
	 * Processes the resizing of a CombinedFragment to accommodate new elements, specifically tailored for ALT CombinedFragments.
	 * This method generates commands to extend the height of the CombinedFragment based on the default height of an InteractionOperand.
	 * It supports the creation and adjustment of ALT CombinedFragments within sequence diagrams, ensuring that the diagram reflects
	 * the logical structure and interaction flows accurately. Only ALT CombinedFragments are eligible for automatic resizing through
	 * this method, aligning with the need to visually represent alternative flows within the sequence diagram. The method carefully
	 * constructs a ChangeBoundsRequest to resize the CombinedFragment, either directly or through its parent, depending on the context.
	 *
	 * @param parentCfEp
	 *            The CombinedFragmentEditPart representing the parent CombinedFragment to be resized.
	 * @param newCommand
	 *            The CompoundCommand to which the resize command will be added.
	 * @param isChild
	 *            Indicates whether the resize operation should be treated as resizing a child element.
	 * @param createRequest
	 *            The original request for creating a new diagram element that necessitated the resize.
	 * @return A Command that, when executed, will apply the necessary resize operation to the CombinedFragment.
	 */
	private Command processCombinedFragmentResize(CombinedFragmentEditPart parentCfEp, CompoundCommand newCommand, boolean isChild, CreateViewAndElementRequest createRequest) {

		CombinedFragment parentCf = (CombinedFragment) parentCfEp.resolveSemanticElement();

		if (!parentCf.getInteractionOperator().equals(InteractionOperatorKind.ALT_LITERAL)) {
			return UnexecutableCommand.INSTANCE; // Return an unexecutable command if conditions are not met
		}

		// Create a request to resize the parent fragment
		ChangeBoundsRequest parentCfResizeRequest = new ChangeBoundsRequest();
		parentCfResizeRequest.setEditParts(parentCfEp);
		Rectangle cmbFrgBounds = parentCfEp.getFigure().getBounds().getCopy();
		cmbFrgBounds.height += AdoneInteractionOperandEditPart.DEFAULT_HEIGHT;
		parentCfResizeRequest.setSizeDelta(new Dimension(0, AdoneInteractionOperandEditPart.DEFAULT_HEIGHT));
		parentCfResizeRequest.setResizeDirection(PositionConstants.SOUTH);
		parentCfResizeRequest.setType(isChild ? RequestConstants.REQ_RESIZE_CHILDREN : RequestConstants.REQ_RESIZE);

		Command cfResizeCommand = isChild ? parentCfEp.getParent().getCommand(parentCfResizeRequest) : getHost().getCommand(parentCfResizeRequest);

		if (cfResizeCommand != null) {
			newCommand.add(cfResizeCommand);
		}

		return newCommand;
	}

}
