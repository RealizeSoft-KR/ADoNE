/******************************************************************************
 * Copyright (c) 2002, 2008 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - For recursive call messages, add a default RelativeBendpoint.
 ****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneInteractionHelper;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Message;

/**
 * A command to set bendpoints for a connection, with special handling for recursive call messages.
 * It adjusts the connection's bendpoints based on the source and target reference points or
 * adds default RelativeBendpoints for recursive messages to visually represent the message flow.
 */
public class AdoneSetConnectionBendpointsCommand extends AbstractTransactionalCommand {

	private IAdaptable edgeAdapter;
	private PointList newPointList;
	private Point sourceRefPoint;
	private Point targetRefPoint;

	/**
	 * @param editingDomain
	 *            the editing domain through which model changes are made
	 * @see java.lang.Object#Object()
	 */
	public AdoneSetConnectionBendpointsCommand(TransactionalEditingDomain editingDomain) {
		super(editingDomain, DiagramUIMessages.Commands_SetBendpointsCommand_Label, null);
	}

	@Override
	public List getAffectedFiles() {
		View view = edgeAdapter.getAdapter(View.class);
		if (view != null) {
			return getWorkspaceFiles(view);
		}
		return super.getAffectedFiles();
	}

	/**
	 * Returns the edgeAdaptor.
	 *
	 * @return IAdaptable
	 */
	public IAdaptable getEdgeAdaptor() {
		return edgeAdapter;
	}

	/**
	 * Returns the targetRefPoint.
	 *
	 * @return Point
	 */
	public Point getTargetRefPoint() {
		return targetRefPoint;
	}

	/**
	 * Returns the newPointList.
	 *
	 * @return PointList
	 */
	public PointList getNewPointList() {
		return newPointList;
	}

	/**
	 * Returns the sourceRefPoint.
	 *
	 * @return Point
	 */
	public Point getSourceRefPoint() {
		return sourceRefPoint;
	}

	/**
	 * Sets the edgeAdaptor.
	 *
	 * @param edgeAdapter
	 *            The edgeAdaptor to set
	 */
	public void setEdgeAdapter(IAdaptable edgeAdapter) {
		this.edgeAdapter = edgeAdapter;
	}

	/**
	 * Method setNewPointList.
	 *
	 * @param newPointList
	 * @param sourceRefPoint
	 * @param targetRefPoint
	 */
	public void setNewPointList(
			PointList newPointList,
			Point sourceRefPoint,
			Point targetRefPoint) {
		this.newPointList = new PointList(newPointList.size());
		for (int i = 0; i < newPointList.size(); i++) {
			this.newPointList.addPoint(newPointList.getPoint(i));
		}
		this.sourceRefPoint = sourceRefPoint;
		this.targetRefPoint = targetRefPoint;
	}

	/**
	 * set a new point list
	 *
	 * @param newPointList
	 *            the new point list to set
	 * @param sourceAnchor
	 * @param targetAnchor
	 */
	public void setNewPointList(
			PointList newPointList,
			ConnectionAnchor sourceAnchor,
			ConnectionAnchor targetAnchor) {

		this.newPointList = new PointList(newPointList.size());
		for (int i = 0; i < newPointList.size(); i++) {
			this.newPointList.addPoint(newPointList.getPoint(i));
		}
		if (sourceAnchor != null) {
			sourceRefPoint = sourceAnchor.getReferencePoint();
			sourceAnchor.getOwner().translateToRelative(sourceRefPoint);
		}
		if (targetAnchor != null) {
			targetRefPoint = targetAnchor.getReferencePoint();
			targetAnchor.getOwner().translateToRelative(
					targetRefPoint);
		}
	}

	@Override
	protected CommandResult doExecuteWithResult(
			IProgressMonitor progressMonitor, IAdaptable info)
			throws ExecutionException {

		Assert.isNotNull(newPointList);
		Assert.isNotNull(sourceRefPoint);
		Assert.isNotNull(targetRefPoint);

		Edge edge = getEdgeAdaptor().getAdapter(Edge.class);
		Assert.isNotNull(edge);

		Element msg = (Element) edge.getElement();

		boolean isRecursive = AdoneInteractionHelper.isRecursiveMessage((Message) msg);

		if (isRecursive) {

			// Special handling for recursive messages to visually indicate the loopback.
			// Sets default RelativeBendpoints for a visually consistent representation.

			// A-------B
			// ........|
			// D-------C
			// 시작점 A, 종료점 D, A~B 사이 거리 : 50, A~D 사이 거리 : 20

			// 첫번째(A) 포인트 : 시작점에서 상대적으로 그대로 유지하고, 종료점으로 부터 상대적으로 Y 만 -20
			// 두번째(B) 포인트 : 시작점에서 상대적으로 X가 50 만큼 증가하고, 종료점으로 부터 상대적으로 X가 50 증가하고, Y 도 -20 인점
			// 세번째(C) 포인트 : 시작점에서 상대적으로 X가 50 증가하고 Y 도 20 증가, 종료점으로부터 X 만 50 증가
			// 네번째(D) 포인트 : 시작점에서 상대적으로 Y만 20 증가하고, 종료점에서 상대적으로 그래도 유지

			// 시작점과 종료점 제거 -> 다이어그램 생성 시 오류 발생 안함. 하지만 메시지 X 값 반영 안됨 (2024-02-04)
			ArrayList<RelativeBendpoint> bendpoints = new ArrayList<>(4);
			// RelativeBendpoint point1 = new RelativeBendpoint(0, 0, 0, -20);
			RelativeBendpoint point2 = new RelativeBendpoint(50, 0, 50, -20);
			RelativeBendpoint point3 = new RelativeBendpoint(50, 20, 50, 0);
			// RelativeBendpoint point4 = new RelativeBendpoint(0, 20, 0, 0);

			// bendpoints.add(point1);
			bendpoints.add(point2);
			bendpoints.add(point3);
			// bendpoints.add(point4);

			RelativeBendpoints points = (RelativeBendpoints) edge.getBendpoints();
			points.setPoints(bendpoints);

		} else {

			// Standard handling for non-recursive messages.
			// Translates the newPointList into RelativeBendpoints based on the source and target reference points.

			List<RelativeBendpoint> newBendpoints = new ArrayList<>();
			int numOfPoints = newPointList.size();

			for (short i = 0; i < numOfPoints; i++) {
				Dimension s = newPointList.getPoint(i).getDifference(sourceRefPoint);
				Dimension t = newPointList.getPoint(i).getDifference(targetRefPoint);
				newBendpoints.add(new RelativeBendpoint(s.width, s.height, t.width, t.height));
			}

			RelativeBendpoints points = (RelativeBendpoints) edge.getBendpoints();
			points.setPoints(newBendpoints);
		}

		return CommandResult.newOKCommandResult();
	}


}
