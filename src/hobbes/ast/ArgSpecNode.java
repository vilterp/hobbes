package hobbes.ast;

import hobbes.parser.Token;

public class ArgSpecNode implements SyntaxNode {
	
	private ArgType type;
	private Token name;
	private Token className;
	private AtomNode defaultValue;
	
	public ArgSpecNode(Token n, ArgType t, Token cn, AtomNode d) {
		name = n;
		className = cn;
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
		if(classNameSpecified())
			ans += ":" + className.getValue();
		if(defaultSpecified())
			ans += "=" + defaultValue;
		return ans;
	}
	
	private boolean classNameSpecified() {
		return className != null;
	}
	
	private boolean defaultSpecified() {
		return defaultValue != null;
	}
	
}
