/*****************************************************************************
 * Copyright (c) 2017, 2018 CEA LIST, ALL4TEC, Christian W. Damus, and others.
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
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 519621, 526803
 *   Vincent Lorenzo (CEA LIST) vincent.lorenzo@cea.fr - Bug 531520
 *   Christian W. Damus - bugs 533672, 536486
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhance lifeline features including custom dimensions,
 *   edit policies, and improved interaction behaviors.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeRequest;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.figure.node.RoundedRectangleNodePlateFigure;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneLifelineSelectionEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneUpdateNodeReferenceEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.UpdateWeakReferenceForExecSpecEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.UpdateWeakReferenceForMessageSpecEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneLifelineFigure;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneLifelineNodePlate;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.AdoneLifeLineGraphicalNodeEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.AdoneLifeLineXYLayoutEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.tools.AdoneDragLifelineEditPartsTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionOperatorKind;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Type;

/**
 * Enhances the CLifeLineEditPart with additional features and customizations for lifeline representations in sequence diagrams.
 * This class provides modifications to default lifeline dimensions, edit policies, and user interaction behaviors. Key enhancements include:
 * - Customized dimensions for lifeline figures to accommodate more complex interactions.
 * - Improved edit policies to refine drag-and-drop interactions and lifeline resizing.
 * - Enhanced user interaction with support for deactivating order change mode via the ESC key.
 * - Proactive feedback control to prevent unintended creation of InteractionOperands in non-ALT CombinedFragments and to manage connection re-setting dynamically.
 * This class aims to provide a more intuitive and user-friendly experience in sequence diagram editing and visualization.
 */
public class AdoneLifeLineEditPart extends CLifeLineEditPart {

	public static int DEFAUT_HEIGHT = 700;
	public static int DEFAUT_WIDTH = 100;
	public static final int FIRST_LIFELINE_LEFT_MARGIN = 20;
	public static final int MIN_LIFELINE_SPACING = 10;

	private EditPart activeCreateFeedbackEditPart;

	private Listener keyDownListener;

	public AdoneLifeLineEditPart(View view) {
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
		// finish order change mode
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
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		// Removed policies due to issues with strong & weak references and other references not functioning properly.
		removeEditPolicy(AdoneUpdateNodeReferenceEditPolicy.UDPATE_NODE_REFERENCE);
		removeEditPolicy(UpdateWeakReferenceForMessageSpecEditPolicy.UDPATE_WEAK_REFERENCE_FOR_MESSAGE);
		removeEditPolicy(UpdateWeakReferenceForExecSpecEditPolicy.UDPATE_WEAK_REFERENCE_FOR_EXECSPEC);

		installEditPolicy(EditPolicy.PRIMARY_DRAG_ROLE, new AdoneLifelineSelectionEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new AdoneLifeLineGraphicalNodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AdoneLifeLineXYLayoutEditPolicy());

	}

	@Override
	public DragTracker getDragTracker(Request request) {
		return new AdoneDragLifelineEditPartsTracker(this);
	}

	@Override
	protected IFigure createNodeShape() {
		return primaryShape = new AdoneLifelineFigure();
	}

	@Override
	protected NodeFigure createSVGNodePlate() {
		if (null == svgNodePlate) {
			svgNodePlate = new AdoneLifelineNodePlate(this, -1, -1).withLinkLFEnabled();
			svgNodePlate.setDefaultNodePlate(createNodePlate());
		}
		return svgNodePlate;
	}

	@Override
	protected NodeFigure createNodePlate() {
		RoundedRectangleNodePlateFigure result = new RoundedRectangleNodePlateFigure(DEFAUT_WIDTH, DEFAUT_HEIGHT);
		return result;
	}

	@Override
	public Command getCommand(Request request) {

		// Returns null to prevent the link dialog from appearing on double-clicking a lifeline (2024-02-01)
		if (request.getType().equals(REQ_OPEN)) {
			return null;
		}

		// Prevents re-setting of message connections (2024-02-04)
		// When moving message vertically, "do not check horizontality" is set in ExtendedData, except in this case, it is not allowed.
		if (request.getType().equals("Reconnection target") && !request.getExtendedData().containsKey("do not check horizontality")) {
			return UnexecutableCommand.INSTANCE;
		}

		if (request.getType().equals("Reconnection source") && !request.getExtendedData().containsKey("do not check horizontality")) {
			return UnexecutableCommand.INSTANCE;
		}


		return super.getCommand(request);
	}

	@Override
	protected void refreshVisuals() {

		// Prevents message color reset caused by refresh execution during change order mode (2024-02-02)
		if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			return;
		}

		super.refreshVisuals();
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
	public String toString() {
		Lifeline lifeline = (Lifeline) this.resolveSemanticElement();
		if (lifeline != null && lifeline.getRepresents() != null) {
			Type lifelineType = lifeline.getRepresents().getType();
			if (lifelineType != null) {
				return lifelineType.getQualifiedName() + "\n" + super.toString();
			} else {
				return lifeline.getName() + "\n" + super.toString();
			}
		}
		return super.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void showTargetFeedback(Request request) {
		if (request instanceof CreateUnspecifiedTypeRequest) {
			((CreateUnspecifiedTypeRequest) request).getElementTypes().forEach(t -> {
				CreateRequest req = ((CreateUnspecifiedTypeRequest) request).getRequestForType((IElementType) t);
				EditPart targetEP = getTargetEditPart(req);

				// To prevent creation of InteractionOperands in CombinedFragments other than ALT,
				// feedback is suppressed in such cases (2023-12-06).
				if (targetEP instanceof CCombinedFragmentCombinedFragmentCompartmentEditPart) {
					CombinedFragmentEditPart parentCfEp = (CombinedFragmentEditPart) targetEP.getParent();
					CombinedFragment parentCf = (CombinedFragment) parentCfEp.resolveSemanticElement();
					if (!parentCf.getInteractionOperator().equals(InteractionOperatorKind.ALT_LITERAL)) {
						return;
					}
				}
				if (activeCreateFeedbackEditPart != targetEP) {
					if (activeCreateFeedbackEditPart != null) {
						activeCreateFeedbackEditPart.eraseTargetFeedback(request);
					}
					activeCreateFeedbackEditPart = targetEP;
				}
				if (targetEP != this) {
					targetEP.showTargetFeedback(request);
				} else {
					super.showTargetFeedback(request);
				}
			});
			return;
		}
		super.showTargetFeedback(request);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void eraseTargetFeedback(Request request) {
		if (request instanceof CreateUnspecifiedTypeRequest) {
			((CreateUnspecifiedTypeRequest) request).getElementTypes().forEach(t -> {
				CreateRequest req = ((CreateUnspecifiedTypeRequest) request).getRequestForType((IElementType) t);
				EditPart targetEP = getTargetEditPart(req);
				if (activeCreateFeedbackEditPart != null && activeCreateFeedbackEditPart != this) {
					activeCreateFeedbackEditPart.eraseTargetFeedback(request);
					activeCreateFeedbackEditPart = null;
				}
				if (targetEP != this) {
					targetEP.eraseTargetFeedback(request);
				} else {
					super.eraseTargetFeedback(request);
				}
			});
			return;
		}
		super.eraseTargetFeedback(request);
	}

}
