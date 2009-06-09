package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class CharNode implements AtomNode {
	
	private Token origin;
	
	public CharNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "'" + origin.getValue() + "'";
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
