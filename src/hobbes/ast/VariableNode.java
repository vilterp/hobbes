package hobbes.ast;

import hobbes.parser.Token;

public class VariableNode implements ObjectNode {
	
	private Token origin;
	
	public VariableNode(Token t) {
		origin = t;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
}
