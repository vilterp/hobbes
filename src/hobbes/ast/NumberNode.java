package hobbes.ast;

import hobbes.parser.Token;

public class NumberNode implements ObjectNode {
	
	public Token origin;
	// TODO: capture line? for runtime errors... variables too...
	
	public NumberNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
}
