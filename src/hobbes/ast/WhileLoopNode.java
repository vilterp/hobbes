package hobbes.ast;

public class WhileLoopNode implements ControlStructureNode {
	
	private ExpressionNode cond;
	private BlockNode block;
	
	public WhileLoopNode(ExpressionNode c, BlockNode b) {
		cond = c;
		block = b;
	}
	
	public String toString() {
		return "while(" + cond + "," + block + ")";
	}
	
	public ExpressionNode getCondition() {
		return cond;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
}
