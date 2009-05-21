package hobbes.ast;

public class GlobalNode implements StatementNode {
	
	private VariableNode var;
	
	public GlobalNode(VariableNode v) {
		var = v;
	}
	
	public String toString() {
		return "global(" + var + ")";
	}
	
}
