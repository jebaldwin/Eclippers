package eclippers.patch.history.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
public class ChangeHistoryListener implements ISelectionListener {

	private IProject lastProj = null;
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IProject proj = null;
		
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();

			if (selected instanceof IResource) {
				IResource resource = (IResource) selected;
				proj = resource.getProject();
			} else if (selected instanceof IJavaProject) {
				proj = ((IJavaProject) selected).getProject();
			} else if (selected instanceof IResource) {
				IResource resource = (IResource) selected;
				proj = resource.getProject();
			} else if (selected instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) selected;
				proj = cu.getJavaProject().getProject();
			} else {
				IFile file = (IFile) part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
				proj = file.getProject();
			}
		} else if (selection instanceof TextSelection) {
			IFile file = (IFile) part.getSite().getWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
			proj = file.getProject();
		}

		if (proj != lastProj && proj != null) {

			lastProj = proj;

			HistoryView.populate(proj);
		}
	}

}
