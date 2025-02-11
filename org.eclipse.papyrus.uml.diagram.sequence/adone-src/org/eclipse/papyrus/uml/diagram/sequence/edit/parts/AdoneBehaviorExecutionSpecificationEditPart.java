/*****************************************************************************
 * Copyright (c) 2018 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - improved BehaviorExecutionSpecification by removing
 *   unnecessary EditPolicies and adding a dedicated EditPolicy for resizing.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneBehaviorExecutionSpecResizePolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.SequenceReferenceEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.UpdateConnectionReferenceEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.UpdateWeakReferenceForExecSpecEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneExecutionSpecificationRectangleFigure;
import org.eclipse.papyrus.uml.diagram.sequence.tools.AdoneBehaviorExecutionDragEditPartsTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SelfMessageHelper;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Type;

/**
 * This class represents an edit part for BehaviorExecutionSpecification in a sequence diagram.
 * In this revision, superfluous EditPolicies that were not necessary for the correct operation
 * of BehaviorExecutionSpecifications have been removed to streamline the editing process.
 * Furthermore, a specific EditPolicy, AdoneBehaviorExecutionSpecResizePolicy, has been added
 * to facilitate the resizing of BehaviorExecutionSpecifications. This adjustment aims to
 * improve the user experience by allowing more intuitive and flexible manipulation of
 * BehaviorExecutionSpecifications within sequence diagrams.
 */
