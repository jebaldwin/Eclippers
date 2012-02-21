package eclippers.patch.editor.extension;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

public class PatchContainingEditor extends CompilationUnitEditor {
	
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