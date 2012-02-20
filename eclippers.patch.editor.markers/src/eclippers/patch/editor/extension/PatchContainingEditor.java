package eclippers.patch.editor.extension;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;

public class PatchContainingEditor extends CompilationUnitEditor {
   
    public void dispose(){
    	this.doRevertToSaved();
    	this.close(false);
    }
}