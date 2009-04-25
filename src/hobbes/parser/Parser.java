package hobbes.parser;

import hobbes.ast.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser {
	
	// FIXME: "+2" => Stack Overflow
	
	private static final Pattern variablePattern =
					Pattern.compile("[a-zA-Z][a-zA-Z0-9]?\\??");
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		
		Scanner s = new Scanner(System.in);
		while(true) {
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			String line = s.nextLine();
			try {
				t.addCode(line);
				if(t.isReady() && t.numTokens() > 0)
					System.out.println(p.parse(t.getTokens(), line));
			} catch (MismatchException e) {
				System.err.println(e.getMessage());
			} catch (UnexpectedTokenException e) {
				System.err.println(e.getMessage());
			} catch (SyntaxError e) {
				System.err.println(e.getMessage());
				System.err.println(e.show());
			}
			
		}
		
//		String code = "2";
//		try {
//			t.addCode(code);
//			System.out.println(p.parse(t.getTokens(), code));
//		} catch (MismatchException e) {
//			e.printStackTrace();
//		} catch (UnexpectedTokenException e) {
//			e.printStackTrace();
//		} catch (SyntaxError e) {
//			System.err.println(e.getMessage());
//			System.err.println(e.show());
//		}
		
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
		if(expression())
			return stack.pop();
		else
			throw new SyntaxError("invalid syntax",tokens.peek().getStart(),line);
		// TODO: clear instance variables after parsing
	}
	
	public void clear() {
		stack.clear();
		tokens.clear();
		line = "";
	}
	
	private boolean expression() throws SyntaxError {
		if(!or())
			if(!parenthesizedExpression())
				return false;
		return true;
	}
	
	private boolean parenthesizedExpression() throws SyntaxError {
		if(symbol("("))
			stack.pop();
		else
			return false;
		if(!expression())
			throw new SyntaxError("no expression after (",
								  lastToken().getEnd(),line);
		symbol(")"); // tokenizer makes sure it's there
		stack.pop();
		return true;
	}
	
	private boolean or() throws SyntaxError {
		if(!and())
			if(!parenthesizedExpression())
				return false;
		if(orOp()) {
			if(or()) {
				makeExpression();
				return true;
			} else
				throw new SyntaxError("No expression after \"and\"",
									  lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean and() throws SyntaxError {
		if(!addition())
			if(!parenthesizedExpression())
				return false;
		if(andOp()) {
			if(and()) {
				makeExpression();
				return true;
			} else
				throw new SyntaxError("No expression after \"and\"",
									  lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean addition() throws SyntaxError {
		if(!multiplication())
			if(!parenthesizedExpression())
				return false;
		if(addOp()) {
			if(addition()) {
				makeExpression();
				return true;
			} else
				throw new SyntaxError("no expression after +",
									  lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean multiplication() throws SyntaxError {
		if(!exponent())
			if(!parenthesizedExpression())
				return false;
		if(multOp()) {
			if(multiplication()) {
				makeExpression();
				return true;
			} else
				throw new SyntaxError("no expression after *",
									  lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean exponent() throws SyntaxError {
		if(!object()) // range()
			if(!parenthesizedExpression())
				return false;
		if(powerOp()) {
			if(exponent()) {
				makeExpression();
				return true;
			} else
				throw new SyntaxError("no expression after ^",
									  lastToken().getEnd(),line);
		} else
			return true;
	}
	
	/*
	private boolean range() throws SyntaxError {
		if(!object())
			if(!parenthesizedExpression())
				return false;
		if(symbol("..") || symbol("...")) {
			if(expression() || parenthesizedExpression()) {
				ExpressionNode end = (ExpressionNode)stack.pop();
				Token dots = ((TempNode)stack.pop()).getToken();
				ExpressionNode start = (ExpressionNode)stack.pop();
				stack.push(new RangeNode(start,dots,end));
				return true;
			} else
				throw new SyntaxError("no expression after "+lastToken().getValue(),
									  lastToken().getEnd(),line);
			
		} else
			return true;
	}
	*/

	private boolean object() throws SyntaxError {
		if(number() || variable() || list()) {
			stack.push(new ExpressionNode((ObjectNode)stack.pop()));
			return true;
		} else
			return false;
	}

	private boolean list() throws SyntaxError {
		if(symbol("["))
			stack.pop();
		else
			return false;
		if(symbol("]")) {
			stack.pop();
			stack.push(new ListNode());
			return true;
		}
		ArrayList<ExpressionNode> elements = new ArrayList<ExpressionNode>();
		while(expression()) {
			elements.add((ExpressionNode)stack.pop());
			if(symbol(","))
				stack.pop();
			else {
				if(symbol("]")) {
					stack.pop();
					stack.push(new ListNode(elements));
					return true;
				}
			}
		}
		return false;
	}

	private boolean number() {
		if(token(TokenType.NUMBER)) {
			Token numberToken = ((TempNode)stack.pop()).getToken();
			stack.push(new NumberNode(numberToken));
			return true;
		} else
			return false;
	}

	private boolean variable() {
		if(wordWithPattern(variablePattern)) {
			Token variableToken = ((TempNode)stack.pop()).getToken();
			stack.push(new VariableNode(variableToken));
			return true;
		} else
			return false;
	}

	private boolean orOp() {
		if(word("or"))
			return true;
		else
			return false;
	}

	private boolean andOp() {
		if(word("and"))
			return true;
		else
			return false;
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

	private boolean powerOp() {
		if(symbol("^"))
			return true;
		else
			return false;
	}

	private boolean symbol(String value) {
		return token(TokenType.SYMBOL,value);
	}
	
	private boolean word(String value) {
		return token(TokenType.WORD,value);
	}
	
	private boolean wordWithPattern(Pattern pattern) {
		if(token(TokenType.WORD) &&
		   pattern.matcher(lastToken().getValue()).matches())
			return true;
		else
			return false;
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
	
	private Token lastToken() {
		return ((TempNode)stack.peek()).getToken();
	}
	
	private void makeExpression() {
		ExpressionNode right = (ExpressionNode)stack.pop();
		Token operator = ((TempNode)stack.pop()).getToken();
		ExpressionNode left = (ExpressionNode)stack.pop();
		stack.push(new ExpressionNode(left,operator,right));
	}
	
}