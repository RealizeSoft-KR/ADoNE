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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.swt.graphics.Image;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;

/**
 * A label provider for UML elements in the Adone toolset. It extends the basic LabelProvider to offer
 * custom icons and text for various UML elements like classes, interfaces, packages, associations, and
 * more, based on their specific type. This provider supports enhancing the visual representation of UML
 * elements within dialogs or views by associating each UML element type with a unique icon and providing
 * a formatted text label that can include additional details like association endpoints or generalization
 * relationships. It is designed to be used within selection dialogs or any UI component that needs to
 * display a list or tree of UML elements with more context than just their names.
 */
public class AdoneUMLElementLabelProvider extends LabelProvider implements ILabelProvider {

	private static final String UML_PACKAGE_ICON_PATH = "/adone-icons/obj16/package.gif";
	private static final String UML_CLASS_ICON_PATH = "/adone-icons/obj16/class.gif";
	private static final String UML_INTERFACE_ICON_PATH = "/adone-icons/obj16/interface.gif";
	private static final String UML_ENUMERATION_ICON_PATH = "/adone-icons/obj16/enumeration.gif";
	private static final String UML_ENUMERATION_LITERAL_ICON_PATH = "/adone-icons/obj16/enumerationLiteral.gif";
	private static final String UML_PROPERTY_ICON_PATH = "/adone-icons/obj16/property.gif";
	private static final String UML_ASSOCIATION_ICON_PATH = "/adone-icons/obj16/association.gif";
	private static final String UML_GENERALIZATION_ICON_PATH = "/adone-icons/obj16/generalization.gif";
	// private static final String UML_RELATIONSHIP_ICON_PATH = "/adone-icons/obj16/association.gif";
	private static final String UML_PRIMITIVETYPE_ICON_PATH = "/adone-icons/obj16/primitiveType.gif";
	private static final String UML_MODEL_ICON_PATH = "/adone-icons/obj16/model.gif";

	/*
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {

		Image umlIconImage = null;

		if (element instanceof Model) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_MODEL_ICON_PATH);
		} else if (element instanceof Package) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_PACKAGE_ICON_PATH);
		} else if (element instanceof Class) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_CLASS_ICON_PATH);
		} else if (element instanceof Interface) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_INTERFACE_ICON_PATH);
		} else if (element instanceof Property) {
			Property property = (Property) element;
			if (property.getAssociation() == null) {
				umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_PROPERTY_ICON_PATH);
			} else {
				umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_ASSOCIATION_ICON_PATH);
			}
		} else if (element instanceof Enumeration) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_ENUMERATION_ICON_PATH);
		} else if (element instanceof EnumerationLiteral) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_ENUMERATION_LITERAL_ICON_PATH);

		} else if (element instanceof Association) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_ASSOCIATION_ICON_PATH);

		} else if (element instanceof Generalization) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_GENERALIZATION_ICON_PATH);

		} else if (element instanceof PrimitiveType) {
			umlIconImage = UMLDiagramEditorPlugin.getInstance().getBundledImage(UML_PRIMITIVETYPE_ICON_PATH);
		}

		return umlIconImage;

	}

	/*
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof Association) {
			Association asso = (Association) element;
			StringBuilder sb = new StringBuilder();

			try {
				if (asso.getEndTypes().size() > 1) {
					sb.append(asso.getEndTypes().get(1).getName());
					sb.append(":");
					sb.append(asso.getEndTypes().get(0).getName());
					return sb.toString();
				} else {
					return "";
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else if (element instanceof Generalization) {
			Generalization generalization = (Generalization) element;
			StringBuilder sb = new StringBuilder();

			try {
				sb.append(generalization.getSpecific().getName());
				sb.append("->");
				sb.append(generalization.getGeneral().getName());
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		} else if (element instanceof NamedElement) {
			NamedElement namedElement = (NamedElement) element;

			if (namedElement.getName() == null) {
				return "";
			}

			if (element instanceof Property) {
				Property associationProperty = (Property) element;
				if (associationProperty.getAssociation() != null) {
					return associationProperty.getNamespace().getName() + "-" + namedElement.getName();
				} else {
					return namedElement.getName();
				}
			} else {

				return namedElement.getName();
			}
		} else {

			return "";
		}

	}

}
