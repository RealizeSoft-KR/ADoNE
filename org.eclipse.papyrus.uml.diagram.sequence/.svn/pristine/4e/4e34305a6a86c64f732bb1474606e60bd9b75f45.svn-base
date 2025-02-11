/*****************************************************************************
 * Copyright (c) 2016 - 2017 CEA LIST and others.
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
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 519408
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhances layout scalability and lifeline label visibility with AdoneLifeLineLayoutManager.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.diagram.ui.figures.BorderedNodeFigure;
import org.eclipse.papyrus.infra.gmfdiag.common.figure.node.ScalableCompartmentFigure;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.figures.LifelineFigure.LifelineHeaderFigure;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneInteractionHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

/**
 * This class customizes the layout manager for lifeline figures in sequence diagrams, specifically to adjust for
 * additional header height and to properly position execution specifications, especially for recursive message calls.
 * It overrides various layout methods to ensure elements are positioned correctly within the lifeline,
 * including an adjustment for the header's height.
 */
public class AdoneLifeLineLayoutManager extends LifeLineLayoutManager {

	// Increased lifeline header height from 28 to 36 - extra height of 8.
	protected final int bottomHeaderAddtionalHeight = 8;

	// Redefines due to superclass's private fields.
	protected int bottomHeaderY = 0;

	/**
	 * Adjusts lifeline figures layout, notably for recursive message execution specifications. It positions execution
	 * specifications and the lifeline header within the figure container to meet specific layout requirements.
	 *
	 * @param container
	 *            The container holding lifeline and child figures.
	 */
	@Override
	protected void layoutXYFigure(IFigure container) {

		for (Object child : container.getChildren()) {

			if (child instanceof BorderedNodeFigure) {
				if (((BorderedNodeFigure) child).getMainFigure() instanceof ILifelineInternalFigure) {
					Rectangle theConstraint = constraints.get(child).getCopy();

					if (theConstraint != null) {
						theConstraint.translate(container.getBounds().getTopLeft());

						if (((BorderedNodeFigure) child).getMainFigure() instanceof ExecutionSpecificationNodePlate) {

							ExecutionSpecificationNodePlate nodePlate = (ExecutionSpecificationNodePlate) ((BorderedNodeFigure) child).getMainFigure();

							if (isRecursiveMessageExecutionSpec(nodePlate)) {
								// Shifts the execution specification figure to the right by 3 units for recursive message visibility enhancement.
								theConstraint.setX(container.getBounds().getCopy().getCenter().x - AdoneBehaviorExecutionSpecificationEditPart.DEFAUT_WIDTH / 2 + 3);
							} else {
								theConstraint.setX(container.getBounds().getCopy().getCenter().x - AdoneBehaviorExecutionSpecificationEditPart.DEFAUT_WIDTH / 2);
							}

							theConstraint.setWidth(AdoneBehaviorExecutionSpecificationEditPart.DEFAUT_WIDTH);

						}

						((BorderedNodeFigure) child).setBounds(theConstraint);

					}
				}
			} else if (child instanceof LifelineHeaderFigure) {
				((LifelineHeaderFigure) child).setBounds(container.getBounds().getCopy().setHeight(getBottomHeader() - container.getBounds().y + 1));
			}
		}
	}

