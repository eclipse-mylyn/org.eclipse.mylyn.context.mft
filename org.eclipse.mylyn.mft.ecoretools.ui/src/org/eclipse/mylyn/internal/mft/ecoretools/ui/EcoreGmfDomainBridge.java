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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.mylyn.mft.emf.core.ecore.EcoreDomainBridge;

/**
 * @author Miles Parker
 */
public class EcoreGmfDomainBridge extends EcoreDomainBridge {

	@Override
	public Object getDomainObject(Object object) {
		if (object instanceof View) {
			EObject semanticElement = ((View) object).getElement();
			return semanticElement == null ? null : super.getDomainObject(semanticElement);
		}
		return super.getDomainObject(object);
	}

}