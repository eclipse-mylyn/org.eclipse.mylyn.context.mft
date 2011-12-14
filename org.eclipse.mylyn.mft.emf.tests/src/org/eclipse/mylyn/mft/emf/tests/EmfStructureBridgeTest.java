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

package org.eclipse.mylyn.mft.emf.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.mft.emf.core.ecore.EcoreDomainBridge;
import org.eclipse.mylyn.mft.sdk.util.AbstractEmfContextTest;

/**
 * @author Miles Parker
 */
public class EmfStructureBridgeTest extends AbstractEmfContextTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		structureModelBridge = new EcoreDomainBridge();
	}

	public void testSimpleHandle() {
		String elementHandle = "platform:/resource/org.eclipse.mylyn.modeling.tests.ecorediagram/model/library.ecore#//Book"; //$NON-NLS-1$
		Object objectForHandle = structureModelBridge.getObjectForHandle(elementHandle);
		assertTrue(objectForHandle instanceof EClass);
		assertEquals("Book", ((EClass) objectForHandle).getName()); //$NON-NLS-1$
	}

	public void testHandles() throws Exception {
		ResourceSet rs = new ResourceSetImpl();
		Resource resource = rs.getResource(URI.createPlatformResourceURI(
				"/org.eclipse.mylyn.modeling.tests.ecorediagram/model/library.ecore", false), true); //$NON-NLS-1$
		EObject eObject = resource.getEObject("//Book"); //$NON-NLS-1$
		EClass fragmentClass = (EClass) eObject;
		assertTrue(eObject instanceof EClass);
		String handleIdentifier = structureModelBridge.getHandleIdentifier(eObject);
		Object objectForHandle = structureModelBridge.getObjectForHandle(handleIdentifier);
		EClass obtainedClass = (EClass) objectForHandle;
		assertTrue("Same eobject", !eObject.equals(objectForHandle)); //$NON-NLS-1$
		assertEquals(fragmentClass.getClassifierID(), obtainedClass.getClassifierID());
	}

}
