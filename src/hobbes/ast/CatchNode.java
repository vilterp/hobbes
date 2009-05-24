package hobbes.ast;

public class CatchNode implements SyntaxNode {
	
	private StringNode errorName;
	private BlockNode block;
	
	public CatchNode(StringNode n, BlockNode b) {
		errorName = n;
		block = b;
	}
	
	public String toString() {
		return errorName + "(" + block + ")";
	}
	
}
