package hobbes.ast;

import java.util.ArrayList;

public class CallNode implements ObjectNode {
	
	private ExpressionNode receiver;
	private ArrayList<ExpressionNode> arguments;
	 
	public CallNode(ExpressionNode rec, ArrayList<ExpressionNode> args) {
		receiver = rec;
		arguments = args;
	}
	
	public String toString() {
		return "call(" + arguments + "," + receiver + ")";
	}
	
}
