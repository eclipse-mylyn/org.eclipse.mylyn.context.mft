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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoration;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecorator;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget;
import org.eclipse.mylyn.mft.gmf.ui.figures.IRevealableFigure;

/**
 * @author Miles Parker
 */
public abstract class ContextDecorator implements IDecorator {

	private final ContextDecoratorProvider provider;

	boolean wasInteresting;

	boolean wasLandmark;

	boolean wasFocussed;

	boolean initialized;

	private final List<IRevealableFigure> decorationFigures;

	private final List<IDecoration> decorations;

	private final IDecoratorTarget target;

	public ContextDecorator(ContextDecoratorProvider provider, IDecoratorTarget target) {
		this.provider = provider;
		this.target = target;
		decorationFigures = new ArrayList<IRevealableFigure>();
		decorations = new ArrayList<IDecoration>();
		target.installDecorator(ContextDecoratorProvider.MYLYN_DETAIL, this);
	}

	public void activate() {
		refresh();
	}

	public void deactivate() {
		initialized = false;
		removeDecorations();
	}

	public void refresh() {
		boolean interesting = isInteresting();
		boolean landmark = isLandmark();
		boolean focussed = provider.isFocussed();
		if (focussed != wasFocussed || interesting != wasInteresting || landmark != wasLandmark || !initialized) {
			removeDecorations();
			if (focussed) {
				createDecoration();
			}
		}
		wasInteresting = interesting;
		wasLandmark = landmark;
		wasFocussed = focussed;
		initialized = true;
	}

	protected abstract void createDecoration();

	IDecoration addDecoration(IRevealableFigure decorationFigure) {
		GraphicalEditPart ownerEditPart = (GraphicalEditPart) getDecoratorTarget().getAdapter(GraphicalEditPart.class);
		if (ownerEditPart == null) {
			throw new RuntimeException();
		}
		IDecoration decoration = getDecoratorTarget().addDecoration(decorationFigure, decorationFigure, true);
		decorationFigures.add(decorationFigure);
		decorations.add(decoration);
		return decoration;
	}

	protected void removeDecorations() {
		for (IRevealableFigure figure : decorationFigures) {
			figure.restore();
			IGraphicalEditPart part = (IGraphicalEditPart) getTarget().getAdapter(IGraphicalEditPart.class);
			RevealMouseListener listenerForRoot = provider.getListenerForRoot(part.getRoot());
			if (listenerForRoot != null) {
				listenerForRoot.removeDecoration(figure);
			}
		}
		decorationFigures.clear();
		for (IDecoration decoration : decorations) {
			getDecoratorTarget().removeDecoration(decoration);
		}
		decorations.clear();
	}

	public ContextDecoratorProvider getProvider() {
		return provider;
	}

	IDecoratorTarget getDecoratorTarget() {
		return getTarget();
	}

	public abstract boolean isInteresting();

	public abstract boolean isLandmark();

	public IDecoratorTarget getTarget() {
		return target;
	}

	IGraphicalEditPart getEditPart() {
		return (IGraphicalEditPart) getTarget().getAdapter(IGraphicalEditPart.class);
	}

	/**
	 * Assumes that part has already been created.
	 * 
	 * @return
	 */
	public IFigure getDecoratedFigure() {
		if (getEditPart() != null) {
			return getEditPart().getFigure();
		}
		return null;
	}

	public List<IRevealableFigure> getDecorationFigures() {
		return decorationFigures;
	}

}
