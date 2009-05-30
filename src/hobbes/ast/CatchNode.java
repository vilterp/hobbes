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
	
	public StringNode getName() {
		return errorName;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
}
