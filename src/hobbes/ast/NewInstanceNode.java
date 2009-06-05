package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class NewInstanceNode implements AtomNode {
	
	private VariableNode classVar;
	private ArrayList<ExpressionNode> args;
	private Token newWord;
	
	public NewInstanceNode(VariableNode c, Token t, ArrayList<ExpressionNode> a) {
		classVar = c;
		newWord = t;
		if(a != null)
			args = a;
		else
			args = new ArrayList<ExpressionNode>();
	}
	
	public ArrayList<ExpressionNode> getArgs() {
		return args;
	}
	
	public VariableNode getClassVar() {
		return classVar;
	}
	
	public Token getNewWord() {
		return newWord;
	}
	
}
