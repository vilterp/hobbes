package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class CatchNode implements SyntaxNode {
	
	private Token origin;
	private ObjectNode exceptionClass;
	private BlockNode block;
	
	public CatchNode(Token o, ObjectNode ec, BlockNode b) {
		origin = o;
		exceptionClass = ec;
		block = b;
	}
	
	public String toString() {
		return exceptionClass + "(" + block + ")";
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
