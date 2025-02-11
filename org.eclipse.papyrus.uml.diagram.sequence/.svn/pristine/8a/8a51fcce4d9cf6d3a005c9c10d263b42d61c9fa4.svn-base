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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Lifeline;

/**
 * A dialog for selecting the type of Combined Fragment (e.g., Alt, Opt, Loop, Assert)
 * and the lifelines that it covers. This dialog facilitates the creation of Combined Fragments
 * in UML sequence diagrams by allowing users to specify interaction conditions and covered lifelines.
 */
public class AdoneCombinedFragmentOptionSelectionDialog extends Dialog {

	private List<Lifeline> coveredLifelines;
	private List<Lifeline> selectedLifelines = new ArrayList<>();
	private Point msgLocation;
	private Table table;
	private Button btnAlt;
	private Button btnOpt;
	private Button btnLoop;
	private String selectedOption;
	private Label label;
	private Button btnAssert;

	public AdoneCombinedFragmentOptionSelectionDialog(Shell parent, List<Lifeline> coveredLifelines, Point msgLocation) {
		super(parent);
		this.coveredLifelines = coveredLifelines;
		this.msgLocation = msgLocation;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));

		Label lblSelectType = new Label(composite, SWT.NONE);
		GridData gd_lblSelectType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectType.widthHint = 76;
		lblSelectType.setLayoutData(gd_lblSelectType);
		lblSelectType.setText("Select Type");

		Group group_1 = new Group(composite, SWT.NONE);
		GridData gd_group_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_group_1.heightHint = 38;
		group_1.setLayoutData(gd_group_1);
		group_1.setLayout(new GridLayout(4, true));

		this.btnAlt = new Button(group_1, SWT.RADIO);
		this.btnAlt.setAlignment(SWT.CENTER);
		this.btnAlt.setText("Alt");

		this.btnOpt = new Button(group_1, SWT.RADIO);
		this.btnOpt.setAlignment(SWT.CENTER);
		this.btnOpt.setText("Opt");

		this.btnLoop = new Button(group_1, SWT.RADIO);
		this.btnLoop.setAlignment(SWT.CENTER);
		this.btnLoop.setText("Loop");
		this.btnLoop.setSelection(true);

		this.btnAssert = new Button(group_1, SWT.RADIO);
		this.btnAssert.setAlignment(SWT.CENTER);
		this.btnAssert.setText("Assert");

		this.label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		this.label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		Label lblSelectCoveredLifelines = new Label(composite, SWT.NONE);
		lblSelectCoveredLifelines.setText("Select Covered Lifelines");
		new Label(composite, SWT.NONE);

		CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
		this.table = checkboxTableViewer.getTable();
		this.table.setLinesVisible(true);
		this.table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		for (Lifeline lifeline : coveredLifelines) {

			TableItem item = new TableItem(table, SWT.NONE);

			if (lifeline.getRepresents().getType() != null) {
				item.setText(lifeline.getRepresents().getType().getName());
			} else {
				item.setText(lifeline.getName());
			}

			item.setData(lifeline);

			item.setChecked(true);

		}

		return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setModified(true);
		super.configureShell(newShell);
		newShell.setText("CombinedFragment Creation Option");
	}

	@Override
	protected Point getInitialSize() {
		Point result = super.getInitialSize();
		if ((getDialogBoundsStrategy() & DIALOG_PERSISTSIZE) != 0) {
			try {
				int width = 400;
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

		selectedLifelines.clear();

		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				selectedLifelines.add((Lifeline) item.getData());
			}
		}

		if (btnAlt.getSelection()) {
			selectedOption = "Alt";
		} else if (btnOpt.getSelection()) {
			selectedOption = "Opt";
		} else if (btnLoop.getSelection()) {
			selectedOption = "Loop";
		} else if (btnAssert.getSelection()) {
			selectedOption = "Assert";
		}

		super.okPressed();
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (msgLocation == null) {
			Point shellCenter = getCenterPoint();
			return new Point(shellCenter.x, shellCenter.y * 1 / 3);
		} else {
			return new Point(msgLocation.x + 500, msgLocation.y + 100);
		}
		// return super.getInitialLocation(initialSize);
	}

	public Point getCenterPoint() {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Rectangle shellBounds = parentShell.getBounds();
		return new Point(shellBounds.x + shellBounds.width / 2, (shellBounds.y + shellBounds.height) / 2);
	}


	public List<Lifeline> getSelectedLifelines() {
		return selectedLifelines;
	}

	public String getSelectedOption() {
		return selectedOption;
	}
}
