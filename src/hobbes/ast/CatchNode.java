package hobbes.ast;

public class CatchNode implements SyntaxNode {
	
	private ObjectNode exceptionClass;
	private BlockNode block;
	
	public CatchNode(ObjectNode ec, BlockNode b) {
		exceptionClass = ec;
		block = b;
	}
	
	public String toString() {
		return exceptionClass + "(" + block + ")";
	}
	
}
