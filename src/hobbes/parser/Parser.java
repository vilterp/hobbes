package hobbes.parser;

import hobbes.ast.NumberNode;
import hobbes.ast.SyntaxNode;
import hobbes.ast.TempNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Parser {
	
	private Stack<SyntaxNode> stack;
	private LinkedList<Token> tokens;
	private String line; // line of code currently being parsed (for error messages)
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
		tokens = new LinkedList<Token>();
		line = "";
	}
	
	public SyntaxNode parse(ArrayList<Token> tokenList, String code) {
		for(Token t: tokenList)
			tokens.add(t);
		line = code;
		return null;
	}
	
	public void clear() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean term() throws SyntaxError {
		if(!number())
			return false;
		while(multOp()) {
			if(!number())
				throw new SyntaxError("no expression after multiplication operator",
									  ((TempNode)stack.peek()).getToken().getEnd(),
									  line);
		}
		return true;
	}
	
	private boolean addOp() {
		if(symbol("+"))
			return true;
		if(symbol("-"))
			return true;
		return false;
	}
	
	private boolean multOp() {
		if(symbol("*"))
			return true;
		if(symbol("/"))
			return true;
		return false;
	}
	
	private boolean number() {
		if(token(TokenType.NUMBER)) {
			TempNode numberToken = (TempNode)stack.pop();
			stack.push(new NumberNode(numberToken.getToken()));
			return true;
		} else
			return false;
	}
	
	private boolean symbol(String value) {
		return token(TokenType.SYMBOL,value);
	}
	
	private boolean token(TokenType type, String value) {
		if(tokens.isEmpty())
			return false;
		else if(tokens.peek().getType() == type &&
				tokens.peek().getValue().equals(value)) {
			stack.push(new TempNode(tokens.poll()));
			return true;
		} else
			return false;
	}
	
	private boolean token(TokenType type) {
		if(tokens.isEmpty())
			return false;
		if(tokens.peek().getType() == type) {
			stack.push(new TempNode(tokens.poll()));
			return true;
		} else
			return false;
	}
	
	
}