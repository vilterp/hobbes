package hobbes.ast;

import hobbes.parser.Token;

public class StringNode implements ObjectNode {
	
	private Token origin;
	
	public StringNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "\"" + origin.getValue() + "\"";
	}
	
}
