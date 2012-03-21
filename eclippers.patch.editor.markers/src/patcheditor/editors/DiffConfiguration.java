package patcheditor.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

public class DiffConfiguration extends TextSourceViewerConfiguration {
	private RuleBasedScanner scanner;
	private DiffColorManager colorManager;

	public DiffConfiguration(DiffColorManager colorManager) {
		this.colorManager = colorManager;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE };
	}

	protected RuleBasedScanner getPatchScanner() {
		if (scanner == null) {
			// scanner = new PatchScanner(colorManager);
			scanner = new DiffJavaXMLScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IDiffColorConstants.DEFAULT))));
		}
		return scanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getPatchScanner());
		reconciler.setDamager(dr, DiffJavaXMLScanner.PATCH_STRING);
		reconciler.setRepairer(dr, DiffJavaXMLScanner.PATCH_STRING);

		dr = new DefaultDamagerRepairer(getPatchScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(IDiffColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, DiffJavaXMLScanner.PATCH_STRING);
		reconciler.setRepairer(ndr, DiffJavaXMLScanner.PATCH_STRING);

		return reconciler;
	}

}