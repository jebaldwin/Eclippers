package textmarker.actions;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import eclippers.patch.editor.extension.PatchContainingEditor;

import textmarker.add.AddMarkers;
import textmarker.parse.ParseXMLForMarkers;

public class AddMarkerAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public AddMarkerAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IEditorInput ei = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
		IProject proj = ((FileEditorInput)ei).getFile().getProject();
		ParseXMLForMarkers.parseXML(proj);
		
		//putting in lines, adding markers and forcing close with no save might work best 
		//this happens after the previously highlighted line because of the line numbers
		CompilationUnitEditor part = (CompilationUnitEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		//only mess up the editor if code opened with our editor
		if(part instanceof PatchContainingEditor){
			part = (PatchContainingEditor) part;
			ITextEditor editor = (ITextEditor) part;
			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());
			try {
				//TODO this needs to move to parseXML method
				String code = "test\n";
			    doc.replace(0, 0, code);
			    String fullpatchpath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toPortableString() + File.separator + proj.getName() + "/org/jhotdraw/samples/net/NetApp.java";
			    //TODO adding lines moves other lines' colors forward that amount
			    AddMarkers.addMarkerToFile("AddEllipseFigure", fullpatchpath, 0, proj, code, true, false, 0);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			Object selected = ((IStructuredSelection) selection).getFirstElement();
			
			if (selected instanceof IResource) {
				IResource resource = (IResource) selected;
				ParseXMLForMarkers.parseXML(resource.getProject());
			}
		} else if (selection instanceof TextSelection) {
			IEditorInput ei = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			IProject proj = ((FileEditorInput)ei).getFile().getProject();
			ParseXMLForMarkers.parseXML(proj);
		}
	}
	

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}