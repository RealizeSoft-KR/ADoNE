/*****************************************************************************
 * Copyright (c) 2009, 2014 Atos Origin, CEA, and others.
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
 *   Atos Origin - Initial API and implementation
 *   Christian W. Damus (CEA) - bug 392301
 *   Nicolas FAUVERGUE (ALL4TEC) nicolas.fauvergue@all4tec.net - Bug 496905
 *
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence;

import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.core.editor.BackboneException;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;

/**
 * Editor used in multitabs editor.
 */
public class AdoneUmlSequenceDiagramForMultiEditor extends UmlSequenceDiagramForMultiEditor {


	/**
	 * Constructor.
	 *
	 */
	public AdoneUmlSequenceDiagramForMultiEditor(ServicesRegistry servicesRegistry, Diagram diagram) throws BackboneException, ServiceException {
		super(servicesRegistry, diagram);
	}

	/**
	 *
	 */

	@Override
	public void setFocus() {

		// Model Explorer 상 Set Focus 시 Exception 발생
		try {
			super.setFocus();
		} catch (Exception e) {
			// e.printStackTrace();
		}

	}


}
