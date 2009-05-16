package hobbes.ast;

public class WhileLoopNode implements StatementNode {
	
	private ExpressionNode cond;
	private BlockNode block;
	
	public WhileLoopNode(ExpressionNode c, BlockNode b) {
		cond = c;
		block = b;
	}
	
	public String toString() {
		return "while(" + cond + "," + block + ")";
	}
	
}
