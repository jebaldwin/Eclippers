package patcheditor.editors;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

import patcheditor.PatchEditorPlugin;
import patcheditor.actions.NavigateToSourceAction;

public class PatchDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer fText;

	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;

		if (pos < 0)
			return;

		fText = part;

		selectWord(pos);
	}

	protected boolean selectWord(int caretPos) {

		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try {

			// want startPos to be start of line
			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR)
					break;
				--pos;
			}

			startPos = pos;

			if (pos > 0)
				startPos++;

			// want endPos to be start of line
			pos = caretPos;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR)
					break;
				pos++;
			}
			endPos = pos;

			// navigate from offset to where patch applies in source
			String lineSelected = fText.getDocument().get().substring(
					startPos + 1, endPos);
			
			//find last diff section in order to get filename for offsets numbers			
			int startPosOrig = startPos;
			int endPosOrig = endPos;

			// need to put startpos up to start of diff
			pos = startPos + 1;

			while (pos >= 0) {
				c = doc.getChar(pos);
				char c1 = doc.getChar(pos + 1);
				char c2 = doc.getChar(pos + 2);
				char c3 = doc.getChar(pos + 3);
				if (c == 'd' && c1 == 'i' && c2 == 'f' && c3 == 'f')
					break;
				pos--;
			}
			startPos = pos + 1;

			// need to put endpos to end of diff
			// get the whole 3 lines
			pos = startPos;

			// need 2 more line breaks
			int breakCount = 0;
			while (pos >= 0) {
				c = doc.getChar(pos);
				if (c == Character.LINE_SEPARATOR) {
					breakCount++;
					if (breakCount == 3)
						break;
				}
				pos++;
			}
			endPos = pos;

			String diffLine = "";
			
			if (lineSelected.startsWith("diff")
					|| lineSelected.startsWith("+++")
					|| lineSelected.startsWith("---")) {

				selectRange(startPos - 2, endPos);

				// navigate from offset to where patch applies in source
				lineSelected = fText.getDocument().get().substring(startPos - 1, endPos);
			} else {
				diffLine = fText.getDocument().get().substring(startPos, endPos);
				selectRange(startPosOrig, endPosOrig);
			}
			
			String toolTip = PatchEditorPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitleToolTip();
			String project = toolTip.split("/")[0];
			NavigateToSourceAction.navigateToSource(lineSelected, diffLine, project);
			
			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}
	
	private void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
}