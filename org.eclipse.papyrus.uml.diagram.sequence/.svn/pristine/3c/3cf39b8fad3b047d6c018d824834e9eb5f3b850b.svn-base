/*****************************************************************************
 * Copyright (c) 2009, 2014 Atos Origin, CEA, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Atos Origin - Initial API and implementation
 *   Christian W. Damus (CEA) - bug 410346
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - customizes to enforce snap-to-grid settings and applying a specific CSS theme
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.part;

import java.io.IOException;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.papyrus.infra.gmfdiag.common.preferences.PreferencesConstantsHelper;
import org.eclipse.papyrus.infra.gmfdiag.css.Activator;
import org.eclipse.papyrus.infra.gmfdiag.css.preferences.ThemePreferences;
import org.osgi.framework.BundleContext;

/**
 * This class extends the UMLDiagramEditorPlugin to enforce specific configurations
 * for the sequence diagram editor in the Adone project, including setting
 * the snap-to-grid option and applying a custom CSS theme upon plugin start.
 */
public class AdoneUMLDiagramEditorPlugin extends UMLDiagramEditorPlugin {

	public AdoneUMLDiagramEditorPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setSnapToGridPreference();
		setAdoneCssThemePrefrence();
	}

	/**
	 * Sets the CSS theme to the Adone project's custom theme.
	 */
	private void setAdoneCssThemePrefrence() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String key = ThemePreferences.CURRENT_THEME;
		String themeId = "org.eclipse.papyrus.css.adone_papyrus_theme";
		store.setDefault(key, themeId);
		store.setValue(key, themeId);
		savePreferenceStore(store);
	}

	/**
	 * Enforces the snap-to-grid preference for sequence diagrams.
	 */
	private void setSnapToGridPreference() {
		IPreferenceStore store = getPreferenceStore();
		String key = PreferencesConstantsHelper.getPapyrusEditorConstant(PreferencesConstantsHelper.SNAP_TO_GRID);
		boolean defaultValue = false;
		store.setDefault(key, defaultValue);
		store.setValue(key, defaultValue);
		savePreferenceStore(store);
	}

	/**
	 * Saves the preference store if it's persistent.
	 *
	 * @param store
	 *            The preference store to save.
	 */
	private void savePreferenceStore(IPreferenceStore store) {
		if (store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore) store).save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

