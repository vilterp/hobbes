package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

import java.util.ArrayList;

public class MethodCallNode implements FunctionNode {
	
	private ExpressionNode receiver;
	private Token origin;
	private ArrayList<ExpressionNode> args;
	private String methodName;
	
	public MethodCallNode(ExpressionNode r, Token t) {
		receiver = r;
		origin = t;
		methodName = t.getValue();
		args = new ArrayList<ExpressionNode>();
	}
	
	public MethodCallNode(ExpressionNode r, Token t, ArrayList<ExpressionNode> a) {
		receiver = r;
		origin = t;
		methodName = t.getValue();
		args = a;
	}
	
	public MethodCallNode(ExpressionNode r, Token t, String mn,
						ArrayList<ExpressionNode> a) {
		receiver = r;
		origin = t;
		methodName = mn;
		args = a;
	}
	
	public String toString() {
		return "call(" + 
		receiver + "," 
		+ methodName + ","
		+ args
		+ ")";
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
	
	public ArrayList<ExpressionNode> getArgs() {
		return args;
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
