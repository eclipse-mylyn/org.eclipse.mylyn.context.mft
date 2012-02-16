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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.mylyn.mft.emf.core.EmfStructureBridge;

/**
 * @author Miles Parker
 */
public abstract class GmfStructureBridge extends EmfStructureBridge {

	/**
	 * Maps the diagram object to the domain object in the most general way possible. GMF diagram implementations
	 * typically shouldn't need to override this.
	 */
	@Override
	public Object getDomainObject(Object object) {
		if (object instanceof View) {
			EObject semanticElement = ((View) object).getElement();
			return semanticElement == null ? null : super.getDomainObject(semanticElement);
		}
		return super.getDomainObject(object);
	}

}
