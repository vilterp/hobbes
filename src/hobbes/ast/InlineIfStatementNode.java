package hobbes.ast;

public class InlineIfStatementNode implements ExpressionNode {
	
	private ExpressionNode condition;
	private ExpressionNode theIf;
	private ExpressionNode theElse;
	
	public InlineIfStatementNode(ExpressionNode c, ExpressionNode i, ExpressionNode e) {
		condition = c;
		theIf = i;
		theElse = e;
	}
	
	public String toString() {
		return "inlineIf(" + condition + "," + theIf + "," + theElse + ")";
	}
	
}
