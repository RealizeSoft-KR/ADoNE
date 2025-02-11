/*****************************************************************************
 * Copyright (c) 2010 CEA
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Soyatec - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 522305
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhances message sync, intuitive controls, and visual
 *   consistency in sequence diagrams, including target anchor view resizing.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeConnectionRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AdoneConnectionTargetAnchor;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AdoneSolidArrowFilledConnectionDecoration;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.ConnectionSourceAnchor;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneMessageConnectionLineSegEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.AdoneMessageDefaultSemanticEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.edit.policies.SequenceReferenceEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.figures.AdoneMessageSync;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLElementTypes;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.AdoneLifeLineGraphicalNodeEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.tools.AdoneDragMessageEditPartTracker;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneElementOrderChangeManager;
import org.eclipse.papyrus.uml.diagram.sequence.util.DurationLinkUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.GeneralOrderingUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.uml2.uml.Message;

/**
 * Extends CustomMessageSyncEditPart to enhance message synchronization in sequence diagrams.
 * Incorporates functionality to deactivate order change mode with ESC key, ensuring intuitive
 * interaction patterns. It dynamically supports reordering messages above the top message in
 * drag-and-drop mode by leveraging Papyrus's move mode. Additionally, it addresses color
 * reset issues during refresh in move mode, maintaining visual consistency. Custom edit policies
 * and connection routing are tailored to improve message representation and interaction,
 * with specific support for duration links and general ordering connections.
 */
public class AdoneMessageSyncEditPart extends CustomMessageSyncEditPart {

	private Listener keyDownListener;

	public AdoneMessageSyncEditPart(View view) {
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
	protected Connection createConnectionFigure() {
		return new AdoneMessageSync();
	}

	@Override
	public boolean mustReorderMessage() {
		// Fix inability to move messages above the top one in D&D mode by forcing Papyrus's own move mode on (2023-01-04)
		return true;
	}

	@Override
	protected void createDefaultEditPolicies() {
		super.createDefaultEditPolicies();

		// Removed policies due to issues with strong & weak references and other references not functioning properly.
		removeEditPolicy(SequenceReferenceEditPolicy.SEQUENCE_REFERENCE);

		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new AdoneMessageConnectionLineSegEditPolicy());
		installEditPolicy("CustomMessageEditPolicy", new AdoneMessageDefaultSemanticEditPolicy());

	}

	@Override
	public DragTracker getDragTracker(Request req) {
		return new AdoneDragMessageEditPartTracker(this);
	}

	@Override
	protected void installRouter() {
		getConnectionFigure().setConnectionRouter(AdoneLifeLineGraphicalNodeEditPolicy.adoneMessageRouter);
		getConnectionFigure().setCursor(org.eclipse.gmf.runtime.gef.ui.internal.l10n.Cursors.CURSOR_SEG_MOVE);
		refreshBendpoints();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof CreateUnspecifiedTypeConnectionRequest) {
			CreateUnspecifiedTypeConnectionRequest createRequest = (CreateUnspecifiedTypeConnectionRequest) request;
			List<?> relationshipTypes = createRequest.getElementTypes();
			for (Object type : relationshipTypes) {
				if (UMLElementTypes.DurationConstraint_Edge.equals(type) || UMLElementTypes.DurationObservation_Edge.equals(type) || UMLElementTypes.GeneralOrdering_Edge.equals(type)) {
					return OccurrenceSpecificationUtil.isSource(getConnectionFigure(), createRequest.getLocation()) ? new ConnectionSourceAnchor(getPrimaryShape()) : new AdoneConnectionTargetAnchor(getPrimaryShape());
				}
			}
		} else if (request instanceof CreateConnectionViewRequest) {
			CreateConnectionViewRequest createRequest = (CreateConnectionViewRequest) request;
			if (DurationLinkUtil.isDurationLink(createRequest) || GeneralOrderingUtil.isGeneralOrderingLink(createRequest)) {
				return OccurrenceSpecificationUtil.isSource(getConnectionFigure(), createRequest.getLocation()) ? new ConnectionSourceAnchor(getPrimaryShape()) : new AdoneConnectionTargetAnchor(getPrimaryShape());
			}
		} else if (request instanceof ReconnectRequest) {
			ReconnectRequest reconnectRequest = (ReconnectRequest) request;
			if (DurationLinkUtil.isDurationLink(reconnectRequest) || GeneralOrderingUtil.isGeneralOrderingLink(reconnectRequest)) {
				return OccurrenceSpecificationUtil.isSource(getConnectionFigure(), reconnectRequest.getLocation()) ? new ConnectionSourceAnchor(getPrimaryShape()) : new AdoneConnectionTargetAnchor(getPrimaryShape());
			}
		}
		return super.getTargetConnectionAnchor(request);
	}

	@Override
	protected RotatableDecoration getConnectionDecoration(final String arrowType) {
		RotatableDecoration decoration = new AdoneSolidArrowFilledConnectionDecoration();
		return decoration;
	}

	@Override
	public void refresh() {

		// Prevent message color reset during refresh in move mode (2024-02-02)
		if (AdoneElementOrderChangeManager.getInstance().isElementOrderChangeMode()) {
			return;
		}
		super.refresh();

	}

	@Override
	public String toString() {
		if (this.resolveSemanticElement() != null) {
			Message msg = (Message) this.resolveSemanticElement();

			if (msg.getSignature() != null) {
				return msg.getSignature().getQualifiedName() + "\n" + super.toString();
			} else {
				return msg.getQualifiedName() + "\n" + super.toString();
			}

		} else {
			return super.toString();
		}
	}

	@Override
	public void deactivate() {

		if (this.keyDownListener != null) {
			Display.getCurrent().removeFilter(SWT.KeyDown, this.keyDownListener);
		}

		super.deactivate();
	}

}
