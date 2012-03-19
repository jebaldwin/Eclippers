package patcheditor.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class PatchScanner extends RuleBasedScanner {

	public static final String PATCH_STRING = "___patch_";

	public PatchScanner(ColorManager manager) {
		//IToken add = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.ADD)));
		IToken add = new Token(new TextAttribute(
				manager.getColor(IPatchColorConstants.DEFAULT),
				manager.getColor(IPatchColorConstants.GREEN),
				0));

		//IToken minus = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.MINUS)));
		IToken minus = new Token(new TextAttribute(
				manager.getColor(IPatchColorConstants.DEFAULT),
				manager.getColor(IPatchColorConstants.RED),
				TextAttribute.STRIKETHROUGH));
		
		IToken offset = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.OFFSET)));

		//IToken diff = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.DIFF)));
		IToken diff = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.BLUE)));

		IToken defaultToken = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.DEFAULT)));

		IRule[] rules = new IRule[10];
		rules[0] = new SingleLineRule("@@", "@@", offset);
		rules[1] = new EndOfLineRule("diff ", diff);
		rules[2] = new EndOfLineRule("+++ ", diff);
		rules[3] = new EndOfLineRule("--- ", diff);
		rules[4] = new EndOfLineRule(" + ", defaultToken);
		rules[5] = new EndOfLineRule(" - ", defaultToken);
		//rules[6] = new EndOfLineRule("+", add);
		rules[6] = new StartAndEndOfLineRule("+", add);
		//rules[7] = new EndOfLineRule("-", minus);
		rules[7] = new StartAndEndOfLineRule("-", minus);
		rules[8] = new WhitespaceRule(new PatchWhitespaceDetector());
		rules[9] = new EndOfLineRule("index ", diff);
		
		setRules(rules);
	}
}
