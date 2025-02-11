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

import java.util.List;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;

/**
 * 추후 확인 후 삭제 요망 (2024-02-08)
 */
public class AdoneChangeElementOrderRequest extends Request {

	public static final String REQ_CHANGE_ELEMENT_ORDER = "ChangeElementOrder";

	protected List<EditPart> sourceEditPartList;
	protected IDiagramEditDomain diagramEditDomaim;
	protected TransactionalEditingDomain transactionalEditingDomain;

	public AdoneChangeElementOrderRequest(List<EditPart> sourceEditPartList, IDiagramEditDomain diagramEditDomaim, TransactionalEditingDomain transactionalEditingDomain) {
		this.sourceEditPartList = sourceEditPartList;
		this.diagramEditDomaim = diagramEditDomaim;
		this.transactionalEditingDomain = transactionalEditingDomain;
	}

	public List<EditPart> getSourceEditPartList() {
		return sourceEditPartList;
	}

	public void setSourceEditPartList(List<EditPart> targetEditPartList) {
		this.sourceEditPartList = targetEditPartList;
	}

	public IDiagramEditDomain getDiagramEditDomaim() {
		return diagramEditDomaim;
	}

	public void setDiagramEditDomaim(IDiagramEditDomain diagramEditDomaim) {
		this.diagramEditDomaim = diagramEditDomaim;
	}

	public TransactionalEditingDomain getTransactionalEditingDomain() {
		return transactionalEditingDomain;
	}

	public void setTransactionalEditingDomain(TransactionalEditingDomain transactionalEditingDomain) {
		this.transactionalEditingDomain = transactionalEditingDomain;
	}

	@Override
	public Object getType() {
		return REQ_CHANGE_ELEMENT_ORDER;
	}
}
