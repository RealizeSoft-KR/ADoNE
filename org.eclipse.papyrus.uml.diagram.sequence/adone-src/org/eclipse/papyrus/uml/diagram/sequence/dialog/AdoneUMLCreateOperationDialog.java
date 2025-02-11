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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneClassHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneUMLCommonHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.VisibilityKind;

/**
 * A dialog for creating a new UML operation within a specified classifier. This dialog allows users to specify
 * operation details including the name, visibility, input and return parameters, along with their types and
 * multiplicity. It provides a comprehensive interface for defining new operations directly from the UML model,
 * supporting the enhancement of class and interface specifications through a user-friendly graphical interface.
 * The dialog also includes functionality for input validation to ensure the integrity of the model changes.
 */
public class AdoneUMLCreateOperationDialog extends Dialog {

	private Classifier owner;
	private Text txtOwnerName;
	private Text txtOpName;
	private String txtOpNameInput;
	private Text txtInputParamName;
	private String txtInputParamNameInput;
	private Text txtReturnParamName;
	private String txtReturnParamNameInput;
	private Type inputParamType;
	private Type returnParamType;
	private Text txtInputTypeName;
	private Text txtReturnTypeName;
	private Combo cmbInputPrimitiveType;
	private Combo cmbReturnPrimitiveType;
	private Operation createdOp;
	private Button btnInputMultiChkBtn;
	private Button btnReturnMultiChkBtn;
	private boolean isInputMulti;
	private boolean isReturnMulti;
	private boolean isPrivate;
	private Button btnIsPrivate;
	private boolean checkTranslation;

