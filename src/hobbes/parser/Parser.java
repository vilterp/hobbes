package hobbes.parser;

import hobbes.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser {
	
	// TODO: "x if C else y"
	
	private static final Pattern variablePattern =
					Pattern.compile("[a-zA-Z][a-zA-Z0-9]*\\??");
	
	private static final HashSet<String> reservedWords = new HashSet<String>();
	static {
		reservedWords.add("or");
		reservedWords.add("and");
		reservedWords.add("to");
		reservedWords.add("class");
		reservedWords.add("def");
		reservedWords.add("while");
		reservedWords.add("until");
		reservedWords.add("for");
		reservedWords.add("if");
		reservedWords.add("unless");
		reservedWords.add("end");
	}
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		
		Scanner s = new Scanner(System.in);
		while(true) {
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			String line = null;
			try {
				line = s.nextLine();
			} catch(NoSuchElementException e) {
				System.out.println();
			}
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
				p.clear();
			}
			
		}
		
//		String code = "{}";
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
		if(expression()) {
			if(tokens.isEmpty())
				return stack.pop();
			else
				throw new SyntaxError("invalid syntax",tokens.peek().getEnd(),line);
		} else if(!tokens.isEmpty())
			throw new SyntaxError("invalid syntax",tokens.peek().getEnd(),line);
		else
			throw new SyntaxError("invalid syntax",0,line);
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
		if(word("or")) {
			if(or()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("No expression after \"and\"",
									  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean and() throws SyntaxError {
		if(!not())
			if(!parenthesizedExpression())
				return false;
		if(word("and")) {
			if(and()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("No expression after \"and\"",
									  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean not() throws SyntaxError {
		if(word("not")) {
			stack.pop();
			if(test()) {
				stack.push(new NotNode((ExpressionNode)stack.pop()));
				return true;
			} else
				throw new SyntaxError("No expression after \"not\"",
									  	lastToken().getEnd(),line);
		} else if(test())
			return true;
		else
			return false;
	}
	
	private boolean test() throws SyntaxError {
		if(!to())
			if(!parenthesizedExpression())
				return false;
		if(testOp()) {
			if(test()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("No expression after "+lastToken().getValue(),
						  			  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean to() throws SyntaxError {
		if(!addition())
			if(!parenthesizedExpression())
				return false;
		if(word("to")) {
			if(to()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("No expression after \"to\"",
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
				makeOperation();
				return true;
			} else
				throw new SyntaxError("no expression after "+lastToken().getValue(),
									  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean multiplication() throws SyntaxError {
		if(!negative())
			if(!parenthesizedExpression())
				return false;
		if(multOp()) {
			if(multiplication()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("no expression after "+lastToken().getValue(),
									  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean negative() throws SyntaxError {
		if(symbol("-")) {
			Token negative = lastToken();
			stack.pop();
			if(exponent() || parenthesizedExpression()) {
				stack.push(new NegativeNode((ExpressionNode)stack.pop()));
				return true;
			} else {
				tokens.addFirst(negative);
				return false;
			}
		} else if(exponent())
			return true;
		else
			return false;
	}
	
	private boolean exponent() throws SyntaxError {
		if(!object())
			if(!parenthesizedExpression())
				return false;
		if(powerOp()) {
			if(exponent()) {
				makeOperation();
				return true;
			} else
				throw new SyntaxError("no expression after ^",
									  	lastToken().getEnd(),line);
		} else
			return true;
	}
	
	private boolean object() throws SyntaxError {
		if(!atom())
			if(!parenthesizedExpression())
				return false;
		while(true) {
			if(attribute())
				continue;
			else if(call())
				continue;
			else if(subscript())
				continue;
			else
				break;
		}
		return true;
	}
	
	private boolean attribute() throws SyntaxError {
		if(!symbol("."))
			return false;
		else
			stack.pop();
		// FIXME: a methodname rule would be better here,
			//but it wouldn't put TempNodes on the stack
		if(wordWithPattern(variablePattern)) {
			String attr = ((TempNode)stack.pop()).getToken().getValue();
			ExpressionNode expr = (ExpressionNode)stack.pop();
			stack.push(new AttributeNode(expr,attr));
			return true;
		} else
			throw new SyntaxError("no attribute name after \".\"",
								  	lastToken().getEnd(),line);
	}
	
	private boolean subscript() throws SyntaxError {
		Token opener = null;
		if(!symbol("["))
			return false;
		else
			opener = ((TempNode)stack.pop()).getToken();
		if(expression()) {
			symbol("]");
			stack.pop();
			ExpressionNode subscr = (ExpressionNode)stack.pop();
			ExpressionNode obj = (ExpressionNode)stack.pop();
			stack.push(new SubscriptNode(obj,subscr));
			return true;
		} else
			throw new SyntaxError("no expression in []'s",
									opener.getEnd(),line);
	}
	
	private boolean call() throws SyntaxError {
		if(!symbol("("))
			return false;
		else
			stack.pop();
		ArrayList<ExpressionNode> args = arguments();
		symbol(")"); // FIXME: would this ever not be there?
		stack.pop();
		stack.push(new CallNode((ExpressionNode)stack.pop(),args));
		return true;
	}
	
	private ArrayList<ExpressionNode> arguments() throws SyntaxError {
		// TODO: *['splat','args'], **{'kw':'args'}
			// there'll have to be an Argument class or something
		ArrayList<ExpressionNode> results = new ArrayList<ExpressionNode>();
		while(expression()) {
			results.add((ExpressionNode)stack.pop());
			if(symbol(",")) {
				stack.pop();
				if(symbol(")"))
					throw new SyntaxError("trailing comma",
											lastToken().getStart(),line);
			}
		}
		return results;
	}
	
	private boolean atom() throws SyntaxError {
		if(!variable())
			if(!number())
				if(!string())
					if(!regex())
						if(!list())
							if(!dictOrSet())
								return false;
		stack.push(new OperationNode((ObjectNode)stack.pop()));
		return true;
	}

	private boolean list() throws SyntaxError {
		if(!symbol("["))
			return false;
		else
			stack.pop();
		if(symbol("]")) {
			stack.push(new ListNode());
			return true;
		}
		ArrayList<ExpressionNode> elements = new ArrayList<ExpressionNode>();
		while(true) {
			if(expression())
				elements.add((ExpressionNode)stack.pop());
			if(symbol(",")) {
				if(symbol(","))
					throw new SyntaxError("double comma",
											lastToken().getEnd(),line);
				else if(symbol("]")) {
					stack.pop();
					throw new SyntaxError("trailing comma",
											lastToken().getEnd(),line);
				} else
					stack.pop();
			} else {
				symbol("]");
				stack.pop();
				stack.push(new ListNode(elements));
				return true;
			}
		}
	}

	private boolean dictOrSet() throws SyntaxError {
		if(!symbol("{"))
			return false;
		else
			stack.pop();
		if(symbol("}")) {
			stack.push(new DictNode());
			return true;
		}
		HashMap<ExpressionNode,ExpressionNode> elements =
							new HashMap<ExpressionNode,ExpressionNode>();
		while(true) {
			if(expression()) {
				ExpressionNode key = (ExpressionNode)stack.pop();
				if(!symbol(":"))
					return set(key);
				else {
					Token colon = lastToken();
					stack.pop();
					if(expression()) {
						ExpressionNode value = (ExpressionNode)stack.pop();
						elements.put(key, value);
					} else
						throw new SyntaxError("no expression after \":\"",
												colon.getEnd(),line);
				}
			}
			if(symbol(",")) {
				if(symbol(","))
					throw new SyntaxError("double comma",
											lastToken().getEnd(),line);
				else if(symbol("}")) {
					stack.pop();
					throw new SyntaxError("trailing comma",
											lastToken().getEnd(),line);
				} else
					stack.pop();
			} else {
				symbol("}");
				stack.pop();
				stack.push(new DictNode(elements));
				return true;
			}
		}
	}
	
	private boolean set(ExpressionNode firstElement) throws SyntaxError {
		HashSet<ExpressionNode> elements = new HashSet<ExpressionNode>();
		elements.add(firstElement);
		if(symbol(","))
			stack.pop();
		else {
			if(symbol("}")) {
				stack.pop();
				stack.push(new SetNode(elements));
				return true;
			} else
				throw new SyntaxError("trailing comma",
										lastToken().getEnd(),line);
		}
		while(true) {
			if(expression())
				elements.add((ExpressionNode)stack.pop());
			// if not?
			if(symbol(",")) {
				if(symbol(","))
					throw new SyntaxError("double comma",
											lastToken().getEnd(),line);
				else if(symbol("}")) {
					stack.pop();
					throw new SyntaxError("trailing comma",
											lastToken().getEnd(),line);
				} else
					stack.pop();
			} else {
				symbol("}");
				stack.pop();
				stack.push(new SetNode(elements));
				return true;
			}
		}
	}

	private boolean number() {
		if(token(TokenType.NUMBER)) {
			Token numberToken = ((TempNode)stack.pop()).getToken();
			stack.push(new NumberNode(numberToken));
			return true;
		} else
			return false;
	}
	
	private boolean string() {
		if(token(TokenType.STRING)) {
			Token stringToken = ((TempNode)stack.pop()).getToken();
			stack.push(new StringNode(stringToken));
			return true;
		} else
			return false;
	}
	
	private boolean regex() {
		if(token(TokenType.REGEX)) {
			Token regexToken = ((TempNode)stack.pop()).getToken();
			stack.push(new RegexNode(regexToken));
			return true;
		} else
			return false;
	}

	private boolean variable() {
		if(wordWithPattern(variablePattern)) {
			Token variableToken = ((TempNode)stack.pop()).getToken();
			if(reservedWords.contains(variableToken.getValue()))
				return false;
			else {
				stack.push(new VariableNode(variableToken));
				return true;
			}
		} else
			return false;
	}
	
	private boolean testOp() {
		if(symbol("=="))
			return true;
		if(symbol("!="))
			return true;
		if(symbol(">="))
			return true;
		if(symbol("<="))
			return true;
		if(symbol("<"))
			return true;
		if(symbol(">"))
			return true;
		if(word("in"))
			return true;
		if(word("is")) {
			return true;
		}
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
	
	private void makeOperation() {
		ExpressionNode right = (ExpressionNode)stack.pop();
		Token operator = ((TempNode)stack.pop()).getToken();
		ExpressionNode left = (ExpressionNode)stack.pop();
		stack.push(new OperationNode(left,operator,right));
	}
	
}
