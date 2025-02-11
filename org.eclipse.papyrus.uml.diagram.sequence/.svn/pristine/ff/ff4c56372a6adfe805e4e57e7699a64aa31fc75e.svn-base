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
 *   RealizeSoft - initial API and implementation
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.impl.ShapeImpl;
import org.eclipse.papyrus.infra.gmfdiag.common.SemanticFromGMFElement;
import org.eclipse.papyrus.uml.diagram.sequence.command.SetResizeCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneMessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.SequenceReferenceEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneUpdateLocationByNewMessageCreationRequest;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

/**
 * Provides a collection of utility methods designed to facilitate graphical processing and manipulations within sequence diagrams.
 * The AdoneSequenceUtil class offers a range of functionalities aimed at enhancing the handling and analysis of sequence diagrams
 * through a variety of operations. These operations include finding lifeline edit parts relative to other elements, calculating
 * absolute bounds for graphical elements, transforming coordinates to account for viewport effects, and identifying nested or
 * covered edit parts within combined fragments or interaction operands.
 */
public class AdoneSequenceUtil {

	/**
	 * Retrieves the previous LifelineEditPart in the sequence relative to the specified base edit part. Since the lifeline information
	 * is stored sequentially, this method iterates through all LifelineEditParts to find the one immediately preceding the base edit part.
	 * This approach ensures the correct chronological order is maintained in alignment with the sequence diagram's structure.
	 *
	 * @param baseEditPart
	 *            The reference edit part from which the previous LifelineEditPart is sought.
	 * @return The LifelineEditPart immediately preceding the specified base edit part, or null if the base edit part is the first in the sequence.
	 */
	public static LifelineEditPart getPreviousLifelineEditPart(EditPart baseEditPart) {
		LifelineEditPart previousPart = null;

		// Iterate through all LifelineEditParts to find the one immediately before the baseEditPart
		for (Object sequenceElement : getAllLifelineEditParts(baseEditPart)) {
			if (sequenceElement instanceof LifelineEditPart) {
				LifelineEditPart part = (LifelineEditPart) sequenceElement;

				// Break the loop once the baseEditPart is found, previousPart will be the one just before it
				if (part.equals(baseEditPart)) {
					break;
				} else {
					previousPart = part;
				}
			}
		}
		return previousPart;
	}

	/**
	 * Retrieves the LifelineEditPart that is immediately to the left of the specified base edit part based on their positions on the X-axis.
	 * This method is particularly useful in diagrams where the spatial arrangement of lifelines represents their sequence or relative order.
	 *
	 * @param baseEditPart
	 *            The reference edit part from which to find the preceding LifelineEditPart.
	 * @return The LifelineEditPart immediately to the left of the specified base edit part, or null if no such part exists.
	 */
	public static LifelineEditPart getPreviousLifelineEditPartByPosition(EditPart baseEditPart) {
		LifelineEditPart previousPart = null;
		int baseX = Integer.MAX_VALUE; // Initialize the X position of the current EditPart to the maximum value.

		// Retrieve the position of the baseEditPart.
		if (baseEditPart instanceof LifelineEditPart) {
			Rectangle baseBounds = ((LifelineEditPart) baseEditPart).getFigure().getBounds();
			baseX = baseBounds.x; // Save the X position.
		}

		int closestLeftDistance = Integer.MAX_VALUE; // Initialize the closest distance to the left to the maximum value.

		for (Object sequenceElement : getAllLifelineEditParts(baseEditPart)) {
			if (sequenceElement instanceof LifelineEditPart) {

				if (sequenceElement.equals(baseEditPart)) {
					continue;
				}

				LifelineEditPart part = (LifelineEditPart) sequenceElement;
				Rectangle partBounds = part.getFigure().getBounds();
				int partX = partBounds.x; // Get the X position of the part.

				// Check if the current part is to the left of baseEditPart and is the closest.
				if (partX < baseX && (baseX - partX) < closestLeftDistance) {
					closestLeftDistance = baseX - partX;
					previousPart = part; // Store the closest left part.
				}
			}
		}

		return previousPart;
	}

	/**
	 * Retrieves the LifelineEditPart that directly follows the specified base edit part in the sequence. This method iterates
	 * through all LifelineEditParts and returns the one immediately after the base edit part, reflecting the sequential order
	 * of lifelines within the sequence diagram.
	 *
	 * @param baseEditPart
	 *            The reference edit part from which the next LifelineEditPart is sought.
	 * @return The LifelineEditPart immediately following the specified base edit part, or null if the base edit part is the last in the sequence.
	 */
	public static LifelineEditPart getNextLifelineEditPart(EditPart baseEditPart) {
		LifelineEditPart nextPart = null;
		boolean found = false;

		// Iterate through all LifelineEditParts to find the one immediately after the baseEditPart
		for (Object sequenceElement : getAllLifelineEditParts(baseEditPart)) {
			if (sequenceElement instanceof LifelineEditPart) {
				LifelineEditPart part = (LifelineEditPart) sequenceElement;

				// Mark as found when the baseEditPart is encountered
				if (part.equals(baseEditPart)) {
					found = true;
				} else {
					// Once found, the next LifelineEditPart is the one we're looking for
					if (found) {
						nextPart = part;
						break;
					}
				}
			}
		}
		return nextPart;
	}

	/**
	 * Retrieves all LifelineEditParts from the interaction compartment that the host edit part belongs to, sorted by their
	 * X-axis positions. This ensures a consistent order that reflects their visual arrangement in the sequence diagram.
	 *
	 * @param hostEditPart
	 *            The edit part from which to start searching for the interaction compartment and its lifelines.
	 * @return A list of LifelineEditParts sorted from left to right according to their position in the sequence diagram.
	 */
	public static List<LifelineEditPart> getAllLifelineEditParts(EditPart hostEditPart) {
		List<LifelineEditPart> foundLifelines = new ArrayList<>();

		// Get the interaction compartment containing lifelines
		final EditPart interactionCompartment = SequenceUtil.getInteractionCompartment(hostEditPart);
		if (null != interactionCompartment) {
			for (Object child : interactionCompartment.getChildren()) {
				if (child instanceof LifelineEditPart) {
					final LifelineEditPart lifelineEditPart = (LifelineEditPart) child;
					foundLifelines.add(lifelineEditPart);
				}
			}
		}

		// Sort lifelines based on their X-axis positions to ensure left-to-right order
		Collections.sort(foundLifelines, new Comparator<LifelineEditPart>() {

			@Override
			public int compare(LifelineEditPart part1, LifelineEditPart part2) {

				if (part1.getFigure().getBounds().getLeft().x > part2.getFigure().getBounds().getLeft().x) {
					return 1;
				} else if (part1.getFigure().getBounds().getLeft().x < part2.getFigure().getBounds().getLeft().x) {
					return -1;
				}

				return 0;
			}
		});

		return foundLifelines;
	}

	/**
	 * @param changeBoundsRequest
	 * @return
	 */
	public static GraphicalEditPart getPreviousLifelineEditPart(ChangeBoundsRequest request) {
		return getPreviousLifelineEditPart((LifelineEditPart) (request.getEditParts().get(0)));
	}

	/**
	 * @param changeBoundsRequest
	 * @return
	 */
	public static GraphicalEditPart getNextLifelineEditPart(ChangeBoundsRequest changeBoundsRequest) {
		return getNextLifelineEditPart((LifelineEditPart) (changeBoundsRequest.getEditParts().get(0)));
	}

	/**
	 * Retrieves the EditPart representing the sequence diagram that contains the given source edit part. This method
	 * navigates the edit part hierarchy to find the diagram edit part corresponding to different types of sequence diagram
	 * components like lifelines, messages, and combined fragments.
	 *
	 * @param sourceEditPart
	 *            The source edit part for which to find the containing sequence diagram edit part.
	 * @return The EditPart of the sequence diagram containing the source edit part, or null if it cannot be determined.
	 */
	public static EditPart getSequenceDiagramEditPart(EditPart sourceEditPart) {
		if (sourceEditPart instanceof LifelineEditPart) {
			// For lifelines, navigate three levels up the hierarchy to get to the sequence diagram edit part
			return sourceEditPart.getParent().getParent().getParent();
		} else if (sourceEditPart instanceof AbstractMessageEditPart || sourceEditPart instanceof CombinedFragmentEditPart) {
			// For messages and combined fragments, the sequence diagram edit part is one level up
			return sourceEditPart.getParent();
		}

		// Return null if the source edit part does not match the expected types
		return null;
	}


	/**
	 * Retrieves a list of LifelineEditParts that are positioned to the right of the target EditPart involved in a move operation.
	 * This method is useful for determining the sequence of lifelines that will be affected by the relocation of a specific edit part.
	 *
	 * @param changeBoundsRequest
	 *            The request containing information about the move operation, including the target location.
	 * @return A sorted list of LifelineEditParts that are located to the right of the target EditPart after the move operation.
	 */
	public static List<LifelineEditPart> getAllNextLifelineEditPart(ChangeBoundsRequest changeBoundsRequest) {
		// Extract the X position from the move target location.
		int targetX = changeBoundsRequest.getLocation().x;
		EditPart baseEp = (EditPart) changeBoundsRequest.getEditParts().get(0);

		// Retrieve all LifelineEditParts from the parent EditPart.
		List<LifelineEditPart> allLifelines = getAllLifelineEditParts(baseEp.getParent());

		List<LifelineEditPart> nextLifelines = new ArrayList<>();

		// Identify and collect all LifelineEditParts that are positioned after the target EditPart.
		boolean foundBaseEp = false;
		for (Object element : allLifelines) {
			if (element instanceof LifelineEditPart) {
				LifelineEditPart lifeline = (LifelineEditPart) element;
				// Check if the target EditPart has been found.
				if (lifeline.equals(baseEp)) {
					foundBaseEp = true;
				} else if (foundBaseEp) {
					// Add lifelines that are positioned after the target EditPart.
					Rectangle bounds = lifeline.getFigure().getBounds();
					if (bounds.x > targetX) {
						nextLifelines.add(lifeline);
					}
				}
			}
		}

		// Sort the collected lifelines by their X coordinates.
		Collections.sort(nextLifelines, new Comparator<GraphicalEditPart>() {
			@Override
			public int compare(GraphicalEditPart o1, GraphicalEditPart o2) {
				Rectangle bounds1 = o1.getFigure().getBounds();
				Rectangle bounds2 = o2.getFigure().getBounds();
				return Integer.compare(bounds1.x, bounds2.x);
			}
		});

		return nextLifelines;
	}

	/**
	 * Retrieves a list of LifelineEditParts that are positioned to the right of the specified position within the same parent EditPart.
	 * This method is useful for identifying lifelines that will be affected by operations targeting a specific location within a sequence diagram.
	 *
	 * @param position
	 *            The reference position used to determine the "next" lifelines.
	 * @param ep
	 *            The EditPart used as a reference point for finding the parent container and its lifelines.
	 * @return A sorted list of LifelineEditParts to the right of the specified position, sorted by their X-axis positions.
	 */
	public static List<LifelineEditPart> getAllNextLifelineEditPart(Point position, EditPart ep) {
		// Extract the X position from the reference location.
		int targetX = position.x;

		// Retrieve all LifelineEditParts from the parent EditPart.
		List<LifelineEditPart> allLifelines = getAllLifelineEditParts(ep.getParent());

		List<LifelineEditPart> nextLifelines = new ArrayList<>();

		// Identify and collect all LifelineEditParts that are positioned to the right of the specified position.
		for (Object element : allLifelines) {
			if (element instanceof LifelineEditPart) {
				LifelineEditPart lifeline = (LifelineEditPart) element;
				// Adjusted comparison to consider lifelines slightly to the left of the target position as
				// "next" due to potential alignment or margin considerations.
				Rectangle bounds = lifeline.getFigure().getBounds();
				if (bounds.x > targetX - 2) {
					nextLifelines.add(lifeline);
				}
			}
		}

		// Sort the collected lifelines by their X coordinates to ensure they are ordered from left to right.
		Collections.sort(nextLifelines, new Comparator<GraphicalEditPart>() {
			@Override
			public int compare(GraphicalEditPart o1, GraphicalEditPart o2) {
				Rectangle bounds1 = ((LifelineEditPart) o1).getFigure().getBounds();
				Rectangle bounds2 = ((LifelineEditPart) o2).getFigure().getBounds();
				return bounds1.x - bounds2.x;
			}
		});

		return nextLifelines;
	}

	/**
	 * Finds and returns the IGraphicalEditPart corresponding to a given semantic element within the context of a specific edit part's diagram.
	 * This method searches through all primary edit parts (ignoring compartments and label edit parts) to find one that represents the specified semantic element.
	 *
	 * @param editpart
	 *            The EditPart that provides the context for the search, typically the root or a parent edit part in the diagram.
	 * @param semanticElement
	 *            The semantic element for which the corresponding IGraphicalEditPart is being searched.
	 * @return The IGraphicalEditPart that represents the given semantic element, or null if no such edit part can be found.
	 */
	public static IGraphicalEditPart getEditPartFromSemantic(EditPart editpart, final Object semanticElement) {

		if (semanticElement == null) {
			return null;
		}

		IGraphicalEditPart researchedEditPart = null;
		final SemanticFromGMFElement semanticFromGMFElement = new SemanticFromGMFElement();
		final EditPartViewer editPartViewer = editpart.getViewer();
		if (editPartViewer != null) {
			// look for all edit part if the semantic is contained in the list
			final Iterator<?> iter = editPartViewer.getEditPartRegistry().values().iterator();

			while (iter.hasNext() && researchedEditPart == null) {
				final Object currentEditPart = iter.next();
				// look only amidst IPrimary editpart to avoid compartment and labels of links
				if (currentEditPart instanceof IPrimaryEditPart) {
					final Object currentElement = semanticFromGMFElement.getSemanticElement(currentEditPart);
					if (semanticElement.equals(currentElement)) {
						researchedEditPart = ((IGraphicalEditPart) currentEditPart);
					}
				}
			}
		}
		return researchedEditPart;
	}

	/**
	 * Retrieves all LifelineEditParts that are positioned to the right of a given LifelineEditPart within the same container.
	 * This method is useful for identifying subsequent lifelines in a sequence diagram that follow a specific lifeline.
	 *
	 * @param lifelineEditPart
	 *            The LifelineEditPart from which to find all subsequent (right-positioned) lifelines.
	 * @return A list of LifelineEditParts that are located to the right of the specified lifeline.
	 */
	public static List<LifelineEditPart> getAllNextLifelineEditParts(LifelineEditPart lifelineEditPart) {
		List<LifelineEditPart> nextLifelines = new ArrayList<>();

		// Obtain the parent EditPart of the current lifeline to iterate over its children.
		EditPart parent = lifelineEditPart.getParent();

		// If the lifeline doesn't have a parent, return an empty list.
		if (parent == null) {
			return nextLifelines; // 빈 리스트 반환
		}

		// Get all sibling EditParts of the current lifeline.
		List<?> siblings = parent.getChildren();

		// Retrieve the location of the current lifeline for comparison.
		Point currentLifelineLocation = lifelineEditPart.getFigure().getBounds().getLocation();

		// Iterate through all sibling EditParts to identify those that are LifelineEditParts to the right of the current one.
		for (Object sibling : siblings) {
			if (sibling instanceof LifelineEditPart) {
				LifelineEditPart siblingLifeline = (LifelineEditPart) sibling;
				Point siblingLocation = siblingLifeline.getFigure().getBounds().getLocation();

				// Add sibling lifelines that are positioned to the right of the current lifeline to the list.
				if (siblingLocation.x > currentLifelineLocation.x) {
					nextLifelines.add(siblingLifeline);
				}
			}
		}

		return nextLifelines;
	}

