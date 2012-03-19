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
		// TODO Auto-generated method stub

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
		// TODO Auto-generated method stub

	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IFile file = ((IFileEditorInput) editorInput).getFile();
				if (file.getName().endsWith(".xml")) { 
					System.out.println("Xml file openend ");
				} else if (file.getName().endsWith(".java")) {
					System.out.println("Java file opened");
					// check if there is highlighting in this project
					//ISelection sel = event.getSelection();
					
					// get project
					IProject proj = file.getProject();
					
					// check if highlighting is going on
					if(proj == PackageDecoratorLightweight.currProj){
						// refresh markers
						ParseXMLForMarkers.parseXML(proj, (IEditorPart) part, "", ParseXMLForMarkers.currFilter);
					}
				}
			}
		}
	}

}
