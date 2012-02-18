package eclippers.patch.editor.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

public class AppliesUpdater implements IMarkerUpdater{

	public AppliesUpdater() {
	}

	public String getMarkerType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean updateMarker(IMarker marker, IDocument document, Position position) {
		// TODO may be a way to save the line numbers
		System.out.println("testing");
		return false;
	}

}
