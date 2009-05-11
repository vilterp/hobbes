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
	
	public static void main(String[] args) {
			Tokenizer t = new Tokenizer();
			Parser p = new Parser();
			
			int lineNo = 1;
			Scanner s = new Scanner(System.in);
			while(true) {
				System.out.print(lineNo + ":");
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
					t.addLine(new SourceLine(line,lineNo));
					if(t.isReady() && t.numTokens() > 0)
						System.out.println(p.parse(t.getTokens()));
				} catch (SyntaxError e) {
					System.err.println(e.getMessage());
					System.err.println(e.getLocation().show());
					p.reset();
					t.reset();
				}
				lineNo++;			
			}
			
//			String code = "'hello'";
//			try {
//				//t.addLine(new SourceLine(code,1));
//				t.addLine(new SourceLine("|abc| {",1));
//				t.addLine(new SourceLine("  print(abc)",2));
//				t.addLine(new SourceLine("}",3));
//				System.out.println(p.parse(t.getTokens()));
//			} catch (SyntaxError e) {
//				System.err.println(e.getMessage());
//				System.err.println(e.getLocation().show());
//			}
			
		}

	private static final Pattern variablePattern =
					Pattern.compile("(_?[a-zA-Z0-9]|[a-zA-Z][a-zA-Z0-9]*(\\?|!)?)");
	private static final Pattern classNamePattern = 
					Pattern.compile("[A-Z][a-zA-Z0-9]*");
	
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
	
	private Stack<SyntaxNode> stack;
	private LinkedList<Token> tokens;
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
		tokens = new LinkedList<Token>();
	}
	
	public SyntaxNode parse(ArrayList<Token> tokenList) throws SyntaxError {
		if(tokenList.isEmpty())
			return null;
		Token firstToken = tokenList.get(0);
		for(Token t: tokenList)
			tokens.add(t);
		if(expression()) {
			if(tokens.isEmpty() && stack.size() == 1)
				return stack.pop();
			else
				throw new SyntaxError("invalid syntax",tokens.peek().getEnd());
		} else if(!tokens.isEmpty())
			throw new SyntaxError("invalid syntax",tokens.peek().getEnd());
		else
			throw new SyntaxError("invalid syntax",firstToken.getStart());
	}
	
	public void reset() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean expression() throws SyntaxError {
		if(!inlineIfStatement())
			if(!parenthesizedExpression())
				return false;
		return true;
	}
	
	private boolean parenthesizedExpression() throws SyntaxError {
		if(symbol("("))
			stack.pop();
		else
			return false;
		if(symbol(")"))
			throw getSyntaxError("nothing inside ()'s");
		if(!expression())
			throw getSyntaxError("no expression after (");
		symbol(")"); // tokenizer makes sure it's there
		stack.pop();
		return true;
	}
	
	private boolean inlineIfStatement() throws SyntaxError {
		if(!or())
			if(!parenthesizedExpression())
				return false;
		if(word("if") || word("unless")) {
			Token ifOrUnless = ((TempNode)stack.pop()).getToken();
			ExpressionNode theIf = (ExpressionNode)stack.pop();
			ExpressionNode condition = null;
			if(or()) {
				condition = (ExpressionNode)stack.pop();
				if(word("else")) {
					Token elseWord = ((TempNode)stack.pop()).getToken();
					if(or()) {
						ExpressionNode theElse = (ExpressionNode)stack.pop();
						BlockNode ifBlock = new BlockNode(theIf);
						BlockNode elseBlock = new BlockNode(theElse);
						stack.push(new IfStatementNode(ifOrUnless,condition,ifBlock,elseBlock));
						return true;
					} else
						throw new SyntaxError("No expression after \"else\"",elseWord.getEnd());
				} else {
					BlockNode ifBlock = new BlockNode(theIf);
					stack.push(new IfStatementNode(ifOrUnless,condition,ifBlock));
					return true;
				}
			} else
				throw new SyntaxError("No expression after \"" + 
										ifOrUnless.getValue() + "\"",
										ifOrUnless.getEnd());
		} else
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
				throw getSyntaxError("No expression after \"and\"");
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
				throw getSyntaxError("No expression after \"and\"");
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
				throw getSyntaxError("No expression after \"not\"");
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
						  			  	lastToken().getEnd());
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
				throw getSyntaxError("No expression after \"to\"");
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
									  	lastToken().getEnd());
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
									  	lastToken().getEnd());
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
		} else if(mod())
			return true;
		else
			return false;
	}
	
	private boolean mod() throws SyntaxError {
		if(!exponent())
			if(!parenthesizedExpression())
				return false;
		if(symbol("%")) {
			if(mod()) {
				makeOperation();
				return true;
			} else
				throw getSyntaxError("no expression after %");
		} else
			return true;
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
				throw getSyntaxError("no expression after ^");
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
		Token dot = null;
		if(!symbol("."))
			return false;
		else
			dot = ((TempNode)stack.pop()).getToken();
		// FIXME: a methodname rule would be better here,
			//but it wouldn't put TempNodes on the stack
		if(wordWithPattern(variablePattern)) {
			String attr = ((TempNode)stack.pop()).getToken().getValue();
			ExpressionNode expr = (ExpressionNode)stack.pop();
			stack.push(new AttributeNode(expr,attr));
			return true;
		} else
			throw new SyntaxError("no attribute name after \".\"",dot.getEnd());
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
			throw new SyntaxError("no expression in []'s",opener.getEnd());
	}
	
	private boolean call() throws SyntaxError {
		if(!symbol("("))
			return false;
		else
			stack.pop();
		if(symbol(")")) {
			stack.pop();
			stack.push(new CallNode((ExpressionNode)stack.pop()));
			return true;
		}
		ArrayList<ExpressionNode> args = new ArrayList<ExpressionNode>();
		while(true) {
			if(expression())
				args.add((ExpressionNode)stack.pop());
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("double comma");
				else if(symbol(")")) {
					stack.pop();
					throw getSyntaxError("trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol(")")) {
					stack.pop();
					stack.push(new CallNode((ExpressionNode)stack.pop(),args));
					return true;
				} else
					throw new SyntaxError("missing comma",tokens.peek().getStart().next());
			}
		}
	}
	
	private boolean atom() throws SyntaxError {
		// TODO: switch to or? does it break after one returns true?
		if(variable() || number() || string() || regex() || list() || dictOrSet() ||
				character() || anonymousFunction()) {
			stack.push(new OperationNode((AtomNode)stack.pop()));
			return true;
		} else
			return false;
	}

	private boolean character() {
		if(token(TokenType.CHAR)) {
			Token charToken = ((TempNode)stack.pop()).getToken();
			stack.push(new CharNode(charToken));
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

	private boolean list() throws SyntaxError {
		if(!symbol("["))
			return false;
		else
			stack.pop();
		if(symbol("]")) {
			stack.pop();
			stack.push(new ListNode());
			return true;
		}
		ArrayList<ExpressionNode> elements = new ArrayList<ExpressionNode>();
		while(true) {
			if(expression())
				elements.add((ExpressionNode)stack.pop());
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("double comma");
				else if(symbol("]")) {
					stack.pop();
					throw getSyntaxError("trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("]")) {
					stack.pop();
					stack.push(new ListNode(elements));
					return true;
				} else
					throw new SyntaxError("missing comma",tokens.peek().getStart().next());
			}
		}
	}

	private boolean dictOrSet() throws SyntaxError {
		if(!symbol("{"))
			return false;
		else
			stack.pop();
		if(symbol("}")) {
			stack.pop();
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
												colon.getEnd());
				}
			}
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("double comma");
				else if(symbol("}")) {
					stack.pop();
					throw getSyntaxError("trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("}")) {
					stack.pop();
					stack.push(new DictNode(elements));
					return true;
				} else
					throw new SyntaxError("missing comma",tokens.peek().getStart().next());
			}
		}
	}
	
	private boolean set(ExpressionNode firstElement) throws SyntaxError {
		HashSet<ExpressionNode> elements = new HashSet<ExpressionNode>();
		elements.add(firstElement);
		if(symbol(",")) {
			if(symbol(","))
				throw getSyntaxError("double comma");
			else if(symbol("}")) {
				stack.pop();
				throw getSyntaxError("trailing comma");
			} else
				stack.pop();
		} else
			throw new SyntaxError("missing comma",tokens.peek().getStart().next());
		while(true) {
			if(expression())
				elements.add((ExpressionNode)stack.pop());
			// if not?
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("double comma");
				else if(symbol("}")) {
					stack.pop();
					throw getSyntaxError("trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("}")) {
					stack.pop();
					stack.push(new SetNode(elements));
					return true;
				} else
					throw new SyntaxError("missing comma",tokens.peek().getStart().next());
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

	private boolean anonymousFunction() throws SyntaxError {
		ArgsSpecNode args = null;
		if(!argsSpec("|","|"))
			return false;
		args = (ArgsSpecNode)stack.pop();
		VariableNode returnType = null;
		if(classSpec())
			returnType = (VariableNode)stack.pop();
		if(!symbol("{"))
			throw new SyntaxError("no block after anonymous function " +
									"argument specification",
									args.getClosingToken().getEnd());
		else
			stack.pop();
		block();
		BlockNode funcBlock = (BlockNode)stack.pop();
		symbol("}");
		stack.pop();
		stack.push(new AnonymousFunctionNode(args,returnType,funcBlock));
		return true;
	}
	
	private boolean block() throws SyntaxError {
		ArrayList<SyntaxNode> lines = new ArrayList<SyntaxNode>();
		// clear out tabs -- for now
		while(token(TokenType.TAB))
			stack.pop();
		while(expression())
			lines.add(stack.pop());
		stack.push(new BlockNode(lines));
		return true;
	}
	
	private boolean argsSpec(String open, String close) throws SyntaxError {
		if(!symbol(open))
			return false;
		else
			stack.pop();
		ArrayList<ArgSpecNode> args = new ArrayList<ArgSpecNode>();
		while(true) {
			if(argSpec()) {
				args.add((ArgSpecNode)stack.pop());
				if(symbol(",")) {
					if(symbol(","))
						throw getSyntaxError("double comma");
					else if(symbol(close))
						throw getSyntaxError("trailing comma");
					else
						stack.pop();
				}
			} else {
				symbol(close);
				Token closingToken = ((TempNode)stack.pop()).getToken();
				stack.push(new ArgsSpecNode(args,closingToken));
				return true;
			}
		}
	}
	
	private boolean argSpec() throws SyntaxError {
		ArgSpecType type = ArgSpecType.NORMAL;
		if(symbol("*")) {
			stack.pop();
			if(symbol("*")) {
				stack.pop();
				type = ArgSpecType.KEYWORDS;
			} else
				type = ArgSpecType.SPLAT;
		}
		if(variable()) {
			Token argName = ((VariableNode)stack.pop()).getOrigin();
			Token className = null;
			if(classSpec())
				className = ((VariableNode)stack.pop()).getOrigin();
			OperationNode defaultValue = null;
			if(defaultSpec())
				defaultValue = (OperationNode)stack.pop();
			stack.push(new ArgSpecNode(argName,type,className,defaultValue));
			return true;
		} else
			return false;
	}
	
	private boolean classSpec() throws SyntaxError {
		if(!symbol(":"))
			return false;
		else {
			Token colon = ((TempNode)stack.pop()).getToken();
			if(className())
				return true;
			else if(word("nil")) {
				stack.push(new VariableNode(((TempNode)stack.pop()).getToken()));
				return true;
			} else
				throw new SyntaxError("no class name after \":\"",
											colon.getEnd());
		}
	}
	
	private boolean defaultSpec() throws SyntaxError {
		if(!symbol("="))
			return false;
		else {
			Token equals = ((TempNode)stack.pop()).getToken();
			if(expression())
				return true;
			else
				throw new SyntaxError("no default value specified after =",
										equals.getStart());
		}
	}

	private boolean className() {
		if(wordWithPattern(classNamePattern)) {
			Token classNameToken = ((TempNode)stack.pop()).getToken();
			stack.push(new VariableNode(classNameToken));
			return true;
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
		if(!token(TokenType.WORD))
			return false;
		else {
			if(pattern.matcher(lastToken().getValue()).matches())
				return true;
			else {
				tokens.addFirst(lastToken());
				stack.pop();
				return false;
			}
		}
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
	
	private SyntaxError getSyntaxError(String message) {
		return new SyntaxError(message,lastToken().getEnd());
	}
	
}
