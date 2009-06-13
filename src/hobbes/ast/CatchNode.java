package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class CatchNode implements SyntaxNode {
	
	private Token origin;
	private ObjectNode errorClass;
	private BlockNode block;
	
	public CatchNode(Token o, ObjectNode ec, BlockNode b) {
		origin = o;
		errorClass = ec;
		block = b;
	}
	
	public String toString() {
		return errorClass + "(" + block + ")";
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
	public ObjectNode getErrorClass() {
		return errorClass;
	}
	
}
