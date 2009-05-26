package hobbes.ast;

public class ForLoopNode implements ControlStructureNode {
	
	private VariableNode indexVar;
	private VariableNode loopVar;
	private ExpressionNode collection;
	private BlockNode block;
	
	public ForLoopNode(VariableNode i, VariableNode l, ExpressionNode c, BlockNode b) {
		indexVar = i;
		loopVar = l;
		collection = c;
		block = b;
	}
	
	public String toString() {
		return "for("
				+ (indexVar == null ? "" : indexVar + ": ")
				+ loopVar + ","
				+ collection + ","
				+ block
				+ ")";
	}
	
}
