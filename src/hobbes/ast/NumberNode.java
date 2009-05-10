package hobbes.ast;

import hobbes.parser.Token;

public class NumberNode implements AtomNode {
	
	public Token origin;
	public Token negative;
	// TODO: capture line? for runtime errors... variables too...
	
	public NumberNode(Token t) {
		origin = t;
		negative = null;
	}
	
	public NumberNode(Token neg, Token val) {
		origin = val;
		negative = neg;
	}
	
	public String toString() {
		return ((negative != null) ? "-" : "") + origin.getValue();
	}
	
}
