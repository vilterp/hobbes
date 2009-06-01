package hobbes.ast;

import hobbes.parser.Token;

public class DeletionNode implements StatementNode {
	
	private Token origin;
	private VarNode var;
	
	public DeletionNode(Token o, VarNode v) {
		origin = o;
		var = v;
	}
	
	public String toString() {
		return "del(" + var + ")";
	}
	
	public String getVarName() {
		return var.getName();
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
