package eclippers.patch.editor.extension;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;

public class PatchContainingEditor extends CompilationUnitEditor {

    /**
     * The same as CompilationUnitEditor.AdaptedSourceViewer,
     * Only need this class because we want to add some functionality to the cource viewer,
     * but CompilationUnitEditor.AdaptedSourceViewer is package protected
     */
    @SuppressWarnings("restriction")
	class ContainingAdaptedSourceViewer extends JavaSourceViewer  {

		public ContainingAdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
			// TODO Auto-generated constructor stub
		}
    	
    }
    
	/**
	 * rather than making changes to the super class, make changes to this we
	 * want to ensure that the class ContainingAdaptedSourceViewer stays as
	 * close as possible to the JDT class
	 * CompilationUnitEditor.AdaptedSourceViewer
	 */
	public class ContainingSourceViewer extends ContainingAdaptedSourceViewer {

		public ContainingSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
		}
	}

	public ISourceViewer internalGetSourceViewer() {
		return getSourceViewer();
	}

	public void addToEditor() {
		getSourceViewer().revealRange(0, 20);
	}

    @Override
    public void dispose() {
    	System.out.println("here");
    	this.doRevertToSaved();
    	this.close(false);
    }
}