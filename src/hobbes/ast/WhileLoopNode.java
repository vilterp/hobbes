package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class WhileLoopNode implements StatementNode {
	
	private Token origin;
	private ExpressionNode cond;
	private BlockNode block;
	
	public WhileLoopNode(Token o, ExpressionNode c, BlockNode b) {
		origin = o;
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