	/**
	 * Create the dialog.
	 *
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public AdoneUMLCreateOperationDialog(Shell parentShell) {
		super(parentShell);
	}

	public AdoneUMLCreateOperationDialog(Shell parentShell, Classifier owner) {
		super(parentShell);
		this.owner = owner;
	}

	/**
	 * Create contents of the dialog.
	 *
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(7, false);
		gl_container.horizontalSpacing = 4;
		container.setLayout(gl_container);

		Label lblOwner = new Label(container, SWT.NONE);
		lblOwner.setText("Owner :");

		txtOwnerName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtOwnerName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 6, 1));
		txtOwnerName.setText(this.owner.getName());

		Label lblOpname = new Label(container, SWT.NONE);
		lblOpname.setText("Operation Name :");

		txtOpName = new Text(container, SWT.BORDER);
		txtOpName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		txtOpName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				txtOpNameInput = txtOpName.getText();

			}
		});

		txtOpName.setFocus();

		btnIsPrivate = new Button(container, SWT.CHECK);
		btnIsPrivate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnIsPrivate.setText("private Yn");
		btnIsPrivate.setSelection(false);
		isPrivate = false;

		btnIsPrivate.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnIsPrivate.getSelection()) {
					isPrivate = true;
				} else {
					isPrivate = false;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 7, 1));

		Label lblInput = new Label(container, SWT.NONE);
		lblInput.setText("Input Param Name :");

		txtInputParamName = new Text(container, SWT.BORDER);
		GridData gd_txtInputParamName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtInputParamName.widthHint = 100;
		txtInputParamName.setLayoutData(gd_txtInputParamName);
		txtInputParamName.setText("in");

		txtInputParamName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				txtInputParamNameInput = txtInputParamName.getText();

			}
		});
		txtInputParamNameInput = "in";

		txtInputTypeName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtInputTypeName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtInputTypeName.widthHint = 100;
		txtInputTypeName.setLayoutData(gd_txtInputTypeName);
		txtInputTypeName.setText("String");

		cmbInputPrimitiveType = new Combo(container, SWT.DROP_DOWN);
		GridData gd_cmbInputPrimitiveType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_cmbInputPrimitiveType.widthHint = 40;
		cmbInputPrimitiveType.setLayoutData(gd_cmbInputPrimitiveType);
		cmbInputPrimitiveType.add("String");
		cmbInputPrimitiveType.add("int");
		cmbInputPrimitiveType.add("boolean");
		cmbInputPrimitiveType.add("double");
		cmbInputPrimitiveType.add("long");
		cmbInputPrimitiveType.add("Date");
		cmbInputPrimitiveType.setText("String");

		cmbInputPrimitiveType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String typeName = cmbInputPrimitiveType.getText();

				if ("String".equals(typeName)) {
					inputParamType = AdoneUMLCommonHelper.getPrimitiveType(typeName);
				} else if ("Date".equals(typeName) || "Object".equals(typeName)) {
					inputParamType = AdoneUMLCommonHelper.getPrimitiveType("String");
				} else {
					inputParamType = AdoneUMLCommonHelper.getJavaPrimitiveType(typeName);
				}
				txtInputTypeName.setText(typeName);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button btnInputSelectType = new Button(container, SWT.NONE);
		GridData gd_btnSelectType = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_btnSelectType.heightHint = 25;
		btnInputSelectType.setLayoutData(gd_btnSelectType);
		btnInputSelectType.setText("Select DTO ...");

		btnInputSelectType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				AdoneUMLElementTreeSelectorDialog dialog = new AdoneUMLElementTreeSelectorDialog(getShell(), owner);

				if (dialog.open() == Dialog.OK) {
					Type selectedType = dialog.getSelectedType();
					if (selectedType != null) {
						inputParamType = selectedType;
						txtInputTypeName.setText(inputParamType.getName());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnInputMultiChkBtn = new Button(container, SWT.CHECK);
		btnInputMultiChkBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnInputMultiChkBtn.setText("Multi Yn");
		isInputMulti = false;

		btnInputMultiChkBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnInputMultiChkBtn.getSelection()) {
					isInputMulti = true;
				} else {
					isInputMulti = false;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		inputParamType = AdoneUMLCommonHelper.getPrimitiveType("String");

		Label lblReturn = new Label(container, SWT.NONE);
		lblReturn.setText("Return Param name :");

		txtReturnParamName = new Text(container, SWT.BORDER);
		GridData gd_txtReturnParamName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtReturnParamName.widthHint = 100;
		txtReturnParamName.setLayoutData(gd_txtReturnParamName);
		txtReturnParamName.setText("out");

		txtReturnParamName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				txtReturnParamNameInput = txtReturnParamName.getText();

			}
		});

		txtReturnTypeName = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		txtReturnTypeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtReturnTypeName.setText("String");
		txtReturnParamNameInput = "out";

		cmbReturnPrimitiveType = new Combo(container, SWT.NONE);
		cmbReturnPrimitiveType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		cmbReturnPrimitiveType.add("String");
		cmbReturnPrimitiveType.add("int");
		cmbReturnPrimitiveType.add("boolean");
		cmbReturnPrimitiveType.add("double");
		cmbReturnPrimitiveType.add("long");
		cmbReturnPrimitiveType.add("Date");
		cmbReturnPrimitiveType.setText("String");

		cmbReturnPrimitiveType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String typeName = cmbReturnPrimitiveType.getText();

				if ("String".equals(typeName)) {
					returnParamType = AdoneUMLCommonHelper.getPrimitiveType(typeName);
				} else if ("Date".equals(typeName) || "Object".equals(typeName)) {
					returnParamType = AdoneUMLCommonHelper.getPrimitiveType(typeName);
				} else {
					returnParamType = AdoneUMLCommonHelper.getJavaPrimitiveType(typeName);
				}
				txtReturnTypeName.setText(typeName);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Button btnReturnSelectType = new Button(container, SWT.NONE);
		btnReturnSelectType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReturnSelectType.setText("Select DTO ...");
		btnReturnSelectType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				AdoneUMLElementTreeSelectorDialog dialog = new AdoneUMLElementTreeSelectorDialog(getShell(), owner);

				if (dialog.open() == Dialog.OK) {
					Type selectedType = dialog.getSelectedType();
					if (selectedType != null) {
						returnParamType = selectedType;
						txtReturnTypeName.setText(returnParamType.getName());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnReturnMultiChkBtn = new Button(container, SWT.CHECK);
		btnReturnMultiChkBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		btnReturnMultiChkBtn.setText("Multi Yn");
		isReturnMulti = false;

		btnReturnMultiChkBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnReturnMultiChkBtn.getSelection()) {
					isReturnMulti = true;
				} else {
					isReturnMulti = false;
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		checkTranslation = false;

		returnParamType = AdoneUMLCommonHelper.getPrimitiveType("String");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 *
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(689, 234);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New Operation");
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point shellCenter = getCenterPoint();
		return new Point(shellCenter.x * 2 / 3, shellCenter.y * 2 / 3);
	}

	public Point getCenterPoint() {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Rectangle shellBounds = parentShell.getBounds();
		return new Point(shellBounds.x + shellBounds.width / 2, (shellBounds.y + shellBounds.height) / 2);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {

		if (validateInput()) {
			super.okPressed();
		}

		return;

	}

	private boolean validateInput() {

		if (txtOpNameInput == null) {
			MessageDialog.openInformation(getShell(), "- Input Error Message",
					"Please enter the operation name.");
			return false;
		}

		if (owner.getMember(txtOpNameInput) != null) {
			MessageDialog.openInformation(getShell(), "- Input Error Message",
					"The operation name already exists.");
			return false;
		}

		if (txtInputParamNameInput == null) {
			MessageDialog.openInformation(getShell(), "- Input Error Message",
					"Please enter the input parameter name.");
			return false;
		}

		if (txtReturnParamNameInput == null) {
			MessageDialog.openInformation(getShell(), "- Input Error Message",
					"Please enter the return parameter name.");
			return false;
		}

		createdOp = AdoneClassHelper.createOperation(owner, txtOpNameInput);


		if (isPrivate) {
			createdOp.setVisibility(VisibilityKind.PRIVATE_LITERAL);
		}

		Parameter inParam = createdOp.createOwnedParameter(txtInputParamNameInput, inputParamType);
		inParam.setDirection(ParameterDirectionKind.IN_LITERAL);
		if (isInputMulti) {
			inParam.setLower(0);
			inParam.setUpper(-1);
		} else {
			inParam.setLower(0);
			inParam.setUpper(1);
		}

		Parameter returnParam = createdOp.createOwnedParameter(txtReturnParamNameInput, returnParamType);
		returnParam.setDirection(ParameterDirectionKind.RETURN_LITERAL);
		if (isReturnMulti) {
			returnParam.setLower(0);
			returnParam.setUpper(-1);
		} else {
			returnParam.setLower(0);
			returnParam.setUpper(1);
		}
		return true;
	}

	public Operation getCreatedOp() {
		return this.createdOp;
	}

}
