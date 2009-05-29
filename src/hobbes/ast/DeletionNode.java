package hobbes.ast;

import hobbes.parser.Token;

public class DeletionNode implements StatementNode {
	
	private Token origin;
	private VariableNode var;
	
	public DeletionNode(Token o, VariableNode v) {
		origin = o;
		var = v;
	}
	
	public String toString() {
		return "del(" + var + ")";
	}
	
	public String getVarName() {
		return var.getValue();
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
