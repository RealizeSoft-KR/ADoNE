/*****************************************************************************
 * Copyright (c) 2018 CEA LIST, EclipseSource and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Prepared for future modifications and enhancements
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.anchors;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Point;


/**
 * Anchors a Connection to the target of a {@link PolylineConnection} (Typically the receiveEvent of a Message)
 */
public class AdoneConnectionTargetAnchor extends ConnectionTargetAnchor {

	/**
	 * Constructor.
	 *
	 * @param anchorage
	 */
	public AdoneConnectionTargetAnchor(PolylineConnection anchorage) {
		super(anchorage);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see org.eclipse.papyrus.uml.diagram.sequence.anchors.ConnectionTargetAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 *
	 * @param reference
	 * @return
	 */
	@Override
	public Point getLocation(Point reference) {
		// 아래 내용 확인 필요 (2023-12-08)
		// Point center = getOwner().getBounds().getCenter().getCopy();
		// getOwner().translateToAbsolute(center);
		// return center;

		return super.getLocation(reference);
	}

}
