package hobbes.ast;

import hobbes.parser.Token;

public class CharNode implements AtomNode {
	
	private Token origin;
	
	public CharNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "'" + origin.getValue() + "'";
	}
	
}