	/**
	 * Identifies and returns a list of LifelineEditParts that are covered by the bounds of a given CombinedFragmentEditPart.
	 * This method is particularly useful for determining which lifelines are included within the scope of a combined fragment in a sequence diagram.
	 *
	 * @param cmbFrgEp
	 *            The CombinedFragmentEditPart whose bounds are used to determine coverage of lifelines.
	 * @return A list of LifelineEditParts that fall within the bounds of the specified CombinedFragmentEditPart.
	 */
	public static List<LifelineEditPart> getCoveredLifelinesByFigureBounds(CombinedFragmentEditPart cmbFrgEp) {
		List<LifelineEditPart> coveredLifelines = new ArrayList<>();

		// Obtain the bounds of the CombinedFragmentEditPart's figure for comparison.
		Rectangle cmbFrgBounds = cmbFrgEp.getFigure().getBounds();

		// Iterate through the children of the CombinedFragmentEditPart's parent to check for overlap with its bounds.
		for (Object child : cmbFrgEp.getParent().getChildren()) {
			if (child instanceof LifelineEditPart) {
				LifelineEditPart lifelineEp = (LifelineEditPart) child;
				Rectangle lifelineBounds = lifelineEp.getFigure().getBounds();

				// If the lifeline's bounds intersect with the combined fragment's bounds, add it to the list.
				if (cmbFrgBounds.intersects(lifelineBounds)) {
					coveredLifelines.add(lifelineEp);
				}
			}
		}

		return coveredLifelines;
	}

	/**
	 * Retrieves a list of LifelineEditParts that are covered by a CombinedFragment based on the model element relationships.
	 * This approach uses the semantic model to determine coverage rather than relying on graphical bounds, providing a more accurate
	 * representation of the combined fragment's scope within a sequence diagram.
	 *
	 * @param cmbFrgEp
	 *            The CombinedFragmentEditPart representing the combined fragment in question.
	 * @return A list of LifelineEditParts that are semantically covered by the specified CombinedFragmentEditPart.
	 */
	public static List<LifelineEditPart> getCoveredLifelinesByModel(CombinedFragmentEditPart cmbFrgEp) {

		List<LifelineEditPart> coveredLifelines = new ArrayList<>();

		// Obtain the semantic model element of the CombinedFragmentEditPart.
		CombinedFragment combinedFragment = (CombinedFragment) ((View) cmbFrgEp.getModel()).getElement();

		// Retrieve the list of lifelines covered by the combined fragment according to the model.
		List<Lifeline> covered = combinedFragment.getCovereds();

		// Iterate through the parent's children to find LifelineEditParts that correspond to the covered lifelines.
		for (Object child : cmbFrgEp.getParent().getChildren()) {
			if (child instanceof LifelineEditPart) {
				LifelineEditPart lifelineEp = (LifelineEditPart) child;
				Lifeline lifeline = (Lifeline) ((View) lifelineEp.getModel()).getElement();
				if (covered.contains(lifeline)) {
					coveredLifelines.add(lifelineEp);
				}
			}
		}

		return coveredLifelines;

	}

	/**
	 * Retrieves the LifelineEditPart immediately to the left of a given position within a sequence diagram.
	 * This method is useful for identifying the lifeline that precedes a particular location or edit part based on the X coordinate.
	 * It compares the X coordinates of all lifelines to find the one closest to, but not exceeding, the target position.
	 *
	 * @param changeBoundsRequest
	 *            The request containing the target location for which the preceding lifeline is sought.
	 * @param parentEditPart
	 *            The parent edit part, typically the interaction compartment, containing the lifelines.
	 * @return The LifelineEditPart immediately preceding the target location, or null if no such lifeline exists.
	 */
	public static LifelineEditPart getPreviousLifelineEditPartByPosition(ChangeBoundsRequest changeBoundsRequest, EditPart parentEditPart) {
		LifelineEditPart previousLifeline = null;
		int targetX = changeBoundsRequest.getLocation().x; // Target X position from the request.
		int closestDistance = Integer.MAX_VALUE; // Initialize with the maximum possible distance.

		// Obtain all lifeline edit parts from the parent edit part. The implementation may vary.
		List<LifelineEditPart> lifelines = getAllLifelineEditParts(parentEditPart);

		for (LifelineEditPart lifeline : lifelines) {
			Rectangle bounds = lifeline.getFigure().getBounds();
			int lifelineX = bounds.x; // Current lifeline's X position.

			// Check if the current lifeline is to the left of the target and closer than any previously found.
			if (lifelineX < targetX) {
				int distance = targetX - lifelineX;
				if (distance < closestDistance) {
					closestDistance = distance;
					previousLifeline = lifeline; // Update the closest left-hand lifeline.
				}
			}
		}

		// Return the lifeline immediately to the left of the target position, if any.
		return previousLifeline;
	}

	/**
	 * Retrieves the EditParts associated with a specific model element of a selected message. This method navigates through the
	 * model to identify related EditParts such as Behavior Execution Specifications, Messages, and Combined Fragments that are
	 * connected via weak references to the selected message.
	 *
	 * @param selectedMessageEp
	 *            The graphical edit part of the selected message from which to find related EditParts.
	 * @return A list of EditParts related to the selected message, including behavior executions, messages, and combined fragments.
	 */
	public static List<EditPart> getMessageRelatedEditPartByModelElement(GraphicalEditPart selectedMessageEp) {

		List<EditPart> relatedEditParts = new ArrayList<>();

		// Check if 'selectedMessageEp' represents a UML Message in the model.
		if (selectedMessageEp.getModel() instanceof View) {
			View view = (View) selectedMessageEp.getModel();
			if (view.getElement() instanceof Message) {
				Message message = (Message) view.getElement();
				MessageEnd sendEvent = message.getSendEvent();
				MessageEnd receiveEvent = message.getReceiveEvent();

				// Retrieve the Interaction or InteractionOperand containing the fragment.
				MessageEnd fragment = receiveEvent;

				Element owner = fragment.getOwner();
				List<InteractionFragment> fragments = null;

				// Fetch the list of fragments.
				if (owner instanceof Interaction) {
					fragments = ((Interaction) owner).getFragments();
				} else if (owner instanceof InteractionOperand) {
					fragments = ((InteractionOperand) owner).getFragments();
				}

				// Find the start index for the fragments.
				int startIndex = fragments.indexOf(receiveEvent) + 1;

				// Identify the end point of the message call through weak references.
				InteractionFragment msgEndFragment = null;

				if (selectedMessageEp.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE) != null) {
					SequenceReferenceEditPolicy references = (SequenceReferenceEditPolicy) selectedMessageEp.getEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);
					for (Entry<EditPart, String> iterator : references.getWeakReferences().entrySet()) {
						if (iterator.getKey() instanceof MessageSyncEditPart) {
							MessageSyncEditPart msgEp = (MessageSyncEditPart) iterator.getKey();
							if (msgEp.getSource().equals(((MessageSyncEditPart) selectedMessageEp).getSource())) {
								Message refMsg = (Message) msgEp.resolveSemanticElement();
								msgEndFragment = (InteractionFragment) refMsg.getSendEvent();
								break;
							}
						} else if (iterator.getKey() instanceof BehaviorExecutionSpecificationEditPart) {
							BehaviorExecutionSpecificationEditPart besEp = (BehaviorExecutionSpecificationEditPart) iterator.getKey();

							if (besEp.getParent().equals(((MessageSyncEditPart) selectedMessageEp).getSource())) {
								BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) besEp.resolveSemanticElement();
								msgEndFragment = bes.getFinish();
								break;
							}
						}
					}
				}

