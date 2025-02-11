/*****************************************************************************
 * Copyright (c) 2016, 2018 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *   Mickaël ADAM (ALL4TEC) mickael.adam@all4tec.net - Bug 526079
 *   Nicolas FAUVERGUE (CEA LIST) nicolas.fauvergue@cea.fr - Bug 538466
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Customized behavior execution specification creation and
 *   resizing in sequence diagrams for improved layout consistency and simplicity.
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.referencialgrilling;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.infra.gmfdiag.common.utils.DiagramEditPartsUtil;
import org.eclipse.papyrus.uml.diagram.sequence.command.SetResizeAndLocationCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AdoneBehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.service.types.element.UMLDIElementTypes;
import org.eclipse.papyrus.uml.service.types.utils.ElementUtil;

/**
 * Extends LifeLineXYLayoutEditPolicy to specifically tailor the creation and resizing of
 * BehaviorExecutionSpecifications within sequence diagrams. It ensures that these elements
 * are created with default dimensions that align with the diagram's layout considerations,
 * such as grid spacing. Furthermore, this class simplifies the process of changing these elements' constraints,
 * focusing on adjusting their geometric bounds without altering Strong References or other properties.
 * This approach enhances the visual coherence and standardization of execution specifications across diagrams.
 */
public class AdoneLifeLineXYLayoutEditPolicy extends LifeLineXYLayoutEditPolicy {


	/**
	 * Adjusts the default height and width of BehaviorExecutionSpecifications to align with the overall layout of the diagram,
	 * considering grid spacing and visual consistency. This method ensures that each BehaviorExecutionSpecification, when created,
	 * has dimensions that are harmonious with the diagram's grid and spacing settings, thereby enhancing the visual coherence and
	 * readability of the sequence diagram. The adjustments reflect a thoughtful consideration of the diagram's layout, aiming to
	 * provide a standardized appearance for these elements across different instances and diagrams.
	 */
	@Override
	protected Object getConstraintFor(CreateRequest request) {
		Object constraint = super.getConstraintFor(request);
		if (request instanceof CreateViewAndElementRequest) {
			CreateViewAndElementRequest req = (CreateViewAndElementRequest) request;
			ViewAndElementDescriptor descriptor = (req).getViewAndElementDescriptor();
			IElementType elementType = descriptor.getElementAdapter().getAdapter(IElementType.class);
			if (ElementUtil.isTypeOf(elementType, UMLDIElementTypes.ACTION_EXECUTION_SPECIFICATION_SHAPE) ||
					ElementUtil.isTypeOf(elementType, UMLDIElementTypes.BEHAVIOR_EXECUTION_SPECIFICATION_SHAPE)) {
				if (constraint instanceof Rectangle) {
					Rectangle constraintRect = (Rectangle) constraint;
					RootEditPart drep = getHost().getRoot();
					if (drep instanceof DiagramRootEditPart) {

						double spacing = ((DiagramRootEditPart) drep).getGridSpacing();

						// Modify default height
						constraintRect.height = AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT;
						// }
						// Modify default width
						constraintRect.width = AdoneBehaviorExecutionSpecificationEditPart.DEFAUT_WIDTH;

						Rectangle absoluteBounds = constraintRect.getCopy();

						getHostFigure().translateToAbsolute(absoluteBounds);

						if (DiagramEditPartsUtil.isSnapToGridActive(getHost())) {
							int modulo = AdoneBehaviorExecutionSpecificationEditPart.DEFAULT_HEIGHT / (int) spacing;
							constraintRect.height = modulo * (int) spacing;
						}

						constraint = constraintRect;
					}
				}
			}
		}
		return constraint;
	}

	/**
	 * Overrides the superclass method to adjust the bounds of a BehaviorExecutionSpecification without modifying its
	 * Strong References or additional properties. This streamlined approach focuses solely on updating the geometric
	 * bounds of the specified element within the sequence diagram. By eliminating the adjustment of Strong References
	 * and other ancillary properties, this method ensures a more targeted and efficient update process, directly
	 * addressing the spatial positioning and size of the element in question.
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {

		Rectangle newBounds = (Rectangle) constraint;
		View shapeView = (View) child.getModel();

		TransactionalEditingDomain editingDomain = ((IGraphicalEditPart) getHost()).getEditingDomain();
		ICommand boundsCommand = new SetResizeAndLocationCommand(editingDomain, DiagramUIMessages.SetLocationCommand_Label_Resize,
				new EObjectAdapter(shapeView), newBounds);

		CompoundCommand compoundCommand = new CompoundCommand();
		compoundCommand.add(new ICommandProxy(boundsCommand));

		return compoundCommand;
	}

}
