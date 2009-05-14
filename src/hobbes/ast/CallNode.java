package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class CallNode implements ObjectNode {
	
	private ExpressionNode receiver;
	private Token origin;
	private ArrayList<ArgNode> args;
	private String identifier;
	
	public CallNode(ExpressionNode r, Token id) {
		receiver = r;
		origin = id;
		identifier = id.getValue();
		args = null;
	}
	
	public CallNode(ExpressionNode r, Token t, ArrayList<ArgNode> a) {
		receiver = r;
		origin = t;
		identifier = t.getValue();
		args = a;
	}
	
	public CallNode(ExpressionNode r, Token t, String mn,
						ArrayList<ArgNode> a) {
		receiver = r;
		origin = t;
		identifier = mn;
		args = a;
	}
	
	public CallNode(Token t) {
		receiver = null;
		origin = t;
		identifier = t.getValue();
		args = null;
	}
	
	public String toString() {
		String ans = "call(" + 
		(receiver == null ? "themodule" : "receiver") + "," 
		+ identifier;
		if(args != null)
			ans += "," + args;
		ans += ")";
		return ans;
	}
	
}
