package eclippers.patch.editor.extension;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

import patcheditor.editors.DiffEditor;

public class PatchContainingEditor extends DiffEditor {
	
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