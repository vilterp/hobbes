package hobbes.ast;

import hobbes.parser.Token;

public class AssignmentNode implements StatementNode {
	
	private VariableNode var;
	private Token equalsToken;
	private ExpressionNode expr;
	
	public AssignmentNode(VariableNode v, Token et, ExpressionNode e) {
		var = v;
		equalsToken = et;
		expr = e;
	}
	
	public String toString() {
		return "=(" + var + "," + expr + ")";
	}
	
	public VariableNode getVar() {
		return var;
	}
	
	public ExpressionNode getExpr() {
		return expr;
	}
	
	public Token getEqualsToken() {
		return equalsToken;
	}
	
}
