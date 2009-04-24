package hobbes.parser;

import hobbes.ast.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;

public class Parser {
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
//		Scanner s = new Scanner(System.in);
//		while(true) {
//			if(t.isReady())
//				System.out.print(">> ");
//			else
//				System.out.print(t.getLastOpener() + "> ");
//			try {
//				String line = s.nextLine(); 
//				t.addCode(line);
//				if(t.isReady())
//					System.out.println(p.parse(t.getTokens(), line));
//			} catch (MismatchException e) {
//				System.err.println(e.getMessage());
//			} catch (UnexpectedTokenException e) {
//				System.err.println(e.getMessage());
//			} catch (SyntaxError e) {
//				System.err.println(e.getMessage());
//				System.err.println(e.show());
//			}
//			
//		}
		String code = "(2+2)^2";
		try {
			t.addCode(code);
			System.out.println(p.parse(t.getTokens(), code));
		} catch (MismatchException e) {
			e.printStackTrace();
		} catch (UnexpectedTokenException e) {
			e.printStackTrace();
		} catch (SyntaxError e) {
			e.printStackTrace();
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
		if(expression())
			return stack.pop();
		else {
			System.out.println(stack);
			return null; // TODO: throw error if no rules matched
		}
		// TODO: clear instance variables after parsing
	}
	
	public void clear() {
		stack.clear();
		tokens.clear();
		line = "";
	}
	
	/*
	 * term , { multOp , expression }	
	 */
	private boolean expression() throws SyntaxError {
		if(!term())
			if(!parentheticalExpression())
				return false;
		if(addOp()) {
			if(!expression()) {
				Token addOpToken = ((TempNode)stack.peek()).getToken();
				throw new SyntaxError("no expression after "+addOpToken.getValue(),
									  addOpToken.getEnd(),
									  line);
			} else {
				ExpressionNode right = (ExpressionNode)stack.pop();
				String operator = ((TempNode)stack.pop()).getToken().getValue();
				ExpressionNode left = (ExpressionNode)stack.pop();
				stack.push(new ExpressionNode(left,operator,right));
				return true;
			}
		} else {
			stack.push(new ExpressionNode((ExpressionNode)stack.pop()));
			return true;
		}
	}
	
	private boolean parentheticalExpression() throws SyntaxError {
		if(!symbol("("))
			return false;
		if(!expression())
			throw new SyntaxError("no expression after (",lastToken().getEnd(),line);
		symbol(")"); // tokenizer ensures it's there
		return true;
	}
	
	/*
	 * powerResult , { addOp , term }
	 */
	private boolean term() throws SyntaxError {
		if(!powerResult())
			if(!parentheticalExpression())
				return false;
		if(multOp()) {
			if(!term()) {
				Token multOpToken = ((TempNode)stack.peek()).getToken();
				throw new SyntaxError("no expression after "+multOpToken.getValue(),
									  multOpToken.getEnd(),
									  line);
			} else {
				ExpressionNode right = (ExpressionNode)stack.pop();
				String operator = ((TempNode)stack.pop()).getToken().getValue();
				ExpressionNode left = (ExpressionNode)stack.pop();
				stack.push(new TermNode(left,operator,right));
				return true;
			}
		} else {
			if(stack.peek() instanceof PowerResultNode)
				stack.push(new TermNode((PowerResultNode)stack.pop()));
			else
				
			return true;
		}
	}
	
	/*
	 * [ number | expression ] , { powerOp , powerResult }
	 * eg 2^2, (2+2)^4, (2+2)^(2+2)
	 */
	private boolean powerResult() throws SyntaxError {
		if(!number())
			if(!parentheticalExpression())
				return false;
		if(powerOp()) {
			if(!powerResult()) {
				Token powerOpToken = ((TempNode)stack.peek()).getToken();
				throw new SyntaxError("no expression after ^",
									  powerOpToken.getEnd(),
									  line);
			} else {
				ExpressionNode right = (ExpressionNode)stack.pop();
				stack.pop(); // don't have to get value cuz only one power operator
				ExpressionNode left = (ExpressionNode)stack.pop();
				stack.push(new PowerResultNode(left,right));
				return true;
			}
		} else {
			if(stack.peek() instanceof NumberNode)
				stack.push(new PowerResultNode((NumberNode)stack.pop()));
			else if(stack.peek() instanceof ExpressionNode)
				stack.push(new PowerResultNode((ExpressionNode)stack.pop()));
			return true;
		}
	}
	
	private boolean powerOp() {
		if(symbol("^"))
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
	
	private Token lastToken() {
		return ((TempNode)stack.peek()).getToken();
	}
	
}