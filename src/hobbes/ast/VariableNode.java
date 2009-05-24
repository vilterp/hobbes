package hobbes.ast;

import hobbes.parser.Token;

public class VariableNode implements AtomNode {
	
	private Token origin;
	
	public VariableNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public String getValue() {
		return origin.getValue();
	}
	
}
