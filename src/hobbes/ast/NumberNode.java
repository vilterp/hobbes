package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class NumberNode implements AtomNode {
	
	public Token origin;
	
	public NumberNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
	public String getValue() {
		return origin.getValue();
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
