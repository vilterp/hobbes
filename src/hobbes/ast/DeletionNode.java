package hobbes.ast;

public class DeletionNode implements StatementNode {
	
	private VariableNode var;
	
	public DeletionNode(VariableNode v) {
		var = v;
	}
	
	public String toString() {
		return "del(" + var + ")";
	}
	
}
