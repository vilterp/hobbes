package hobbes.ast;

import java.util.ArrayList;

public class AssignmentNode implements SyntaxNode {
	
	private ArrayList<VariableNode> vars;
	private ExpressionNode expr;
	
	public AssignmentNode(ArrayList<VariableNode> v, ExpressionNode e) {
		vars = v;
		expr = e;
	}
	
	public String toString() {
		return "=(" + (vars.size() == 1 ? vars.get(0).toString() : vars.toString())
				+ "," + expr + ")"; 
	}
	
}
