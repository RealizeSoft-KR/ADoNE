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

package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;

/**
 * Manages the state of elements' order change in sequence diagrams, enabling modifications to the positioning of lifelines, messages,
 * and CombinedFragment items within the diagram. This class facilitates toggling between normal interaction modes and an element order
 * change mode, where selected elements can be visually distinguished and their order adjusted as required.
 * This class plays a crucial role in enhancing the user experience by providing intuitive and efficient tools for reordering elements
 * within sequence diagrams, contributing to the precise and logical arrangement of diagram components.
 */
public class AdoneElementOrderChangeManager {

	private static AdoneElementOrderChangeManager manager = null;
	protected boolean isElementOrderChangeMode;
	protected List<IFigure> selectedFigureList = new ArrayList<>();

	/**
	 * Retrieves the singleton instance of the ElementOrderChangeManager. If no instance exists, it initializes a new one.
	 * This ensures that element order management across the sequence diagram is centralized.
	 *
	 * @return The single instance of AdoneElementOrderChangeManager.
	 */
	public static AdoneElementOrderChangeManager getInstance() {
		if (manager == null) {
			manager = new AdoneElementOrderChangeManager();
		}
		return manager;
	}

	/**
	 * Checks if the element order change mode is active. This mode allows for the reordering of diagram elements.
	 *
	 * @return True if the mode is active, false otherwise.
	 */
	public boolean isElementOrderChangeMode() {
		return isElementOrderChangeMode;
	}

	/**
	 * Sets the element order change mode. When activated, this mode allows for the modification of the order of elements
	 * within the sequence diagram. Exiting this mode resets the color of selected figures to default.
	 *
	 * @param isElementOrderChangeMode
	 *            True to activate the mode, false to deactivate.
	 */
	public void setElementOrderChangeMode(boolean isElementOrderChangeMode) {
		this.isElementOrderChangeMode = isElementOrderChangeMode;
		if (!isElementOrderChangeMode) {
			this.resetFigureLineColorToDefault();
		}
	}

	/**
	 * Resets the line color of all selected figures to the default color (black) and clears the selection list.
	 * This is typically called when exiting the element order change mode.
	 */
	private void resetFigureLineColorToDefault() {
		for (IFigure figure : selectedFigureList) {
			figure.setForegroundColor(ColorConstants.black);
		}
		selectedFigureList.clear();
	}

	/**
	 * Resets the line color of a specific figure to the default color. This can be used for individual figure adjustments.
	 *
	 * @param figure
	 *            The figure whose line color is to be reset.
	 */
	public void resetFigureLineColorToDefault(IFigure figure) {
		figure.setForegroundColor(ColorConstants.black);
	}

	/**
	 * Adds a figure to the list of selected figures for order change. If the figure is not already in the list,
	 * it is added and its line color is set to blue to indicate selection.
	 *
	 * @param figure
	 *            The figure to add to the selection list.
	 */
	public void addSelectedFigure(IFigure figure) {
		if (!this.selectedFigureList.contains(figure)) {
			figure.setForegroundColor(ColorConstants.blue);
			this.selectedFigureList.add(figure);
		}
	}

	/**
	 * Retrieves the list of currently selected figures. These are the figures subject to order changes.
	 * 
	 * @return A list of selected IFigures.
	 */
	public List<IFigure> getSelectedFigureList() {
		return selectedFigureList;
	}

	/**
	 * Clears the list of selected figures. This is typically used when exiting the element order change mode or
	 * starting a new selection process.
	 */
	public void clear() {
		this.selectedFigureList.clear();
	}

}
