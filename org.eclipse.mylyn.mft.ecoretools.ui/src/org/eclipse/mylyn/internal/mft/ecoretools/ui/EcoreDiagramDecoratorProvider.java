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

package org.eclipse.mylyn.internal.mft.ecoretools.ui;

import org.eclipse.mylyn.mft.emf.ui.DiagramUiBridge;
import org.eclipse.mylyn.mft.gmf.ui.ContextDecoratorProvider;

/**
 * @author Miles Parker
 */
public class EcoreDiagramDecoratorProvider extends ContextDecoratorProvider {

	@Override
	public DiagramUiBridge getDomainUIBridge() {
		return EcoreDiagramUiBridge.getInstance();
	}

}
