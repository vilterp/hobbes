package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class ThrowNode implements StatementNode {
	
	private Token origin;
	private ExpressionNode exception;
	
	public ThrowNode(Token o, ExpressionNode e) {
		origin = o;
		exception = e;
	}
	
	public String toString() {
		return "throw(" + exception + ")";
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
