/**
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
 */
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.common.editpolicies.AbstractBaseItemSemanticEditPolicy;
import org.eclipse.papyrus.uml.diagram.sequence.expressions.UMLOCLFactory;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLDiagramEditorPlugin;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLElementTypes;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.DurationConstraint;
import org.eclipse.uml2.uml.DurationObservation;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.GeneralOrdering;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Namespace;
import org.eclipse.uml2.uml.OccurrenceSpecification;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * @generated
 */
public class UMLBaseItemSemanticEditPolicy extends AbstractBaseItemSemanticEditPolicy {

	/**
	 * @generated
	 */
	protected UMLBaseItemSemanticEditPolicy(IElementType elementType) {
		super(elementType);
	}

	/**
	 * @generated
	 */
	@Override
	protected String getVisualIdFromView(View view) {
		return UMLVisualIDRegistry.getVisualID(view);
	}

	/**
	 * @generated
	 */
	@Override
	protected IElementType getContextElementType(IEditCommandRequest request) {
		IElementType requestContextElementType = UMLElementTypes.getElementType(getVisualID(request));
		return requestContextElementType != null ? requestContextElementType : getBaseElementType();
	}

	/**
	 * @generated
	 */
	public static LinkConstraints getLinkConstraints() {
		LinkConstraints cached = UMLDiagramEditorPlugin.getInstance().getLinkConstraints();
		if (cached == null) {
			UMLDiagramEditorPlugin.getInstance().setLinkConstraints(cached = new LinkConstraints());
		}
		return cached;
	}

	/**
	 * @generated
	 */
	public static class LinkConstraints {

		/**
		 * @generated
		 */
		public LinkConstraints() {
			// use static method #getLinkConstraints() to access instance
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_SynchEdge(Interaction container, Element source, Element target) {
			return canExistMessage_SynchEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_AsynchEdge(Interaction container, Element source, Element target) {
			return canExistMessage_AsynchEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_ReplyEdge(Interaction container, Element source, Element target) {
			return canExistMessage_ReplyEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_CreateEdge(Interaction container, Element source, Element target) {
			return canExistMessage_CreateEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_DeleteEdge(Interaction container, Element source, Element target) {
			return canExistMessage_DeleteEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_LostEdge(Interaction container, Element source, Element target) {
			return canExistMessage_LostEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateMessage_FoundEdge(Interaction container, Element source, Element target) {
			return canExistMessage_FoundEdge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateComment_AnnotatedElementEdge(Comment source, Element target) {
			if (source != null) {
				if (source.getAnnotatedElements()
						.contains(target)) {
					return false;
				}
			}
			return canExistComment_AnnotatedElementEdge(source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateConstraint_ConstrainedElementEdge(Constraint source, Element target) {
			if (source != null) {
				if (source.getConstrainedElements()
						.contains(target)) {
					return false;
				}
			}
			return canExistConstraint_ConstrainedElementEdge(source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateGeneralOrdering_Edge(InteractionFragment container, OccurrenceSpecification source, OccurrenceSpecification target) {
			return canExistGeneralOrdering_Edge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateConstraint_ContextEdge(Constraint source, Namespace target) {
			if (source != null) {
				if (source.getContext() != null) {
					return false;
				}
			}
			if (target != null && (target.getOwnedRules()
					.contains(source))) {
				return false;
			}
			return canExistConstraint_ContextEdge(source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateDurationConstraint_Edge(Namespace container, Element source, Element target) {
			return canExistDurationConstraint_Edge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canCreateDurationObservation_Edge(Package container, NamedElement source, NamedElement target) {
			return canExistDurationObservation_Edge(container, null, source, target);
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_SynchEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(2, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(3, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_AsynchEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(22, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(6, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_ReplyEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(8, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(22, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_CreateEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(11, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(12, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_DeleteEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(14, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(15, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_LostEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(22, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(18, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistMessage_FoundEdge(Interaction container, Message linkInstance, Element source, Element target) {
			try {
				if (source == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object sourceVal = UMLOCLFactory.getExpression(20, UMLPackage.eINSTANCE.getElement(), env).evaluate(source, Collections.singletonMap("oppositeEnd", target)); //$NON-NLS-1$
					if (false == sourceVal instanceof Boolean || !((Boolean) sourceVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				if (target == null) {
					return true;
				} else {
					Map<String, EClassifier> env = Collections.<String, EClassifier> singletonMap("oppositeEnd", UMLPackage.eINSTANCE.getElement()); //$NON-NLS-1$
					Object targetVal = UMLOCLFactory.getExpression(22, UMLPackage.eINSTANCE.getElement(), env).evaluate(target, Collections.singletonMap("oppositeEnd", source)); //$NON-NLS-1$
					if (false == targetVal instanceof Boolean || !((Boolean) targetVal).booleanValue()) {
						return false;
					} // else fall-through
				}
				return true;
			} catch (Exception e) {
				UMLDiagramEditorPlugin.getInstance().logError("Link constraint evaluation error", e); //$NON-NLS-1$
				return false;
			}
		}

		/**
		 * @generated
		 */
		public boolean canExistComment_AnnotatedElementEdge(Comment source, Element target) {
			return true;
		}

		/**
		 * @generated
		 */
		public boolean canExistConstraint_ConstrainedElementEdge(Constraint source, Element target) {
			return true;
		}

		/**
		 * @generated
		 */
		public boolean canExistGeneralOrdering_Edge(InteractionFragment container, GeneralOrdering linkInstance, OccurrenceSpecification source, OccurrenceSpecification target) {
			return true;
		}

		/**
		 * @generated
		 */
		public boolean canExistConstraint_ContextEdge(Constraint source, Namespace target) {
			return true;
		}

		/**
		 * @generated
		 */
		public boolean canExistDurationConstraint_Edge(Namespace container, DurationConstraint linkInstance, Element source, Element target) {
			return true;
		}

		/**
		 * @generated
		 */
		public boolean canExistDurationObservation_Edge(Package container, DurationObservation linkInstance, NamedElement source, NamedElement target) {
			return true;
		}
	}
}
