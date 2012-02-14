package textmarker.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import textmarker.TextMarkerPlugin;

public class NavigateToSourceAction {

	public static void openFile(String patchFileName, IProject proj) {
		IPath path = new Path(proj.getName() + File.separator + patchFileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		try {
			ITextEditor editor = (ITextEditor) IDE.openEditor(TextMarkerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}