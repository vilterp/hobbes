package hobbes.ast;

import hobbes.parser.SourceLine;

public class ForLoopNode implements StatementNode {
	
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
	
	public SourceLine getLine() {
		return loopVar.getOrigin().getLine();
	}
	
}
