package hobbes.ast;

import hobbes.parser.Token;

public class ArgSpecNode implements SyntaxNode {
	
	private ArgSpecType type;
	private Token name;
	private Token className;
	private OperationNode defaultValue;
	
	public ArgSpecNode(Token n, ArgSpecType t, Token cn, OperationNode d) {
		name = n;
		className = cn;
		defaultValue = d;
		type = t;
	}
	
	public String toString() {
		String ans = "";
		if(type == ArgSpecType.KEYWORDS)
			ans += "**";
		else if(type == ArgSpecType.SPLAT)
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
