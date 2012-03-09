package importpatch.popup.actions;

import importpatch.parser.ParsePatch;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class DeleteAction implements IObjectActionDelegate {

	private Shell shell;
	File patchFile;
	IProject proj;
	
	/**
	 * Constructor for Action1.
	 */
	public DeleteAction() {
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
		ParsePatch.removePatch(patchFile.getName(), proj, "");
		
		//delete patch file from project
		try {
			patchFile.delete(true, null);
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
