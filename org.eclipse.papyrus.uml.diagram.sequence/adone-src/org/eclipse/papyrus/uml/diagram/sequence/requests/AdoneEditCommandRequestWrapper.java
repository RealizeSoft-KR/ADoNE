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
package org.eclipse.papyrus.uml.diagram.sequence.requests;

import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;

/**
 * A wrapper class for edit command requests, specifically designed to prevent recursive
 * invocation of child CombinedFragments deletion. It acts as a safeguard by wrapping the
 * original edit command request with a specific request type, thereby ensuring that deletion
 * operations on CombinedFragments do not unintentionally trigger recursive deletions of their
 * nested CombinedFragments.
 */
public class AdoneEditCommandRequestWrapper extends EditCommandRequestWrapper {

	public AdoneEditCommandRequestWrapper(IEditCommandRequest editCommandRequest) {
		super(RequestConstants.REQ_SEMANTIC_WRAPPER, editCommandRequest, null);
	}

}
