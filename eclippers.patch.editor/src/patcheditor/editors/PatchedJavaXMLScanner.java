package patcheditor.editors;

import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;


public class PatchedJavaXMLScanner extends RuleBasedScanner {

	private String[] JavaKeys = {"abstract", "continue", "for", "new", "switch",
			"assert", "default", "goto", "package", "synchronized", "boolean", "do", "if",
			"byte",	"else",	"import", "public",	"throws", "break", "protected",
			"double", "implements",	"protected", "throw",
			"case",	"enum",	"instanceof", "return",	"transient",
			"catch", "extends",	"int", "short",	"try",
			"char",	"final", "interface", "static", "void",
			"class", "finally",	"long",	"strictfp",	"volatile",
			"const", "float", "native",	"super", "while"};
	
	public PatchedJavaXMLScanner(DiffColorManager manager) {
		IToken add = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.DEFAULT), manager.getColor(IDiffColorConstants.GREEN), 0));
		IToken minus = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.DEFAULT), manager.getColor(IDiffColorConstants.RED), TextAttribute.STRIKETHROUGH));
		IToken offset = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.OFFSET)));
		IToken diff = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.BLUE)));
		IToken defaultToken = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.DEFAULT)));

		// comments, keywords, font
		IToken comment = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.COMMENT)));
		IToken string = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.STRING)));
		IToken keyword = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.KEYWORD)));

		// xml
		IToken xmlComment = new Token(new TextAttribute(manager.getColor(IDiffColorConstants.XML_COMMENT)));

		IRule[] rules = new IRule[23];
		rules[0] = new SingleLineRule("@@", "@@", offset);
		rules[1] = new EndOfLineRule("diff ", diff);
		rules[2] = new EndOfLineRule("+++ ", diff);
		rules[3] = new EndOfLineRule("--- ", diff);
		rules[4] = new EndOfLineRule(" + ", defaultToken);
		rules[5] = new EndOfLineRule(" - ", defaultToken);
		rules[6] = new StartAndEndOfLineRule("+", add);
		rules[7] = new StartAndEndOfLineRule("-", minus);
		rules[8] = new WhitespaceRule(new DiffWhitespaceDetector());
		rules[9] = new EndOfLineRule("index ", diff);

		rules[10] = new EndOfLineRule("//", comment);
		rules[11] = new MultiLineRule("/*", "*/", comment);
		rules[12] = new SingleLineRule("\"", "\"", string, '\\');
		rules[13] = new SingleLineRule("'", "'", string, '\\');
		rules[14] = new SingleLineRule("@", " ", xmlComment);
		
		WordRule wordRule = new WordRule(new JavaWordDetector());
		for (int i = 0, n = JavaKeys.length; i < n; i++){
			wordRule.addWord(JavaKeys[i], keyword); 
		}
		rules[15] = wordRule;
		
		// XML rules
		rules[16] = new MultiLineRule("<!--", "-->", xmlComment);
		rules[17] = new SingleLineRule("<?", "?>", xmlComment);
		rules[18] = new SingleLineRule("<", " ", comment);
		rules[19] = new SingleLineRule("</", ">", comment);
		rules[20] = new SingleLineRule("> ", "", comment);
		rules[21] = new SingleLineRule(">", "\r", comment);
		rules[22] = new SingleLineRule(">", "\n", comment);
		
		setRules(rules);
	}
}
