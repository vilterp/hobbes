package hobbes.ast;

import hobbes.parser.Token;

public class OperationNode implements ExpressionNode {
	
	private ExpressionNode left;
	private Token operator;
	private ExpressionNode right;
	
	private AtomNode object;
	
	public OperationNode(ExpressionNode l, Token o, ExpressionNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public OperationNode(AtomNode obj) {
		object = obj;
	}
	
	public String toString() {
		if(operator == null && right == null)
			return object.toString();
		else
			return operator.getValue() + "(" + left + "," + right + ")";
	}
	
}
