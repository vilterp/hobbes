package hobbes.ast;

import java.util.ArrayList;

import hobbes.ast.SyntaxNode;
import hobbes.parser.Token;

public class ArgumentsNode implements SyntaxNode {
	
	private ArrayList<ExpressionNode> args;
	private Token opener;
	
	public ArgumentsNode(ArrayList<ExpressionNode> a, Token o) {
		args = a;
		opener = o;
	}
	
	public ArrayList<ExpressionNode> getArgs() {
		return args;
	}
	
	public Token getOpener() {
		return opener;
	}
	
}
