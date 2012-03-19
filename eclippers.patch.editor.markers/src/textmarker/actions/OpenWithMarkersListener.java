package textmarker.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import textmarker.parse.ParseXMLForMarkers;

import eclippers.patch.editor.markers.PackageDecoratorLightweight;

public class OpenWithMarkersListener implements IPartListener {

	@Override
	public void partActivated(IWorkbenchPart part) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) editorInput).getFile();
				// check if there is highlighting in this project
				IProject proj = file.getProject();
				if(proj == PackageDecoratorLightweight.currProj){
					ParseXMLForMarkers.parseXML(proj, (IEditorPart) part, "", ParseXMLForMarkers.currFilter);
				}
			}
		}
	}

}
