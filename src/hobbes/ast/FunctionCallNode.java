package hobbes.ast;

import hobbes.parser.SourceLocation;

import java.util.ArrayList;

public class FunctionCallNode implements FunctionNode {
	
	private ExpressionNode function;
	private ArrayList<ExpressionNode> args;
	private SourceLocation parenLoc; // to provide a location, for tracebacks
	
	public FunctionCallNode(ExpressionNode f, ArrayList<ExpressionNode> a, SourceLocation o) {
		function = f;
		args = a;
		parenLoc = o;
	}
	
	public String toString() {
		return "call(" + function + "," + args + ")";
	}
	
	public SourceLocation getParenLoc() {
		return parenLoc;
	}
	
	public ExpressionNode getFuncExpr() {
		return function;
	}
	
	public ArrayList<ExpressionNode> getArgs() {
		return args;
	}
	
}
