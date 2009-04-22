package hobbes.parser;

import hobbes.ast.NumberNode;
import hobbes.ast.SyntaxNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Parser {
	
	public Stack<SyntaxNode> stack;
	public LinkedList<Token> tokens;
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
		tokens = new LinkedList<Token>();
	}
	
	public SyntaxNode parse(ArrayList<Token> tokenList) {
		for(Token t: tokenList)
			tokens.add(t);
		return null;
	}
	
	public void clear() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean number() {
		Token t = token(TokenType.NUMBER);
		if(t == null)
			return false;
		else {
			stack.push(new NumberNode(t.getValue()));
			return true;
		}
	}
	
	private Token token(TokenType type) {
		if(tokens.isEmpty())
			return null;
		else if(tokens.peek().getType() == type)
			return tokens.poll();
		else
			return null;
	}
	
	private Token token(TokenType type, String value) {
		Token t = token(type);
		if(t == null)
			return null;
		else if(t.getValue().equals(value))
			return t;
		else
			return null;
	}
	
}
