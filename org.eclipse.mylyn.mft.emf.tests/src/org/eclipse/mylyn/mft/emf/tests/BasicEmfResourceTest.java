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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourceInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiPreferenceInitializer;
import org.eclipse.mylyn.mft.sdk.util.AbstractEmfContextTest;

/**
 * @author Miles Parker
 */
public class BasicEmfResourceTest extends AbstractEmfContextTest {

	protected ResourceInteractionMonitor resmonitor = new ResourceInteractionMonitor();

	protected ResourceStructureBridge resourceBridge = new ResourceStructureBridge();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
//		resourceModelBridge = new EcoreDomainBridge();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(true);

		ContextTestUtil.triggerContextUiLazyStart();
		// disable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, false);
	}

	public void testResourceSelect() throws CoreException {
		IFile file = getEmfProject().getProject().getFile("model/library.ecore"); //$NON-NLS-1$
		assertTrue(file.exists());

		// opening editors can cause selection events
		context.reset();

		String handleIdentifier = resourceBridge.getHandleIdentifier(file);
		IInteractionElement element = ContextCore.getContextManager().getElement(handleIdentifier);
		assertNotNull(element);
		assertNotNull(element.getInterest());
		assertFalse(element.getInterest().isInteresting());

		PackageExplorerPart pe = PackageExplorerPart.openInActivePerspective();
		printContext(ContextCore.getContextManager().getActiveContext());
		resmonitor.selectionChanged(pe, new StructuredSelection(file));
		printContext(ContextCore.getContextManager().getActiveContext());
		element = ContextCore.getContextManager().getElement(handleIdentifier);
		assertNotNull(element);
	}

	public static void printContext(IInteractionContext activeContext) {
		//sure diagnostics already exist somewhere, too lazy to find it..
		for (IInteractionElement elem : activeContext.getAllElements()) {
			System.err.println(elem + " " + elem.getContentType()); //$NON-NLS-1$
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourcesUiBridgePlugin.getInterestUpdater().setSyncExec(false);
		// re-enable ResourceModifiedDateExclusionStrategy
		ResourcesUiBridgePlugin.getDefault()
				.getPreferenceStore()
				.setValue(ResourcesUiPreferenceInitializer.PREF_MODIFIED_DATE_EXCLUSIONS, true);
	}

}
