package hobbes.ast;

import hobbes.parser.Token;

public class IfStatementNode implements ExpressionNode {
	
	private ExpressionNode condition;
	private BlockNode theIf;
	private BlockNode theElse;
	
	public IfStatementNode(ExpressionNode c, BlockNode i) {
		condition = c;
		theIf = i;
		theElse = null;
	}
	
	public IfStatementNode(ExpressionNode c, BlockNode i, BlockNode e) {
		condition = c;
		theIf = i;
		theElse = e;
	}
	
	public String toString() {
		String ans = "if(" + condition + "," + theIf;
		if(theElse != null) ans += "," + theElse;
		ans += ")";
		return ans;
	}
	
}
