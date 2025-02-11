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
 * Manages the mapping of Java primitive types to their UML PrimitiveType counterparts within the Adone UML tooling environment.
 * This class initializes and provides access to UML PrimitiveType representations of standard Java primitive types, such as int, float, boolean, etc.,
 * facilitating their use in UML models. It utilizes the UML Java Primitive Types Library to ensure accurate representation of these types.
 */
public class AdoneUMLJavaPrimitiveTypeManager {

	private static AdoneUMLJavaPrimitiveTypeManager manager;

	private Model model;

	private final String JAVA_PRIMITIVE_TYPE_STRING = "String";
	private final String JAVA_PRIMITIVE_TYPE_INT = "int";
	private final String JAVA_PRIMITIVE_TYPE_LONG = "long";
	private final String JAVA_PRIMITIVE_TYPE_FLOAT = "float";
	private final String JAVA_PRIMITIVE_TYPE_BOOLEAN = "boolean";
	private final String JAVA_PRIMITIVE_TYPE_DOUBLE = "double";

	private PrimitiveType stringJavaPrimitiveType;
	private PrimitiveType intJavaPrimitiveType;
	private PrimitiveType longJavaPrimitiveType;
	private PrimitiveType floatJavaPrimitiveType;
	private PrimitiveType booleanJavaPrimitiveType;
	private PrimitiveType doubleJavaPrimitiveType;

	private AdoneUMLJavaPrimitiveTypeManager() {
		// Initializes the manager by loading the Java Primitive Types Library and setting up the primitive types.
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createURI(UMLResource.JAVA_PRIMITIVE_TYPES_LIBRARY_URI), true);
		model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);
		this.initalize();
	}

	/**
	 * Initializes the mapping of Java primitive type names to their corresponding UML PrimitiveType objects.
	 */
	private void initalize() {
		this.stringJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_STRING);
		this.intJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_INT);
		this.longJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_LONG);
		this.floatJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_FLOAT);
		this.booleanJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_BOOLEAN);
		this.doubleJavaPrimitiveType = (PrimitiveType) model.getOwnedType(JAVA_PRIMITIVE_TYPE_DOUBLE);
	}

	/**
	 * Provides a global access point to the singleton instance of the JavaPrimitiveTypeManager.
	 *
	 * @return The singleton instance of the AdoneUMLJavaPrimitiveTypeManager.
	 */
	public static AdoneUMLJavaPrimitiveTypeManager getInstance() {
		if (manager == null) {
			manager = new AdoneUMLJavaPrimitiveTypeManager();
		}
		return manager;
	}

	/**
	 * Retrieves the UML PrimitiveType corresponding to a given Java primitive type name.
	 *
	 * @param primitiveTypeName
	 *            The name of the Java primitive type.
	 * @return The corresponding UML PrimitiveType object, or null if the type name does not match any known Java primitive types.
	 */
	public PrimitiveType getPrimitiveType(String primitiveTypeName) {
		if (primitiveTypeName == null || "".equals(primitiveTypeName)) {
			return null;
		}

		if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_STRING)) {
			return this.stringJavaPrimitiveType;
		} else if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_INT)) {
			return this.intJavaPrimitiveType;
		} else if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_LONG)) {
			return this.longJavaPrimitiveType;
		} else if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_FLOAT)) {
			return this.floatJavaPrimitiveType;
		} else if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_BOOLEAN)) {
			return this.booleanJavaPrimitiveType;
		} else if (primitiveTypeName.equals(JAVA_PRIMITIVE_TYPE_DOUBLE)) {
			return this.doubleJavaPrimitiveType;
		} else {
			return null;
		}

	}

}
