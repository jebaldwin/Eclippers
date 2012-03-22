package patcheditor.editors;

import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;

public class PatchEditor extends TextEditor {

	private ColorManager colorManager;

	public PatchEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new PatchConfiguration(colorManager));
		setDocumentProvider(new FileDocumentProvider());
	}
	
	protected void initializeEditor() {
		super.initializeEditor();
	}

	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	
	@Override
	public boolean isEditable() {
	    return false;
	}

	@Override
	public boolean isEditorInputModifiable() {
	    return false;
	}

	@Override
	public boolean isEditorInputReadOnly() {
	    return true;
	}

	@Override
	public boolean isDirty() {
	    return false;
	}
}
