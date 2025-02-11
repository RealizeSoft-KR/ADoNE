/*****************************************************************************
 * Copyright (c) 2010, 2018 CEA List, EclipseSource, Christian W. Damus, and others
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
 *   EclipseSource - Bug 536641
 *   Christian W. Damus - bug 536486
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - replaced default editParts with customized versions for enhancement
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.providers;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.editpart.SilentEditpart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCCombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneInteractionOperandGuardEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifeLineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneLifelineNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneMessageNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneMessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneSequenceDiagramEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CDestructionOccurrenceSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ConsiderIgnoreFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.Constraint2EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ConstraintEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomConstraint2EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomConstraintEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomDurationConstraintLinkEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomDurationObservationLinkEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomGeneralOrderingEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName2EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName3EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName4EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName5EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName6EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomMessageName7EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomStateInvariantEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomStateInvariantLabelEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomTimeConstraintBorderNodeEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CustomTimeObservationBorderNodeEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.DestructionOccurrenceSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.DurationConstraintLinkEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.DurationObservationLinkEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.GeneralOrderingEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandGuardEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageAsyncNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageCreateNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageDeleteNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageFoundNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageLostNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageReplyNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageSyncEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.MessageSyncNameEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.SequenceDiagramEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.StateInvariantEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.StateInvariantLabelEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.TimeConstraintBorderNodeEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.TimeObservationBorderNodeEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling.GrillingEditpart;

/**
 * Enhances Papyrus's EditPart provision by selectively replacing default EditParts with
 * customized versions for enhanced functionality. This provider selectively overrides
 * specific EditParts for sequence diagrams, such as AdoneInteractionOperandGuardEditPart
 * and AdoneSequenceDiagramEditPart, among others, to introduce custom behaviors and
 * visual enhancements. It serves as a central point for customizing diagram
 * components in the Adone project, ensuring that enhancements are consistently
 * applied across different diagram elements.
 */
public class AdoneCustomEditPartProvider extends UMLEditPartProvider {

	@Override
	protected IGraphicalEditPart createEditPart(View view) {
		IGraphicalEditPart customEditPart = createCustomEditPart(view);
		if (customEditPart != null) {
			return customEditPart;
		}
		IGraphicalEditPart graphicalEditPart = super.createEditPart(view);
		if (graphicalEditPart == null) {
			return new SilentEditpart(view);
		}
		return graphicalEditPart;
	}

	protected IGraphicalEditPart createCustomEditPart(View view) {

		if (InteractionOperandGuardEditPart.GUARD_TYPE.equals(view.getType())) {
			return new AdoneInteractionOperandGuardEditPart(view);
		}

		switch (UMLVisualIDRegistry.getVisualID(view)) {
		case SequenceDiagramEditPart.VISUAL_ID:
			return new AdoneSequenceDiagramEditPart(view);
		case InteractionEditPart.VISUAL_ID:
			return new AdoneInteractionEditPart(view);
		case GrillingEditpart.VISUAL_ID:
			return new GrillingEditpart(view);
		case ConsiderIgnoreFragmentEditPart.VISUAL_ID:
			return new AdoneCombinedFragmentEditPart(view);
		case CombinedFragmentEditPart.VISUAL_ID:
			return new AdoneCombinedFragmentEditPart(view);
		case BehaviorExecutionSpecificationEditPart.VISUAL_ID:
			return new AdoneBehaviorExecutionSpecificationEditPart(view);
		case CombinedFragmentCombinedFragmentCompartmentEditPart.VISUAL_ID:
			return new AdoneCCombinedFragmentCombinedFragmentCompartmentEditPart(view);
		case InteractionOperandEditPart.VISUAL_ID:
			return new AdoneInteractionOperandEditPart(view);
		case InteractionInteractionCompartmentEditPart.VISUAL_ID:
			return new AdoneInteractionInteractionCompartmentEditPart(view);
		case LifelineEditPart.VISUAL_ID:
			return new AdoneLifeLineEditPart(view);
		case LifelineNameEditPart.VISUAL_ID:
			return new AdoneLifelineNameEditPart(view);
		case StateInvariantEditPart.VISUAL_ID:
			return new CustomStateInvariantEditPart(view);
		case ConstraintEditPart.VISUAL_ID:
			return new CustomConstraintEditPart(view);
		case Constraint2EditPart.VISUAL_ID:
			return new CustomConstraint2EditPart(view);
		case TimeConstraintBorderNodeEditPart.VISUAL_ID:
			return new CustomTimeConstraintBorderNodeEditPart(view);
		case TimeObservationBorderNodeEditPart.VISUAL_ID:
			return new CustomTimeObservationBorderNodeEditPart(view);
		case DestructionOccurrenceSpecificationEditPart.VISUAL_ID:
			return new CDestructionOccurrenceSpecificationEditPart(view);
		case MessageSyncEditPart.VISUAL_ID:
			return new AdoneMessageSyncEditPart(view);
		case MessageSyncNameEditPart.VISUAL_ID:
			return new AdoneMessageNameEditPart(view);
		case MessageAsyncNameEditPart.VISUAL_ID:
			return new CustomMessageName2EditPart(view);
		case MessageReplyNameEditPart.VISUAL_ID:
			return new CustomMessageName3EditPart(view);
		case MessageCreateNameEditPart.VISUAL_ID:
			return new CustomMessageName4EditPart(view);
		case MessageDeleteNameEditPart.VISUAL_ID:
			return new CustomMessageName5EditPart(view);
		case MessageLostNameEditPart.VISUAL_ID:
			return new CustomMessageName6EditPart(view);
		case MessageFoundNameEditPart.VISUAL_ID:
			return new CustomMessageName7EditPart(view);
		case GeneralOrderingEditPart.VISUAL_ID:
			return new CustomGeneralOrderingEditPart(view);
		case StateInvariantLabelEditPart.VISUAL_ID:
			return new CustomStateInvariantLabelEditPart(view);
		case DurationConstraintLinkEditPart.VISUAL_ID:
			return new CustomDurationConstraintLinkEditPart(view);
		case DurationObservationLinkEditPart.VISUAL_ID:
			return new CustomDurationObservationLinkEditPart(view);
		}
		return null;
	}
}
