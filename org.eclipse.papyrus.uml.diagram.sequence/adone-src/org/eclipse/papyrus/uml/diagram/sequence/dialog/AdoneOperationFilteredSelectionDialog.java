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

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;

/**
 * A dialog for selecting an operation to set as the signature for a UML message. It provides a filterable
 * list of operations based on the classifier type of the selected lifeline or element. Users can search through
 * the list of available operations and select one to assign as the message signature. This dialog supports
 * creating a new operation if the desired operation is not found in the list. The selection process is facilitated
 * through a text input for filtering and a table to display and choose from the filtered operations.
 */
public class AdoneOperationFilteredSelectionDialog extends Dialog {

	private List<Operation> input;
	private Text txtSearchInput;
	private Table tblSearchResult;
	private TableViewer tbvSearchResult;
	private IContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private Object selectedElement;
	private Label lblNewLabel;
	private Operation selectedOperation;
	private Point msgLocation;
	private Classifier owner;


	/**
	 * @wbp.parser.constructor
	 */
	public AdoneOperationFilteredSelectionDialog(Shell shell, Classifier owner, EList<Operation> input) {
		super(shell);
		this.input = input;
		this.contentProvider = new FilteredSelectionOperationContensProvider();
		this.labelProvider = new FilteredSelectionOperationLabelProvider();
		this.owner = owner;

	}

	/**
	 * Constructor.
	 *
	 * @param shell
	 * @param operations
	 * @param msgLocation
	 */
	public AdoneOperationFilteredSelectionDialog(Shell shell, Classifier owner, EList<Operation> operations, Point msgLocation) {
		super(shell);
		this.input = owner.getOperations();
		this.contentProvider = new FilteredSelectionOperationContensProvider();
		this.labelProvider = new FilteredSelectionOperationLabelProvider();
		this.selectedOperation = null;
		this.msgLocation = msgLocation;
		this.owner = owner;

	}

	/**
	 * Constructor.
	 *
	 * @param shell
	 * @param operations
	 * @param msgLocation2
	 */
	public AdoneOperationFilteredSelectionDialog(Shell shell, EList<Operation> operations, org.eclipse.draw2d.geometry.Point msgLocation2) {
		super(shell);
	}

	public Object getSelectedElement() {
		return selectedElement;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(1, false));

		lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Select Operation (* = any string, ? = any char):");

		txtSearchInput = new Text(composite, SWT.BORDER);
		txtSearchInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		tbvSearchResult = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		tblSearchResult = tbvSearchResult.getTable();
		tblSearchResult.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		if (this.contentProvider != null) {
			tbvSearchResult.setContentProvider(this.contentProvider);
		}

		if (this.labelProvider != null) {
			tbvSearchResult.setLabelProvider(this.labelProvider);
		}

		if (this.input != null) {
			tbvSearchResult.setInput(this.input);
		}

		final PatternFilter filter = new PatternFilter() {
			@Override
			protected boolean isParentMatch(Viewer viewer, Object element) {
				return viewer instanceof AbstractTreeViewer && super.isParentMatch(viewer, element);
			}
		};

		tbvSearchResult.addFilter(filter);
		txtSearchInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				filter.setPattern(((Text) e.widget).getText());
				tbvSearchResult.refresh();
			}
		});

		tbvSearchResult.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();

				selectedElement = selection.getFirstElement();

				if (selectedElement instanceof Operation) {
					selectedOperation = (Operation) selectedElement;
				}

			}
		});

		tbvSearchResult.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				selectedElement = selection.getFirstElement();
				if (selectedElement instanceof Operation) {
					selectedOperation = (Operation) selectedElement;
				}

				okPressed();

			}
		});

		// Combo box label
		Label comboLabel = new Label(composite, SWT.NONE);
		comboLabel.setText("Select Operation:");
		comboLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		// Combo box for operations
		Combo comboOperations = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboOperations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Populate the combo box with operations' names
		String[] operationNames = input.stream().map(Operation::getName).toArray(String[]::new);
		comboOperations.setItems(operationNames);

		// Listener for combo box selection changes
		comboOperations.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int selectedIndex = comboOperations.getSelectionIndex();
				if (selectedIndex != -1) {
					selectedOperation = input.get(selectedIndex);
					// Now selectedOperation holds the operation selected from the combo box
					// Additional action can be performed here based on the selection
				}
			}
		});

		Button newOperationButton = new Button(composite, SWT.PUSH);
		newOperationButton.setText("New Operation");
		newOperationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		newOperationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AdoneUMLCreateOperationDialog dialog = new AdoneUMLCreateOperationDialog(getParentShell(), owner);
				if (dialog.OK == dialog.open()) {
					tbvSearchResult.setInput(owner.getOperations());
					Operation createdOp = dialog.getCreatedOp();

					if (createdOp != null) {
						IStructuredSelection selection = new StructuredSelection(createdOp);
						tbvSearchResult.setSelection(selection, true);
					}


				}
			}
		});

		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setModified(true);
		super.configureShell(newShell);
		newShell.setText("Select Operation For Signature");
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
				int height = 350;
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
		super.okPressed();
	}

	public Operation getSelectedOperation() {
		return this.selectedOperation;
	}


	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (msgLocation == null) {
			Point shellCenter = getCenterPoint();
			return new Point(shellCenter.x, shellCenter.y * 1 / 3);
		} else {
			return new Point(msgLocation.x + 350, msgLocation.y + 100);
			// return new Point(msgLocation.x, msgLocation.y);
		}
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

}

class FilteredSelectionOperationLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof Operation) {
			Operation operation = (Operation) element;
			return operation.getName();
		}
		return "";
	}

}

class FilteredSelectionOperationContensProvider implements IStructuredContentProvider {

	private List<Operation> operationes;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		if (newInput != null) {
			this.operationes = (List<Operation>) newInput;
		}

	}

	@Override
	public Object[] getElements(Object inputElement) {

		return this.operationes.toArray();

	}

}
