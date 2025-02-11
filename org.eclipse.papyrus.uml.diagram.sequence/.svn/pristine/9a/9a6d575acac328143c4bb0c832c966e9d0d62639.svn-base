/*****************************************************************************
 * Copyright (c) 2017 CEA LIST and others.
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
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - enhances BES image position for recursive calls
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ExecutionSpecificationRectangleFigure;

/**
 * This class extends ExecutionSpecificationRectangleFigure to adjust the image position of
 * the BehaviorExecutionSpecificationEditPart for recursive calls slightly to the right.
 * It introduces a 'host' field to reference the AdoneBehaviorExecutionSpecificationEditPart,
 * facilitating the required positional adjustments.
 */
public class AdoneExecutionSpecificationRectangleFigure extends ExecutionSpecificationRectangleFigure {

	AdoneBehaviorExecutionSpecificationEditPart host;

	public AdoneExecutionSpecificationRectangleFigure(AdoneBehaviorExecutionSpecificationEditPart host) {
		super();
		this.host = host;
	}

	public AdoneBehaviorExecutionSpecificationEditPart getHost() {
		return host;
	}

}