package hobbes.ast;

public class ArgNode implements SyntaxNode {
	
	private ExpressionNode value;
	private String name;
	private ArgType type;
	
	public ArgNode(ExpressionNode v, ArgType t) {
		value = v;
		type = t;
		name = null;
	}
	
	public ArgNode(String n, ExpressionNode v) {
		name = n;
		value = v;
		type = ArgType.NORMAL; // named args can't be splat or kw
	}
	
	public String toString() {
		String ans = "";
		if(type == ArgType.KEYWORDS)
			ans += "**";
		if(type == ArgType.SPLAT)
			ans += "*";
		if(name != null)
			ans += name + "=";
		ans += value;
		return ans;
	}
	
}
