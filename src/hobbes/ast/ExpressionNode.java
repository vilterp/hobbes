package hobbes.ast;

import hobbes.parser.Token;

public class ExpressionNode implements SyntaxNode {
	
	private ExpressionNode left;
	private Token operator;
	private ExpressionNode right;
	
	private ObjectNode object;
	
	public ExpressionNode(ExpressionNode l, Token o, ExpressionNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public ExpressionNode(ObjectNode obj) {
		object = obj;
	}
	
	public String toString() {
		if(operator == null && right == null)
			return object.toString();
		else
			return operator.getValue() + "(" + left + "," + right + ")";
	}
	
}
