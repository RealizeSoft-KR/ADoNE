/*****************************************************************************
 * Copyright (c) 2019 CEA LIST and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   CEA LIST - Initial API and implementation
 *
 * Additional Modifications:
 *   Copyright (c) 2024 RealizeSoft
 *   RealizeSoft - Optimized connection handle placement for improved modeling
 *   interaction by adjusting handler positions and visibility.
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandleLocator;

/**
 * Extends ConnectionHandleLocator to fine-tune handler positioning and visibility for an enhanced modeling experience.
 * This class implements adjustments such as transforming bottom handlers to the right side, removing left-side handlers,
 * precisely aligning right-side handlers with the BehaviorExecutionSpecification boundary, and hiding the Connection Handle
 * in the lifeline header area to avoid clutter and improve usability. These modifications ensure a more intuitive and
 * efficient interaction by optimizing the placement of connection handles with defined spacing for clearer manipulation
 * and control, thus providing a cleaner and more focused diagramming environment.
 */
public class AdoneConnectionHandleLocator extends ConnectionHandleLocator {

	/** number of pixels between connection handles */
	private static int HANDLE_SPACING = 20;

	/** the MARGIN to leave by the edge of the parent figure */
	private static int MARGIN = 2;

	/**
	 * the margin outside the shape in which the handles will appear to
	 * determine if the handles will be outside the viewport
	 */
	private static Dimension HANDLE_MARGIN = new Dimension(25, 25);

	/** list of connection handles for the shape */
	private List<ConnectionHandle> handles = new ArrayList<>();

	/** a point in the shape used when locating the handles */
	private Point cursorPosition = null;

	/** the parent figure */
	private IFigure reference = null;

	/** the side on which the handles will appear, value from PositionConstants */
	private int side = PositionConstants.WEST;

	/** the point on the border where the handles should appear */
	private Point borderPoint = new Point(0, 0);

	/**
	 * flag to indicate if the connection handle should only be on the east,
	 * west, or south side
	 */
	private boolean bEastWestSouth = true;

	private static final int LIFELINE_HEADER_HEIGHT = 40;

	/**
	 * Constructor for ConnectionHandleLocator.
	 *
	 * @param reference
	 *            the figure
	 * @param cursorPosition
	 *            the handles will be located on the border of the shape as
	 *            close to this position as possible
	 */
	public AdoneConnectionHandleLocator(IFigure reference, Point cursorPosition) {
		super(reference, cursorPosition);
		this.reference = reference;
		this.cursorPosition = cursorPosition;
	}

	/**
	 * Adds a connection handle to the list of handles.
	 *
	 * @param handle
	 *            the <code>ConnectionHandle</code> to add
	 */
	@Override
	public void addHandle(ConnectionHandle handle) {
		handles.add(handle);
	}

