package hobbes.ast;

import hobbes.parser.Token;

public class ArgSpecNode implements SyntaxNode {
	
	private ArgType type;
	private Token name;
	private AtomNode defaultValue;
	
	public ArgSpecNode(Token n, ArgType t, AtomNode d) {
		name = n;
		defaultValue = d;
		type = t;
	}
	
	public String toString() {
		String ans = "";
		if(type == ArgType.KEYWORDS)
			ans += "**";
		else if(type == ArgType.SPLAT)
			ans += "*";
		ans += name.getValue();
		if(defaultSpecified())
			ans += "=" + defaultValue;
		return ans;
	}
	
	private boolean defaultSpecified() {
		return defaultValue != null;
	}
	
	public ArgType getType() {
		return type;
	}
	
	public Token getNameToken()	{
		return name;
	}
	
}
