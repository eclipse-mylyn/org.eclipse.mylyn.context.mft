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

package org.eclipse.mylyn.mft.emf.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;

/**
 * @author Miles Parker
 * @author Benjamin Muskalla
 */
public abstract class EmfStructureBridge extends DomainModelContextStructureBridge {

	/**
	 * Returns a unique path to the object within an actual resource. EMF-based implementations typically shouldn't need
	 * to override this.
	 */
	@Override
	public String getDomainHandleIdentifier(Object object) {
		return getGenericDomainHandleIdentifier(object, parentContentType);
	}

	/**
	 * Made static so we can reuse generic functionality.
	 * 
	 * @param object
	 * @param parentContentType
	 * @return
	 */
	public static String getGenericDomainHandleIdentifier(Object object, String parentContentType) {
		if (object instanceof EObject) {
			EObject eobject = ((EObject) object);
			URI uri = EcoreUtil.getURI(eobject);
//			if (eobject instanceof EPackage) {
//				EPackage pack = (EPackage) eobject;
//				if (pack.eResource() != null) {
//					IFile file = getFile(pack.eResource());
//					if (file != null && file.exists()) {
//						AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
//						return parentBridge.getHandleIdentifier(file);
//					}
//
//				}
//			}
			return uri.toString();
		}
		return null;
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof Resource) {
			Resource resource = (Resource) object;
			URI uri = resource.getResourceSet().getURIConverter().normalize(resource.getURI());
			return uri.toString();
		}
		return super.getHandleIdentifier(object);
	}

	/**
	 * If it's a domain object, we accept it. Implementors generally should not override.
	 */
	@Override
	public boolean acceptsObject(Object object) {
		return getDomainObject(object) != null;
	}

	@Override
	public Object getObjectForHandle(String handle) {
		if (isDocument(handle)) {
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			Object objectForHandle = parentBridge.getObjectForHandle(handle);
			if (objectForHandle instanceof IFile) {
				IFile file = (IFile) objectForHandle;
				ResourceSet rs = getDomainObjectResourceSet();
				URI createFileURI = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
				Resource createResource = rs.getResource(createFileURI, true);
				return createResource;
			}
			return null;
		}
		return super.getObjectForHandle(handle);
	}

	/**
	 * Must return a class that is an equivalent (e.g. a deep copy) but not necessarily the same object as the original
	 * mapped object. This allows us to make generic references to objects that might not have equivalent mappings in
	 * memory, e.g. an EMF object that is loaded from a new resource set. EMF-based implementations typically shouldn't
	 * need to override this.
	 * 
	 * @see #getDomainObjectResourceSet()
	 */
	@Override
	public Object getDomainObjectForHandle(String handle) {
		if (handle.equals("/")) {
			return null;
		}
		URI uri = URI.createURI(handle);
		ResourceSet resourceSet = getDomainObjectResourceSet();
		try {
			if (!uri.hasFragment()) {
				Resource resource = resourceSet.getResource(uri, true);
				return resource;
			}
			EObject eObject = resourceSet.getEObject(uri, true);
			if (eObject != null) {
				return eObject;
			}
		} catch (WrappedException e) {
			// this is a reasonable thing to happen in the case where the resource is no longer available.
		}
		return null;
	}

	/**
	 * Returns a ResourceSet that can be used to obtain domain objects from domain handles. The default implementation
	 * returns a new {@link ResourceSetImpl} on each call. EMF-based implementations needing specific ResourceSet
	 * implementations or instances can override this.
	 */
	protected ResourceSet getDomainObjectResourceSet() {
		return new ResourceSetImpl();
	}

	/**
	 * Returns all of the "children" that a given object is responsible for. Here we just use EMF containment. This is
	 * similar to org.eclipse.emf.edit.provider.ITreeItemContentProvider#getChildren. You shouldn't need to override
	 * this unless you want to use some other references for children.
	 */
	@Override
	public List<String> getChildHandles(String handle) {
		Object domainObject = getObjectForHandle(handle);
		if (domainObject instanceof EObject) {
			List<String> childHandles = new ArrayList<String>();
			EObject eo = (EObject) domainObject;
			for (EObject child : eo.eContents()) {
				childHandles.add(getDomainHandleIdentifier(child));
			}
			return childHandles;
		}
		return Collections.emptyList();
	}

	/**
	 * The inverse of {@link #getChildHandles(String)}. Again, you typically don't need to override this.
	 */
	@Override
	public String getParentHandle(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof EObject) {
			EObject eObject = (EObject) object;
			if (eObject.eContainer() != null) {
				return getHandleIdentifier(eObject.eContainer());
			} else {
				//must be base package
				Resource resource = eObject.eResource();
				IFile file = getFile(resource);
				if (file != null && file.exists()) {
					AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
					return parentBridge.getHandleIdentifier(file);
				}
				return getHandleIdentifier(resource);
			}
		}
		if (object instanceof Resource) {
			Resource resource = (Resource) object;
			IFile file = getFile(resource);
			if (file != null && file.exists()) {
				AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
				return parentBridge.getHandleIdentifier(file);
			}
		} else if (object instanceof EPackage) {
			EPackage pack = (EPackage) object;
			if (pack.eContainer() == null && pack.eResource() != null) {
				return getDomainHandleIdentifier(pack.eContainer());
			}
		} else if (object instanceof IFile) {
			// String fileHandle = parentBridge.getParentHandle(handle);
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			return parentBridge.getParentHandle(handle);
		}
		//Resources don't have parents, unless we want to get the file hierarchy, which probably isn't what we want.
		return null;
	}

	public static IFile getFile(Resource resource) {
		URI uri = resource.getURI();
		uri = resource.getResourceSet().getURIConverter().normalize(uri);
		String scheme = uri.scheme();
		if ("platform".equals(scheme) && uri.segmentCount() > 1 && "resource".equals(uri.segment(0))) { //$NON-NLS-1$//$NON-NLS-2$
			StringBuffer platformResourcePath = new StringBuffer();
			for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
				platformResourcePath.append('/');
				platformResourcePath.append(uri.segment(j));
			}
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformResourcePath.toString()));
			return file;
		}
		return null;
	}

	/**
	 * Override to specify a feature giving a unique name for the given object.
	 * 
	 * @return
	 */
	public EAttribute getNameFeature(Object object) {
		return EcorePackage.Literals.ENAMED_ELEMENT__NAME;
	}

	/**
	 * Returns the textual representation for Mylyn editors. Uses the feature specified by getNameFeature, so shouldn't
	 * need to override this directly. We should consider using EMF Item Provider adapters to obtain an
	 * IItemLabelProvider for some EMF implementations.
	 */
	@Override
	public String getLabel(Object object) {
		if (object instanceof EObject) {
			EObject eo = (EObject) object;
			return (String) eo.eGet(getNameFeature(object));
		}
		return super.getLabel(object);
	}

	@Override
	public String getContentType(String handle) {
		Object objectForHandle = getObjectForHandle(handle);
		if (objectForHandle instanceof Resource) {
			return parentContentType;
		}
		return getContentType();
	}

	public String[] getFileExtensions() {
		return new String[] { getContentType() };
	}

	@Override
	public boolean isDocument(String handle) {
		if (handle == null || handle.equals("")) { //$NON-NLS-1$
			return false;
		}
		URI uri = URI.createURI(handle);
		return uri.isFile() && !uri.isEmpty();
	}

}
