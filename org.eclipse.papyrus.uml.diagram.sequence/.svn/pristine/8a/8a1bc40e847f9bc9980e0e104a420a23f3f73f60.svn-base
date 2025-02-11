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
 *   RealizeSoft - Original development and implementation.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneCombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.uml2.uml.Lifeline;

public class AdoneAdjustZOrderCommand extends AbstractTransactionalCommand {

	private EditPart host;

	/**
	 * @param editingDomain
	 *            the editing domain through which model changes are made
	 * @see java.lang.Object#Object()
	 */
	public AdoneAdjustZOrderCommand(TransactionalEditingDomain editingDomain, EditPart host) {
		super(editingDomain, DiagramUIMessages.Commands_SetBendpointsCommand_Label, null);
		this.host = host;
	}

	@Override
	protected CommandResult doExecuteWithResult(
			IProgressMonitor progressMonitor, IAdaptable info)
			throws ExecutionException {

		// hostEditPart로부터 EditPartViewer 객체를 가져옴
		EditPartViewer viewer = this.host.getRoot().getViewer();
		// EditPartViewer에서 EditPartRegistry를 가져옴
		Map<?, ?> editPartRegistry = viewer.getEditPartRegistry();

		// 처리할 EditPart를 저장할 리스트를 생성
		List<GraphicalEditPart> relevantEditParts = new ArrayList<>();
		// EditPartRegistry를 순회하며 필요한 유형의 EditPart를 필터링하여 리스트에 추가
		for (Object value : editPartRegistry.values()) {
			if (value instanceof AbstractMessageEditPart || value instanceof CombinedFragmentEditPart || value instanceof InteractionOperandEditPart) {
				relevantEditParts.add((GraphicalEditPart) value);
			}
		}

		// Y 좌표에 따라 필터링된 EditPart들을 정렬
		Collections.sort(relevantEditParts, new Comparator<GraphicalEditPart>() {

			@Override
			public int compare(GraphicalEditPart ep1, GraphicalEditPart ep2) {
				int y1 = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ep1).y;
				int y2 = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(ep2).y;
				return Integer.compare(y1, y2);
			}

		});

		try {

			buildEditPartZOrderStructure(relevantEditParts);

		} catch (Exception e) {
			e.printStackTrace();
		}


		return CommandResult.newOKCommandResult();
	}

	private void buildEditPartZOrderStructure(List<GraphicalEditPart> sortedEditParts) {
		// OLDLifelineXYLayoutEditPolicy.createZOrderCommand()
		// this.view = view;
		// this.index = index;
		// containerView = ViewUtil.getContainerView(view);
		// ViewUtil.repositionChildAt(containerView, view, index);

		// List children = containerView.getChildren();
		// int oldIndex = children.indexOf(toMove);
		// if (oldIndex < children.size()-1)
		// ViewUtil.repositionChildAt(containerView,toMove, oldIndex + 1 );

		// 중첩 구조를 추적하기 위한 스택
		Stack<GraphicalEditPart> fragmentStack = new Stack<>();

		// Z-Order 의 한계로 모든 라이프라인을 CF 앞에 강제로 위치시킴
		this.adjustLifelinePositions(sortedEditParts);

		// CF 들이 중첩 구조일 경우 내포된 CF 들의 위치를 포함하고 있는 CF 앞에 오도록 조정
		for (GraphicalEditPart editPart : sortedEditParts) {

			Rectangle editPartBounds = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp(editPart);

			while (!fragmentStack.isEmpty()) {
				EditPart parentEp = fragmentStack.peek();
				Rectangle parentBound = AdoneSequenceUtil.getAbsoluteBoundsForMessageEp((GraphicalEditPart) parentEp);
				int parentScope = parentBound.y + parentBound.height;
				// 중첩 가능성 때문에 +1 처리
				if (editPartBounds.y + 1 > parentScope) {
					fragmentStack.pop();
				} else {
					break;
				}
			}

			if (editPart instanceof CombinedFragmentEditPart) {

				AdoneCombinedFragmentEditPart cfEp = (AdoneCombinedFragmentEditPart) editPart;

				View cfView = cfEp.getNotationView();
				View containerView = ViewUtil.getContainerView(cfView);

				if (fragmentStack.isEmpty()) {
					// do nothing
				} else {
					InteractionOperandEditPart operandEp = (InteractionOperandEditPart) fragmentStack.peek();
					CombinedFragmentEditPart parentCf = (CombinedFragmentEditPart) operandEp.getParent().getParent();
					View parentCfView = parentCf.getNotationView();

					List children = containerView.getChildren();
					int oldIndex = children.indexOf(parentCfView);
					if (oldIndex < children.size() - 1) {
						ViewUtil.repositionChildAt(containerView, cfView, oldIndex + 1);
					}
				}

				fragmentStack.push(cfEp);

			} else if (editPart instanceof InteractionOperandEditPart) {
				InteractionOperandEditPart ioEditPart = (InteractionOperandEditPart) editPart;
				fragmentStack.push(ioEditPart);
			} else if (editPart instanceof AbstractMessageEditPart) {
				// do nothing
			}
		}


	}

	private void adjustLifelinePositions(List<GraphicalEditPart> sortedEditParts) {
		int latestCfIndex = -1;
		View containerView = null;

		// 가장 마지막 CombinedFragment의 인덱스 찾기
		for (GraphicalEditPart part : sortedEditParts) {
			if (part instanceof CombinedFragmentEditPart) {

				CombinedFragmentEditPart cfPart = (CombinedFragmentEditPart) part;

				containerView = ViewUtil.getContainerView(cfPart.getNotationView());
				int index = containerView.getChildren().indexOf(cfPart.getNotationView());
				if (index > latestCfIndex) {
					latestCfIndex = index;
				}
			}
		}

		// 모든 Lifeline View들을 가장 마지막 CombinedFragment 뒤로 이동
		if (latestCfIndex != -1 && containerView != null) {
			List<View> lifelineViewsToMove = new ArrayList<>();
			for (Object child : containerView.getChildren()) {
				if (child instanceof View) {
					View childView = (View) child;
					if (childView.getElement() instanceof Lifeline) {
						lifelineViewsToMove.add(childView);
					}
				}
			}

			int targetIndex = latestCfIndex + 1;
			for (View lifelineView : lifelineViewsToMove) {
				if (targetIndex < containerView.getChildren().size()) {
					ViewUtil.repositionChildAt(containerView, lifelineView, targetIndex);
				} else {
					// 대상 인덱스가 자식 뷰 개수를 초과하는 경우, 원래 위치에서 삭제 후 리스트 끝에 추가
					containerView.removeChild(lifelineView);
					containerView.insertChild(lifelineView);
				}
			}
		}
	}

}
