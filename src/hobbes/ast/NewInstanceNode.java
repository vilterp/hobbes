package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class NewInstanceNode implements AtomNode {
	
	private VariableNode classVar;
	private ArrayList<ExpressionNode> args;
	private Token opener;
	
	public NewInstanceNode(VariableNode c, Token t, ArrayList<ExpressionNode> a) {
		classVar = c;
		opener = t;
		args = a;
	}
	
	public ArrayList<ExpressionNode> getArgs() {
		return args;
	}
	
	public VariableNode getClassVar() {
		return classVar;
	}
	
	public Token getOpener() {
		return opener;
	}
	
}
