/*****************************************************************************
 * Copyright (c) 2019 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;

/**
 * Manages the status of CombinedFragmentEditParts within a diagram, tracking whether each fragment is in move or resize mode.
 * This singleton class provides a centralized way to manage and query the operational state of CombinedFragmentEditParts,
 * enabling other components of the system to adjust their behavior based on the current mode of a specific fragment.
 *
 * Usage includes registering fragments into move or resize mode and checking the current mode of a fragment. This approach
 * ensures that actions on CombinedFragmentEditParts, like moving or resizing, can be handled consistently across the application.
 */
public class AdoneCombinedFragmentEpStatusManager {

	private static AdoneCombinedFragmentEpStatusManager manager = null;

	private Map<CombinedFragmentEditPart, String> registry = new HashMap<>();

	public static AdoneCombinedFragmentEpStatusManager getInstance() {
		if (manager == null) {
			manager = new AdoneCombinedFragmentEpStatusManager();
		}

		return manager;
	}

	public void initialize() {
		registry.clear();
	}

	public void registerMoveMode(CombinedFragmentEditPart ep) {
		registry.put(ep, "move");
	}

	public void registerResizeMode(CombinedFragmentEditPart ep) {
		registry.put(ep, "resize");
	}

	public boolean isRegistered(CombinedFragmentEditPart ep) {

		if (registry.containsKey(ep)) {
			return true;
		}

		return false;
	}

}
