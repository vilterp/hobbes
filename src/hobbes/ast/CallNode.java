package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class CallNode implements ObjectNode {
	
	private ExpressionNode receiver;
	private Token attr;
	private ArrayList<ArgNode> args;
	
	public CallNode(ExpressionNode r, Token at) {
		receiver = r;
		attr = at;
		args = null;
	}
	
	public CallNode(ExpressionNode r, Token at, ArrayList<ArgNode> a) {
		receiver = r;
		attr = at;
		args = a;
	}
	
	public String toString() {
		String ans = "call(" + receiver + "," + attr;
		if(args != null)
			ans += "," + args;
		ans += ")";
		return ans;
	}
	
}
