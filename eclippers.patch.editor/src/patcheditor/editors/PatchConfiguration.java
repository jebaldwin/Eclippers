package patcheditor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class PatchConfiguration extends SourceViewerConfiguration {
	private PatchDoubleClickStrategy doubleClickStrategy;
	private PatchScanner scanner;
	private ColorManager colorManager;

	public PatchConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE};
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {

		if (doubleClickStrategy == null)
			doubleClickStrategy = new PatchDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected PatchScanner getPatchScanner() {
		if (scanner == null) {
			scanner = new PatchScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IPatchColorConstants.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getPatchScanner());
		reconciler.setDamager(dr, PatchScanner.PATCH_STRING);
		reconciler.setRepairer(dr, PatchScanner.PATCH_STRING);

		dr = new DefaultDamagerRepairer(getPatchScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(IPatchColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, PatchScanner.PATCH_STRING);
		reconciler.setRepairer(ndr, PatchScanner.PATCH_STRING);

		return reconciler;
	}

}