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
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Collaboration;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;

/**
 * A content provider for displaying UML elements in a tree structure. This class provides
 * the necessary logic to populate a TreeViewer with UML elements, supporting hierarchical
 * relationships between elements such as packages, classes, interfaces, and other UML constructs.
 * It handles the structure of UML models and displays elements in a sorted manner for easy navigation.
 */
public class AdoneUMLElementTreeContensProvider implements ITreeContentProvider {

	private Collection<Element> inputElement;

	private TreeViewer treeViewer;

	/*
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {

		List<Object> children = new ArrayList<>();

		if (!(parentElement instanceof NamedElement)) {
			return children.toArray();
		}

		NamedElement ownerElement = (NamedElement) parentElement;

		for (Object object : ownerElement.getOwnedElements()) {

			if (object instanceof Collaboration) {
				continue;
			}

			if (object instanceof Package || object instanceof Class || object instanceof Interface) {
				children.add(object);
			}
		}

		this.sortModel(children);

		return children.toArray();
	}

	private void sortModel(List<Object> array) {

		Comparator<Object> modelComparator = new Comparator<>() {
			@Override
			public int compare(Object r1, Object r2) {

				if (r1 instanceof NamedElement && r2 instanceof NamedElement) {
					NamedElement r1e = (NamedElement) r1;
					NamedElement r2e = (NamedElement) r2;
					return r1e.getName().compareTo(r2e.getName());
				}

				return 0;
			}
		};

		array.sort(modelComparator);
	}

	/*
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof NamedElement) {
			NamedElement ownedElement = (NamedElement) element;
			return ownedElement.getNamespace();
		} else {
			return null;
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {

		if (element instanceof Package || element instanceof Model) {
			NamedElement ownerElement = (NamedElement) element;
			if (ownerElement.getOwnedElements().size() > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> objects = new ArrayList<>();

		Iterator<Element> itr = this.inputElement.iterator();

		while (itr.hasNext()) {
			Element element = itr.next();
			if (element instanceof Model) {
				Model model = (Model) element;
				// if ("UMLPrimitiveTypes".equals(model.getName())) {
				// continue;
				// }
			}
			objects.add(element);
		}

		this.sortModel(objects);

		return objects.toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		Collection<Element> filteredModels = new ArrayList<>();

		if (newInput instanceof List) {
			List<Element> inputList = (List<Element>) newInput;
			for (Element ele : inputList) {
				if (ele instanceof Model) {
					Model model = (Model) ele;
					// if (model.getName().toUpperCase().startsWith("UML")
					// || model.getName().toUpperCase().startsWith("JAVA")) {
					// continue;
					// }
				}
				filteredModels.add(ele);
			}
		}

		this.inputElement = filteredModels;
	}

	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;

	}

	public void selectAndExpandElement(Object selectedElement) {
		if (selectedElement != null && treeViewer != null) {
			treeViewer.expandToLevel(selectedElement, TreeViewer.ALL_LEVELS);
			treeViewer.setSelection(new StructuredSelection(selectedElement), true);

			Tree tree = treeViewer.getTree();
			TreeItem[] items = tree.getSelection();
			if (items.length > 0) {
				int index = tree.indexOf(items[0]);
				if (index > 4) {
					tree.setTopItem(tree.getItem(index - 4));
				} else {
					tree.setTopItem(tree.getItem(0));
				}
			}
		}
	}

}
