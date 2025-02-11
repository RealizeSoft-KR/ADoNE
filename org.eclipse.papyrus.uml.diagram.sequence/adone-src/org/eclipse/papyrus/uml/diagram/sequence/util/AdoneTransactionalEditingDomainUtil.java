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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.core.utils.ServiceUtils;
import org.eclipse.papyrus.uml.tools.model.UmlUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Provides utility methods for obtaining a TransactionalEditingDomain in different contexts.
 * This class simplifies the process of acquiring an editing domain for an EObject or the active editor,
 * facilitating operations that require transactional editing support, such as model modifications within an EMF-based application.
 */
public class AdoneTransactionalEditingDomainUtil {

	/**
	 * Retrieves the TransactionalEditingDomain associated with a given EObject.
	 *
	 * @param eObj
	 *            The EObject for which the editing domain is sought.
	 * @return The TransactionalEditingDomain associated with the EObject, or null if none found.
	 */
	public static TransactionalEditingDomain getEditingDomain(EObject eObj) {
		TransactionalEditingDomain ted = null;
		try {
			if (eObj != null) {
				ted = TransactionUtil.getEditingDomain(eObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ted;
	}

	/**
	 * Retrieves the TransactionalEditingDomain from the currently active editor or the primary UML model if no editor is active.
	 *
	 * @return The current TransactionalEditingDomain, or null if it cannot be determined.
	 */
	public static TransactionalEditingDomain getEditingDomain() {
		TransactionalEditingDomain ted = null;
		try {
			IEditorPart activeEditor = getActiveEditor();
			if (activeEditor != null) {
				ServicesRegistry registry = activeEditor.getAdapter(ServicesRegistry.class);
				ted = ServiceUtils.getInstance().getTransactionalEditingDomain(registry);
			} else if (UmlUtils.getUmlModel() != null) {
				EObject model = UmlUtils.getUmlModel().lookupRoot();
				ted = TransactionUtil.getEditingDomain(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ted;
	}

	/**
	 * Returns the currently active editor within the workbench.
	 *
	 * @return The active IEditorPart, or null if none is active.
	 */
	private static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().getActiveEditor();
	}

}

