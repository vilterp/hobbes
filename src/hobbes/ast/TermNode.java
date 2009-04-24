package hobbes.ast;

import hobbes.parser.Token;

public class TermNode implements SyntaxNode {
	
	private ExpressionNode left;
	private String operator;
	private ExpressionNode right;
	
	public TermNode(ExpressionNode l, String o, ExpressionNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public TermNode(ExpressionNode l) {
		left = l;
		operator = null;
		right = null;
	}
	
	public String toString() {
		if(operator == null && right == null)
			return left.toString();
		else
			return operator + "(" + left + "," + right + ")";
	}
	
}
