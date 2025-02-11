/*****************************************************************************
 * Copyright (c) 2018 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhance lifeline visuals with stereotype image
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.tools.utils.ImageUtil;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;

/**
 * Extends the LifelineNameEditPart to enhance the visual representation of lifelines
 * in sequence diagrams by adding stereotype icons to the lifeline's header text label.
 * This class dynamically retrieves and displays the appropriate stereotype images
 * associated with the type of the lifeline, if any. For UML Class or Interface types,
 * it uses default UML icons to visually indicate the lifeline's type. The addition of
 * stereotype icons helps in providing a more informative and visually appealing
 * representation of lifelines, making it easier to understand the roles and
 * characteristics of each lifeline within the sequence diagram context.
 *
 * Stereotype images are retrieved and managed dynamically, with resources being
 * properly disposed of when no longer needed to ensure efficient memory usage.
 */
public class AdoneLifelineNameEditPart extends LifelineNameEditPart {

	private Image cachedImage = null; // Stereotype Image
	private static final String UML_CLASS_ICON_PATH = "/adone-icons/obj16/class.gif";
	private static final String UML_INTERFACE_ICON_PATH = "/adone-icons/obj16/interface.gif";

	public AdoneLifelineNameEditPart(View view) {
		super(view);
	}

	@Override
	protected Image getLabelIcon() {

		// Dispose previous image if it exists
		if (cachedImage != null && !cachedImage.isDisposed()) {
			cachedImage.dispose();
		}

		AdoneLifeLineEditPart parentLifelineEp = (AdoneLifeLineEditPart) getParent();
		Lifeline lifeline = (Lifeline) parentLifelineEp.resolveSemanticElement();

		if (lifeline.getRelationships() == null) {
			return null;
		}

		if (lifeline.getRepresents().getType() == null) {
			// Return lifeline icon if the lifeline type is missing
			return DiagramEditPartsUtil.getIcon(getParserElement(), getViewer());
		}

		Type type = lifeline.getRepresents().getType();

		// Logic to find the Stereotype image
		if (type != null) {
			for (Stereotype stereotype : type.getAppliedStereotypes()) {
				cachedImage = getStereotypeImage(stereotype);
				if (cachedImage != null) {
					// Return the first found Stereotype image
					return cachedImage;
				}
			}

			Image bundledImage = null;
			if (type instanceof Class) {
				bundledImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_CLASS_ICON_PATH);
			} else if (type instanceof Interface) {
				bundledImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_INTERFACE_ICON_PATH);
			}

			if (bundledImage != null) {
				return bundledImage;
			}

		}

		if (cachedImage != null) {
			return cachedImage;
		}

		return null;
	}

	private Image getStereotypeImage(Stereotype stereotype) {
		// Logic to retrieve the path of the image resource associated with the Stereotype
		Image image = null;
		try {

			if (!stereotype.getIcons().isEmpty()) {
				org.eclipse.uml2.uml.Image stImage = stereotype.getIcons().get(0);
				image = ImageUtil.getContent(stImage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// In case the image path is missing or loading fails
		return image;
	}

	@Override
	public void deactivate() {
		super.deactivate();
		this.dispose();
	}

	// Dispose method to clean up the cached image
	public void dispose() {
		if (cachedImage != null && !cachedImage.isDisposed()) {
			cachedImage.dispose();
		}
	}

}
