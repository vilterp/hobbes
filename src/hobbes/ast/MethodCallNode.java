package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class MethodCallNode implements ObjectNode {
	
	private ExpressionNode receiver;
	private Token origin;
	private ArrayList<ArgNode> args;
	private String methodName;
	
	public MethodCallNode(ExpressionNode r, Token t) {
		receiver = r;
		origin = t;
		methodName = t.getValue();
		args = null;
	}
	
	public MethodCallNode(ExpressionNode r, Token t, ArrayList<ArgNode> a) {
		receiver = r;
		origin = t;
		methodName = t.getValue();
		args = a;
	}
	
	public MethodCallNode(ExpressionNode r, Token t, String mn,
						ArrayList<ArgNode> a) {
		receiver = r;
		origin = t;
		methodName = mn;
		args = a;
	}
	
	public MethodCallNode(Token t) {
		receiver = null;
		origin = t;
		methodName = t.getValue();
		args = null;
	}
	
	public String toString() {
		String ans = "call(" + 
		(receiver == null ? "themodule" : receiver) + "," 
		+ methodName;
		if(args != null)
			ans += "," + args;
		ans += ")";
		return ans;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public boolean hasArgs() {
		return args != null && args.size() > 0;
	}
	
	public ExpressionNode getReceiver() {
		return receiver;
	}
	
	public ArrayList<ArgNode> getArgs() {
		return args;
	}
	
}
