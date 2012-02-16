/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.mft.papyrus.ui;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.mylyn.mft.uml2.core.Uml2StructureBridge;

/**
 * @author Carsten Reckord
 */
public class Uml2GmfStructureBridge extends Uml2StructureBridge {

	@Override
	public Object getDomainObject(Object object) {
		if (object instanceof View) {
			EObject semanticElement = ((View) object).getElement();
			return semanticElement == null ? null : super.getDomainObject(semanticElement);
		}
		return super.getDomainObject(object);
	}

}
