/*****************************************************************************
 * Copyright (c) 2011 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *
 *		CEA LIST - Initial API and implementation
 *      Vincent Lorenzo - bug 492522
 *      Benoit Maggi (CEA LIST) benoit.maggi@cea.fr - bug 514289
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Enhanced bulk deletion in sequence diagrams by providing
 *   a comprehensive removal solution for CombinedFragments and their nested elements
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.EditCommandRequestWrapper;
import org.eclipse.gmf.runtime.emf.type.core.IClientContext;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.gmfdiag.common.Activator;
import org.eclipse.papyrus.infra.gmfdiag.common.editpolicies.DefaultSemanticEditPolicy;
import org.eclipse.papyrus.infra.services.edit.context.TypeContext;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractMessageEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.requests.AdoneEditCommandRequestWrapper;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneDeleteActionUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.AdoneSequenceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.uml.CombinedFragment;

/**
 * Extends DefaultSemanticEditPolicy to support enhanced bulk deletion functionality within sequence diagrams,
 * focusing on CombinedFragments and their nested elements. Offers a dialog for bulk deletion confirmation,
 * facilitating comprehensive removal of CombinedFragments along with all child elements, including nested
 * CombinedFragments and Messages. This policy aims to rectify issues related to dangling elements post-deletion
 * and streamline the deletion process in complex diagrams. Future revisions are encouraged to address deletion
 * anomalies and ensure the integrity of the diagram.
 */
public class AdoneCombinedFragmentDefaultSemanticEditPolicy extends DefaultSemanticEditPolicy {

	public static final Object SEMANTIC_ROLE = "adoneSemanticRole";

	/**
	 * Extends the deletion process to offer a dialog for bulk deletion of a CombinedFragment and all its child elements.
	 * This enhanced approach allows users to efficiently manage sequence diagrams by facilitating the removal of complex
	 * structures in a single operation. Upon initiating a delete request for a CombinedFragment, the user is prompted to confirm
	 * the deletion of not just the CombinedFragment but all associated child elements, ensuring a thorough cleanup with minimal effort.
	 *
	 * @param request
	 *            The request that triggers command generation, potentially involving the deletion of diagram elements.
	 * @return A Command that, when executed, performs the bulk deletion operation as confirmed by the user, or null if the operation is canceled or not applicable.
	 */
	@Override
	public Command getCommand(Request request) {

		// To-Do: When CombinedFragmen is recursively nesting many combinedFragments inside,
		// cases have been found where some Messages and BehaviorExecutionSpecifications are not
		// deleted and remain in a Dangling state. Future efforts should be directed towards resolving this error.

		if (request instanceof EditCommandRequestWrapper) {

			final IEditCommandRequest editCommandRequest = ((EditCommandRequestWrapper) request).getEditCommandRequest();

			if (editCommandRequest instanceof DestroyElementRequest) {

				CompoundCommand deleteCfCompoundCommand = new CompoundCommand();

				Command command = super.getCommand(request);

				if (command != null && command.canExecute()) {
					deleteCfCompoundCommand.add(command);

					// To prevent recursive calls for nested CFs. That is, the selected (root) CF is passed,
					// but the nested CFs inside the selected CF are wrapped in the AdoneEditCommandRequestWrapper
					// class to prevent this logic from being called recursively.
					// It seems like this class might not be needed if using the setExtendedData of the wrapper.
					// Anyway, this will be reviewed later. (2024-02-08)
					if (!(request instanceof AdoneEditCommandRequestWrapper)) {
						// Only for the topmost CF, a dialog is used to ask the user for bulk deletion.
						Shell shell = new Shell();
						MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
						dialog.setText("Bulk Deletion Confirmation");
						dialog.setMessage("Delete the selected element and all its child elements?");

						int response = dialog.open();
						if (response == SWT.YES) {

							CombinedFragment cf = (CombinedFragment) ((DestroyElementRequest) editCommandRequest).getElementToDestroy();
							CombinedFragmentEditPart cfEp = (CombinedFragmentEditPart) AdoneSequenceUtil.getEditPartFromSemantic(getHost(), cf);
							List<GraphicalEditPart> nestedChildEditParts = AdoneSequenceUtil.getCombinedFragmentNestedEps(cfEp);

							for (GraphicalEditPart nestedChildEp : nestedChildEditParts) {
								if (nestedChildEp instanceof CombinedFragmentEditPart) {
									DestroyElementRequest destroyCfRequest = new DestroyElementRequest(false);
									AdoneEditCommandRequestWrapper destroyCfReqeustWrapper = new AdoneEditCommandRequestWrapper(destroyCfRequest);
									Command deleteCfCommand = nestedChildEp.getCommand(destroyCfReqeustWrapper);
									if (deleteCfCommand != null && deleteCfCommand.canExecute()) {
										deleteCfCompoundCommand.add(deleteCfCommand);
									}

								} else if (nestedChildEp instanceof AbstractMessageEditPart) {
									AbstractMessageEditPart msgEp = (AbstractMessageEditPart) nestedChildEp;
									Command deleteMessageCommand = AdoneDeleteActionUtil.getDeleteFromModelCommand(msgEp, getEditingDomain());
									if (deleteMessageCommand != null && deleteMessageCommand.canExecute()) {
										deleteCfCompoundCommand.add(deleteMessageCommand);
									}
								}
							}
						}

						shell.dispose();
					}
				}

				return deleteCfCompoundCommand.unwrap();
			}
		}

		return super.getCommand(request);
	}

	/**
	 * To address issues encountered during Bulk Deletion, this method has been redefined to simplify
	 * the content of the super method. The phenomenon of CombinedFragments or Messages not being deleted
	 * appears to be related to the redefinition of this method.
	 * Therefore, a thorough review and necessary modifications are advised in the future to resolve these issues.
	 */
	@Override
	protected Command getDestroyElementCommand(DestroyElementRequest req) {

		try {
			IClientContext clientContext = TypeContext.getContext(getEditingDomain());

			IElementEditService commandService;
			commandService = ElementEditServiceUtils.getCommandProvider(((IGraphicalEditPart) getHost()).resolveSemanticElement(), clientContext);

			if (commandService != null) {
				ICommand semanticCommand = commandService.getEditCommand(req);
				if ((semanticCommand != null && semanticCommand.canExecute())) {
					return getGEFWrapper(semanticCommand);
				}
			}
		} catch (ServiceException e) {
			Activator.log.error(e);
		}
		return UnexecutableCommand.INSTANCE;
	}

}