				// Iterate through all fragments to find related Behavior Execution Specifications and Messages.
				for (int i = startIndex; i < fragments.size(); i++) {
					InteractionFragment currentFragment = fragments.get(i);

					// Stop the iteration if the end fragment of the message call is reached.
					if (msgEndFragment != null && currentFragment.equals(msgEndFragment)) {
						break;
					}

					// If the current fragment is an instance of ExecutionSpecification, find its corresponding EditPart.
					if (currentFragment instanceof BehaviorExecutionSpecification) {
						GraphicalEditPart execSpecEditPart = (GraphicalEditPart) SequenceUtil.getEditPart(selectedMessageEp.getRoot(), currentFragment, AdoneBehaviorExecutionSpecificationEditPart.class);
						if (execSpecEditPart != null) {
							relatedEditParts.add(execSpecEditPart);
						}
					}

					// If the current fragment is associated with a message, find its corresponding EditPart.
					if (currentFragment instanceof MessageOccurrenceSpecification) {
						Message currentMessage = ((MessageOccurrenceSpecification) currentFragment).getMessage();
						if (currentMessage != null) {
							GraphicalEditPart messageEditPart = (GraphicalEditPart) SequenceUtil.getEditPart(selectedMessageEp.getRoot(), currentMessage, AdoneMessageSyncEditPart.class);
							if (messageEditPart != null) {
								relatedEditParts.add(messageEditPart);
							}
						}
					}

					// If the current fragment is a CombinedFragment, find its corresponding EditPart.
					if (currentFragment instanceof CombinedFragment) {
						CombinedFragment cmbFragment = (CombinedFragment) currentFragment;
						GraphicalEditPart messageEditPart = findEditPartForCombinedFragment(cmbFragment, selectedMessageEp.getViewer());
						if (messageEditPart != null) {
							relatedEditParts.add(messageEditPart);
						}
					}
				}
			}
		}
		return relatedEditParts;
	}


	/**
	 * Searches for the GraphicalEditPart representing a CombinedFragment within the given viewer's EditPartRegistry.
	 * This method looks up the CombinedFragment in the EditPartRegistry to find its corresponding EditPart.
	 *
	 * @param cmbFragment
	 *            The CombinedFragment model element for which the EditPart is sought.
	 * @param viewer
	 *            The EditPartViewer containing the EditPartRegistry to search within.
	 * @return The GraphicalEditPart corresponding to the CombinedFragment if found; otherwise, returns null.
	 */
	private static GraphicalEditPart findEditPartForCombinedFragment(CombinedFragment cmbFragment, EditPartViewer viewer) {
		// Locate the EditPart corresponding to the CombinedFragment in the viewer's EditPartRegistry.
		EditPart editPart = (EditPart) viewer.getEditPartRegistry().get(cmbFragment);

		// Verify if the located EditPart is an instance of GraphicalEditPart.
		if (editPart instanceof GraphicalEditPart) {
			// Cast and return the found EditPart as a GraphicalEditPart.
			return (GraphicalEditPart) editPart;
		}

		// Return null if no corresponding EditPart for the CombinedFragment was found.
		return null;
	}

	/**
	 * Retrieves a list of GraphicalEditParts that are graphically related to the given message EditPart.
	 * This method determines relationships based on graphical proximity or overlap within the sequence diagram.
	 *
	 * @param msgEp
	 *            The GraphicalEditPart representing a message for which related EditParts are sought.
	 * @return A list of GraphicalEditParts that are considered graphically related to the message EditPart.
	 */
	public static List<GraphicalEditPart> getMessageRelatedEditPartsByView(GraphicalEditPart msgEp) {
		List<GraphicalEditPart> relatedEditParts = new ArrayList<>();

		// Obtain the bounds of the message EditPart.
		Rectangle msgBounds = msgEp.getFigure().getBounds();

		// Iterate over all EditParts in the sequence diagram.
		Map<?, ?> editPartRegistry = msgEp.getViewer().getEditPartRegistry();
		for (Object value : editPartRegistry.values()) {
			if (value instanceof GraphicalEditPart) {
				GraphicalEditPart part = (GraphicalEditPart) value;
				// Identify EditParts that could be related to the message based on graphical criteria (e.g., location).
				if (isGraphicallyRelated(msgBounds, part)) {
					relatedEditParts.add(part);
				}
			}
		}

		return relatedEditParts;
	}


	/**
	 * Determines whether two EditParts are graphically related based on their bounds.
	 * This method is used to identify if the specified EditPart is within a certain graphical
	 * proximity or overlap with the message EditPart, indicating a graphical relationship.
	 *
	 * @param msgBounds
	 *            The bounds of the message EditPart.
	 * @param part
	 *            The EditPart to be checked for a graphical relationship with the message EditPart.
	 * @return true if the two EditParts are graphically related, indicating proximity or overlap.
	 */
	private static boolean isGraphicallyRelated(Rectangle msgBounds, GraphicalEditPart part) {
		// This method should contain the logic defining the graphical relationship.
		// For example, you can consider EditParts related if the position of one is within a certain range of the other.
		Rectangle partBounds = part.getFigure().getBounds();
		// Add specific conditions here to determine graphical relationship.
		// For instance, check if the y-coordinate of the other EditPart falls within the y-coordinate range of the message.
		return msgBounds.intersects(partBounds);
	}


	/**
	 * Retrieves all EditParts below a specified move target EditPart based on a change request.
	 * This method filters EditParts that are visually located below the move target EditPart,
	 * which is crucial for determining layout adjustments or interactions that depend on spatial relationships.
	 *
	 * @param moveTargetEp
	 *            The target EditPart being moved or affected by the move.
	 * @param changeBoundsRequest
	 *            The request that contains information about the move, including location and delta.
	 * @return A list of GraphicalEditParts that are visually located below the move target EditPart.
	 */
	public static List<GraphicalEditPart> getAllBelowEditParts(GraphicalEditPart moveTargetEp, ChangeBoundsRequest changeBoundsRequest) {
		List<GraphicalEditPart> belowParts = new ArrayList<>();

		if (moveTargetEp != null && moveTargetEp.getParent() != null) {

			Rectangle moveTargetBounds;
			;
			if (moveTargetEp instanceof AbstractMessageEditPart) {

				ConnectionEditPart connectionEditPart = (ConnectionEditPart) moveTargetEp;
				Connection connectionFigure = connectionEditPart.getConnectionFigure();

				// Get the PointList which contains all points of the Connection
				PointList points = connectionFigure.getPoints();

				// Retrieve the start and end points
				Point startPoint = points.getFirstPoint().getCopy();
				Point endPoint = points.getLastPoint().getCopy();

				// Translate these points to absolute coordinates
				connectionFigure.getParent().translateToAbsolute(startPoint);
				connectionFigure.getParent().translateToAbsolute(endPoint);

				Point msgPoint = SequenceUtil.getAbsoluteEdgeExtremity((ConnectionNodeEditPart) connectionEditPart, false);

				moveTargetBounds = new Rectangle();
				moveTargetBounds.setLocation(msgPoint);


			} else {
				moveTargetBounds = moveTargetEp.getFigure().getBounds().getCopy();
				moveTargetEp.getFigure().getParent().translateToAbsolute(moveTargetBounds);
			}

			// Adjust the target's Y-coordinate based on move or resize request.
			int moveTargetY = 0;

			if (moveTargetEp instanceof CombinedFragmentEditPart || moveTargetEp instanceof InteractionOperandEditPart) {
				if (changeBoundsRequest.getType().equals(RequestConstants.REQ_MOVE_CHILDREN)) {
					moveTargetY = moveTargetBounds.y;
				} else if (changeBoundsRequest.getType().equals(RequestConstants.REQ_RESIZE_CHILDREN)) {
					moveTargetY = moveTargetBounds.y + moveTargetBounds.height;
				}

			} else {
				moveTargetY = moveTargetBounds.y;
			}

			// Access the diagram's edit part registry to iterate over all components.
			Map registry = moveTargetEp.getViewer().getEditPartRegistry();

			for (Object child : registry.values()) {

				// Skip iteration if the child is the move target itself.
				if (child.equals(moveTargetEp)) {
					continue;
				}

				// Handle LifelineEditPart instances to find connections below the target.
				if (child instanceof LifelineEditPart) {

					LifelineEditPart lifeline = (LifelineEditPart) child;

					// Iterate over all source connections of the lifeline.
					for (Object srcCon : lifeline.getSourceConnections()) {

						ConnectionEditPart connectionEditPart = (ConnectionEditPart) srcCon;

						if (connectionEditPart.equals(moveTargetEp)) {
							continue;
						}

						Connection connectionFigure = connectionEditPart.getConnectionFigure();

						// Get the PointList which contains all points of the Connection
						PointList points = connectionFigure.getPoints();

						// Retrieve the start and end points
						Point startPoint = points.getFirstPoint().getCopy();
						Point endPoint = points.getLastPoint().getCopy();

						// Translate these points to absolute coordinates
						connectionFigure.getParent().translateToAbsolute(startPoint);
						connectionFigure.getParent().translateToAbsolute(endPoint);

						// Now you have the absolute Y-coordinates of the start and end points of the message
						int startY = startPoint.y;
						int endY = endPoint.y;

						// Skip if start or end Y-coordinates are negative (temporary fix for scrolling issues). (2024-01-02)
						if (startY < 0 || endY < 0) {
							continue;
						}

						// You can use these Y-coordinates for further logic
						// For example, check if the message is below a certain point
						if (startY >= moveTargetY || endY >= moveTargetY) {
							// Add the message EditPart to the list if it's below the target Y-coordinate
							if (!belowParts.contains(connectionEditPart)) {
								belowParts.add(connectionEditPart);
							}
						}

					}
				}

				if (child instanceof BehaviorExecutionSpecificationEditPart || child instanceof CombinedFragmentEditPart) {
					GraphicalEditPart childEp = (GraphicalEditPart) child;

					Rectangle childBounds = AdoneSequenceUtil.getAbsoluteBounds(childEp);

					if (childBounds.y >= moveTargetY) {
						if (!belowParts.contains(childEp)) {
							belowParts.add(childEp);
						}
					}

					// Additional checks for relationships between messages and BehaviorExecutionSpecifications. (2024-01-02)
					if (moveTargetEp instanceof AbstractMessageEditPart && child instanceof BehaviorExecutionSpecificationEditPart) {
						BehaviorExecutionSpecificationEditPart besEp = (BehaviorExecutionSpecificationEditPart) child;
						BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) besEp.resolveSemanticElement();
						AbstractMessageEditPart msgEp = (AbstractMessageEditPart) moveTargetEp;
						Message msg = (Message) msgEp.resolveSemanticElement();
						if (msg.getReceiveEvent().equals(bes.getStart())) {
							if (!belowParts.contains(childEp)) {
								belowParts.add(childEp);
							}
						}
					}
				}
			}
		}

		// Additional verification for message and BehaviorExecutionSpecification relationships. (2024-01-31)
		if (moveTargetEp instanceof AbstractMessageEditPart) {
			AbstractMessageEditPart msgEp = (AbstractMessageEditPart) moveTargetEp;
			Message msg = (Message) msgEp.resolveSemanticElement();
			BehaviorExecutionSpecification bes = AdoneSequenceUtil.getFollowingBehaviorExeSpec(msg);
			BehaviorExecutionSpecificationEditPart besEp = (BehaviorExecutionSpecificationEditPart) AdoneSequenceUtil.getEditPartFromSemantic(msgEp, bes);
			if (!belowParts.contains(besEp)) {
				belowParts.add(besEp);
			}
		}

		// Return the list of edit parts found below the specified target.
		return belowParts;
	}

	/**
	 * Retrieves all graphical edit parts that are located below a specified Y-coordinate in the diagram.
	 * This method iterates through all edit parts in the diagram's registry and checks if they are positioned
	 * below the given Y-coordinate. It specifically handles connections associated with lifelines and
	 * considers both Behavior Execution Specifications and Combined Fragments.
	 *
	 * @param hostEp
	 *            The graphical edit part that serves as the reference context, typically the host or container
	 *            in which the search is conducted.
	 * @param positionY
	 *            The Y-coordinate that serves as the threshold for determining whether an edit part
	 *            is considered "below". All edit parts with a lower Y-coordinate (i.e., a higher value)
	 *            than this threshold are included in the result.
	 * @return A list of GraphicalEditPart objects that are positioned below the specified Y-coordinate. This
	 *         includes connections that originate from lifelines, as well as Behavior Execution Specifications
	 *         and Combined Fragments that meet the criteria.
	 */
	public static List<GraphicalEditPart> getAllBelowEditPartsByYPosition(GraphicalEditPart hostEp, int positionY) {
		List<GraphicalEditPart> belowParts = new ArrayList<>();

		Map registry = hostEp.getViewer().getEditPartRegistry();

		for (Object child : registry.values()) {

			// Handle LifelineEditPart instances to find connections below the specified Y-coordinate.
			if (child instanceof LifelineEditPart) {

				LifelineEditPart lifeline = (LifelineEditPart) child;

				for (Object srcCon : lifeline.getSourceConnections()) {

					ConnectionEditPart connectionEditPart = (ConnectionEditPart) srcCon;

					Connection connectionFigure = connectionEditPart.getConnectionFigure();

					// Get the PointList which contains all points of the Connection
					PointList points = connectionFigure.getPoints();

					// Retrieve the start and end points
					Point startPoint = points.getFirstPoint().getCopy();
					Point endPoint = points.getLastPoint().getCopy();

					// Translate these points to absolute coordinates
					connectionFigure.getParent().translateToAbsolute(startPoint);
					connectionFigure.getParent().translateToAbsolute(endPoint);

					// Now you have the absolute Y-coordinates of the start and end points of the message
					int startY = startPoint.y;
					int endY = endPoint.y;

					// Skip if Y-coordinates are negative, addressing a temporary issue with scrolling. (2024-01-02)
					if (startY < 0 || endY < 0) {
						continue;
					}

					// Add the connection edit part to the list if it's below the specified Y-coordinate.
					if (startY >= positionY || endY >= positionY) {
						if (!belowParts.contains(connectionEditPart)) {
							belowParts.add(connectionEditPart);
						}
					}

				}
			}

			// Check for instances of Behavior Execution Specification EditPart or Combined Fragment EditPart.
			if (child instanceof BehaviorExecutionSpecificationEditPart || child instanceof CombinedFragmentEditPart) {
				GraphicalEditPart childEp = (GraphicalEditPart) child;

				// Calculate the absolute bounds and determine if the Y-coordinate is below the specified position.
				Rectangle childBounds = SequenceUtil.getAbsoluteBounds((IGraphicalEditPart) childEp);
				if (childBounds.y >= positionY) {
					if (!belowParts.contains(childEp)) {
						belowParts.add(childEp);
					}
				}
			}
		}

		// Return the list of edit parts found below the specified Y-coordinate.
		return belowParts;
	}


	/**
	 * Retrieves all Behavior Execution Specification Edit Parts that could be resized to accommodate a new message
	 * at a given location within a diagram. This method iterates over all lifeline edit parts within the diagram
	 * and checks their child Behavior Execution Specification Edit Parts against the specified location. If a
	 * Behavior Execution Specification's bounds make it a potential resize target based on the message's location,
	 * it is added to the list of related edit parts.
	 *
	 * @param editPart
	 *            The edit part (typically a diagram or compartment edit part) serving as the context for the search.
	 * @param location
	 *            The point where the new message is intended to be placed, used to determine potential resize targets.
	 * @return A list of Behavior Execution Specification Edit Parts that are potential resize targets for accommodating
	 *         the new message. These are determined based on their spatial relationship to the specified location.
	 */
	public static List<BehaviorExecutionSpecificationEditPart> getAllResizeTargetBehaviorExeSpecEditPartsForNewMessage(EditPart editPart, Point location) {
		// Initialize a list to store potential resize targets.
		List<BehaviorExecutionSpecificationEditPart> relatedEditParts = new ArrayList<>();

		// Iterate over all children of the interaction compartment, focusing on lifelines.
		for (Object child : SequenceUtil.getInteractionCompartment(editPart).getChildren()) {

			if (child instanceof LifelineEditPart) {
				LifelineEditPart lf = (LifelineEditPart) child;

				// Further iterate over the children of each lifeline, looking for Behavior Execution Specifications.
				for (Object lfChild : lf.getChildren()) {
					if (lfChild instanceof BehaviorExecutionSpecificationEditPart) {
						BehaviorExecutionSpecificationEditPart besEditPart = (BehaviorExecutionSpecificationEditPart) lfChild;
						IFigure figure = besEditPart.getFigure();

						// Retrieve the bounds of the Behavior Execution Specification's figure.
						Rectangle bounds = figure.getBounds();

						// Evaluate conditions to determine if the BES Edit Part overlaps with the new message location.
						boolean condition1 = bounds.x < location.x; // Check if BES is left of the new message.
						boolean condition2 = location.y >= bounds.y; // Check if BES starts above or at the same level as the new message.
						boolean condition3 = location.y <= (bounds.y + bounds.height); // Check if BES extends below the new message.

						// If all conditions are met, the BES Edit Part is a potential resize target.
						if (condition1 && condition2 && condition3) {
							relatedEditParts.add(besEditPart);
						}
					}
				}
			}
		}

		// Return the list of Behavior Execution Specification Edit Parts that are potential resize targets.
		return relatedEditParts;
	}


	/**
	 * Identifies and returns a set of lifelines covered by a specified selection rectangle within a diagram.
	 * This method iterates over all edit parts in the diagram's registry, filtering for those that are instances
	 * of ShapeEditPart and represent Lifeline elements. It then calculates the center X-coordinate of each
	 * lifeline's figure and checks whether this central line intersects with the selection rectangle provided.
	 * If an intersection is detected, the corresponding Lifeline model element is added to the set of covered
	 * lifelines to be returned.
	 *
	 * @param selectionRect
	 *            The rectangle defining the selection area on the diagram. Lifelines intersecting
	 *            this rectangle are considered to be covered by the selection.
	 * @param hostEditPart
	 *            The edit part that serves as the context for this operation, typically the diagram
	 *            or a compartment within it. It is used to access the diagram's edit part registry.
	 * @return A set of Lifeline elements that are covered by the selection rectangle. This set may be empty
	 *         if no lifelines intersect with the selection area.
	 */
	public static Set<Lifeline> getCoveredLifelines(Rectangle selectionRect, EditPart hostEditPart) {
		Set<Lifeline> coveredLifelines = new HashSet<>();
		// retrieve all the edit parts in the registry
		Set<Entry<Object, EditPart>> allEditPartEntries = hostEditPart.getViewer().getEditPartRegistry().entrySet();
		for (Entry<Object, EditPart> epEntry : allEditPartEntries) {
			EditPart ep = epEntry.getValue();
			if (ep instanceof ShapeEditPart) {
				ShapeEditPart sep = (ShapeEditPart) ep;
				EObject elem = sep.getNotationView().getElement();
				if (elem instanceof Lifeline) {
					Rectangle figureBounds = SequenceUtil.getAbsoluteBounds(sep);
					int lifelineCenterX = figureBounds.x + figureBounds.width / 2;
					// Check if the center line of the lifeline intersects the selection rectangle
					if (selectionRect.x <= lifelineCenterX && lifelineCenterX <= selectionRect.x + selectionRect.width) {
						coveredLifelines.add((Lifeline) elem);
					}
				}
			}
		}
		return coveredLifelines;
	}

	/**
	 * Retrieves a set of LifelineEditParts that are intersected by a specified selection rectangle within a diagram.
	 * This method iterates over all edit parts registered in the diagram's viewer, focusing on those that are instances
	 * of ShapeEditPart and represent Lifeline elements. It calculates the center X-coordinate of each lifeline's figure
	 * to determine if it intersects with the given selection rectangle. Lifelines whose central line intersects the
	 * selection area are deemed "covered" and their corresponding EditParts are added to the set to be returned.
	 *
	 * @param selectionRect
	 *            The rectangle defining the selection area on the diagram. This is used to identify
	 *            lifelines that intersect with this area, based on the central X-coordinate of their figures.
	 * @param hostEditPart
	 *            The edit part that acts as the context for this search, typically the diagram or a compartment
	 *            within it. It provides access to the diagram's edit part registry for iterating over potential
	 *            lifeline edit parts.
	 * @return A set of LifelineEditPart objects that are covered by the selection rectangle. This set could be empty if
	 *         no lifeline figures intersect with the selection area.
	 */
	public static Set<LifelineEditPart> getCoveredLifelineEditParts(Rectangle selectionRect, EditPart hostEditPart) {
		Set<LifelineEditPart> coveredLifelines = new HashSet<>();
		// retrieve all the edit parts in the registry
		Set<Entry<Object, EditPart>> allEditPartEntries = hostEditPart.getViewer().getEditPartRegistry().entrySet();
		for (Entry<Object, EditPart> epEntry : allEditPartEntries) {
			EditPart ep = epEntry.getValue();
			if (ep instanceof ShapeEditPart) {
				ShapeEditPart sep = (ShapeEditPart) ep;
				EObject elem = sep.getNotationView().getElement();
				if (elem instanceof Lifeline) {
					Rectangle figureBounds = SequenceUtil.getAbsoluteBounds(sep);
					int lifelineCenterX = figureBounds.x + figureBounds.width / 2;
					// Check if the center line of the lifeline intersects the selection rectangle
					if (selectionRect.x <= lifelineCenterX && lifelineCenterX <= selectionRect.x + selectionRect.width) {
						coveredLifelines.add((LifelineEditPart) sep);
					}
				}
			}
		}
		return coveredLifelines;
	}

	/**
	 * Returns the directly enclosing CombinedFragmentEditPart that contains a given absolute location point.
	 * This method traverses the children of the specified root EditPart to find the nearest CombinedFragmentEditPart
	 * that encompasses the specified point. The search is not recursive; it only considers direct children of the root.
	 * If such a CombinedFragmentEditPart is found, it is returned; otherwise, the method returns null, indicating
	 * no containing CombinedFragmentEditPart was found at that location.
	 *
	 * @param root
	 *            The root EditPart from which the search for the CombinedFragmentEditPart begins. This root should
	 *            ideally be the topmost parent in the edit part hierarchy to ensure a comprehensive search.
	 * @param point
	 *            The absolute location point for which the containing CombinedFragmentEditPart is sought. This point
	 *            should be specified in absolute coordinates relative to the root EditPart's coordinate system.
	 * @return The CombinedFragmentEditPart that directly contains the specified point, if one exists. If no such
	 *         CombinedFragmentEditPart is found, returns null.
	 */
	public static CombinedFragmentEditPart getCombinedFragmentEp(RootEditPart root, Point point) {
		if (root != null && root.getChildren() != null) {
			for (Object child : root.getChildren()) {
				CombinedFragmentEditPart result = findCombinedFragmentEditPart(child, point);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}

	/**
	 * Finds the CombinedFragmentEditPart containing the specified point.
	 * This method recursively searches through the edit parts, starting from a specified part,
	 * to find a CombinedFragmentEditPart that encompasses the given point.
	 *
	 * @param part
	 *            The starting part for the search, typically the root or a child edit part.
	 * @param point
	 *            The point in absolute coordinates to locate within a CombinedFragmentEditPart.
	 * @return The found CombinedFragmentEditPart, or null if none contains the point.
	 */
	private static CombinedFragmentEditPart findCombinedFragmentEditPart(Object part, Point point) {
		if (part instanceof AbstractGraphicalEditPart) {
			AbstractGraphicalEditPart graphicalEditPart = (AbstractGraphicalEditPart) part;

			// Get the bounds of the part and convert to absolute coordinates to check containment.
			Rectangle bounds = graphicalEditPart.getFigure().getBounds().getCopy();
			graphicalEditPart.getFigure().translateToAbsolute(bounds);

			// If the point is within bounds and the part is a CombinedFragmentEditPart, return it.
			if (bounds.contains(point) && part instanceof CombinedFragmentEditPart) {
				return (CombinedFragmentEditPart) part;
			}

			// Recursively search children if the current part doesn't match or contain the point.
			for (Object child : graphicalEditPart.getChildren()) {
				CombinedFragmentEditPart result = findCombinedFragmentEditPart(child, point);
				if (result != null) {
					return result;
				}
			}
		}

		// Return null if no matching part is found in this branch of the search.
		return null;
	}


	/**
	 * Retrieves interaction fragments covered by a selection rectangle, excluding specified ones.
	 * This method identifies InteractionFragments within the diagram that are fully enclosed by
	 * the selection rectangle. It considers both shape and connection edit parts, and excludes
	 * fragments specified in the ignoreSet. Fragments nested within covered CombinedFragments or
	 * associated with Messages are also included.
	 *
	 * @param selectionRect
	 *            The selection rectangle in absolute coordinates.
	 * @param hostEditPart
	 *            The starting point for the search, usually the diagram's root edit part.
	 * @param ignoreSet
	 *            Fragments to ignore during the search.
	 * @return A sorted list of covered InteractionFragments, excluding those in the ignoreSet.
	 */
	@SuppressWarnings("unchecked")
	public static List<InteractionFragment> getCoveredInteractionFragments(Rectangle selectionRect, EditPart hostEditPart, Set<InteractionFragment> ignoreSet) {
		Set<InteractionFragment> coveredInteractionFragments = new HashSet<>();

		Map<InteractionFragment, InteractionOperand> fragmentToOperandMap = new HashMap<>();

		if (ignoreSet == null) {
			ignoreSet = new HashSet<>();
		}

		// retrieve all the edit parts in the registry
		Set<Entry<Object, EditPart>> allEditPartEntries = hostEditPart.getViewer().getEditPartRegistry().entrySet();
		for (Entry<Object, EditPart> epEntry : allEditPartEntries) {
			EditPart ep = epEntry.getValue();
			if (ep instanceof ShapeEditPart) {
				ShapeEditPart sep = (ShapeEditPart) ep;
				EObject elem = sep.getNotationView().getElement();
				if (elem instanceof InteractionFragment && !ignoreSet.contains(elem)) {
					Rectangle figureBounds = SequenceUtil.getAbsoluteBounds(sep);
					// Rectangle intersection = selectionRect.getIntersection(figureBounds);
					// keep the fragment if its figure is at least partially in the selection

					// Check if the fragment's bounds are fully within the selection rectangle.
					if (selectionRect.x < figureBounds.x
							&& selectionRect.y < figureBounds.y
							&& selectionRect.x + selectionRect.width > figureBounds.x + figureBounds.width
							&& selectionRect.y + selectionRect.height > figureBounds.y + figureBounds.height) {

						if (isNestedCombinedFragment(coveredInteractionFragments, (InteractionFragment) elem)) {
							continue;
						}

						coveredInteractionFragments.add((InteractionFragment) elem);

						if (elem instanceof ExecutionSpecification) {
							ExecutionSpecification es = (ExecutionSpecification) elem;
							coveredInteractionFragments.add(es.getStart());
							coveredInteractionFragments.add(es.getFinish());
						}

					}

				}
			} else if (ep instanceof ConnectionEditPart) {
				ConnectionEditPart cep = (ConnectionEditPart) ep;
				EObject elem = cep.getNotationView().getElement();
				// for connections, messages have ends that are ift but don't have theirs own edit parts
				// => use anchors to determine if they should be included in the set
				if (elem instanceof Message) {
					Message msg = (Message) elem;

					Connection msgFigure = cep.getConnectionFigure();
					Point sourcePoint = msgFigure.getSourceAnchor().getReferencePoint();
					Point targetPoint = msgFigure.getTargetAnchor().getReferencePoint();
					if (selectionRect.contains(sourcePoint)) {
						MessageEnd msgSendEnd = msg.getSendEvent();

						if (isNestedCombinedFragment(coveredInteractionFragments, (InteractionFragment) msgSendEnd)) {
							continue;
						}


						if (msgSendEnd instanceof InteractionFragment) {
							coveredInteractionFragments.add((InteractionFragment) msgSendEnd);
						}
					}
					if (selectionRect.contains(targetPoint)) {
						MessageEnd msgReceiveEnd = msg.getReceiveEvent();
						if (msgReceiveEnd instanceof InteractionFragment) {
							coveredInteractionFragments.add((InteractionFragment) msgReceiveEnd);
						}
					}
				}
			}
		}

		// After covering all edit parts, map each InteractionFragment to its InteractionOperand.
		for (InteractionFragment fragment : coveredInteractionFragments) {
			EObject container = fragment.eContainer();
			while (container != null) {
				if (container instanceof InteractionOperand) {
					fragmentToOperandMap.put(fragment, (InteractionOperand) container);
					break;
				}
				container = container.eContainer();
			}
		}

		// Filter out fragments that do not need to be considered based on various criteria.
		Set<InteractionFragment> filteredFragments = new HashSet<>();
		for (InteractionFragment fragment : coveredInteractionFragments) {
			if (fragment instanceof InteractionOperand) {
				CombinedFragment parentCombinedFragment = (CombinedFragment) fragment.eContainer();
				if (!coveredInteractionFragments.contains(parentCombinedFragment)) {
					filteredFragments.add(fragment);
				}
			} else {
				InteractionOperand operand = fragmentToOperandMap.get(fragment);
				if (operand == null || !coveredInteractionFragments.contains(operand)) {
					filteredFragments.add(fragment);
				}
			}
		}



		// Convert the set to a list and sort it
		List<InteractionFragment> sortedFragments = new ArrayList<>(filteredFragments);
		Collections.sort(sortedFragments, new Comparator<InteractionFragment>() {
			@Override
			public int compare(InteractionFragment o1, InteractionFragment o2) {
				Interaction interaction = findEnclosingInteraction(o1);
				List<InteractionFragment> allFragments = getAllInteractionFragments(interaction);
				return Integer.compare(allFragments.indexOf(o1), allFragments.indexOf(o2));
			}
		});

		return sortedFragments;

	}

	/**
	 * Retrieves graphical edit parts for interaction fragments that are covered by a given selection rectangle.
	 * This method scans through all edit parts registered with the host edit part's viewer, identifying those
	 * that represent interaction fragments within the specified selection area. It optionally includes or excludes
	 * nested combined fragments based on the given parameter and ignores fragments specified in the ignoreSet.
	 *
	 * @param selectionRect
	 *            The selection rectangle in absolute coordinates used to identify covered edit parts.
	 * @param hostEditPart
	 *            The host edit part, typically the diagram's root edit part, from which the search begins.
	 * @param isIncludingNestedCf
	 *            Flag indicating whether nested combined fragments should be included in the search.
	 * @param ignoreSet
	 *            A set of interaction fragments to be ignored during the search.
	 * @return A list of GraphicalEditPart objects representing the covered interaction fragments.
	 */
	public static List<GraphicalEditPart> getCoveredInteractionFragmentEditParts(Rectangle selectionRect, EditPart hostEditPart, boolean isInncludingNestedCf, Set<InteractionFragment> ignoreSet) {

		// Initialize collections for storing the results and intermediary data.
		List<GraphicalEditPart> coveredInteractionFragmentEditParts = new ArrayList<>();
		Set<InteractionFragment> coveredInteractionFragments = new HashSet<>();
		Map<InteractionFragment, InteractionOperand> fragmentToOperandMap = new HashMap<>();

		// Ensure ignoreSet is initialized to avoid null checks later.
		if (ignoreSet == null) {
			ignoreSet = new HashSet<>();
		}

		// retrieve all the edit parts in the registry
		Set<Entry<Object, EditPart>> allEditPartEntries = hostEditPart.getViewer().getEditPartRegistry().entrySet();
		for (Entry<Object, EditPart> epEntry : allEditPartEntries) {
			EditPart ep = epEntry.getValue();
			if (ep instanceof ShapeEditPart) {
				ShapeEditPart sep = (ShapeEditPart) ep;
				EObject elem = sep.getNotationView().getElement();
				if (elem instanceof InteractionFragment && !ignoreSet.contains(elem)) {
					Rectangle figureBounds = SequenceUtil.getAbsoluteBounds(sep);

					// Determine if the figure bounds are fully within the selection rectangle.
					if (selectionRect.x < figureBounds.x
							&& selectionRect.y < figureBounds.y
							&& selectionRect.x + selectionRect.width > figureBounds.x + figureBounds.width
							&& selectionRect.y + selectionRect.height > figureBounds.y + figureBounds.height) {

						// Optionally skip nested combined fragments.
						if (!isInncludingNestedCf) {
							if (isNestedCombinedFragment(coveredInteractionFragments, (InteractionFragment) elem)) {
								continue;
							}
						}

						// Collect the covered interaction fragment and its graphical edit part.
						coveredInteractionFragments.add((InteractionFragment) elem);
						coveredInteractionFragmentEditParts.add(sep);

						// Handle execution specifications specifically.
						if (elem instanceof ExecutionSpecification) {
							ExecutionSpecification es = (ExecutionSpecification) elem;
							coveredInteractionFragments.add(es.getStart());
							coveredInteractionFragments.add(es.getFinish());
						}

					}

				}
			} else if (ep instanceof ConnectionEditPart) {
				ConnectionEditPart cep = (ConnectionEditPart) ep;
				EObject elem = cep.getNotationView().getElement();
				// for connections, messages have ends that are ift but don't have theirs own edit parts
				// => use anchors to determine if they should be included in the set
				if (elem instanceof Message) {
					Message msg = (Message) elem;

					Connection msgFigure = cep.getConnectionFigure();
					Point sourcePoint = msgFigure.getSourceAnchor().getReferencePoint();
					Point targetPoint = msgFigure.getTargetAnchor().getReferencePoint();
					if (selectionRect.contains(sourcePoint)) {
						MessageEnd msgSendEnd = msg.getSendEvent();

						if (isNestedCombinedFragment(coveredInteractionFragments, (InteractionFragment) msgSendEnd)) {
							continue;
						}

						if (msgSendEnd instanceof InteractionFragment) {
							coveredInteractionFragments.add((InteractionFragment) msgSendEnd);
						}

						coveredInteractionFragmentEditParts.add(cep);

					}
					if (selectionRect.contains(targetPoint)) {
						MessageEnd msgReceiveEnd = msg.getReceiveEvent();
						if (msgReceiveEnd instanceof InteractionFragment) {
							coveredInteractionFragments.add((InteractionFragment) msgReceiveEnd);
						}
					}
				}
			}
		}

		// After identifying covered fragments, determine their corresponding operands.
		for (InteractionFragment fragment : coveredInteractionFragments) {
			EObject container = fragment.eContainer();
			while (container != null) {
				if (container instanceof InteractionOperand) {
					fragmentToOperandMap.put(fragment, (InteractionOperand) container);
					break;
				}
				container = container.eContainer();
			}
		}

		// Filter out fragments based on whether they're part of an operand or not.
		Set<InteractionFragment> filteredFragments = new HashSet<>();
		for (InteractionFragment fragment : coveredInteractionFragments) {
			if (fragment instanceof InteractionOperand) {
				CombinedFragment parentCombinedFragment = (CombinedFragment) fragment.eContainer();
				if (!coveredInteractionFragments.contains(parentCombinedFragment)) {
					filteredFragments.add(fragment);
				}
			} else {
				InteractionOperand operand = fragmentToOperandMap.get(fragment);
				if (operand == null || !coveredInteractionFragments.contains(operand)) {
					filteredFragments.add(fragment);
				}
			}
		}

		// Convert the set to a list and sort it
		List<InteractionFragment> sortedFragments = new ArrayList<>(filteredFragments);
		Collections.sort(sortedFragments, new Comparator<InteractionFragment>() {
			@Override
			public int compare(InteractionFragment o1, InteractionFragment o2) {
				Interaction interaction = findEnclosingInteraction(o1);

				if (interaction == null) {
					return 0;
				}

				List<InteractionFragment> allFragments = getAllInteractionFragments(interaction);
				return Integer.compare(allFragments.indexOf(o1), allFragments.indexOf(o2));
			}
		});

		return coveredInteractionFragmentEditParts;

	}

	/**
	 * Determines if the specified InteractionFragment is nested within a CombinedFragment that is already covered.
	 * This method checks if the InteractionFragment is a child of an InteractionOperand that belongs to a
	 * CombinedFragment already present in the set of covered InteractionFragments. This is used to avoid
	 * including nested fragments that are within a CombinedFragment that has been selected or covered.
	 *
	 * @param coveredInteractionFragments
	 *            The set of InteractionFragments that are already considered covered.
	 * @param frg
	 *            The InteractionFragment to check for nested status within a CombinedFragment.
	 * @return true if the InteractionFragment is nested within a covered CombinedFragment; false otherwise.
	 */
	private static boolean isNestedCombinedFragment(Set<InteractionFragment> coveredInteractionFragments, InteractionFragment frg) {
		if (frg.eContainer() instanceof InteractionOperand) {
			InteractionOperand operand = (InteractionOperand) frg.eContainer();
			CombinedFragment cfg = (CombinedFragment) operand.getOwner();
			// Determine if the CombinedFragment is in the set of covered fragments.
			if (coveredInteractionFragments.contains(cfg)) {
				// The fragment is nested within a covered CombinedFragment.
				return true;
			}
		}

		return false;
	}

	/**
	 * Finds the nearest enclosing Interaction instance for a given EObject.
	 * This method traverses the containment hierarchy of the provided EObject, moving upwards,
	 * until it finds an object of type Interaction. This is useful for determining the Interaction context
	 * in which a particular model element resides, especially in UML diagrams where elements are nested within Interactions.
	 *
	 * @param eObject
	 *            The starting EObject from which to search for an enclosing Interaction.
	 *            The search begins with this object and moves up the containment hierarchy.
	 * @return The enclosing Interaction instance if found; otherwise, null if the EObject is not contained within an Interaction.
	 */
	public static Interaction findEnclosingInteraction(EObject eObject) {

		// Traverse up the containment hierarchy looking for an Interaction instance.
		while (eObject != null && !(eObject instanceof Interaction)) {
			eObject = eObject.eContainer();// Move to the containing EObject.
		}

		// Cast and return the found Interaction, or null if not found.
		return (Interaction) eObject;
	}

	/**
	 * Retrieves all interaction fragments associated with a given Interaction.
	 * This method initially collects the direct fragments of the Interaction and then recursively
	 * collects additional fragments that might be nested within other structures (like CombinedFragments)
	 * within the Interaction. The purpose is to compile a comprehensive list of all interaction fragments,
	 * including those not directly owned by the Interaction but are part of its containment hierarchy.
	 *
	 * @param interaction
	 *            The Interaction instance from which to collect all associated interaction fragments.
	 * @return A list of all InteractionFragment instances found within the given Interaction, including both
	 *         directly owned fragments and those nested within other structures.
	 */
	public static List<InteractionFragment> getAllInteractionFragments(Interaction interaction) {
		List<InteractionFragment> fragments = new ArrayList<>();
		// Initially add all direct fragments of the interaction.
		fragments.addAll(interaction.getFragments());

		// Recursively collect additional interaction fragments that might be nested within other structures.
		collectAllInteractionFragments(interaction, fragments);
		return fragments;
	}

	/**
	 * Recursively collects interaction fragments from a given UML model element.
	 * This method explores the containment hierarchy of the provided UML model element (EObject),
	 * identifying and collecting all instances of InteractionFragment. It specifically handles
	 * CombinedFragments by recursively collecting fragments from each of their InteractionOperands.
	 * This ensures a comprehensive collection of all interaction fragments, including those nested
	 * within CombinedFragments or other structural elements.
	 *
	 * @param element
	 *            The starting model element (EObject) from which to begin the recursive collection.
	 * @param fragments
	 *            The list of InteractionFragment instances collected so far. This method adds
	 *            new findings directly to this list.
	 */
	private static void collectAllInteractionFragments(EObject element, List<InteractionFragment> fragments) {
		for (EObject child : element.eContents()) {
			if (child instanceof InteractionFragment) {

				if (!fragments.contains(child)) {
					fragments.add((InteractionFragment) child);
				}

				// Handle CombinedFragments by exploring their InteractionOperands.
				if (child instanceof CombinedFragment) {
					CombinedFragment cb = (CombinedFragment) child;

					for (InteractionOperand operand : cb.getOperands()) {
						// Recursively collect fragments from each operand of the CombinedFragment.
						collectAllInteractionFragments(operand, fragments);
					}
				}
			}
		}
	}

	/**
	 * Retrieves all parent InteractionOperandEditParts that contain the specified move target edit part.
	 * This method iterates through all edit parts registered in the diagram's viewer to identify
	 * InteractionOperandEditParts that are parent to the move target edit part, based on their
	 * graphical bounds. It's useful for determining the hierarchy of interaction operands that
	 * directly or indirectly contain a given move target within a sequence diagram.
	 *
	 * @param moveTargetEp
	 *            The graphical edit part for which parent interaction operands are being sought.
	 *            Typically, this would be an edit part representing an element that is being
	 *            moved or considered for some operation within the diagram.
	 * @return A list of InteractionOperandEditParts that are parents to the specified move target edit part.
	 */
	public static List<InteractionOperandEditPart> getAllParentInteractionOperandEps(GraphicalEditPart moveTargetEp) {
		Map<?, ?> editPartRegistry = moveTargetEp.getViewer().getEditPartRegistry();
		List<InteractionOperandEditPart> parentOperands = new ArrayList<>();

		// Retrieve the bounds or points for the move target edit part to compare with others.
		Rectangle targetBounds = getBoundsOrPoints(moveTargetEp);

		// Iterate over all edit parts in the registry to find InteractionOperandEditParts.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart operandEditPart = (InteractionOperandEditPart) value;

				// Get absolute bounds for each InteractionOperandEditPart for containment check.
				Rectangle operandBounds = getAbsoluteBounds(operandEditPart);

				// Check if the operand edit part bounds contain the target edit part bounds.
				if (operandBounds.contains(targetBounds) && !operandBounds.equals(targetBounds)) {
					// If so, add the operand edit part to the list of parent operands.
					parentOperands.add(operandEditPart);
				}

			}
		}

		return parentOperands;
	}

	/**
	 * Retrieves the bounds or defining points of a graphical edit part, handling both regular figures and connections.
	 * For MessageSyncEditParts, which represent synchronous messages in a sequence diagram, this method calculates
	 * a rectangle that spans from the first to the last point of the connection figure. For all other edit parts,
	 * it simply returns the absolute bounds of the figure. This method is useful for operations that need to compare
	 * the spatial relationships of different edit parts, such as containment or intersection checks.
	 *
	 * @param editPart
	 *            The graphical edit part for which to retrieve bounds or defining points.
	 * @return A rectangle representing the bounds of the edit part or the span of its defining points.
	 */
	private static Rectangle getBoundsOrPoints(GraphicalEditPart editPart) {
		// Special handling for MessageSyncEditParts representing connections (e.g., synchronous messages).
		if (editPart instanceof MessageSyncEditPart) {
			Connection connection = ((MessageSyncEditPart) editPart).getConnectionFigure();
			PointList points = connection.getPoints().getCopy();
			connection.translateToAbsolute(points);
			// Create a rectangle that spans from the first to the last point of the connection.
			return new Rectangle(points.getFirstPoint(), points.getLastPoint());
		} else {
			// For all other edit parts, get the figure's bounds in absolute coordinates.
			Rectangle bounds = editPart.getFigure().getBounds().getCopy();
			editPart.getFigure().translateToAbsolute(bounds);
			return bounds;
		}
	}

	/**
	 * Retrieves the absolute bounds of a graphical edit part's figure.
	 * This method calculates the absolute bounds of the figure associated with the provided
	 * graphical edit part. Absolute bounds are useful for spatial calculations that need to
	 * consider the entire diagram's coordinate system, rather than just the local coordinates
	 * of a parent figure. This method is essential for operations like collision detection,
	 * containment checks, and layout adjustments.
	 *
	 * @param editPart
	 *            The graphical edit part whose figure's absolute bounds are to be calculated.
	 * @return A Rectangle representing the absolute bounds of the edit part's figure.
	 */
	private static Rectangle getAbsoluteBounds(GraphicalEditPart editPart) {
		Rectangle bounds = editPart.getFigure().getBounds().getCopy();
		editPart.getFigure().translateToAbsolute(bounds);
		return bounds;
	}

	/**
	 * Finds the closest parent InteractionOperandEditPart that encloses the given move target edit part.
	 * This method iterates through all edit parts in the diagram's registry to find the InteractionOperandEditPart
	 * that is the closest ancestor of the move target edit part. It uses the bounds or points (for message connections)
	 * of the move target to determine containment within InteractionOperand bounds. This is useful for identifying
	 * the logical grouping of model elements within sequence diagrams, particularly when moving or resizing elements.
	 *
	 * @param moveTargetEp
	 *            The graphical edit part for which the closest enclosing InteractionOperandEditPart is sought.
	 * @return The closest ancestor InteractionOperandEditPart that encloses the move target, or null if none is found.
	 */
	public static InteractionOperandEditPart getParentInteractionOperandEp(GraphicalEditPart moveTargetEp) {

		Map<?, ?> editPartRegistry = moveTargetEp.getViewer().getEditPartRegistry();

		Rectangle targetBounds = null;
		PointList targetPoints = null;

		// Special handling for MessageSyncEditParts to use connection points.
		if (moveTargetEp instanceof MessageSyncEditPart) {
			Connection connection = ((MessageSyncEditPart) moveTargetEp).getConnectionFigure();
			targetPoints = connection.getPoints();
		} else {
			// For other edit parts, use the absolute bounds.
			targetBounds = moveTargetEp.getFigure().getBounds().getCopy();
			moveTargetEp.getFigure().translateToAbsolute(targetBounds);
		}

		InteractionOperandEditPart closestAncestorOperand = null;
		int minDistance = Integer.MAX_VALUE;

		// Iterate over the edit parts to find the closest enclosing InteractionOperandEditPart.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart operandEditPart = (InteractionOperandEditPart) value;

				// Skip if the move target is a direct child of the operand (to avoid self-containment).
				if (moveTargetEp.equals(operandEditPart.getParent().getParent())) {
					continue;
				}

				// Calculate the absolute bounds of the operand edit part.
				Rectangle operandBounds = operandEditPart.getFigure().getBounds().getCopy();
				operandEditPart.getFigure().translateToAbsolute(operandBounds);

				// Check if the move target is contained within the operand bounds.
				boolean isContained = targetPoints != null
						? operandBounds.contains(targetPoints.getFirstPoint()) && operandBounds.contains(targetPoints.getLastPoint())
						: operandBounds.contains(targetBounds);

				// If contained, determine if this operand is the closest ancestor.
				if (isContained) {
					int distance = (targetBounds != null ? targetBounds.y : targetPoints.getBounds().y) - operandBounds.y;
					if (distance < minDistance && distance >= 0) {
						minDistance = distance;
						closestAncestorOperand = operandEditPart;
					}
				}
			}
		}

		return closestAncestorOperand;


	}

	/**
	 * Identifies all InteractionOperandEditParts that enclose a specified location relative to a target edit part.
	 * This method iterates through all edit parts within the diagram's registry to find InteractionOperandEditParts
	 * whose bounds contain the given location. It's particularly useful for determining which interaction operands
	 * (representing logical groupings in sequence diagrams) encompass a specific point in the diagram, aiding in
	 * operations such as element placement or interaction detection based on user actions or automated layout algorithms.
	 *
	 * @param targetEp
	 *            The reference graphical edit part used to access the diagram's edit part registry.
	 * @param location
	 *            The point in diagram coordinates to test against the bounds of InteractionOperandEditParts.
	 * @return A list of InteractionOperandEditParts that contain the specified location, which may be empty if no
	 *         such operands are found.
	 */
	public static List<InteractionOperandEditPart> getAllParentInteractionOperandEpsByLocation(GraphicalEditPart targetEp, Point location) {
		Map<?, ?> editPartRegistry = targetEp.getViewer().getEditPartRegistry();
		List<InteractionOperandEditPart> parentOperands = new ArrayList<>();

		// Iterate over all edit parts in the registry to find InteractionOperandEditParts.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart operandEditPart = (InteractionOperandEditPart) value;
				Rectangle operandBounds = getAbsoluteBounds(operandEditPart);

				// Check if the operand edit part bounds contain the specified location.
				if (operandBounds.contains(location)) {
					parentOperands.add(operandEditPart);
				}

			}
		}

		return parentOperands;
	}

	/**
	 * Retrieves all graphical edit parts located below a specific Y-coordinate within an interaction compartment.
	 * This method iterates through the edit parts registered in the interaction compartment's viewer, including
	 * lifelines and their source connections, as well as behavior execution specifications and combined fragments,
	 * to identify those that are positioned below a given Y-coordinate. This is particularly useful for operations
	 * such as determining the impact of inserting new messages or elements within a sequence diagram based on their
	 * vertical location.
	 *
	 * @param interactionCompartmentEp
	 *            The interaction compartment edit part serving as the context for the search.
	 * @param changeBoundsRequest
	 *            A request object containing the location information used to define the Y-coordinate
	 *            threshold for identifying edit parts located below this point.
	 * @return A list of GraphicalEditPart objects that are positioned below the specified Y-coordinate.
	 */
	public static List<GraphicalEditPart> getAllBelowEditPartsByLocation(GraphicalEditPart interactionCompartmentEp, AdoneUpdateLocationByNewMessageCreationRequest changeBoundsRequest) {
		List<GraphicalEditPart> belowParts = new ArrayList<>();

		// Determine the Y-coordinate threshold from the change bounds request.
		int moveTargetY = changeBoundsRequest.getLocation().y;

		// Access the edit part registry to iterate over potential child edit parts.
		Map registry = interactionCompartmentEp.getViewer().getEditPartRegistry();

		for (Object child : registry.values()) {

			// Process lifeline edit parts to evaluate their source connections.
			if (child instanceof LifelineEditPart) {

				LifelineEditPart lifeline = (LifelineEditPart) child;

				for (Object srcCon : lifeline.getSourceConnections()) {

					ConnectionEditPart connectionEditPart = (ConnectionEditPart) srcCon;
					Connection connectionFigure = connectionEditPart.getConnectionFigure();

					// Get the PointList which contains all points of the Connection
					PointList points = connectionFigure.getPoints();

					// Retrieve the start and end points
					Point startPoint = points.getFirstPoint().getCopy();
					Point endPoint = points.getLastPoint().getCopy();

					// Translate these points to absolute coordinates
					connectionFigure.translateToAbsolute(startPoint);
					connectionFigure.translateToAbsolute(endPoint);

					// Now you have the absolute Y-coordinates of the start and end points of the message
					int startY = startPoint.y;
					int endY = endPoint.y;

					// You can use these Y-coordinates for further logic
					// For example, check if the message is below a certain point
					if (startY >= moveTargetY || endY >= moveTargetY) {
						// Add the message EditPart to the list if it's below the target Y-coordinate
						if (!belowParts.contains(connectionEditPart)) {
							belowParts.add(connectionEditPart);
						}
					}
				}
			}

			// Process behavior execution specifications and combined fragments.
			if (child instanceof BehaviorExecutionSpecificationEditPart || child instanceof CombinedFragmentEditPart) {
				GraphicalEditPart childEp = (GraphicalEditPart) child;
				Rectangle childBounds = childEp.getFigure().getBounds().getCopy();
				childEp.getFigure().translateToAbsolute(childBounds);

				// Add the edit part to the list if it's located below the Y-coordinate threshold.
				if (childBounds.y >= moveTargetY) {
					if (!belowParts.contains(childEp)) {
						belowParts.add(childEp);
					}
				}
			}
		}

		return belowParts;
	}

	/**
	 * Calculates the absolute bounds of a graphical edit part, with special handling for message edit parts.
	 * For AbstractMessageEditPart instances, representing messages in sequence diagrams, it computes a rectangle
	 * that spans the start and end points of the message's connection figure. For all other edit parts, it returns
	 * the absolute bounds of the edit part's figure. This method is crucial for operations requiring precise spatial
	 * positioning of elements within a diagram, such as collision detection, layout adjustments, or visual rendering.
	 *
	 * @param editPart
	 *            The graphical edit part for which to calculate absolute bounds. This can be any type of
	 *            graphical edit part, including those representing messages.
	 * @return A rectangle representing the absolute bounds of the edit part. For messages, this rectangle spans
	 *         from the start to the end point of the message's connection figure.
	 */
	public static Rectangle getAbsoluteBoundsForMessageEp(GraphicalEditPart editPart) {
		// Handle message edit parts specifically to account for the span of their connection figures.
		if (editPart instanceof AbstractMessageEditPart) {
			Connection connection = ((AbstractMessageEditPart) editPart).getConnectionFigure();
			PointList points = connection.getPoints();
			Point start = points.getFirstPoint().getCopy();
			Point end = points.getLastPoint().getCopy();

			connection.translateToAbsolute(start);
			connection.translateToAbsolute(end);

			// Create a rectangle that spans from the start to the end point, including both points in the bounds.
			return new Rectangle(start.x, start.y, end.x - start.x, end.y - start.y).union(new Rectangle(end.x, end.y, 0, 0));

		} else {
			// For non-message edit parts, simply return the figure's absolute bounds.
			Rectangle bounds = editPart.getFigure().getBounds().getCopy();
			editPart.getFigure().translateToAbsolute(bounds);
			return bounds;
		}
	}

	public static Point getBoundsWithViewPort(Point p, EditPart host) {

		// Retrieve ViewPort location = the area where compartment children
		// are located
		// Retrieve ViewPort view location = the relative location of the
		// viewed compartment
		// depending on the current scroll bar state
		// Viewport compartmentViewPort = compartmentFigure.getScrollPane().getViewport();
		// Point compartmentViewPortLocation = compartmentViewPort.getLocation();
		// Point compartmentViewPortViewLocation = compartmentViewPort.getViewLocation();
		// Calculate the delta between the targeted element position for
		// drop (the Composite figure)
		// and the View location with eventual scroll bar.
		// Point delta = compartmentViewPortLocation.translate(targetLocation.negate());
		// delta = delta.translate(compartmentViewPortViewLocation.negate());
		// Translate the requested drop location (relative to parent)
		// dropLocation = dropRequest.getLocation().getTranslated(delta);

		Viewport vp = findViewport((GraphicalEditPart) host.getViewer().getRootEditPart());
		if (vp != null) {
			// Point loc1 = vp.getClientArea().getLocation().getCopy();
			Point compartmentViewPortViewLocation = vp.getViewLocation();
			// Point location = resizeBounds.getLocation();
			// location.translate(compartmentViewPortViewLocation);

		}

		return null;

	}

	/**
	 * Searches for and returns the nearest Viewport ancestor of a given graphical edit part.
	 * This method iterates up the figure hierarchy starting from the content pane of the specified
	 * edit part, looking for a figure that is an instance of Viewport. Viewports are used in graphical
	 * editing frameworks to represent scrollable areas that can contain other figures. Finding the viewport
	 * for an edit part is useful for operations that need to take scrolling into account, such as coordinate
	 * transformations or visibility checks.
	 *
	 * @param part
	 *            The graphical edit part whose viewport ancestor is being sought.
	 * @return The nearest Viewport ancestor if found; otherwise, null.
	 */
	private static Viewport findViewport(GraphicalEditPart part) {
		IFigure figure = null;
		Viewport port = null;

		// Start the search from the content pane of the provided edit part.
		do {
			if (figure == null) {
				figure = part.getContentPane();
			} else {
				figure = figure.getParent();
			}

			// Check if the current figure in the iteration is a Viewport.
			if (figure instanceof Viewport) {
				port = (Viewport) figure;
				break;// Exit the loop if a Viewport is found.
			}

			// Continue until reaching the top of the figure hierarchy or the figure equals the part's own figure.
		} while (figure != part.getFigure() && figure != null);

		// Return the found Viewport or null if none was found.
		return port;
	}

	/**
	 * Generates a command to resize the height of the first Behavior Execution Specification (BES)
	 * edit part below a specified location within an interaction. This method identifies the first BES
	 * within the interaction that the host edit part belongs to and calculates the required height adjustment
	 * to ensure it encompasses all subsequent BES, Combined Fragments, or Messages up to a certain point,
	 * specified by the createdLocation parameter. The goal is to adjust the BES to visually accommodate
	 * newly created or moved elements within a sequence diagram.
	 *
	 * @param hostEp
	 *            The graphical edit part serving as the context for the operation, typically an interaction compartment.
	 * @param createdLocation
	 *            The location that dictates the minimum required height of the first BES.
	 * @return A Command that, when executed, will resize the first BES edit part to meet the specified requirements,
	 *         or null if no resize is necessary or possible.
	 */
	public static Command getResizeFirstBesEpHeightCommand(GraphicalEditPart hostEp, Point createdLocation) {

		// Retrieve the semantic object associated with the host edit part.
		View view = (View) hostEp.getModel();
		EObject semanticObject = view.getElement();

		// Find the enclosing Interaction for the semantic object.
		Interaction intac;
		EObject container = semanticObject;
		while (container != null && !(container instanceof Interaction)) {
			container = container.eContainer();
		}

		intac = (Interaction) container;

		Map<?, ?> registry = hostEp.getRoot().getViewer().getEditPartRegistry();

		// Identify the first BES within the interaction.
		BehaviorExecutionSpecification firstBes = null;
		EditPart firstBesEditPart = null;
		for (InteractionFragment ifg : intac.getFragments()) {
			if (ifg instanceof BehaviorExecutionSpecification) {
				firstBes = (BehaviorExecutionSpecification) ifg;
				break;
			}
		}

		if (firstBes == null) {
			return null;// No BES found.
		}

		firstBesEditPart = AdoneSequenceUtil.getEditPartFromSemantic(hostEp, firstBes);

		// Identify the lowest edit part that affects the first BES's required height.
		EditPart lowestEditPart = null;
		int maxY = createdLocation.y;
		for (Object value : registry.values()) {
			EditPart editPart = (EditPart) value;
			if (editPart instanceof BehaviorExecutionSpecificationEditPart ||
					editPart instanceof CombinedFragmentEditPart) {

				if (editPart != firstBesEditPart) { // Exclude the first BES itself.
					IFigure figure = ((GraphicalEditPart) editPart).getFigure();
					Rectangle bounds = figure.getBounds().getCopy();
					figure.translateToAbsolute(bounds);

					// Determine the lowest point that should be encompassed by the first BES.
					int bottomY = bounds.y + bounds.height;
					if (bottomY > maxY) {
						maxY = bottomY;
						lowestEditPart = editPart;
					}
				}
			}
		}

		// Calculate the required height adjustment for the first BES.
		IFigure figure = ((GraphicalEditPart) firstBesEditPart).getFigure();
		Rectangle firstBesBounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(firstBesBounds);
		int currentFirstBesHeight = firstBesBounds.height;
		int relativeMaxY = (firstBesBounds.y + currentFirstBesHeight) - maxY;
		int firstBesDeltaHeight;

		// Determine the new height adjustment for the first BES based on its relative position.
		if (relativeMaxY < 0) {
			// If the BES needs to extend further down to encompass elements below it, calculate the necessary extension.
			firstBesDeltaHeight = maxY + 40 - currentFirstBesHeight; // 음수일 때: maxY + 40에서 현재 높이 뺀 값
		} else if (relativeMaxY <= 40) {
			// If the BES is very close to or within 40 pixels of the elements, ensure it has a minimum height extension.
			firstBesDeltaHeight = 40 - currentFirstBesHeight; // 양수이며 40 이하일 때: 최소 높이 40에서 현재 높이 뺀 값
		} else {
			// For cases where the BES is sufficiently covering elements, adjust the height to just encompass them.
			firstBesDeltaHeight = relativeMaxY - currentFirstBesHeight; // 양수이며 40보다 클 때: relativeMaxY에서 현재 높이 뺀 값
		}

		// check this ?
		firstBesDeltaHeight = 60;

		// Prepare a request to resize the first BES edit part.
		ChangeBoundsRequest resizeReq = new ChangeBoundsRequest();
		resizeReq.setEditParts(firstBesEditPart);
		resizeReq.setSizeDelta(new Dimension(0, firstBesDeltaHeight));
		resizeReq.setType(RequestConstants.REQ_RESIZE);

		// Obtain the command to execute the resize based on the request.
		Command setHeightCommand = firstBesEditPart.getCommand(resizeReq);

		// Check if the command can be executed and return it; otherwise, return null.
		if (setHeightCommand != null && setHeightCommand.canExecute()) {
			return setHeightCommand;
		}

		return null;

	}

	/**
	 * Generates a command to resize the first Behavior Execution Specification (BES) based on a vertical move delta.
	 * This method calculates a resize command for the first BES within the interaction that the move target edit part
	 * belongs to, adjusting its height by a specified delta. This adjustment is crucial for maintaining diagram integrity
	 * during vertical moves or resizes of elements, ensuring that the BES visually encapsulates associated elements correctly.
	 *
	 * @param moveTargetEp
	 *            The graphical edit part being moved, which necessitates the BES resize.
	 * @param resizeDeltaY
	 *            The vertical delta by which the first BES's height should be adjusted. A positive value
	 *            indicates an increase in height, while a negative value indicates a decrease.
	 * @return A Command that, when executed, will resize the first BES edit part by the specified delta, or null if
	 *         no resize is necessary or possible.
	 */
	public static Command getResizeFirstBesEpHeightForMoveCommand(GraphicalEditPart moveTargetEp, int resizeDeltaY) {

		// Retrieve the semantic object to identify the associated Interaction.
		View view = (View) moveTargetEp.getModel();
		EObject semanticObject = view.getElement();

		// Traverse up the semantic hierarchy to find the enclosing Interaction.
		Interaction intac;
		EObject container = semanticObject;
		while (container != null && !(container instanceof Interaction)) {
			container = container.eContainer();
		}

		intac = (Interaction) container;

		// Identify the first BES within the Interaction.
		BehaviorExecutionSpecification firstBes = null;
		EditPart firstBesEditPart = null;
		for (InteractionFragment ifg : intac.getFragments()) {
			if (ifg instanceof BehaviorExecutionSpecification) {

				firstBes = (BehaviorExecutionSpecification) ifg;

				// Additional checks for specific conditions (e.g., associated Messages). (2024-01-01)
				if (semanticObject instanceof Message) {
					if (firstBes.equals(AdoneSequenceUtil.getFollowingBehaviorExeSpec((Message) semanticObject))) {
						return null;
					}
				}

				break;
			}
		}

		if (firstBes == null) {
			// No applicable BES found.
			return null;
		}

		// Retrieve the edit part for the first BES.
		firstBesEditPart = AdoneSequenceUtil.getEditPartFromSemantic(moveTargetEp, firstBes);

		// Prepare the resize request with the specified delta.
		ChangeBoundsRequest resizeReq = new ChangeBoundsRequest();

		// Mark the operation as part of a group move to handle top constraint avoidance. (2024-01-31)
		resizeReq.getExtendedData().put("GroupMove", resizeReq);
		resizeReq.setEditParts(firstBesEditPart);
		resizeReq.setSizeDelta(new Dimension(0, resizeDeltaY));

		// Set the resize direction based on the delta sign.
		if (resizeDeltaY > 0) {
			resizeReq.setResizeDirection(PositionConstants.SOUTH);
		} else {
			resizeReq.setResizeDirection(PositionConstants.NORTH);
		}
		resizeReq.setType(RequestConstants.REQ_RESIZE);

		// Generate the resize command.
		Command setHeightCommand = firstBesEditPart.getCommand(resizeReq);

		// Return the command if it's executable, otherwise return null.
		if (setHeightCommand != null && setHeightCommand.canExecute()) {
			return setHeightCommand;
		}

		return null;
	}

	/**
	 * Generates a command to rearrange Behavior Execution Specification Edit Parts (BES EPs)
	 * that are mismatched with their corresponding message receive events in terms of vertical positioning.
	 * This method identifies BES EPs associated with messages that have their receive events located differently
	 * from the host message receive event and generates commands to adjust their vertical positions.
	 *
	 * @param hostEp
	 *            The host graphical edit part, typically a message edit part, used as a reference for alignment.
	 * @return A Command that, when executed, rearranges the mismatched BES EPs to align correctly with their
	 *         corresponding message receive events. Returns null if no rearrangement is needed.
	 */
	public static Command getRearrangeBehaviorSpecEpLocationCommand(GraphicalEditPart hostEp) {
		List<BehaviorExecutionSpecificationEditPart> mismatchedBesParts = new ArrayList<>();

		// Resolve the semantic element for the host message.
		Message hostMessage = (Message) ((ConnectionEditPart) hostEp).resolveSemanticElement();
		MessageOccurrenceSpecification hostReceiveEvent = null;
		if (hostMessage != null) {
			hostReceiveEvent = (MessageOccurrenceSpecification) hostMessage.getReceiveEvent();
		}

		// Access the edit part registry to find related BES EPs.
		Map<?, ?> registry = hostEp.getRoot().getViewer().getEditPartRegistry();
		for (Object value : registry.values()) {
			EditPart editPart = (EditPart) value;
			if (editPart instanceof AbstractMessageEditPart) {
				Message message = (Message) ((ConnectionEditPart) editPart).resolveSemanticElement();
				if (message != null) {
					MessageOccurrenceSpecification receiveEvent = (MessageOccurrenceSpecification) message.getReceiveEvent();
					for (Object nestedValue : registry.values()) {
						EditPart nestedEditPart = (EditPart) nestedValue;
						if (nestedEditPart instanceof BehaviorExecutionSpecificationEditPart) {
							BehaviorExecutionSpecificationEditPart connectedBes = (BehaviorExecutionSpecificationEditPart) nestedEditPart;
							BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) ((View) connectedBes.getModel()).getElement();
							// Check if the BES is associated with the receive event and located on a different lifeline.
							if (receiveEvent.equals(bes.getStart()) && !isLocatedAtSameLifeline((AbstractMessageEditPart) hostEp, connectedBes)) {
								if (!bes.getStart().equals(hostReceiveEvent)) {
									mismatchedBesParts.add(connectedBes);
								}
							}
						}
					}
				}
			}
		}

		// If there are mismatched BES EPs, generate commands to relocate them.
		if (!mismatchedBesParts.isEmpty()) {
			CompoundCommand relocationCommand = new CompoundCommand();
			for (BehaviorExecutionSpecificationEditPart bes : mismatchedBesParts) {
				Point location = bes.getLocation();
				ChangeBoundsRequest requestForMove = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
				requestForMove.setEditParts(bes);
				location.y += -40; // Adjust the Y coordinate for relocation.
				requestForMove.setMoveDelta(new Point(0, -40));
				requestForMove.setLocation(location);
				Command moveCommand = bes.getCommand(requestForMove);
				if (moveCommand != null) {
					relocationCommand.add(moveCommand);
				}
			}
			return relocationCommand.unwrap();
		}

		return null;
	}

	/**
	 * Determines whether a message edit part and a behavior execution specification (BES) edit part
	 * are located on the same lifeline within a sequence diagram. This method is useful for identifying
	 * relationships between elements in a sequence diagram, specifically to ensure that message interactions
	 * and behavior executions are correctly associated with the same lifeline, indicating that they are part
	 * of the same interaction context.
	 *
	 * @param messageEditPart
	 *            The edit part representing the message, from which to extract the source lifeline.
	 * @param besEditPart
	 *            The edit part representing the behavior execution specification, from which to extract
	 *            the associated lifeline.
	 * @return true if both the message and the BES are associated with the same lifeline; false otherwise.
	 */
	private static boolean isLocatedAtSameLifeline(AbstractMessageEditPart messageEditPart, BehaviorExecutionSpecificationEditPart besEditPart) {
		// Extract the lifeline associated with the source of the message.
		Lifeline messageLifeline = null;
		if (messageEditPart.getSource() instanceof LifelineEditPart) {
			LifelineEditPart lifelineEp = (LifelineEditPart) messageEditPart.getSource();
			messageLifeline = (Lifeline) lifelineEp.resolveSemanticElement();
		}

		// Extract the lifeline associated with the BES.
		Lifeline besLifeline = null;
		if (besEditPart.getParent() instanceof LifelineEditPart) {
			LifelineEditPart lifelineEp = (LifelineEditPart) besEditPart.getParent();
			besLifeline = (Lifeline) lifelineEp.resolveSemanticElement();
		}

		// Compare the two lifelines to determine if they are the same.
		return messageLifeline != null && besLifeline != null && messageLifeline.equals(besLifeline);
	}


	/**
	 * Determines if a message edit part and a behavior execution specification (BES) edit part are located
	 * at the same Y-coordinate within a sequence diagram. This method is particularly useful for verifying
	 * alignment between a message and its corresponding behavior execution, ensuring that they are visually
	 * represented as occurring at the same point in time within the sequence diagram.
	 *
	 * @param messageEditPart
	 *            The edit part representing the message, from which to extract the Y-coordinate
	 *            of its start point.
	 * @param connectedBesEditPart
	 *            The edit part representing the behavior execution specification, from which
	 *            to extract the Y-coordinate of its location.
	 * @return true if both the message and the BES are located at the same Y-coordinate; false otherwise.
	 */
	public static boolean isLocatedAtSameYPosition(AbstractMessageEditPart messageEditPart, BehaviorExecutionSpecificationEditPart connectedBesEditPart) {
		// Extract the Y-coordinate of the start point of the message's connection figure.
		Point startPoint = messageEditPart.getConnectionFigure().getPoints().getFirstPoint();
		messageEditPart.getConnectionFigure().translateToAbsolute(startPoint);
		int messageY = startPoint.y;

		// Extract the Y-coordinate of the BES's figure location.
		Rectangle bounds = connectedBesEditPart.getFigure().getBounds();
		connectedBesEditPart.getFigure().translateToAbsolute(bounds);
		int besY = bounds.y;

		// Compare the Y-coordinates to determine if they are the same.
		return messageY == besY;
	}

	/**
	 * Retrieves the InteractionInteractionCompartmentEditPart associated with the given host edit part's interaction.
	 * This method navigates up the semantic model from the host edit part to find an Interaction element and then
	 * searches the edit part registry for the InteractionInteractionCompartmentEditPart that corresponds to this
	 * Interaction. This is useful for operations that require manipulating or querying the graphical representation
	 * of the entire interaction, such as layout adjustments or adding new elements within the interaction compartment.
	 *
	 * @param hostEp
	 *            The graphical edit part from which to start the search for the associated interaction compartment.
	 * @return The InteractionInteractionCompartmentEditPart corresponding to the interaction associated with the host edit part,
	 *         or null if no such compartment edit part can be found.
	 */
	public static InteractionInteractionCompartmentEditPart getInteractionInteractionCompartmentEditPart(GraphicalEditPart hostEp) {

		if (hostEp != null) {
			// Retrieve the semantic model associated with the host edit part.
			Object model = hostEp.getModel();
			View view = (View) model;
			EObject element = view.getElement();

			// Navigate up the semantic hierarchy to find an Interaction instance.
			while (element != null && !(element instanceof Interaction)) {
				element = element.eContainer();
			}

			// Once an Interaction is found, search the edit part registry for its compartment edit part.
			if (element instanceof Interaction) {
				Map registry = hostEp.getViewer().getEditPartRegistry();
				for (Object part : registry.values()) {
					if (part instanceof InteractionInteractionCompartmentEditPart) {
						InteractionInteractionCompartmentEditPart interactionPart = (InteractionInteractionCompartmentEditPart) part;
						// Check if the compartment edit part corresponds to the found Interaction.
						if (interactionPart.resolveSemanticElement().equals(element)) {
							return interactionPart;
						}
					}
				}
			}
		}

		// Return null if no corresponding compartment edit part is found.
		return null;
	}

	/**
	 * Retrieves the Behavior Execution Specification (BES) that immediately follows the receipt of a specified message.
	 * This method examines the receive event of the message to find the corresponding BES within the same interaction.
	 * It is particularly useful for determining the execution context or the effect of a message within a sequence diagram,
	 * allowing for more precise modeling and analysis of interactions.
	 *
	 * @param msg
	 *            The message for which the following Behavior Execution Specification is sought.
	 * @return The Behavior Execution Specification that follows the message's receive event, if any; otherwise, null.
	 */
	public static BehaviorExecutionSpecification getFollowingBehaviorExeSpec(Message msg) {

		// Ensure the message and its receive event are properly defined.
		if (msg != null && msg.getReceiveEvent() instanceof MessageOccurrenceSpecification) {
			MessageOccurrenceSpecification msgOccurrence = (MessageOccurrenceSpecification) msg.getReceiveEvent();

			// Retrieve the interaction that covers the message occurrence.
			Interaction interaction = msgOccurrence.getCovered().getInteraction();

			if (interaction != null) {

				// Iterate through all interaction fragments to find the BES that follows the message.
				for (InteractionFragment fra : getAllInteractionFragments(interaction)) {

					if (fra instanceof BehaviorExecutionSpecification) {

						BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) fra;

						// Check if the BES starts with the message's occurrence specification.
						if (bes != null && bes.getStart() != null && bes.getStart().equals(msgOccurrence)) {
							return bes;// Found the corresponding BES.
						}

					}
				}
			}
		}
		return null;// Return null if no matching BES is found.
	}

	/**
	 * Determines whether a given lifeline is covered by a specified combined fragment.
	 * This method checks if the lifeline associated with the provided LifelineEditPart is among
	 * the lifelines covered by the CombinedFragment associated with the given CombinedFragmentEditPart.
	 * It is useful for validating model consistency and for operations that depend on understanding
	 * the structural relationships within sequence diagrams, such as rendering or analysis tasks.
	 *
	 * @param lifelinePart
	 *            The LifelineEditPart representing the lifeline to check.
	 * @param cf
	 *            The CombinedFragmentEditPart representing the combined fragment to check against.
	 * @return true if the lifeline is covered by the combined fragment; false otherwise.
	 */
	public static boolean isLifelineCoveredByCombinedFragment(LifelineEditPart lifelinePart, CombinedFragmentEditPart cf) {
		// Extract the UML elements from the edit parts.
		Lifeline lifeline = (Lifeline) lifelinePart.resolveSemanticElement();
		CombinedFragment combinedFragment = (CombinedFragment) cf.resolveSemanticElement();

		// Check if both elements are successfully retrieved.
		if (lifeline != null && combinedFragment != null) {

			// Iterate through the lifelines covered by the combined fragment.
			for (Lifeline coveredLifeline : combinedFragment.getCovereds()) {

				// Check if the specified lifeline is among those covered.
				if (lifeline.equals(coveredLifeline)) {
					return true; // The specified lifeline is covered by the combined fragment.
				}
			}
		}

		return false; // The specified lifeline is not covered by the combined fragment.
	}

	/**
	 * Determines if a specified Y-position on a lifeline is within the bounds of any BehaviorExecutionSpecification.
	 * This method checks if the given Y-position intersects with the vertical span of any BehaviorExecutionSpecification
	 * (BES) elements covered by the specified lifeline. It is particularly useful for identifying whether an interaction
	 * or an event at a specific Y-coordinate on a lifeline corresponds to a recursive call (i.e., a call within the same
	 * execution specification).
	 *
	 * @param liflineEditPart
	 *            The graphical edit part representing the lifeline to check.
	 * @param behaviorExeSpecYposition
	 *            The Y-coordinate to check for intersection with any BES on the lifeline.
	 * @return true if the Y-position intersects with any BES on the lifeline, indicating a potential recursive call; false otherwise.
	 */
	public static boolean isRecursiveCallBehaviorExSpec(GraphicalEditPart liflineEditPart, int behaviorExeSpecYposition) {
		// Retrieve the Lifeline model element from the lifeline edit part.
		Lifeline lifeline = (Lifeline) ((View) liflineEditPart.getModel()).getElement();

		// Find all BehaviorExecutionSpecifications covered by the lifeline.
		List<BehaviorExecutionSpecification> coveredBes = lifeline.getCoveredBys().stream()
				.filter(BehaviorExecutionSpecification.class::isInstance)
				.map(BehaviorExecutionSpecification.class::cast)
				.collect(Collectors.toList());

		// Check each BES's position and height against the specified Y-position.
		for (BehaviorExecutionSpecification bes : coveredBes) {
			EditPart besEditPart = SequenceUtil.getEditPart(liflineEditPart, bes, AdoneBehaviorExecutionSpecificationEditPart.class);
			if (besEditPart != null) {
				Rectangle bounds = getAbsoluteBounds((GraphicalEditPart) besEditPart);

				// Check if the Y-position is within the bounds of the BES.
				if (bounds.y <= behaviorExeSpecYposition && behaviorExeSpecYposition <= bounds.y + bounds.height) {
					// The specified Y-position is within the range of a BES.
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Generates a command to resize the height of all lifeline edit parts within a sequence diagram.
	 * This method iterates through the edit part registry to identify all lifeline edit parts and
	 * constructs a compound command that, when executed, will adjust their heights by a specified delta.
	 * This adjustment is particularly useful for ensuring that lifelines are appropriately sized to
	 * accommodate changes in the diagram, such as the addition or removal of messages or execution specifications.
	 *
	 * @param moveTargetEp
	 *            The graphical edit part initiating the resize operation, used to access the edit part registry.
	 * @param resizeDeltaY
	 *            The vertical delta by which the height of each lifeline should be adjusted. A positive value
	 *            indicates an increase in height, while a negative value indicates a decrease.
	 * @return A Command that, when executed, will resize all lifeline heights by the specified delta.
	 */
	public static Command getResizeLifelineHeightCommand(GraphicalEditPart moveTargetEp, int resizeDeltaY) {

		EditPartViewer viewer = moveTargetEp.getRoot().getViewer();
		Map<?, ?> editPartRegistry = viewer.getEditPartRegistry();

		List<GraphicalEditPart> relevantEditParts = new ArrayList<>();
		List<LifelineEditPart> lifelineEditParts = new ArrayList<>();

		// Populate lists with relevant edit parts.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof AbstractMessageEditPart || value instanceof CombinedFragmentEditPart || value instanceof BehaviorExecutionSpecificationEditPart) {
				relevantEditParts.add((GraphicalEditPart) value);
			}

			if (value instanceof LifelineEditPart) {
				lifelineEditParts.add((LifelineEditPart) value);
			}

		}

		CompoundCommand compoundCommand = new CompoundCommand();

		// Iterate over lifeline edit parts to construct resize commands.
		for (LifelineEditPart lifeLineEP : lifelineEditParts) {

			final Rectangle absoluteBounds = SequenceUtil.getAbsoluteBounds(lifeLineEP);

			if (absoluteBounds.height() + resizeDeltaY <= AdoneLifeLineEditPart.DEFAUT_HEIGHT) {
				// Commented out to always apply resize adjustments (2024-01-23)
				// continue;
			}

			final Shape view = (ShapeImpl) lifeLineEP.getModel();
			final Dimension newDimension = new Dimension(absoluteBounds.width(), absoluteBounds.height() + resizeDeltaY);

			// Create and add resize command for the lifeline.
			final ICommand heightCommand = new SetResizeCommand(lifeLineEP.getEditingDomain(), DiagramUIMessages.SetLocationCommand_Label_Resize, new EObjectAdapter(view), newDimension);
			compoundCommand.add(new ICommandProxy(heightCommand));
		}

		return compoundCommand;
	}

	/**
	 * Calculates the height adjustment delta for lifelines based on the vertical extents of relevant edit parts.
	 * This method evaluates the vertical positioning of specified edit parts (messages, combined fragments,
	 * behavior executions) relative to the default height of lifelines to determine how much the lifelines
	 * need to be extended to accommodate these elements. The resulting delta can be used to resize lifelines
	 * ensuring that all relevant diagram elements are visually contained within the lifeline's extent.
	 *
	 * @param lifelineEditParts
	 *            The list of lifeline edit parts to consider for the height calculation.
	 * @param relevantEditParts
	 *            The list of other edit parts (e.g., messages, combined fragments) that may influence
	 *            the required height of the lifelines.
	 * @param resizeDeltaY
	 *            The vertical delta to be added to the height of edit parts, affecting the overall calculation.
	 * @return The calculated delta by which the lifeline heights should be increased to accommodate the specified edit parts.
	 */
	private static int calculateLifelineHeightDelta(List<LifelineEditPart> lifelineEditParts, List<GraphicalEditPart> relevantEditParts, int resizeDeltaY) {
		// Obtain the absolute bounds of the first lifeline as a reference for the default height calculation.
		Rectangle lifelineBounds = SequenceUtil.getAbsoluteBounds(lifelineEditParts.get(0));

		int defaultHeight;

		// Adjust the default height based on the initial Y position of the lifeline.
		if (lifelineBounds.y < 0) {
			Rectangle bounds = lifelineEditParts.get(0).getFigure().getBounds().getCopy();
			defaultHeight = bounds.y + AdoneLifeLineEditPart.DEFAUT_HEIGHT;
		} else {
			defaultHeight = lifelineBounds.y + AdoneLifeLineEditPart.DEFAUT_HEIGHT;
		}

		int maxY = defaultHeight;

		// Iterate through relevant edit parts to find the maximum Y extent.
		for (GraphicalEditPart ep : relevantEditParts) {
			Rectangle bounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ep);
			int bottomY = bounds.y + bounds.height + resizeDeltaY;

			// Update maxY if the current edit part extends beyond the current maximum.
			if (bottomY > defaultHeight) {
				maxY = Math.max(maxY, bottomY);
			}
		}

		// Calculate the delta required to adjust the lifeline height.
		return maxY - defaultHeight;
	}

	/**
	 * Identifies lifeline edit parts that are graphically covered by a specified combined fragment edit part.
	 * This method calculates the absolute bounds of the combined fragment and identifies lifelines whose
	 * central points fall within these bounds, effectively determining which lifelines are graphically
	 * "under" or within the scope of the combined fragment in a sequence diagram. This is useful for
	 * operations that involve manipulating or querying the structural relationships within sequence diagrams,
	 * such as rendering or diagrammatic analysis.
	 *
	 * @param cfEp
	 *            The combined fragment edit part whose coverage is to be determined.
	 * @return A list of LifelineEditParts that are graphically covered by the combined fragment, sorted by their
	 *         x-coordinate to maintain their visual order from left to right.
	 */
	public static List<LifelineEditPart> getGraphicallyCoveredLifelineEpList(AdoneCombinedFragmentEditPart cfEp) {
		List<LifelineEditPart> coveredLifelines = new ArrayList<>();

		// Calculate the graphical bounds of the combined fragment.
		Rectangle cfBounds = SequenceUtil.getAbsoluteBounds(cfEp);

		// Iterate through the edit part registry to find lifelines within the combined fragment's bounds.
		for (Object obj : cfEp.getViewer().getEditPartRegistry().values()) {
			if (obj instanceof LifelineEditPart) {
				LifelineEditPart lifelineEp = (LifelineEditPart) obj;

				// Calculate the absolute bounds of the lifeline.
				Rectangle lifelineBounds = SequenceUtil.getAbsoluteBounds(lifelineEp);

				// Determine the center x-coordinate of the lifeline.
				int lifelineCenterX = lifelineBounds.x + lifelineBounds.width / 2;

				// Check if the combined fragment's bounds cover the lifeline's center.
				if (cfBounds.x <= lifelineCenterX && lifelineCenterX <= cfBounds.x + cfBounds.width) {
					coveredLifelines.add(lifelineEp);
				}
			}
		}

		// Sort the covered lifelines by their x-coordinate for visual ordering.
		Collections.sort(coveredLifelines, new Comparator<LifelineEditPart>() {
			@Override
			public int compare(LifelineEditPart ep1, LifelineEditPart ep2) {
				Rectangle bounds1 = SequenceUtil.getAbsoluteBounds(ep1);
				Rectangle bounds2 = SequenceUtil.getAbsoluteBounds(ep2);
				return Integer.compare(bounds1.x, bounds2.x);
			}
		});

		return coveredLifelines;
	}

	/**
	 * Identifies all InteractionOperandEditParts that graphically contain a specified target bounds.
	 * This method iterates through the edit part registry to find InteractionOperandEditParts whose
	 * bounds encompass the given target bounds. It's particularly useful for identifying hierarchical
	 * relationships within sequence diagrams, especially when determining the nesting of interaction
	 * operands as it relates to combined fragments and other diagram elements.
	 *
	 * @param editPart
	 *            The reference edit part used to access the edit part registry.
	 * @param targetBounds
	 *            The rectangle representing the target bounds to check for containment within interaction operands.
	 * @return A list of InteractionOperandEditParts that contain the specified target bounds.
	 */
	public static List<InteractionOperandEditPart> getAllParentInteractionOperandEps(EditPart editPart, Rectangle targetBounds) {

		Map<?, ?> editPartRegistry = editPart.getViewer().getEditPartRegistry();
		List<InteractionOperandEditPart> parentOperands = new ArrayList<>();

		// Iterate over all edit parts in the registry to find InteractionOperandEditParts.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart operandEditPart = (InteractionOperandEditPart) value;
				Rectangle operandBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(operandEditPart);

				// Check if the operand bounds graphically contain the target bounds.
				// CF 생성 시 생성 범위에 포함된 Operand 가 부모로 인식되는 오류 수정 추후 검토 필요 (2024-01-26)
				if (operandBounds.contains(targetBounds) && !operandBounds.equals(targetBounds)) {
					parentOperands.add(operandEditPart);
				}

			}
		}

		return parentOperands;
	}

	/**
	 * Determines if the given LifelineEditPart represents the second lifeline from the left within its sequence diagram.
	 * This method retrieves all lifeline edit parts from the diagram, sorts them by their x-coordinates, and checks if
	 * the specified lifeline edit part is the second one in the sorted list. This is useful for operations or analyses
	 * that depend on the visual ordering of lifelines within the diagram, such as layout adjustments or identifying
	 * specific interaction patterns based on lifeline positioning.
	 *
	 * @param lifelineEp
	 *            The LifelineEditPart to check.
	 * @return true if the specified LifelineEditPart is the second lifeline from the left; false otherwise.
	 */
	public static boolean isSecondLifelineEp(LifelineEditPart lifelineEp) {
		List<LifelineEditPart> allLifelines = getAllLifelineEditPartsForSecondLifelineEp(lifelineEp);

		// Sort the lifelines by their x-coordinates to establish visual ordering.
		Collections.sort(allLifelines, new Comparator<LifelineEditPart>() {
			@Override
			public int compare(LifelineEditPart ep1, LifelineEditPart ep2) {
				Rectangle bounds1 = SequenceUtil.getAbsoluteBounds(ep1);
				Rectangle bounds2 = SequenceUtil.getAbsoluteBounds(ep2);
				return Integer.compare(bounds1.x, bounds2.x);
			}
		});

		// Check if the specified lifeline edit part is the second one in the sorted list.
		if (allLifelines.size() >= 2 && allLifelines.get(1).equals(lifelineEp)) {
			return true;
		}

		return false;
	}

	/**
	 * Retrieves all LifelineEditParts from the sequence diagram that the specified host edit part belongs to.
	 * This method iterates through the edit part registry of the diagram's viewer to collect all instances of
	 * LifelineEditParts, facilitating operations that require a comprehensive list of lifelines within the diagram,
	 * such as sorting to determine positional relationships or for bulk operations affecting all lifelines.
	 *
	 * @param host
	 *            The reference edit part from which to access the edit part registry. This is typically an
	 *            edit part within the sequence diagram whose lifelines are to be retrieved.
	 * @return A list of LifelineEditParts present in the same diagram as the host edit part.
	 */
	public static List<LifelineEditPart> getAllLifelineEditPartsForSecondLifelineEp(EditPart host) {
		EditPartViewer viewer = host.getRoot().getViewer();
		Map<?, ?> editPartRegistry = viewer.getEditPartRegistry();

		List<LifelineEditPart> lifelineEditParts = new ArrayList<>();

		// Iterate through the registry to find and add LifelineEditParts to the list.
		for (Object value : editPartRegistry.values()) {
			if (value instanceof LifelineEditPart) {
				lifelineEditParts.add((LifelineEditPart) value);
			}
		}

		// Return the collected list of LifelineEditParts.
		return lifelineEditParts;
	}

	/**
	 * Retrieves the Behavior Execution Specification Edit Part (BES EP) that directly follows a given message.
	 * This method first resolves the semantic element (Message) from the provided MessageSyncEditPart and then
	 * identifies the Behavior Execution Specification (BES) that follows the message's receive event. Finally,
	 * it retrieves the corresponding graphical edit part for the BES from the diagram. This functionality is
	 * crucial for sequence diagram operations that require the manipulation or analysis of execution specifications
	 * directly related to specific messages, such as layout adjustments or semantic validations.
	 *
	 * @param messageEp
	 *            The MessageSyncEditPart representing the message of interest.
	 * @return The BehaviorExecutionSpecificationEditPart that follows the given message, or null if no such
	 *         BES EP is found or the message does not lead to a BES.
	 */
	public static BehaviorExecutionSpecificationEditPart getFollowingBehaviorExeSpecEditPart(AdoneMessageSyncEditPart messageEp) {
		// Resolve the semantic element for the message.
		Message msg = (Message) messageEp.resolveSemanticElement();

		// Identify the BES that follows the message.
		BehaviorExecutionSpecification bes = getFollowingBehaviorExeSpec(msg);
		BehaviorExecutionSpecificationEditPart besEp = null;
		GraphicalEditPart ep = getEditPartFromSemantic(messageEp, bes);

		if (ep != null) {
			besEp = (BehaviorExecutionSpecificationEditPart) ep;
		}

		return besEp;
	}

	/**
	 * Retrieves all graphical edit parts that are graphically nested within a specified combined fragment edit part.
	 * This method calculates the absolute bounds of the combined fragment and then identifies all child edit parts
	 * whose graphical representations fall within these bounds. It is particularly useful for operations that require
	 * identifying the structural and hierarchical relationships of elements within combined fragments, such as
	 * diagram analysis, layout adjustments, or extracting specific subsets of the model for focused processing.
	 *
	 * @param cfEp
	 *            The CombinedFragmentEditPart representing the combined fragment of interest.
	 * @return A list of GraphicalEditParts that are nested within the graphical bounds of the combined fragment.
	 */
	public static List<GraphicalEditPart> getCombinedFragmentNestedEps(CombinedFragmentEditPart cfEp) {

		List<GraphicalEditPart> nestedEps = new ArrayList<>();

		Rectangle cfBounds = getAbsoluteBoundsForMessageEp(cfEp);

		// Iterate over child edit parts that are graphically contained within the combined fragment's bounds.
		for (Object childEp : AdoneSequenceUtil.getCoveredInteractionFragmentEditParts(cfBounds, cfEp, true, null)) {
			nestedEps.add((GraphicalEditPart) childEp);
		}

		return nestedEps;
	}

	/**
	 * Retrieves all child edit parts that are graphically contained within the bounds of a specified InteractionOperandEditPart.
	 * This method is useful for identifying all graphical elements that are part of a specific interaction operand in a sequence
	 * diagram, facilitating operations such as analysis, modification, or visualization of specific segments of the diagram.
	 *
	 * @param ioEp
	 *            The InteractionOperandEditPart representing the interaction operand of interest.
	 * @return A list of EditParts that are graphically contained within the interaction operand.
	 */
	public static List<EditPart> getallChildEditPartsInOperand(InteractionOperandEditPart ioEp) {
		List<EditPart> childEditParts = new ArrayList<>();
		Map<?, ?> registry = ioEp.getRoot().getViewer().getEditPartRegistry();

		Rectangle ioEpBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ioEp);

		for (Object value : registry.values()) {
			if (value instanceof GraphicalEditPart) {
				GraphicalEditPart ep = (GraphicalEditPart) value;
				if (ep.getFigure() != null) {
					Rectangle epBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ioEp);
					// 만약 EditPart의 그래픽 정보가 InteractionOperandEditPart의 그래픽 정보 내부에 있다면 리스트에 추가
					// epBounds가 ioEpBounds에 완전히 포함되는지 확인
					// ioEpBounds가 epBounds를 완전히 포함하는지 확인
					// Check if the edit part's bounds are fully contained within the interaction operand's bounds.
					if (ioEpBounds.x < epBounds.x
							&& ioEpBounds.y < epBounds.y
							&& ioEpBounds.x + ioEpBounds.width > epBounds.x + epBounds.width
							&& ioEpBounds.y + ioEpBounds.height > epBounds.y + epBounds.height) {
						childEditParts.add(ep);
					}
				}
			}
		}

		return childEditParts;
	}

	/**
	 * Retrieves all graphical edit parts from the diagram that the specified host edit part belongs to.
	 * This method iterates through the edit part registry of the diagram's viewer to collect all instances
	 * of GraphicalEditParts, providing a comprehensive list of all graphical elements present in the diagram.
	 * This functionality is useful for operations that require a global view of the diagram, such as global
	 * layout adjustments, exporting, or analysis tasks that do not focus on a specific type of diagram element.
	 *
	 * @param hostEp
	 *            The reference edit part used to access the edit part registry. This is typically any
	 *            edit part within the diagram from which to start the collection process.
	 * @return A list of all GraphicalEditParts present in the same diagram as the host edit part.
	 */
	public static List<GraphicalEditPart> getAllEditParts(EditPart hostEp) {

		List<GraphicalEditPart> childEditParts = new ArrayList<>();
		Map<?, ?> registry = hostEp.getRoot().getViewer().getEditPartRegistry();

		for (Object value : registry.values()) {
			if (value instanceof GraphicalEditPart) {
				GraphicalEditPart ep = (GraphicalEditPart) value;
				childEditParts.add(ep);
			}
		}

		return childEditParts;
	}

	/**
	 * Adjusts the provided rectangle's coordinates to account for the viewport's scroll and zoom effects within the diagram.
	 * This method transforms a rectangle's coordinates from screen referential (which may include scroll offsets and zoom levels)
	 * back to the diagram's coordinate system, effectively "canceling" the viewport effects. This is particularly useful for
	 * operations that require precise positioning or dimensions within the diagram's own coordinate system, such as creating
	 * or manipulating graphical elements based on screen-based interactions or calculations.
	 *
	 * @param viewer
	 *            The edit part viewer whose viewport effects are to be considered. Must be a GraphicalViewer instance.
	 * @param rect
	 *            The rectangle whose coordinates are to be adjusted from screen to diagram referential.
	 * @return A new Rectangle instance with its coordinates and size adjusted to reflect the diagram's coordinate system.
	 */
	public static Rectangle cancelViewPortEffect(EditPartViewer viewer, Rectangle rect) {
		// Transform the top-left and bottom-right points of the rectangle from screen to diagram coordinates.
		Point p1 = CoordinateReferentialUtils.transformPointFromScreenToDiagramReferential(rect.getTopLeft(), (GraphicalViewer) viewer);
		Point p2 = CoordinateReferentialUtils.transformPointFromScreenToDiagramReferential(rect.getBottomRight(), (GraphicalViewer) viewer);

		// Update the rectangle's location to the transformed top-left point.
		rect.setLocation(p1);
		// Adjust the rectangle's size based on the transformed points.
		rect.setSize(p2.x - p1.x, p2.y - p1.y);

		// Return the adjusted rectangle.
		return rect;
	}

	/**
	 * Identifies the starting MessageOccurrenceSpecification for a recursive BehaviorExecutionSpecification.
	 * This method scans through all interaction fragments associated with the enclosing interaction of the
	 * target Behavior Execution Specification (BES). It aims to find the MessageOccurrenceSpecification (MOS)
	 * that corresponds to the start of the recursive call, by tracking the last received event before encountering
	 * the start of a BES that does not end with a "Finish" naming convention.
	 *
	 * @param targetBes
	 *            The Behavior Execution Specification for which the starting MessageOccurrenceSpecification
	 *            is sought, typically representing a recursive call within a sequence diagram.
	 * @return The found MessageOccurrenceSpecification that marks the start of the recursive call, or null if
	 *         no such specification can be found.
	 */
	public static MessageOccurrenceSpecification getStartMessageOccurenceSpecForRecursiveBes(BehaviorExecutionSpecification targetBes) {

		MessageOccurrenceSpecification foundMos = null;

		// Retrieve all interaction fragments from the enclosing interaction.
		List<InteractionFragment> fragments = AdoneSequenceUtil.getAllInteractionFragments(targetBes.getEnclosingInteraction());

		// Variable to hold the last received event.
		InteractionFragment lastReceivedEvent = null;

		for (InteractionFragment frg : fragments) {

			// Check for MessageOccurrenceSpecifications and update the last received event.
			if (frg instanceof MessageOccurrenceSpecification) {
				MessageOccurrenceSpecification mos = (MessageOccurrenceSpecification) frg;

				// Skip if the message or its receive event is null.

				if (mos.getMessage() == null) {
					continue;
				}

				if (mos.getMessage().getReceiveEvent() == null) {
					continue;
				}

				if (mos.getMessage().getReceiveEvent().equals(mos)) {
					lastReceivedEvent = mos;
				}

			}

			// Check for BehaviorExecutionSpecifications to identify the start of the target BES.
			if (frg instanceof BehaviorExecutionSpecification) {
				BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) frg;

				if (bes.getStart() == null) {
					continue;
				}

				if (bes.getStart() instanceof ExecutionOccurrenceSpecification) {

					ExecutionOccurrenceSpecification eos = (ExecutionOccurrenceSpecification) bes.getStart();

					// Skip if the name of the ExecutionOccurrenceSpecification ends with "Finish".
					if (eos.getName().endsWith("Finish")) {
						continue;
					}

					// Found the start of the recursive BES. Set the last received event as the found MOS.
					foundMos = (MessageOccurrenceSpecification) lastReceivedEvent;
					break;
				}
			}
		}

		return foundMos;
	}

}
