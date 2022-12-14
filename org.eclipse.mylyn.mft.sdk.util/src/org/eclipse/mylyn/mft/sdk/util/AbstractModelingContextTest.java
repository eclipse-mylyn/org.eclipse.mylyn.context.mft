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

package org.eclipse.mylyn.mft.sdk.util;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;

/**
 * @author Miles Parker
 */
public class AbstractModelingContextTest extends AbstractContextTest {

	protected AbstractContextStructureBridge structureModelBridge;

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected InteractionContext context;

	protected InteractionContextScaling scaling = new InteractionContextScaling();

	protected String taskId = getClass().getName();

	@Override
	protected void setUp() throws Exception {
		WorkspaceSetupHelper.setupWorkspace();

		context = new InteractionContext(taskId, scaling);
		context.reset();
		manager.internalActivateContext(context);
		ContextUiPlugin.getViewerManager().setSyncRefreshMode(true);

		assertEquals(context.getAllElements().size(), 0);
	}

	@Override
	protected void tearDown() throws Exception {
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(taskId);
		manager.deleteContext(taskId);
		ContextCorePlugin.getContextStore().getFileForContext(taskId).delete();

		for (InteractionContext context : manager.getActiveContexts()) {
			manager.deactivateContext(context.getHandleIdentifier());
		}
		assertFalse(manager.isContextActive());
		super.tearDown();
	}

}
