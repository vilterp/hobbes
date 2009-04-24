package hobbes.ast;

public class ExpressionNode implements SyntaxNode {
	
	private TermNode left;
	private String operator;
	private ExpressionNode right;
	
	public ExpressionNode(TermNode l, String o, ExpressionNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public ExpressionNode(TermNode l) {
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
