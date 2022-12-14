/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.mft.gmf.ui;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.Decoration;
import org.eclipse.mylyn.mft.gmf.ui.figures.IRevealableFigure;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;

/**
 * @author Miles Parker
 */
public class RevealMouseListener implements MouseMoveListener, MouseTrackListener {

	private static final int REVEAL_DISTANCE = 180;

	// private final RootEditPart root;

	private Collection<IRevealableFigure> lastDecorations = new HashSet<IRevealableFigure>();

	private final IFigure layer;

	RevealMouseListener(IFigure layer) {
		this.layer = layer;
	}

	private Collection<IRevealableFigure> getTargetFigures(Point mousePoint) {
		Rectangle revealBounds = new Rectangle(mousePoint, new Dimension(REVEAL_DISTANCE * 2, REVEAL_DISTANCE * 2));
		layer.translateFromParent(revealBounds);//translate into viewport's client area coordinates
		revealBounds.translate(-REVEAL_DISTANCE, -REVEAL_DISTANCE);
		HashSet<IRevealableFigure> found = new HashSet<IRevealableFigure>();
		findChildFigure(layer, revealBounds, found);
		return found;
	}

	private void findChildFigure(IFigure parent, Rectangle revealBounds, HashSet<IRevealableFigure> found) {
		for (Object object : parent.getChildren()) {
			IFigure child = (IFigure) object;
			Rectangle clientArea = child.getClientArea();
			Rectangle childRevealBounds = revealBounds.getCopy();
			child.translateFromParent(childRevealBounds);
			if (childRevealBounds.intersects(clientArea)) {
				// only reveal outer-most
				IRevealableFigure figure = getRevealableMember(child);
				if (figure != null) {
					found.add(figure);
				}
				findChildFigure(child, childRevealBounds, found);
			}
		}
	}

	private void findAllChildFigures(IFigure parent, HashSet<IRevealableFigure> found) {
		for (Object object : parent.getChildren()) {
			IFigure child = (IFigure) object;
			IRevealableFigure figure = getRevealableMember(child);
			if (figure != null) {
				found.add(figure);
			}
			findAllChildFigures(child, found);
		}
	}

	public IRevealableFigure getRevealableMember(IFigure candFigure) {
		if (candFigure instanceof IRevealableFigure) {
			return (IRevealableFigure) candFigure;
		} else if (candFigure instanceof Decoration) {
			for (Object object : ((Decoration) candFigure).getChildren()) {
				//there should only be one for each decoration?
				if (object instanceof IRevealableFigure) {
					return (IRevealableFigure) object;
				}
			}
		} else if (candFigure.getParent() != null) {
			return getRevealableMember(candFigure.getParent());
		}
		return null;
	}

	private int distance(Rectangle rectangle, Point point) {
		int dx = 0;
		if (point.x < rectangle.x) {
			dx = rectangle.x - point.x;
		} else if (point.x > rectangle.right()) {
			dx = point.x - rectangle.right();
		}
		int dy = 0;
		if (point.y < rectangle.y) {
			dy = rectangle.y - point.y;
		} else if (point.y > rectangle.bottom()) {
			dy = point.y - rectangle.bottom();
		}
		return (int) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	private double nearness(IFigure figure, Point point) {
		Rectangle clientArea = figure.getClientArea().getCopy();
		figure.translateToAbsolute(clientArea);
		double d = distance(clientArea, point);
		d = Math.min(d, REVEAL_DISTANCE);
		double n = 1.0 - (d / REVEAL_DISTANCE);
		return n;
	}

	public void mouseMove(MouseEvent e) {
		Point mousePoint = new Point(e.x, e.y);
		Collection<IRevealableFigure> newDecorations = getTargetFigures(mousePoint);

		if (!newDecorations.equals(lastDecorations)) {
			Collection<IRevealableFigure> removedFigures = new HashSet<IRevealableFigure>(lastDecorations);
			removedFigures.removeAll(newDecorations);
			for (IRevealableFigure removedFigure : removedFigures) {
				if (removedFigure.getParent() != null && removedFigure.getParent().getParent() != null) {
					removedFigure.unreveal();
				}
			}
		}
		for (IRevealableFigure figure : newDecorations) {
			double n = nearness(figure, mousePoint);
			figure.reveal(n);
		}
		lastDecorations = newDecorations;
	}

	public void removeDecoration(IFigure decoration) {
		lastDecorations.remove(decoration);
	}

	public void mouseEnter(MouseEvent e) {
	}

	public void mouseExit(MouseEvent e) {
		HashSet<IRevealableFigure> allFigures = new HashSet<IRevealableFigure>();
		findAllChildFigures(layer, allFigures);
		for (IRevealableFigure revealableFigure : allFigures) {
			revealableFigure.unreveal();
		}
	}

	public void mouseHover(MouseEvent e) {
		// ignore

	}

}