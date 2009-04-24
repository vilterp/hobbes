package hobbes.ast;

import hobbes.parser.Token;

public class RegexNode implements ObjectNode {
	
	private Token origin;
	
	public RegexNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "/" + origin.getValue() + "/";
	}
	
}
