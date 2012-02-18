package importpatch.popup.actions;

import importpatch.parser.ParsePatch;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ApplyAction implements IObjectActionDelegate {

	private Shell shell;
	File patchFile;
	IProject proj;
	
	/**
	 * Constructor for Action1.
	 */
	public ApplyAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		ParsePatch.markAsPatched(patchFile.getFullPath().toFile(), null, proj, true);
		
		//rename extension to patch from apatch
		String WORKSPACE_PATH = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString();
		java.io.File oFile = new java.io.File(WORKSPACE_PATH + java.io.File.separator + patchFile.getFullPath().toString());
		java.io.File nFile = new java.io.File(WORKSPACE_PATH + java.io.File.separator + patchFile.getFullPath().toString().replace("patch", "patched"));
		oFile.renameTo(nFile);
		try {
			proj.refreshLocal(IProject.DEPTH_ONE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			
			if (selected instanceof IResource) {
				IResource resource = (IResource) selected;
				proj = resource.getProject();
				patchFile = (File) resource;
			}
		}
	}

}
