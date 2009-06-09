package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class WhileLoopNode implements StatementNode {
	
	private Token origin;
	private ExpressionNode cond;
	private BlockNode block;
	
	public WhileLoopNode(Token origin, ExpressionNode c, BlockNode b) {
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
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
