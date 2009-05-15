package hobbes.ast;

import hobbes.parser.Token;

public class ArgSpecNode implements SyntaxNode {
	
	private ArgType type;
	private Token name;
	private ObjectNode typeSpec;
	private AtomNode defaultValue;
	
	public ArgSpecNode(Token n, ArgType t, ObjectNode ts, AtomNode d) {
		name = n;
		typeSpec = ts;
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
			ans += ":" + typeSpec;
		if(defaultSpecified())
			ans += "=" + defaultValue;
		return ans;
	}
	
	private boolean classNameSpecified() {
		return typeSpec != null;
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
