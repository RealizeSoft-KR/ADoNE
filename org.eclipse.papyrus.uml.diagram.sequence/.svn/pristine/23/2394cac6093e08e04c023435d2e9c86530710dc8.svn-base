/*****************************************************************************
 * Copyright (c) 2013 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Laurent Wouters laurent.wouters@cea.fr - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - customizes dynamic diagrams menu to include only essential diagrams.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.papyrus.infra.viewpoints.policy.DynamicContribution;
import org.eclipse.papyrus.infra.viewpoints.policy.PolicyChecker;
import org.eclipse.papyrus.infra.viewpoints.policy.ViewPrototype;

/**
 * Customizes Papyrus's dynamic diagrams menu to provide only essential diagrams: "Use Case",
 * "Class", and "Sequence". This streamlined approach filters out less relevant options,
 * facilitating quicker access to key diagram types. The customization focuses on enhancing
 * user experience by offering a simplified, clutter-free selection directly from the context menu.
 */
public class AdoneDynamicDiagramsMenuContribution extends DynamicContribution {

	public AdoneDynamicDiagramsMenuContribution() {
	}

	public AdoneDynamicDiagramsMenuContribution(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		final EObject selection = getSelection();
		if (selection == null) {
			return new IContributionItem[0];
		}

		// build a list of all the available prototypes
		List<ViewPrototype> data = new ArrayList<>();
		for (final ViewPrototype proto : PolicyChecker.getFor(selection).getPrototypesFor(selection)) {

			String menuName = proto.getLabel();

			if (menuName.startsWith("Use Case") || menuName.startsWith("Class Diagram")
					|| menuName.startsWith("Sequence")) {
				data.add(proto);
			}

		}

		// build the full labels
		List<String> labels = new ArrayList<>(data.size());
		String last = null;
		boolean first = true;
		for (ViewPrototype item : data) {
			String label = item.getLabel();
			if (last != null && last.equals(label)) {
				// name collision
				if (first) {
					labels.set(labels.size() - 1, data.get(labels.size() - 1).getFullLabel());
					first = false;
				}
				labels.add(item.getFullLabel());
			} else {

				if (label.startsWith("Use Case")) {
					label = "유스케이스 다이어그램";
				} else if (label.startsWith("Class")) {
					label = "클래스 다이어그램";
				} else if (label.startsWith("Sequence")) {
					label = "시퀀스 다이어그램";
				}

				labels.add(label);
				last = label;
				first = true;
			}
		}

		// build the menu
		List<IContributionItem> items = new ArrayList<>(data.size());
		for (int i = 0; i != data.size(); i++) {
			final ViewPrototype proto = data.get(i);
			String label = labels.get(i);
			items.add(new ActionContributionItem(new Action(label, proto.getIconDescriptor()) {
				@Override
				public void run() {
					proto.instantiateOn(selection);
				}
			}));
		}
		return items.toArray(new IContributionItem[items.size()]);
	}
}
