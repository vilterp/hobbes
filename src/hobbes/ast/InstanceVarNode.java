package hobbes.ast;

import hobbes.parser.Token;

public class InstanceVarNode implements VarNode {
	
	private Token origin;
	
	public InstanceVarNode(Token t) {
		origin = t;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
}
