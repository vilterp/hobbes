package hobbes.ast;

public class DeletionNode implements SyntaxNode {
	
	private VarNode var;
	
	public DeletionNode(VarNode v) {
		var = v;
	}
	
	public String toString() {
		return "del(" + var + ")";
	}
	
}
