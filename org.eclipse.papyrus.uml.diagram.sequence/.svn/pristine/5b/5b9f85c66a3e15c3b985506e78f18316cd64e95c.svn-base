/*****************************************************************************
 * Copyright (c) 2010, 2017 CEA LIST, ALL4TEC and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA List - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 522305
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - customizes by resizing arrows and redefining connection anchors.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.RotatableDecoration;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AdoneConnectionSourceAnchor;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AdoneConnectionTargetAnchor;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AdoneSolidArrowFilledConnectionDecoration;
import org.eclipse.papyrus.uml.diagram.sequence.anchors.AnchorConstants;

/**
 * This class is designed for overriding functionalities of synchronous messages within sequence diagrams,
 * specifically aiming to resize the arrow size of synchronous messages. The modification is intended to
 * enhance the overall visibility and clarity of diagrams. It redefines connection anchors and employs a
 * custom arrow decoration, adjusting the presentation of synchronous message connections to better align
 * with diagrammatic representation needs.
 */
public class AdoneMessageSync extends MessageSync {

	@Override
	public ConnectionAnchor getConnectionAnchor(String terminal) {
		if (AnchorConstants.START_TERMINAL.equals(terminal)) {
			return new AdoneConnectionSourceAnchor(this);
		} else if (AnchorConstants.END_TERMINAL.equals(terminal)) {
			return new AdoneConnectionTargetAnchor(this);
		}
		return super.getConnectionAnchor(terminal);
	}

	@Override
	protected RotatableDecoration createTargetDecoration() {
		RotatableDecoration decoration = new AdoneSolidArrowFilledConnectionDecoration();
		return decoration;
	}

}
