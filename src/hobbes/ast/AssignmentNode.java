package hobbes.ast;

public class AssignmentNode implements StatementNode {
	
	private VarNode var;
	private ExpressionNode expr;
	
	public AssignmentNode(VarNode v, ExpressionNode e) {
		var = v;
		expr = e;
	}
	
	public String toString() {
		return "=(" + var + "," + expr + ")";
	}
	
}