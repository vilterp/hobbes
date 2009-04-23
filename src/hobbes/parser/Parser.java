package hobbes.parser;

import hobbes.ast.NumberNode;
import hobbes.ast.OperationNode;
import hobbes.ast.SyntaxNode;
import hobbes.ast.TempNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Parser {
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		String code = "2*5*2";
		try {
			t.addCode(code);
			System.out.println(p.parse(t.getTokens(), code));
		} catch (MismatchException e) {
			e.printStackTrace();
		} catch (UnexpectedTokenException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			System.err.println(e.getMessage());
			System.err.println(e.show());
		}
	}
	
	private Stack<SyntaxNode> stack;
	private LinkedList<Token> tokens;
	private String line; // line of code currently being parsed (for error messages)
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
		tokens = new LinkedList<Token>();
		line = "";
	}
	
	public SyntaxNode parse(ArrayList<Token> tokenList, String code) throws SyntaxError {
		for(Token t: tokenList)
			tokens.add(t);
		line = code;
		
		if(term())
			return stack.pop();
		else
			return null; // TODO: throw error if no rules matched
		// clear instance vairables after parsing
	}
	
	public void clear() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean term() throws SyntaxError {
		if(!number())
			return false;
		if(multOp()) {
			if(!term()) {
				Token lastToken = ((TempNode)stack.peek()).getToken();
				throw new SyntaxError("no expression after "+lastToken.getValue(),
									  lastToken.getEnd(),
									  line);
			} else {
				OperationNode right = (OperationNode)stack.pop();
				Token operator = ((TempNode)stack.pop()).getToken();
				NumberNode left = (NumberNode)stack.pop();
				stack.push(new OperationNode(left,operator,right));
				return true;
			}
		} else {
			stack.push(new OperationNode((NumberNode)stack.pop()));
			return true;
		}
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