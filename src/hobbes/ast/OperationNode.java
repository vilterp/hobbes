package hobbes.ast;

import hobbes.parser.Token;

public class OperationNode implements ExpressionNode {
	
	private ExpressionNode left;
	private Token operator;
	private ExpressionNode right;
	
	public OperationNode(ExpressionNode l, Token o, ExpressionNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public String toString() {
		return operator.getValue() + "(" + left + "," + right + ")";
	}
	
}
