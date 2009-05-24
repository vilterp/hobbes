package hobbes.ast;

public class AssignmentNode implements StatementNode {
	
	private VariableNode var;
	private ExpressionNode expr;
	
	public AssignmentNode(VariableNode v, ExpressionNode e) {
		var = v;
		expr = e;
	}
	
	public String toString() {
		return "=(" + var + "," + expr + ")";
	}
	
}
