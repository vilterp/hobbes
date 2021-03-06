package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class VariableNode implements VarNode {
	
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
	
	public String getName() {
		return origin.getValue();
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
