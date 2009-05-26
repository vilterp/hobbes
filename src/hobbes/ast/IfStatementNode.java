package hobbes.ast;

public class IfStatementNode implements ControlStructureNode {
	
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
		return "if(" + condition + "," + theIf
		+ "," + (theElse == null ? "" : theElse)
		+ ")";
	}
	
}
