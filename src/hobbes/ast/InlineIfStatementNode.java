package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class InlineIfStatementNode implements ExpressionNode {
	
	private Token iou;
	private ExpressionNode cond;
	private ExpressionNode theIf;
	private ExpressionNode theElse;
	
	public InlineIfStatementNode(Token o, ExpressionNode c, ExpressionNode i, ExpressionNode e) {
		iou = o;
		cond = c;
		theIf = i;
		theElse = e;
	}
	
	public ExpressionNode getCond() {
		return cond;
	}
	
	public ExpressionNode getIf() {
		return theIf;
	}
	
	public ExpressionNode getElse() {
		return theElse;
	}

	public SourceLine getLine() {
		return iou.getLine();
	}

}
