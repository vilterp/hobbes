package hobbes.ast;

import hobbes.parser.Token;

public class IfStatementNode implements ExpressionNode {
	
	private Token ifOrUnless;
	private ExpressionNode condition;
	private BlockNode theIf;
	private BlockNode theElse;
	
	public IfStatementNode(Token iou, ExpressionNode c, BlockNode i) {
		ifOrUnless = iou;
		condition = c;
		theIf = i;
		theElse = null;
	}
	
	public IfStatementNode(Token iou, ExpressionNode c, BlockNode i, BlockNode e) {
		ifOrUnless = iou;
		condition = c;
		theIf = i;
		theElse = e;
	}
	
	public String toString() {
		String ans = "if(" + condition + "," + theIf;
		if(theElse != null) ans += theElse;
		ans += ")";
		return ans;
	}
	
}
