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

package org.eclipse.mylyn.mft.papyrus.tests;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.impl.ShapeImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.java.WorkspaceSetupHelper;
import org.eclipse.mylyn.internal.mft.papyrus.ui.Uml2GmfStructureBridge;
import org.eclipse.mylyn.internal.mft.papyrus.ui.Uml2UiBridge;
import org.eclipse.mylyn.mft.emf.core.DomainModelContextStructureBridge;
import org.eclipse.mylyn.mft.emf.ui.DiagramUiEditingMonitor;
import org.eclipse.mylyn.mft.sdk.util.AbstractEmfContextTest;
import org.eclipse.papyrus.editor.PapyrusMultiDiagramEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;

/**
 * @author Miles Parker
 */
public class PapyrusDiagramEditorTest extends AbstractEmfContextTest {

	private static final String RESOURCE_URI = "platform:/resource/org.eclipse.mylyn.emf.tests.papyrus/model/model.uml#_xkh2ALJFEeCYupgj-BJj-Q"; //$NON-NLS-1$

	protected DomainModelContextStructureBridge structureBridge;

	private IJavaProject papyrusProject;

	private DiagramUiEditingMonitor monitor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		structureBridge = new Uml2GmfStructureBridge();
		monitor = new DiagramUiEditingMonitor(structureBridge, Uml2UiBridge.getInstance());
		String projectName = "org.eclipse.mylyn.modeling.tests.papyrus"; //$NON-NLS-1$
		papyrusProject = WorkspaceSetupHelper.createJavaPluginProjectFromDirectory(
				CommonTestUtil.getFile(this, "testdata/" + projectName), projectName); //$NON-NLS-1$
		papyrusProject.open(new NullProgressMonitor());
	}

	public void testSelection() throws Exception {

		papyrusProject.open(new NullProgressMonitor());
		IProject project = papyrusProject.getProject();
		IFile file = project.getFile("model/model.di"); //$NON-NLS-1$
		assertNotNull(file);

		assertTrue(file.exists());
		FileEditorInput input = new FileEditorInput(file);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		PapyrusMultiDiagramEditor ed = (PapyrusMultiDiagramEditor) page.openEditor(input,
				"org.eclipse.papyrus.infra.core.papyrusEditor");

		System.out.println(ContextCore.getContextManager().getActiveContext().getAllElements());

		String elemURI = RESOURCE_URI;
		IInteractionElement element = ContextCore.getContextManager().getElement(elemURI);
		assertNotNull(element);
		assertFalse(element.getInterest().isInteresting());
		IInteractionElement iInteractionElement = ContextCore.getContextManager().getActiveContext().get(RESOURCE_URI);
		assertFalse(iInteractionElement.getInterest().isInteresting());

		ShapeImpl bookShape = (ShapeImpl) ed.getEditingDomain()
				.getResourceSet()
				.getResources()
				.get(0)
				.getContents()
				.get(0)
				.eContents()
				.get(0);

		ClassImpl book = (ClassImpl) bookShape.getElement();
		String name = book.getName();
		assertEquals("Book", name); //$NON-NLS-1$

		List<?> findEditPartsForElement = ed.getDiagramGraphicalViewer().findEditPartsForElement(
				EMFCoreUtil.getProxyID(book), ShapeEditPart.class);

		assertEquals(findEditPartsForElement.size(), 1);
		StructuredSelection selection = new StructuredSelection(findEditPartsForElement);
		monitor.handleWorkbenchPartSelection(ed, selection, true);

		Thread.sleep(5000);
		//TODO why doesn't this work?
//		ed.getDiagramGraphicalViewer().setSelection(selection);
//		ed.getDiagramGraphicalViewer().getRootEditPart().refresh();
//		assertTrue(ed.getDiagramGraphicalViewer().getSelectedEditParts().get(0) instanceof EClassEditPart);

		assertNotNull(element);
		assertNotNull(element.getInterest());

		assertNotNull(iInteractionElement);

		IInteractionElement element2 = ContextCore.getContextManager().getElement(RESOURCE_URI);
		iInteractionElement = ContextCore.getContextManager().getActiveContext().get(RESOURCE_URI);

		//TODO why doesn't this work? Can we fix?
//		assertTrue(iInteractionElement.getInterest().isInteresting());
//		assertTrue(element2.getInterest().isInteresting());

//		assertEquals(element2.getContentType(), UML2DomainBridge.UML2_CONTENT_TYPE);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		ResourceTestUtil.deleteProject(papyrusProject.getProject());
	}

}
