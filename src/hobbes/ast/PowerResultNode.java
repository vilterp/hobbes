package hobbes.ast;

public class PowerResultNode implements SyntaxNode {
	
	private ExpressionNode left;
	private NumberNode number;
	private ExpressionNode right;
	
	public PowerResultNode(ExpressionNode l, ExpressionNode r) {
		left = l;
		right = r;
	}
	
	public PowerResultNode(ExpressionNode l) {
		left = l;
		right = null;
	}
	
	public PowerResultNode(NumberNode l) {
		number = l;
	}
	
	public String toString() {
		if(right == null)
			return left.toString();
		else
			return "^" + "(" + left + "," + right + ")";
	}
	
}
