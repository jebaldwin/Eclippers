package patcheditor.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import patcheditor.PatchEditorPlugin;
import textmarker.parse.ParseXMLForMarkers;

public class NavigateToSourceAction {

	public static void navigateToSource(String lineSelected, String diffLine, String projectName) {

		int lineNumber = 1;

		// selected patch offset line
		if (lineSelected.startsWith("@@")) {
			int endIndex = lineSelected.indexOf(',');
			lineNumber = Integer.parseInt(lineSelected.substring(4, endIndex));
			String[] array = diffLine.split("\\s");
			String pathName = array[6];
			if(!new File(pathName).exists()){
				//git pattern of patch file
				pathName = array[10];
				//cut out a/ or b/ at the beginning
				pathName = pathName.substring(2);
			}
			openFile(pathName, lineNumber, projectName);
		} else if (lineSelected.startsWith("diff")) {
			//get filename out of text
			String[] array = lineSelected.split("\\s");
			String pathName = array[6];
			openFile(pathName, 1, projectName);
		}

	}

	private static void openFile(String fileName, int lineNumber, String projectName) {

		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);//.getProjects();
		IPath path = new Path(proj.getName() + File.separator +  fileName);
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		if(!file.exists()){
			//try without src
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path.toString().replaceFirst("src", ".")));
			
			//else try with src
			if(!file.exists()){
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(proj.getName() + File.separator +  "src" + File.separator + fileName));
			}
		}
		//TODO fix this
		//ParseXMLForMarkers.parseXML(proj);
		
		try {
			IEditorPart editor = (IEditorPart) IDE.openEditor(PatchEditorPlugin
					.getDefault().getWorkbench().getActiveWorkbenchWindow()
					.getActivePage(), file, true);
			
			if(editor instanceof ITextEditor){
				gotoLine(lineNumber - 1, (ITextEditor)editor);
			}
			if(editor instanceof FormEditor){
				gotoLineInFormEditor(lineNumber - 1, (FormEditor) editor);				
			}
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Jumps to the given line.
	 * 
	 * @param line
	 *            the line to jump to
	 */
	private static void gotoLine(int line, ITextEditor editor) {

		IDocumentProvider provider = editor.getDocumentProvider();
		IDocument document = provider.getDocument(editor.getEditorInput());
		try {

			int start = document.getLineOffset(line);
			editor.selectAndReveal(start, 0);

			IWorkbenchPage page = editor.getSite().getPage();
			page.activate(editor);

		} catch (BadLocationException x) {
			// ignore
		}
	}
	
	private static void gotoLineInFormEditor(int line, FormEditor editor) {
		//TODO how to do this?
		
	}
}