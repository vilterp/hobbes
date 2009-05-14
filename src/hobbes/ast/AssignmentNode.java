package hobbes.ast;

import java.util.ArrayList;

public class AssignmentNode implements SyntaxNode {
	
	private InstanceVarNode var;
	private ExpressionNode expr;
	
	public AssignmentNode(InstanceVarNode v, ExpressionNode e) {
		var = v;
		expr = e;
	}
	
	public String toString() {
		return "=(" + var + "," + expr + ")";
	}
	
}
