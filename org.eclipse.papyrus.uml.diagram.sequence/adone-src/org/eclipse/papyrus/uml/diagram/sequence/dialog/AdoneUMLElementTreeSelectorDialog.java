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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneUMLModelHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Type;

/**
 * A dialog for selecting UML types from a tree structure, allowing filtering and selection for further operations.
 */
public class AdoneUMLElementTreeSelectorDialog extends Dialog {

	// The type selected by the user.
	private Type selectedType;

	// Viewer for displaying UML elements in a tree.
	private TreeViewer treeViewer;

	// Initially selected element, if any.
	private Element selectedElement;

	// Text field for inputting filter criteria.
	private Text txtFilterText;

	// Filter for the tree viewer based on text input.
	private ViewerFilter treeFilter;

	// The initial location of the dialog.
	private Point dialogLocation;

	protected AdoneUMLElementTreeSelectorDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	public AdoneUMLElementTreeSelectorDialog(Shell parentShell) {
		super(parentShell);
	}

	public AdoneUMLElementTreeSelectorDialog(Shell parentShell, Element selectedElement) {
		super(parentShell);
		this.selectedElement = selectedElement;
	}

	public AdoneUMLElementTreeSelectorDialog(Shell parentShell, Point location) {
		super(parentShell);
		this.dialogLocation = location;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Label lblXx = new Label(container, SWT.NONE);
		lblXx.setText("Enter more than 2 characters to apply filtering");

		this.txtFilterText = new Text(container, SWT.BORDER);
		this.txtFilterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.txtFilterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				applyFilter();
			}
		});

		this.treeViewer = new TreeViewer(container, SWT.BORDER);
		Tree tree = this.treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.treeViewer.setLabelProvider(new AdoneUMLElementLabelProvider());

		AdoneUMLElementTreeContensProvider provider = new AdoneUMLElementTreeContensProvider();
		this.treeViewer.setContentProvider(provider);
		provider.setTreeViewer(this.treeViewer);

		try {
			Collection<Element> workingUmlModel = AdoneUMLModelHelper.getActiveModels(selectedElement);
			this.treeViewer.setInput(workingUmlModel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					return;
				}

				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();

					Object obj = selection.getFirstElement();

					if (obj instanceof Type) {
						selectedType = (Type) obj;
					}
				}
			}

		});

		this.treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});

		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		if (this.selectedElement != null) {
			provider.selectAndExpandElement(this.selectedElement);
		}

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
		return new Point(340, 375);
	}

	@Override
	protected void okPressed() {

		if (this.selectedType == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Selection Error", "Select a Type");
			return;
		}

		super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setModified(true);
		super.configureShell(newShell);
		newShell.setText("Select Type");
	}

	public Type getSelectedType() {
		return this.selectedType;
	}

	private void applyFilter() {
		if (this.txtFilterText.getText().length() >= 2) {
			// When more than 2 characters are entered based on Korean characters
			if (this.treeFilter == null) {
				this.treeFilter = new UMLTypeTreeFilter();
				this.treeViewer.addFilter(this.treeFilter);
			}
			this.treeViewer.refresh();
			this.treeViewer.expandAll();

		} else {
			if (this.treeFilter != null) {
				this.treeViewer.removeFilter(this.treeFilter);
				this.treeFilter = null;
			}
		}
	}

	private class UMLTypeTreeFilter extends ViewerFilter {

		private Set<Classifier> matchingOperations = new HashSet<>();

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof NamedElement) {
				NamedElement namedElement = (NamedElement) element;

				// 1. When the current item matches the filtering criteria
				if (namedElement.getName().contains(txtFilterText.getText())) {
					if (namedElement instanceof Classifier) {
						matchingOperations.add((Classifier) namedElement); // �׸� ����

						return true;
					}
				}
			}

			// 2. When the current item is the parent of an item that matches the filtering criteria
			if (isAncestorOfMatchingElement(viewer, element, txtFilterText.getText())) {
				return true;
			}

			return false;
		}

		private boolean isAncestorOfMatchingElement(Viewer viewer, Object ancestor, String name) {
			ITreeContentProvider provider = (ITreeContentProvider) ((TreeViewer) viewer).getContentProvider();
			Object[] children = provider.getChildren(ancestor);

			for (Object child : children) {
				if (child instanceof NamedElement) {
					NamedElement childElement = (NamedElement) child;
					if (childElement.getName().contains(name)) {
						if (childElement instanceof Classifier) {
							return true;
						}
					}
					if (isAncestorOfMatchingElement(viewer, childElement, name)) {
						return true;
					}
				}
			}

			return false;
		}

	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		if (dialogLocation == null) {
			Point shellCenter = getCenterPoint();
			return new Point(shellCenter.x, shellCenter.y * 1 / 3);
		} else {
			return new Point(dialogLocation.x + 400, dialogLocation.y + 100);
		}
	}

	public Point getCenterPoint() {
		Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		Rectangle shellBounds = parentShell.getBounds();
		return new Point(shellBounds.x + shellBounds.width / 2, (shellBounds.y + shellBounds.height) / 2);
	}

}
