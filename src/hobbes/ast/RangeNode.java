package hobbes.ast;

import hobbes.parser.Token;

public class RangeNode implements SyntaxNode {
	
	private ExpressionNode start;
	private Token dots;
	private ExpressionNode end;
	
	public RangeNode(ExpressionNode s, Token d, ExpressionNode e) {
		start = s;
		dots = d;
		end = e;
	}
	
	public String toString() {
		return start.toString() + dots.getValue() + end;
	}
	
}
