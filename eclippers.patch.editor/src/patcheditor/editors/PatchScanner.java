package patcheditor.editors;

import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class PatchScanner extends RuleBasedScanner {

	public static final String PATCH_STRING = "___patch_";

	public PatchScanner(ColorManager manager) {
		IToken add = new Token(new TextAttribute(manager
				.getColor(IPatchColorConstants.ADD)));

		IToken minus = new Token(new TextAttribute(manager
				.getColor(IPatchColorConstants.MINUS)));

		IToken offset = new Token(new TextAttribute(manager
				.getColor(IPatchColorConstants.OFFSET)));

		IToken diff = new Token(new TextAttribute(manager
				.getColor(IPatchColorConstants.DIFF)));
		
		IToken defaultToken = new Token(new TextAttribute(manager
				.getColor(IPatchColorConstants.DEFAULT)));

		IRule[] rules = new IRule[9];
		rules[0] = new SingleLineRule("@@", "@@", offset);
		rules[1] = new EndOfLineRule("diff ", diff);
		rules[2] = new EndOfLineRule("+++ ", diff);
		rules[3] = new EndOfLineRule("--- ", diff);
		rules[4] = new EndOfLineRule(" + ", defaultToken);
		rules[5] = new EndOfLineRule(" - ", defaultToken);
		rules[6] = new EndOfLineRule("+", add);
		rules[7] = new EndOfLineRule("-", minus);
		rules[8] = new WhitespaceRule(new PatchWhitespaceDetector());
		
		setRules(rules);
	}
}
