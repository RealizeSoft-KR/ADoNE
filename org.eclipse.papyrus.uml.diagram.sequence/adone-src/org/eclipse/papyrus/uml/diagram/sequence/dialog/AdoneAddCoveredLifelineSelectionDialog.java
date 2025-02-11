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
package org.eclipse.papyrus.uml.diagram.sequence.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Lifeline;

/**
 * A dialog for selecting lifelines to add or remove as covered by a Combined Fragment in a UML sequence diagram.
 * It allows users to visually choose which lifelines should be included or excluded from the coverage of a Combined Fragment.
 */
public class AdoneAddCoveredLifelineSelectionDialog extends Dialog {

	// Lifelines initially covered by the Combined Fragment.
	private List<Lifeline> existingLifelines;

	// Lifelines to consider for adding or removing.
	private List<Lifeline> changedLifelines;

	// Lifelines chosen to be removed from coverage.
	private List<Lifeline> removedLifelines;

	// Lifelines chosen to be added to coverage.
	private List<Lifeline> addedLifelines;

	// Initial location for the dialog, based on user interaction.
	private Point msgLocation;

	// Table for displaying lifeline selection options.
	private Table changedCoveredLiflineTable;

	/**
	 * Constructs a dialog with the given shell as its parent, the existing and changed covered lifelines, and the message location.
	 *
	 * @param parent
	 *            The parent shell
	 * @param existingCoveredLifelines
	 *            Lifelines initially covered by the Combined Fragment
	 * @param changedCoveredLifelines
	 *            Lifelines to consider for adding or removing
	 * @param msgLocation
	 *            The location to display the dialog, usually where the user initiated the action
	 */
	public AdoneAddCoveredLifelineSelectionDialog(Shell parent, List<Lifeline> existingCoveredLifelines, List<Lifeline> changedCoveredLifelines, Point msgLocation) {
		super(parent);
		this.existingLifelines = existingCoveredLifelines;
		this.changedLifelines = changedCoveredLifelines;
		this.msgLocation = msgLocation;
		this.removedLifelines = new ArrayList<>();
		this.addedLifelines = new ArrayList<>();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		Label lblSelectCoveredLifelines = new Label(container, SWT.NONE);
		lblSelectCoveredLifelines.setText("Add/Remove Covered Lifelines");
		new Label(container, SWT.NONE);

		CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER | SWT.FULL_SELECTION);
		changedCoveredLiflineTable = checkboxTableViewer.getTable();
		changedCoveredLiflineTable.setLinesVisible(true);
		changedCoveredLiflineTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		for (Lifeline lifeline : changedLifelines) {
			TableItem item = new TableItem(changedCoveredLiflineTable, SWT.NONE);
			updateItemTextAndState(item, lifeline);
		}

		// 체크박스 변경 시 이벤트 리스너 추가
		checkboxTableViewer.addCheckStateListener(event -> {
			Object element = event.getElement();
			if (element instanceof Lifeline) {
				TableItem item = checkboxTableViewer.getTable().getItem(changedLifelines.indexOf(element));
				updateItemTextAndState(item, (Lifeline) element);
			}
		});

		return container;
	}

	private void updateItemTextAndState(TableItem item, Lifeline lifeline) {
		boolean isInExistingLifelines = existingLifelines.contains(lifeline);
		item.setChecked(!isInExistingLifelines);
		String prefix = item.getChecked() ? "[Add] " : "[Remove] ";
		String lifelineName = lifeline.getRepresents().getType() != null ? lifeline.getRepresents().getType().getName() : lifeline.getName();
		item.setText(prefix + lifelineName);
		item.setData(lifeline);
	}


	@Override
	protected void configureShell(Shell newShell) {
		newShell.setModified(true);
		super.configureShell(newShell);
		newShell.setText("Covered Lifeline Option");
	}

	@Override
	protected Point getInitialSize() {
		Point result = super.getInitialSize();
		if ((getDialogBoundsStrategy() & DIALOG_PERSISTSIZE) != 0) {
			try {
				int width = 350;
				if (width != DIALOG_DEFAULT_BOUNDS) {
					result.x = width;
				}
				int height = 300;
				if (height != DIALOG_DEFAULT_BOUNDS) {
					result.y = height;
				}

			} catch (NumberFormatException e) {
			}

		}
		return result;
	}

	@Override
	protected void okPressed() {
		updateLifelinesState();
		super.okPressed();
	}

	private void updateLifelinesState() {
		TableItem[] items = changedCoveredLiflineTable.getItems();
		for (TableItem item : items) {
			Lifeline lifeline = (Lifeline) item.getData();
			if (item.getChecked()) {
				addedLifelines.add(lifeline);
			} else {
				removedLifelines.add(lifeline);
			}
		}
	}

	public List<Lifeline> getRemovedLifelines() {
		return removedLifelines;
	}

	public List<Lifeline> getAddedLifelines() {
		return addedLifelines;
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (msgLocation == null) {
			Point shellCenter = getCenterPoint();
			return new Point(shellCenter.x, shellCenter.y * 1 / 3);
		} else {
			return new Point(msgLocation.x + 500, msgLocation.y + 100);
		}
	}

	public Point getCenterPoint() {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Rectangle shellBounds = parentShell.getBounds();
		return new Point(shellBounds.x + shellBounds.width / 2, (shellBounds.y + shellBounds.height) / 2);
	}

}
