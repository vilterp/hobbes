package hobbes.ast;

public class NumberNode implements SyntaxNode {
	
	public String value;
	
	public NumberNode(String val) {
		value = val;
	}
	
	public String toString() {
		return value;
	}
	
}
