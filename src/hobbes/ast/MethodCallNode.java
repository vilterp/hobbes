package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class MethodCallNode implements ObjectNode {
	
	private ExpressionNode receiver;
	private Token origin;
	private ArrayList<ArgNode> args;
	private String identifier;
	
	public MethodCallNode(ExpressionNode r, Token id) {
		receiver = r;
		origin = id;
		identifier = id.getValue();
		args = null;
	}
	
	public MethodCallNode(ExpressionNode r, Token id, ArrayList<ArgNode> a) {
		receiver = r;
		origin = id;
		identifier = id.getValue();
		args = a;
	}
	
	public MethodCallNode(ExpressionNode r, Token id, String mn,
						ArrayList<ArgNode> a) {
		receiver = r;
		origin = id;
		identifier = mn;
		args = a;
	}
	
	public String toString() {
		String ans = "call(" + receiver + "," + identifier;
		if(args != null)
			ans += "," + args;
		ans += ")";
		return ans;
	}
	
}
