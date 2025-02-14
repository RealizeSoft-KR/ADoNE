/*****************************************************************************
 * Copyright (c) 2017, 2018 CEA LIST, EclipseSource and others.
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
 *   Celine Janssens (celine.janssens@all4tec.net) - Add Coregion  functionnality
 *   EclipseSource - Bug 533770
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Extended CombinedFragment creation functionality by redefining edit policies.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneCombinedFragmentDefaultSemanticEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneCombinedFragmentResizeEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneCombinedFragmentFigure;
import org.eclipse.papyrus.uml.diagram.sequence.tools.AdoneDragCombinedFragmentEditPartsTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

/**
 * This class extends the functionality of the Combined Fragment edit part in UML sequence diagrams.
 * It introduces several enhancements, such as:
 * - Adding a key listener to deactivate reordering mode when the ESC key is pressed.
 * - Modifying the default size of Combined Fragments for better visualization.
 * - Customizing the refresh label method to handle exceptions related to type casting.
 * - Refining the default edit policies to include custom resize behavior and semantic edit policies.
 * - Preventing refresh operations during element reordering mode to maintain message color consistency.
 * - Adjusting drag tracker for customized dragging behavior.
 * - Implementing custom sorting and feedback during drag-and-drop operations to ensure proper alignment and behavior.
 * - Additionally, this class includes a toString method for detailed representation, including order, position, and contained message information for debugging and visualization purposes.
 */
public class AdoneCombinedFragmentEditPart extends CCombinedFragmentEditPart {

	// Placeholder for future value redefinition.
	public static int DEFAULT_HEIGHT = 60;
	public static int DEFAULT_WIDTH = 40;

	private Listener keyDownListener;

	public AdoneCombinedFragmentEditPart(View view) {
		super(view);
	}