	/**
	 * Due to a discrepancy in Papyrus handling of recursive message creation, where `getStart()` on a
	 * `BehaviorExecutionSpecification` unexpectedly returns an `ExecutionOccurrenceSpecification` instead
	 * of the anticipated `MessageOccurrenceSpecification`, this method performs additional steps to locate
	 * the original `MessageOccurrenceSpecification`. This workaround ensures accurate identification and
	 * adjustment for recursive message execution specifications.
	 *
	 * @param nodePlate
	 *            The node plate to check.
	 * @return true if it's for a recursive message, false otherwise.
	 */
	private boolean isRecursiveMessageExecutionSpec(ExecutionSpecificationNodePlate nodePlate) {

		if (nodePlate.getChildren().size() < 2) {
			return false;
		}

		AdoneExecutionSpecificationRectangleFigure besFigure = (AdoneExecutionSpecificationRectangleFigure) nodePlate.getChildren().get(1);
		AdoneBehaviorExecutionSpecificationEditPart besEp = besFigure.getHost();
		BehaviorExecutionSpecification bes = (BehaviorExecutionSpecification) besEp.resolveSemanticElement();
		MessageOccurrenceSpecification mos = null;

		if (bes.getStart() instanceof ExecutionOccurrenceSpecification) {
			mos = AdoneSequenceUtil.getStartMessageOccurenceSpecForRecursiveBes(bes);
		} else {
			mos = (MessageOccurrenceSpecification) bes.getStart();
		}

		Message msg = mos.getMessage();

		if (AdoneInteractionHelper.isRecursiveMessage(msg)) {
			return true;
		}

		return false;
	}

	@Override
	public int getBottomHeader() {
		return bottomHeaderY;
	}

	/**
	 * Adjusts the default layout within the lifeline container, including positioning symbols at the top and aligning other
	 * elements within the lifeline.
	 *
	 * @param container
	 *            The lifeline figure container.
	 */
	@Override
	protected void layoutDefault(IFigure container) {
		ScalableCompartmentFigure symbolFigure = null;
		int i = 0;
		while (symbolFigure == null && i < container.getChildren().size()) {
			if (container.getChildren().get(i) instanceof ScalableCompartmentFigure) {
				symbolFigure = (ScalableCompartmentFigure) container.getChildren().get(i);
			}
			i++;
		}
		super.layoutDefault(container);

		// change coordinate to set the symbol at the top
		if (symbolFigure != null) {
			symbolFigure.getBounds().setY(container.getBounds().getTop().y);
		}
		Rectangle containerBounds = container.getBounds();
		IFigure previous = null;
		for (IFigure child : visibleOthers) {
			Rectangle bound = new Rectangle();
			if (previous != null) {
				if (child.equals(symbolFigure)) {
					bound.y = containerBounds.y + 3;
				} else {
					bound.y = previous.getBounds().getBottomLeft().y + 1;
					bound.x = containerBounds.x + 1;
					bound.width = containerBounds.width;
					bound.height = child.getBounds().height;
					bottomHeaderY = bound.y + bound.height;
				}
			} else {
				bound.x = containerBounds.x + 1;
				// here the symbo may be present
				if (symbolFigure != null) {
					bound.y = containerBounds.y + 3 + symbolFigure.getBounds().height;
				} else {
					bound.y = containerBounds.y + 3;
				}

				bound.width = containerBounds.width;
				bound.height = child.getBounds().height;
			}
			child.setBounds(bound);
			previous = child;
		}


	}

	/**
	 * Adjusts layout for elements within the lifeline container, adding extra height for the lifeline header to ensure
	 * proper alignment of all child figures.
	 *
	 * @param container
	 *            The area for layout adjustment.
	 */
	@Override
	protected void layoutOthers(Rectangle container) {
		super.layoutOthers(container);
		IFigure previous = null;
		for (IFigure child : visibleOthers) {
			Rectangle bound = new Rectangle();
			if (previous != null) {
				bound.y = previous.getBounds().getBottomLeft().y + 1;
				bound.x = container.x + 1;
				bound.width = container.width;
				bound.height = child.getBounds().height + bottomHeaderAddtionalHeight;
				bottomHeaderY = bound.y + bound.height;
			} else {
				bound.x = container.x + 1;
				bound.y = container.y + 2;
				bound.width = container.width;
				bound.height = child.getBounds().height + bottomHeaderAddtionalHeight;
				bottomHeaderY = bound.y + bound.height;
			}
			child.setBounds(bound);
			previous = child;
		}
	}


}