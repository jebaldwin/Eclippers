package textmarker.actions;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import textmarker.TextMarkerPlugin;

public class NavigateToSourceAction {

	public static void openFile(String patchFileName, IProject proj, int lineNumber) {
		IPath path = new Path(proj.getName() + File.separator + patchFileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		try {
			ITextEditor editor = (ITextEditor) IDE.openEditor(TextMarkerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
			gotoLine(lineNumber - 1, editor);
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
}