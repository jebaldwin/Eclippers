		IToken add = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.DEFAULT), manager.getColor(IPatchColorConstants.GREEN), 0));
		IToken minus = new Token(new TextAttribute(manager.getColor(IPatchColorConstants.DEFAULT), manager.getColor(IPatchColorConstants.RED), TextAttribute.STRIKETHROUGH));
		IRule[] rules = new IRule[11];
		// rules[6] = new EndOfLineRule("+", add);
		rules[6] = new StartAndEndOfLineRule("+", add);
		// rules[7] = new EndOfLineRule("-", minus);
		rules[7] = new StartAndEndOfLineRule("-", minus);
		rules[10] = new EndOfLineRule("new file mode ", diff);