	@Override
	public void activate() {

		super.activate();

		// Implements deactivation of Order Change mode upon pressing the ESC key (2024-01-06)
		Display.getCurrent().addFilter(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.keyCode == SWT.ESC) {
					Display.getCurrent().asyncExec(new Runnable() {
						@Override
						public void run() {
							handleEscapeKeyPress();
						}
					});
				}
			}
		});

	}

	private void handleEscapeKeyPress() {
		// finish change order mode
		AdoneElementOrderChangeManager.getInstance().setElementOrderChangeMode(false);
		IFigure figure = this.getContentPane();
		AdoneElementOrderChangeManager.getInstance().resetFigureLineColorToDefault(figure);
	}

	@Override
	public void deactivate() {

		if (this.keyDownListener != null) {
			Display.getCurrent().removeFilter(SWT.KeyDown, this.keyDownListener);
		}
		super.deactivate();

	}

	@Override
	protected void refreshLabel() {

		// Unknown error cause. Temporary workaround implemented (2023-12-14)
		// class org.eclipse.uml2.uml.internal.impl.InteractionImpl cannot be cast to class org.eclipse.uml2.uml.CombinedFragment

		if (!(this.resolveSemanticElement() instanceof CombinedFragment)) {
			return;
		}

		try {
			CombinedFragment semantic = (CombinedFragment) this.resolveSemanticElement();
			if (semantic != null && semantic.getInteractionOperator() != null) {

				// getPrimaryShape().setName(semantic.getInteractionOperator().getLiteral());

				// To work around the issue where the Interaction model information is arbitrarily modified if a Stereotype exists
				// in a CombinedFragment when running the Papyrus sequence diagram editor,
				// only the label of the InteractionOperator is modified, and the actual information is stored in the Annotation.
				// Later, when generating source code, a TryCatch block will be created based on the information in the Annotation. (2024-06-22)

				String tryCatchYn = EcoreUtil.getAnnotation(semantic, "", "TryCatch");

				if (tryCatchYn != null && "true".equals(tryCatchYn)) {
					getPrimaryShape().setName("TryCatch");
				} else {
					getPrimaryShape().setName(semantic.getInteractionOperator().getLiteral());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new AdoneCombinedFragmentResizeEditPolicy());

		removeEditPolicy(EditPolicyRoles.SEMANTIC_ROLE);
		installEditPolicy(AdoneCombinedFragmentDefaultSemanticEditPolicy.SEMANTIC_ROLE, new AdoneCombinedFragmentDefaultSemanticEditPolicy());

	}

	@Override
	public void refresh() {

		// Prevents message color reset caused by refresh execution during change order mode (2024-02-02)
		if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			return;
		}

		super.refresh();

	}

	@Override
	public void showTargetFeedback(Request request) {

		// Force feedback image's x-coordinate to 0 during CF (Combined Fragment) move
		if (request instanceof ChangeBoundsRequest) {
			ChangeBoundsRequest changeBoundsRequest = (ChangeBoundsRequest) request;

			if (changeBoundsRequest.getEditParts().get(0) instanceof LifelineEditPart) {
				changeBoundsRequest.setMoveDelta(new Point(changeBoundsRequest.getMoveDelta().x, 0));
			}
		}
		super.showTargetFeedback(request);
	}

	@Override
	protected IFigure createNodeShape() {
		return primaryShape = new AdoneCombinedFragmentFigure();
	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new AdoneDragCombinedFragmentEditPartsTracker(this);
	}

	@Override
	public String toString() {
		// Finds the order based on Y values.
		int order = 0;
		List<CombinedFragmentEditPart> combinedFragments = new ArrayList<>();
		for (Object obj : getViewer().getEditPartRegistry().values()) {
			if (obj instanceof CombinedFragmentEditPart) {
				combinedFragments.add((CombinedFragmentEditPart) obj);
			}
		}

		// Sorts by Y values.
		Collections.sort(combinedFragments, new Comparator<CombinedFragmentEditPart>() {
			@Override
			public int compare(CombinedFragmentEditPart o1, CombinedFragmentEditPart o2) {
				Rectangle r1 = o1.getFigure().getBounds();
				Rectangle r2 = o2.getFigure().getBounds();
				return Integer.compare(r1.y, r2.y);
			}
		});

		// Finds the current EditPart's order.
		order = combinedFragments.indexOf(this) + 1; // Returns a 1-based index.

		// Absolute position information of the CombinedFragment.
		Rectangle absoluteBounds = this.getFigure().getBounds().getCopy();
		getFigure().translateToAbsolute(absoluteBounds);

		// Information about Messages included in the child InteractionOperand of the CombinedFragment.
		StringBuilder messageInfo = new StringBuilder();
		for (Object child : getChildren()) {

			if (child instanceof CombinedFragmentCombinedFragmentCompartmentEditPart) {
				CombinedFragmentCombinedFragmentCompartmentEditPart cffc = (CombinedFragmentCombinedFragmentCompartmentEditPart) child;

				for (Object obj : cffc.getChildren()) {

					if (obj instanceof InteractionOperandEditPart) {
						InteractionOperandEditPart operand = (InteractionOperandEditPart) obj;

						InteractionOperand io = (InteractionOperand) operand.resolveSemanticElement();

						for (InteractionFragment frg : io.getFragments()) {
							if (frg instanceof MessageOccurrenceSpecification) {

								MessageOccurrenceSpecification mos = (MessageOccurrenceSpecification) frg;

								if (mos.equals(mos.getMessage().getSendEvent())) {
									if (mos.getMessage().getSignature() != null) {
										messageInfo.append(mos.getMessage().getSignature().getName());
									} else {
										messageInfo.append(mos.getMessage().getName());
									}
									messageInfo.append(", ");

								}
							}
						}
					}

				}

			}
		}

		// Constructs the result string.
		return "CombinedFragmentEditPart Order: " + order +
				", Absolute Position: " + absoluteBounds +
				", Messages: [" + messageInfo.toString() + "]";
	}


}
