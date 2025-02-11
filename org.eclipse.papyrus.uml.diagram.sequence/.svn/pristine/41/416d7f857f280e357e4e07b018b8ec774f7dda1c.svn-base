/*****************************************************************************
 * Copyright (c) 2017, 2018 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Patrick Tessier (CEA LIST) - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 521312, 526079, 526191
 *   Vincent LORENZO (CEA LIST) vincent.lorenzo@cea.fr - Bug 531520
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Scheduled for deletion after further review
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.List;

import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.OccurrenceSpecification;

/**
 * This class, extending Papyrus's SequenceReferenceEditPolicy, is currently under evaluation for its necessity within the overall functionality.
 * It has been prepared with the intention to observe its usage and impact more closely before deciding on its potential removal.
 * The policy includes advanced handling for execution specifications associated with events, but its complete set of features
 * has been deemed possibly superfluous. Future observations will determine whether this extension provides essential benefits
 * or if it can be deprecated to streamline the model editing policies.
 */
public class AdoneSequenceReferenceEditPolicy extends SequenceReferenceEditPolicy {

	@Override
	protected ExecutionSpecification getExecutionSpecificationAssociatedToEvent(OccurrenceSpecification event) {
		ExecutionSpecification exec = null;
		if (null != event && !event.getCovereds().isEmpty()) {
			Lifeline currentLifeline = event.getCovereds().get(0);
			int index = 0;
			while (exec == null && index < currentLifeline.getCoveredBys().size()) {
				if (currentLifeline.getCoveredBys().get(index) instanceof ExecutionSpecification) {
					ExecutionSpecification currentExec = (ExecutionSpecification) currentLifeline.getCoveredBys().get(index);

					// The following code is for an impossible case. The start of a message should be located in the middle of BES.
					// Need to modify the code to find a BES that satisfies this condition (2023-11-24)
					if (event.equals(currentExec.getStart())) {
						exec = currentExec;
					}

					// Finding if the message start point is located in the middle of an ExecutionSpecification
					if (isEventBetweenStartAndFinishOfEs(event, currentExec)) {
						exec = currentExec;
					}

					if (event.equals(currentExec.getFinish())) {
						exec = currentExec;
					}
				}
				index++;
			}
		}
		return exec;
	}

	/**
	 * @param messageStartEvent
	 * @param currentExec
	 * @return
	 */
	private boolean isEventBetweenStartAndFinishOfEs(OccurrenceSpecification messageStartEvent, ExecutionSpecification currentExec) {
		// Retrieves the start and end OccurrenceSpecifications of the ExecutionSpecification.
		OccurrenceSpecification start = currentExec.getStart();
		OccurrenceSpecification finish = currentExec.getFinish();

		// Gets the list of events on the Lifeline.
		List<InteractionFragment> eventsOnLifeline = messageStartEvent.getCovereds().get(0).getCoveredBys();

		// Checks if the current event is between the start and finish events.
		int startIndex = eventsOnLifeline.indexOf(start);
		int finishIndex = eventsOnLifeline.indexOf(finish);
		int eventIndex = eventsOnLifeline.indexOf(messageStartEvent);

		// Confirms if the event is located between the start and finish.
		return eventIndex > startIndex && eventIndex < finishIndex;
	}

	@Override
	protected void fillStrongReferencesOfExecutionSpecification(final ExecutionSpecification exec) {

		// The message end of start and/or finish of the execution specification are managed as strong references
		if (exec.getStart() instanceof MessageEnd) {
			MessageEnd messageEnd = (MessageEnd) exec.getStart();
			addMessageIntoReferences(messageEnd, strongReferences, ROLE_START);
		}
		if (exec.getFinish() instanceof MessageEnd) {
			MessageEnd messageEnd = (MessageEnd) exec.getFinish();
			addMessageIntoReferences(messageEnd, strongReferences, ROLE_FINISH);
		}

		// It seems to search for BES mounted on BES, but there's an error finding the next consecutive BES because the end event of BES is created before the main BES. Temporarily need to comment out the logic (2023-11-27)
		// If an execution specification is defined between the start and the finish of the current execution specification, it will be defined as strong reference
		/*
		 * if (exec.getCovereds().size() >= 1) {
		 * final Lifeline currentLifeline = exec.getCovereds().get(0);
		 * int index = currentLifeline.getCoveredBys().indexOf(exec);
		 * Element nextEvent = null;
		 * OccurrenceSpecification foundSubExecSpec = null;
		 * if (index != -1) {
		 * // we look for the next event
		 * index = index + 1;
		 * while ((nextEvent != exec.getFinish() || foundSubExecSpec == null) && (index < currentLifeline.getCoveredBys().size())) {
		 * nextEvent = currentLifeline.getCoveredBys().get(index);
		 * if (nextEvent instanceof OccurrenceSpecification) {
		 * ExecutionSpecification subExecutionSpec = getExecutionSpecificationAssociatedToEvent((OccurrenceSpecification) nextEvent);
		 * if (null != subExecutionSpec && null != subExecutionSpec.getStart() && !subExecutionSpec.equals(exec) && subExecutionSpec.getStart().equals(nextEvent)) {
		 * foundSubExecSpec = (OccurrenceSpecification) nextEvent;
		 * addExecutionSpecIntoReferences(foundSubExecSpec, strongReferences, ROLE_START);
		 * }
		 * }
		 * index++;
		 * }
		 * }
		 * }
		 */
	}

}
