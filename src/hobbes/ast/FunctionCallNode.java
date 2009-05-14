package hobbes.ast;

import java.util.ArrayList;

public class FunctionCallNode implements ObjectNode {
	
	private ExpressionNode function;
	private ArrayList<ArgNode> args;
	
	public FunctionCallNode(ExpressionNode f, ArrayList<ArgNode> a) {
		function = f;
		args = a;
	}
	
	public String toString() {
		return "call(" + function + "," + args + ")";
	}
	
}
