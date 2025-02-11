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

import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

/**
 * Provides utility methods for working with interactions in UML diagrams, specifically focusing on extracting information
 * related to lifelines and message relationships. This class aids in determining the sender and receiver lifelines of messages
 * and identifying whether a message is recursive (i.e., the sender and receiver lifelines are the same).
 */
public class AdoneInteractionHelper {

	/**
	 * Retrieves the lifeline that receives a given message.
	 *
	 * @param message
	 *            The message for which the receiver lifeline is to be determined.
	 * @return The receiving lifeline of the message, or null if the message or its receive event is not defined.
	 */
	public static Lifeline getReceiverLifeline(Message message) {

		if (message == null) {
			return null;
		}

		if (message.getReceiveEvent() == null) {
			return null;
		}

		MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) message.getReceiveEvent();

		if (spec == null) {
			return null;
		}

		if (spec.getCovered(null) == null) {
			return null;
		}

		return spec.getCovered(null);

	}

	/**
	 * Retrieves the lifeline that sends a given message.
	 *
	 * @param message
	 *            The message for which the sender lifeline is to be determined.
	 * @return The sending lifeline of the message, or null if the message or its send event is not defined.
	 */
	public static Lifeline getSenderLifeline(Message message) {

		if (message == null) {
			return null;
		}

		if (message.getSendEvent() == null) {
			return null;
		}

		MessageOccurrenceSpecification spec = (MessageOccurrenceSpecification) message.getSendEvent();

		if (spec == null) {
			return null;
		}

		if (spec.getCovered(null) == null) {
			return null;
		}

		return spec.getCovered(null);

	}

	/**
	 * Determines if a message is recursive, meaning the sender and receiver are the same lifeline.
	 *
	 * @param message
	 *            The message to check for recursiveness.
	 * @return True if the message is recursive; otherwise, false.
	 */
	public static boolean isRecursiveMessage(Message message) {


		if (getSenderLifeline(message) == null
				|| getReceiverLifeline(message) == null) {
			return false;
		}

		if (getSenderLifeline(message).equals(getReceiverLifeline(message))) {
			return true;
		}

		return false;


	}

}
