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
package org.eclipse.mylyn.internal.mft.gmf.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Miles Parker
 */
public class RemoveModelFromContextCommandHandler extends AbstractHandler {

	private final RemoveModelFromContextAction action = new RemoveModelFromContextAction();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		action.selectionChanged(null, HandlerUtil.getCurrentSelection(event));
		action.run(null);
		return null;
	}

}