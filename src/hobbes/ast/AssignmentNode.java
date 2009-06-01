package hobbes.ast;

import hobbes.parser.Token;

public class AssignmentNode implements StatementNode {
	
	private Token origin;
	private VarNode var;
	private ExpressionNode expr;
	
	public AssignmentNode(Token o, VarNode v, ExpressionNode e) {
		origin = o;
		var = v;
		expr = e;
	}
	
	public String toString() {
		return "=(" + var + "," + expr + ")";
	}
	
	public VarNode getVar() {
		return var;
	}
	
	public ExpressionNode getExpr() {
		return expr;
	}
	
	public Token getEqualsToken() {
		return origin;
	}
	
}
