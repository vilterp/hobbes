package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class ForLoopNode implements StatementNode {
	
	private VariableNode indexVar;
	private VariableNode loopVar;
	private ExpressionNode collection;
	private BlockNode block;
	private Token inWord;
	
	public ForLoopNode(VariableNode i, VariableNode l, Token iw,
										ExpressionNode c, BlockNode b) {
		indexVar = i;
		loopVar = l;
		inWord = iw;
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
	
	public VariableNode getLoopVar() {
		return loopVar;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
	public ExpressionNode getCollection() {
		return collection;
	}
	
	public Token getInWord() {
		return inWord;
	}
	
}
