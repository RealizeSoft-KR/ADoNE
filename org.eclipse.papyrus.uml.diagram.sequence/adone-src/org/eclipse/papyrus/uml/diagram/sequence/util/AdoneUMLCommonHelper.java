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

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.PrimitiveType;

/**
 * Provides a set of utility functions for common operations on UML models in the context of Adone UML tooling.
 * This includes methods for retrieving documentation comments, handling primitive types, and ensuring unique
 * names within a given namespace. The utility methods are designed to support the manipulation and interpretation
 * of UML models, enhancing model processing tasks with convenient, reusable logic.
 */
public class AdoneUMLCommonHelper {

	/**
	 * Retrieves the concatenated string of all documentation comments associated with a given UML element.
	 * This method formats newline characters within comments to ensure consistency across different platforms.
	 *
	 * @param element
	 *            The UML element for which to retrieve documentation.
	 * @return A single string containing all documentation comments for the element, or an empty string if none.
	 */
	public static String getDocument(Element element) {

		if (element == null) {
			return "";
		}

		EList<Comment> comments = element.getOwnedComments();
		StringBuffer document = new StringBuffer();
		for (Comment comment : comments) {
			if (comment.getBody() == null || "".equals(comment.getBody())) {
				continue;
			}

			String commentStr = comment.getBody();

			document.append(commentStr);
		}
		return document.toString().replaceAll("null", "");
	}

	/**
	 * Retrieves a UML PrimitiveType based on its name from the Adone-defined UML primitive types.
	 *
	 * @param primitiveTypeName
	 *            The name of the primitive type to retrieve.
	 * @return The PrimitiveType corresponding to the given name, or null if not found.
	 */
	public static PrimitiveType getPrimitiveType(String primitiveTypeName) {
		return AdoneUMLPrimitiveTypeManager.getInstance().getPrimitiveType(primitiveTypeName);
	}

	/**
	 * Retrieves a UML PrimitiveType based on its name from a Java-specific set of primitive types managed by Adone.
	 *
	 * @param primitiveTypeName
	 *            The name of the Java primitive type to retrieve.
	 * @return The PrimitiveType corresponding to the given name within the Java primitive type set, or null if not found.
	 */
	public static PrimitiveType getJavaPrimitiveType(String primitiveTypeName) {
		return AdoneUMLJavaPrimitiveTypeManager.getInstance().getPrimitiveType(primitiveTypeName);
	}

	/**
	 * Generates a unique name within a given UML namespace by appending a numeric suffix to the provided base name
	 * if necessary, ensuring that the resulting name does not collide with any existing member names.
	 *
	 * @param owner
	 *            The namespace within which the uniqueness of the name should be ensured.
	 * @param name
	 *            The base name to be made unique within the namespace.
	 * @return A unique name based on the provided base name, suitable for use within the given namespace.
	 */
	public static String getUniqueName(Namespace owner, String name) {

		if (owner == null || name == null || "".equals(name)) {
			return name;
		}

		String newName = name;
		boolean isCreated = false;
		int count = 1;
		while (!isCreated) {
			if (owner.getOwnedMember(newName) != null) {
				newName = name + count;
				count++;
			} else {
				isCreated = true;
			}
		}
		return newName;
	}

}