	/**
	 * Return the client area of the viewport. If a viewport is not found, a
	 * default rectangle is returned.
	 *
	 * @return the client area of the viewport
	 */
	private Rectangle getViewportArea() {
		IFigure fig = getReference();
		while (fig != null) {
			if (fig instanceof Viewport) {
				return fig.getClientArea();
			}
			fig = fig.getParent();
		}
		return new Rectangle(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/**
	 * Updates the positioning and availability of handlers in relation to the BehaviorExecutionSpecification boundary. This method:
	 * - Transforms the bottom handler into a right-side handle for improved interaction dynamics.
	 * - Eliminates the left-side handle to streamline the interface and focus user interactions on relevant controls.
	 * - Relocates the right-side handler to closely align with the BehaviorExecutionSpecification boundary, enhancing the precision
	 * and intuitiveness of modeling actions. These adjustments optimize the user's control over modeling elements by ensuring
	 * handlers are both minimally intrusive and strategically placed for effective manipulation.
	 */
	private void resetBorderPointAndSide() {
		Rectangle bounds = getReferenceFigureBounds();

		// Get the point on the edge of the reference figure nearest to the
		// cursor position.
		int westDiff = cursorPosition.x - bounds.x;
		int eastDiff = bounds.getRight().x - cursorPosition.x;
		int southDiff = bounds.getBottom().y - cursorPosition.y;
		int northDiff = isEastWestSouth() ? Integer.MAX_VALUE
				: cursorPosition.y - bounds.y;

		// avoid having the handles appear outside the viewport
		Rectangle viewportRect = getViewportArea();
		Rectangle absBounds = bounds.getCopy();
		getReference().translateToAbsolute(absBounds);
		absBounds.translate(viewportRect.getLocation());

		Dimension handleMargin = getHandleMargin();
		if (absBounds.right() + handleMargin.width > viewportRect.right()) {
			// don't use east side
			eastDiff = Integer.MAX_VALUE;
		} else if (absBounds.x - handleMargin.width < viewportRect.x) {
			// don't use west side
			westDiff = Integer.MAX_VALUE;
		}
		if (absBounds.bottom() + handleMargin.height > viewportRect.bottom()) {
			// don't use south side
			southDiff = Integer.MAX_VALUE;
		} else if (absBounds.y - handleMargin.height < viewportRect.y) {
			// don't use north side
			northDiff = Integer.MAX_VALUE;
		}

		double minLR = Math.min(eastDiff, westDiff);
		double minUD = Math.min(southDiff, northDiff);
		// which directions is closest North/South or East/West?
		boolean bUp = (minUD < minLR);

		if (bUp) {
			if (northDiff < southDiff) {
				// re-evaluate if the north is closer than the east since the
				// handles will be placed in the north-west corner because of
				// the action bar.
				if (eastDiff < westDiff
						&& eastDiff < cursorPosition.getDistance(bounds
								.getTopLeft())) {
					borderPoint.setLocation(bounds.x + bounds.width,
							cursorPosition.y);
					side = PositionConstants.EAST;
				} else {
					borderPoint.setLocation(bounds.x, bounds.y);
					side = PositionConstants.NORTH;
				}
			} else {
				borderPoint.setLocation(bounds.x + bounds.width / 2 + 5,
						cursorPosition.y); // Change bottom handler to right-side handle.
				side = PositionConstants.EAST;

			}
		} else {
			if (westDiff < eastDiff) {
				// Remove left-side handle.

			} else {
				borderPoint.setLocation(bounds.x + bounds.width / 2 + 5,
						cursorPosition.y); // Move right-side handler to the boundary of BehaviorExecutionSpecification.
				side = PositionConstants.EAST;
			}
		}
	}

	/**
	 * Gets the side (West, South, or East) on which the handles will appear,
	 * using the cursor position passed in to the constructor.
	 *
	 * @return the side, a value in PositionContstants
	 */
	@Override
	public int getBorderSide() {
		resetBorderPointAndSide();
		return side;
	}

	/**
	 * Locates the figure on the appropriate edge of the parent depending on
	 * which side is closest to the reference point. Then adjusts the figure
	 * depending on the number of handles that this locator has so that the
	 * handles are spaced out from the center along the edge of the shape.
	 *
	 * @see org.eclipse.draw2d.Locator#relocate(org.eclipse.draw2d.IFigure)
	 */
	@Override
	public void relocate(IFigure target) {
		Rectangle bounds = getReferenceFigureBounds();
		resetBorderPointAndSide();
		Point borderPointTranslated = borderPoint.getCopy();

		// Hide the Connection Handle in the lifeline header area (2024-02-23)
		if (borderPointTranslated.y < LIFELINE_HEADER_HEIGHT) {
			return;
		}

		reference.translateToAbsolute(bounds);
		target.translateToRelative(bounds);
		reference.translateToAbsolute(borderPointTranslated);
		target.translateToRelative(borderPointTranslated);

		int width = target.getBounds().width;
		int halfWidth = width / 2;

		int height = target.getBounds().height;
		int halfHeight = height / 2;

		// adjust border point if the handles will extend past the bounds of the
		// shape
		if (side == PositionConstants.WEST || side == PositionConstants.EAST) {
			int halfTotalHeight = (height * handles.size() + HANDLE_SPACING
					* (handles.size() - 1)) / 2;
			if (borderPointTranslated.y - halfTotalHeight < bounds.getTop().y) {
				borderPointTranslated.y = bounds.getTop().y + halfTotalHeight;
			} else if (borderPointTranslated.y + halfTotalHeight > bounds
					.getBottom().y) {
				borderPointTranslated.y = bounds.getBottom().y
						- halfTotalHeight;
			}
		} else {
			int halfTotalWidth = (width * handles.size() + HANDLE_SPACING
					* (handles.size() - 1)) / 2;
			if (borderPointTranslated.x - halfTotalWidth < bounds.getLeft().x) {
				borderPointTranslated.x = bounds.getLeft().x + halfTotalWidth;
			} else if (borderPointTranslated.x + halfTotalWidth > bounds
					.getRight().x) {
				borderPointTranslated.x = bounds.getRight().x - halfTotalWidth;
			}
		}

		// set location based on side
		if (side == PositionConstants.WEST) {
			target.setLocation(borderPointTranslated
					.getTranslated(new Dimension(-(width + MARGIN), -halfHeight)));
		} else if (side == PositionConstants.EAST) {
			target.setLocation(borderPointTranslated
					.getTranslated(new Dimension(MARGIN, -halfHeight)));
		} else if (side == PositionConstants.SOUTH) {
			target.setLocation(borderPointTranslated
					.getTranslated(new Dimension(-halfWidth, MARGIN)));
		} else if (side == PositionConstants.NORTH) {
			target.setLocation(borderPointTranslated
					.getTranslated(new Dimension(MARGIN, -height - MARGIN)));
		}

		// space out handles
		int index = handles.indexOf(target);
		double centerOffset = index - (handles.size() / 2.0) + 0.5;

		if (side == PositionConstants.WEST || side == PositionConstants.EAST) {
			target.translate(0, (int) (centerOffset * HANDLE_SPACING));
		} else {
			target.translate((int) (centerOffset * HANDLE_SPACING), 0);
		}

	}

	/**
	 * Gets the bounding rectangle of the reference figure.
	 *
	 * @return the bounds
	 */
	private Rectangle getReferenceFigureBounds() {
		Rectangle bounds = reference instanceof HandleBounds ? ((HandleBounds) reference)
				.getHandleBounds().getCopy()
				: reference.getBounds().getCopy();
		return bounds;
	}

	/**
	 * Gets the reference figure.
	 *
	 * @return Returns the reference.
	 */
	@Override
	protected IFigure getReference() {
		return reference;
	}

	/**
	 * Gets the cursor position.
	 *
	 * @return Returns the cursorPosition.
	 */
	@Override
	protected Point getCursorPosition() {
		return cursorPosition;
	}

	/**
	 * Can the connection handle can only be located on the east, west, or south
	 * side of the reference figure.
	 *
	 * @return Returns true if the connection handle can only be located on the
	 *         east, west, or south side of the reference figure; false
	 *         otherwise.
	 */
	@Override
	public boolean isEastWestSouth() {
		return bEastWestSouth;
	}

	/**
	 * Set to true if the connection handle can only be located on the east,
	 * west, or south side of the reference figure.
	 *
	 * @param eastWestSouth
	 *            The bEastWestSouth to set.
	 */
	@Override
	public void setEastWestSouth(boolean eastWestSouth) {
		bEastWestSouth = eastWestSouth;
	}

	/**
	 * Returns the margin outside the shape in which the handles will appear to
	 * determine if the handles will be outside the viewport.
	 *
	 * @return the margin
	 */
	@Override
	protected Dimension getHandleMargin() {
		return HANDLE_MARGIN;
	}
}