public class AdoneBehaviorExecutionSpecificationEditPart extends
		BehaviorExecutionSpecificationEditPart {

	// Originally, the width was set to 20. It has been reduced to 12 to improve overall diagram visibility.
	public static int DEFAUT_WIDTH = 12;

	// For main logic BES, height is linked to the lifeline size; for others, it's fixed at 20.
	public static int DEFAULT_HEIGHT = 20;

	public AdoneBehaviorExecutionSpecificationEditPart(View view) {
		super(view);
	}

	@Override
	protected void createDefaultEditPolicies() {

		super.createDefaultEditPolicies();

		// installEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE, new AdoneSequenceReferenceEditPolicy());
		// installEditPolicy(UpdateConnectionReferenceEditPolicy.UDPATE_CONNECTION_REFERENCE, new AdoneUpdateConnectionReferenceEditPolicy());
		// installEditPolicy(UpdateWeakReferenceForExecSpecEditPolicy.UDPATE_WEAK_REFERENCE_FOR_EXECSPEC, new AdoneUpdateWeakReferenceForExecSpecEditPolicy());

		// Removed policies due to issues with strong & weak references and other references not functioning properly.
		removeEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);
		removeEditPolicy(UpdateConnectionReferenceEditPolicy.UDPATE_CONNECTION_REFERENCE);
		removeEditPolicy(UpdateWeakReferenceForExecSpecEditPolicy.UDPATE_WEAK_REFERENCE_FOR_EXECSPEC);

		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new AdoneBehaviorExecutionSpecResizePolicy());

	}

	@Override
	protected IFigure createNodeShape() {
		// Redefines the ExecutionSpecificationRectangleFigure to use a new class for customization.
		return primaryShape = new AdoneExecutionSpecificationRectangleFigure(this);
	}

	@Override
	public String toString() {
		BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) this.resolveSemanticElement();
		if (bes != null) {
			Lifeline parentLifeline = bes.getCovereds().get(0);
			Type lifelineType = parentLifeline.getRepresents().getType();
			if (lifelineType != null) {
				return lifelineType.getQualifiedName() + " : " + getOrderNumberOfBes(parentLifeline, bes) + " th BES" + "\n" + super.toString();
			} else {
				return parentLifeline.getName() + "\n" + super.toString();
			}
		}
		return super.toString();
	}

	/**
	 * Calculates the order number of a specific BehaviorExecutionSpecification (BES) among
	 * all BESs covered by a given lifeline. The order is determined based on the sequence
	 * of InteractionFragments associated with the lifeline.
	 *
	 * @param parent
	 *            The lifeline covering the BESs.
	 * @param targetBes
	 *            The target BES for which the order number is to be determined.
	 * @return The order number of the target BES as a string. If the target BES is not found
	 *         among the lifeline's covered BESs, returns "Not Found".
	 */
	private String getOrderNumberOfBes(Lifeline parent, BehaviorExecutionSpecification targetBes) {
		List<InteractionFragment> fragments = parent.getCoveredBys();
		int order = 1; // Start counting from 1.
		for (InteractionFragment fragment : fragments) {
			if (fragment instanceof BehaviorExecutionSpecification) {
				BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) fragment;
				if (bes.equals(targetBes)) {
					return String.valueOf(order);
				}
				order++; // Move to the next BES in sequence.
			}
		}
		return "Not Found"; // Return "Not Found" if the target BES is not found.
	}

	@Override
	public Command getCommand(Request request) {

		// Prevent reconnection of message connections to different lifelines in sequence diagrams. (2024-02-04)

		if (request.getType().equals("Reconnection target")) {
			return UnexecutableCommand.INSTANCE;
		}

		if (request.getType().equals("Reconnection source")) {
			return UnexecutableCommand.INSTANCE;
		}

		return super.getCommand(request);
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		// Redefines the DragTracker to use a new class for customized drag behavior.
		return new AdoneBehaviorExecutionDragEditPartsTracker(this);
	}

	@Override
	protected void refreshBounds() {

		// Attempts to slightly(+5) move the BES to the right for recursive messages using the following code do not apply the changes.
		// Further investigation is required to find an alternative method for implementation.

		int width = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getSize_Width())).intValue();
		int height = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getSize_Height())).intValue();
		Dimension size = new Dimension(width, height);
		int x = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getLocation_X())).intValue();
		int y = ((Integer) getStructuralFeatureValue(NotationPackage.eINSTANCE.getLocation_Y())).intValue();
		Point loc = new Point(x, y);

		AdoneMessageSyncEditPart messageEp = getStartingMessage();

		if (messageEp != null) {
			if (SelfMessageHelper.isSelfLink(messageEp)) {
				loc.setX(loc.x + 5);
				loc.setY(loc.y);
			}
		}

		((GraphicalEditPart) getParent()).setLayoutConstraint(
				this,
				getFigure(),
				new Rectangle(loc, size));

	}

	/**
	 * Retrieves the EditPart of the message initiating the Behavior Execution Specification (BES).
	 * This is necessary to identify and handle the starting message, especially in cases of recursive messages.
	 * Initially, the start event of BES might be an ExecutionOccurrenceSpecification or explicitly set to a
	 * MessageOccurrenceSpecification, and this method accommodates such variations by searching through the
	 * interaction's messages to find the corresponding starting message.
	 *
	 * @return The EditPart of the starting message if found, null otherwise.
	 */
	private AdoneMessageSyncEditPart getStartingMessage() {

		AdoneMessageSyncEditPart messageEp = null;

		if (this.resolveSemanticElement() != null) {

			BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) this.resolveSemanticElement();

			if (bes.getStart() != null) {

				Message startingMessage = null;

				// For recursive messages, initially, the start event might be an ExecutionOccurrenceSpecification
				// or forcibly set to a MessageOccurrenceSpecification, requiring consideration.
				if (bes.getStart() instanceof ExecutionOccurrenceSpecification) {

					ExecutionOccurrenceSpecification eos = (ExecutionOccurrenceSpecification) bes.getStart();
					Interaction intac = eos.getEnclosingInteraction();

					for (Message msg : intac.getMessages()) {
						if (msg.getReceiveEvent().equals(eos)) {
							startingMessage = msg;
							break;
						}
					}

				} else if (bes.getStart() instanceof MessageOccurrenceSpecification) {
					MessageOccurrenceSpecification mos = (MessageOccurrenceSpecification) bes.getStart();
					startingMessage = mos.getMessage();

				}

				IGraphicalEditPart ep = AdoneSequenceUtil.getEditPartFromSemantic(this, startingMessage);
				if (ep != null) {
					messageEp = (AdoneMessageSyncEditPart) ep;
				}
			}
		}

		return messageEp;
	}

}
