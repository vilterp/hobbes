package hobbes.ast;

import hobbes.parser.Token;

import java.util.ArrayList;

public class ArgsSpecNode implements SyntaxNode {
	
	private ArrayList<ArgSpecNode> args;
	private Token closingToken; // for syntax error in anonymousFunction
	
	public ArgsSpecNode(ArrayList<ArgSpecNode> a, Token c) {
		args = a;
		closingToken = c;
	}
	
	public String toString() {
		return args.toString();
	}
	
	public Token getClosingToken() {
		return closingToken;
	}
	
	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}
	
}
