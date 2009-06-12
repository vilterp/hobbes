package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class IfStatementNode implements StatementNode {

	private Token origin;
	private ExpressionNode condition;
	private BlockNode theIf;
	private BlockNode theElse;
	
	public IfStatementNode(Token o, ExpressionNode c, BlockNode i) {
		this(o,c,i,null);
	}
	
	public IfStatementNode(Token o, ExpressionNode c, BlockNode i, BlockNode e) {
		origin = o;
		condition = c;
		theIf = i;
		theElse = e;
	}
	
	public String toString() {
		return "if(" + condition + "," + theIf
		+ "," + (theElse == null ? "" : theElse)
		+ ")";
	}

	public ExpressionNode getCondition() {
		return condition;
	}

	public BlockNode getIfBlock() {
		return theIf;
	}

	public BlockNode getElseBlock() {
		return theElse;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
