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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Manages UML primitive types within the Adone UML tooling environment, providing access to predefined UML PrimitiveType objects.
 * This class initializes with the UML Primitive Types Library to map common UML primitive types to their corresponding objects,
 * such as String, Integer, and Boolean. It serves as a centralized resource for accessing these types, ensuring consistency
 * and reusability across different components of the tool.
 */
public class AdoneUMLPrimitiveTypeManager {

	private static AdoneUMLPrimitiveTypeManager manager;
	private Model model;
	private PrimitiveType stringPrimitiveType;
	private PrimitiveType integerPrimitiveType;
	private PrimitiveType booleanPrimitiveType;
	private final String UML_PRIMITIVE_TYPE_STRING = "String";
	private final String UML_PRIMITIVE_TYPE_INTEGER = "Integer";
	private final String UML_PRIMITIVE_TYPE_BOOLEAN = "Boolean";

	/**
	 * Private constructor to prevent external instantiation. Initializes the manager by loading the UML Primitive Types Library
	 * and setting up the primitive types.
	 */
	private AdoneUMLPrimitiveTypeManager() {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI), true);
		model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);
		this.initialize();
	}

	/**
	 * Initializes the mapping of UML primitive type names to their corresponding UML PrimitiveType objects.
	 */
	private void initialize() {
		this.stringPrimitiveType = (PrimitiveType) model.getOwnedType(UML_PRIMITIVE_TYPE_STRING);
		this.integerPrimitiveType = (PrimitiveType) model.getOwnedType(UML_PRIMITIVE_TYPE_INTEGER);
		this.booleanPrimitiveType = (PrimitiveType) model.getOwnedType(UML_PRIMITIVE_TYPE_BOOLEAN);
	}

	/**
	 * Provides a global access point to the singleton instance of the PrimitiveTypeManager.
	 *
	 * @return The singleton instance of the AdoneUMLPrimitiveTypeManager.
	 */
	public static AdoneUMLPrimitiveTypeManager getInstance() {
		if (manager == null) {
			manager = new AdoneUMLPrimitiveTypeManager();
		}
		return manager;
	}

	/**
	 * Retrieves the UML PrimitiveType object corresponding to a given UML primitive type name.
	 *
	 * @param primitiveTypeName
	 *            The name of the UML primitive type.
	 * @return The corresponding UML PrimitiveType object, or the default string primitive type if the name does not match any known types.
	 */
	public PrimitiveType getPrimitiveType(String primitiveTypeName) {
		if (primitiveTypeName == null || "".equals(primitiveTypeName)) {
			return null;
		}

		if (primitiveTypeName.equals(UML_PRIMITIVE_TYPE_STRING)) {
			return this.stringPrimitiveType;
		} else if (primitiveTypeName.equals(UML_PRIMITIVE_TYPE_INTEGER)) {
			return this.integerPrimitiveType;
		} else if (primitiveTypeName.equals(UML_PRIMITIVE_TYPE_BOOLEAN)) {
			return this.booleanPrimitiveType;
		} else {
			return this.stringPrimitiveType;
		}
	}

}
