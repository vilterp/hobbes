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
			
//			int lineNo = 1;
//			Scanner s = new Scanner(System.in);
//			while(true) {
//				System.out.print(lineNo + ":");
//				if(t.isReady())
//					System.out.print(">> ");
//				else
//					System.out.print(t.getLastOpener() + "> ");
//				String line = null;
//				try {
//					line = s.nextLine();
//				} catch(NoSuchElementException e) {
//					System.out.println();
//				}
//				try {
//					t.addLine(new SourceLine(line,lineNo));
//					if(t.isReady() && t.numTokens() > 0)
//						System.out.println(p.parse(t.getTokens()));
//				} catch (SyntaxError e) {
//					System.err.println(e.getMessage());
//					System.err.println(e.getLocation().show());
//					p.reset();
//					t.reset();
//				}
//				lineNo++;
//			}
			
			try {
				t.addLine(new SourceLine("a is 2",1));
				System.out.println(p.parse(t.getTokens()));
			} catch (SyntaxError e) {
				System.err.println(e.getMessage());
				System.err.println(e.getLocation().show());
				e.printStackTrace();
			}
			
	}

	private static final Pattern variablePattern =
					Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*(\\?|!)?");
	private static final Pattern classNamePattern = 
					Pattern.compile("[A-Z][a-zA-Z0-9]*");
	
	private static final HashSet<String> reservedWords = new HashSet<String>();
	static {
		reservedWords.add("or");
		reservedWords.add("and");
		reservedWords.add("not");
		reservedWords.add("to");
		reservedWords.add("in");
		reservedWords.add("is");
		reservedWords.add("class");
		reservedWords.add("trait");
		reservedWords.add("def");
		reservedWords.add("while");
		reservedWords.add("until");
		reservedWords.add("for");
		reservedWords.add("if");
		reservedWords.add("unless");
		reservedWords.add("return");
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
		if(statement() || expression()) {
			if(tokens.isEmpty() && stack.size() == 1)
				return stack.pop();
			else
				throw new SyntaxError("invalid syntax",tokens.peek().getStart());
		} else if(!tokens.isEmpty())
			throw new SyntaxError("invalid syntax",tokens.peek().getStart());
		else
			throw new SyntaxError("invalid syntax",firstToken.getStart());
	}
	
	public void reset() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean statement() throws SyntaxError {
		return assignment(); // later, +=, etc
	}
	
	private boolean assignment() throws SyntaxError {
		ArrayList<VariableNode> vars = new ArrayList<VariableNode>();
		while(variable()) {
			vars.add((VariableNode)stack.pop());
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol("="))
					throw getSyntaxError("Trailing comma");
			} else if(symbol("=")) {
				Token equals = getLastToken();
				if(expression()) {
					stack.push(new AssignmentNode(vars,getLastExpression()));
					return true;
				} else
					throw new SyntaxError("No expression after =",equals.getStart());
			}
		}
		if(vars.size() > 0)
			for(int i=vars.size()-1; i >= 0; i--)
				tokens.addFirst(vars.get(i).getOrigin());
		return false;
	}
	
	private boolean expression() throws SyntaxError {
		return inlineIfStatement() || parenthesizedExpression();
	}
	
	private boolean parenthesizedExpression() throws SyntaxError {
		if(symbol("("))
			stack.pop();
		else
			return false;
		if(symbol(")"))
			throw getSyntaxError("nothing inside ()'s");
		if(!expression())
			throw getSyntaxError("No expression after (");
		symbol(")"); // tokenizer makes sure it's there
		stack.pop();
		return true;
	}
	
	private boolean inlineIfStatement() throws SyntaxError {
		if(!or())
			if(!parenthesizedExpression())
				return false;
		if(word("if") || word("unless")) {
			Token ifOrUnless = getLastToken();
			ExpressionNode theIf = getLastExpression();
			ExpressionNode condition = null;
			if(or()) {
				condition = getLastExpression();
				if(word("else")) {
					Token elseWord = getLastToken();
					if(or()) {
						ExpressionNode theElse = getLastExpression();
						BlockNode ifBlock = new BlockNode(theIf);
						BlockNode elseBlock = new BlockNode(theElse);
						stack.push(new IfStatementNode(
										ifOrUnless,condition,ifBlock,elseBlock));
						return true;
					} else
						throw new SyntaxError("No expression after \"else\"",
												elseWord.getEnd());
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
				stack.push(new NotNode(getLastExpression()));
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
		if(word("not")) {
			if(word("in")) {
				if(test()) {
					ExpressionNode right = getLastExpression();
					Token theIn = getLastToken();
					stack.pop(); // the "not"
					ExpressionNode left = getLastExpression();
					makeOperation(left,theIn,right);
					stack.push(new NotNode(getLastExpression()));
					return true;
				} else
					throw getSyntaxError("No expression after \"not in\"");
			} else
				tokens.addFirst(getLastToken());
		} else if(word("is")) {
			if(word("not")) {
				if(test()) {
					ExpressionNode right = getLastExpression();
					stack.pop(); // the "not"
					Token theIs = getLastToken();
					ExpressionNode left = getLastExpression();
					makeOperation(left,theIs,right);
					stack.push(new NotNode(getLastExpression()));
					return true;
				} else
					throw getSyntaxError("No expression after \"is not\"");
			} else
				tokens.addFirst(getLastToken());
		}
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
				throw new SyntaxError("No expression after "+lastToken().getValue(),
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
				throw new SyntaxError("No expression after "+lastToken().getValue(),
									  	lastToken().getEnd());
		} else
			return true;
	}
	
	private boolean negative() throws SyntaxError {
		if(symbol("-")) {
			Token negative = lastToken();
			stack.pop();
			if(exponent() || parenthesizedExpression()) {
				stack.push(new NegativeNode(getLastExpression()));
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
				throw getSyntaxError("No expression after %");
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
				throw getSyntaxError("No expression after ^");
		} else
			return true;
	}
	
	private boolean object() throws SyntaxError {
		if(!atom())
			if(!parenthesizedExpression())
				return false;
		while(true) {
			if(functionCall())
				continue;
			else if(methodCall())
				continue;
			else if(subscript())
				continue;
			else
				break;
		}
		return true;
	}
	
	private boolean subscript() throws SyntaxError {
		Token opener = null;
		if(!symbol("["))
			return false;
		else
			opener = getLastToken();
		if(expression()) {
			if(symbol("]")) {
				stack.pop();
				ExpressionNode subscr = getLastExpression();
				ExpressionNode obj = getLastExpression();
				ArrayList<ArgNode> args = new ArrayList<ArgNode>();
				args.add(new ArgNode(subscr,ArgType.NORMAL));
				stack.push(new MethodCallNode(obj,opener,"[]",args));
				return true;
			} else
				throw new SyntaxError("Only one expression should be in the []'s",
										tokens.peek().getStart());
		} else
			throw new SyntaxError("No expression in []'s",opener.getEnd());
	}
	
	private boolean methodCall() throws SyntaxError {
		// get dot
		Token dot = null;
		if(!symbol("."))
			return false;
		else
			dot = getLastToken();
		// get attribute name
		Token attr = null;
		if(wordWithPattern(variablePattern)) {
			attr = getLastToken();
		} else
			throw new SyntaxError("no attribute name after \".\"",dot.getEnd());
		if(!symbol("(")) {
			stack.push(new MethodCallNode(getLastExpression(),attr));
			return true;
		} else
			stack.pop();
		ArrayList<ArgNode> args = new ArrayList<ArgNode>();
		if(symbol(")")) {
			stack.pop();
			stack.push(new MethodCallNode(getLastExpression(),attr,args));
			return true;
		}
		while(true) {
			if(argument())
				args.add((ArgNode)stack.pop());
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol(")")) {
					stack.pop();
					throw getSyntaxError("Trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol(")")) {
					stack.pop();
					stack.push(new MethodCallNode(getLastExpression(),attr,args));
					return true;
				} else
					throw new SyntaxError("Missing comma",
											tokens.peek().getStart());
			}
		}
	}
	
	private boolean functionCall() throws SyntaxError {
		if(symbol("(")) {
			stack.pop();
			ExpressionNode function = getLastExpression();
			ArrayList<ArgNode> args = new ArrayList<ArgNode>();
			while(true) {
				if(argument())
					args.add((ArgNode)stack.pop());
				if(symbol(",")) {
					if(symbol(","))
						throw getSyntaxError("Double comma");
					if(symbol(")"))
						throw getSyntaxError("Trailing comma");
					else
						stack.pop();
				} else if(symbol(")")) {
					stack.pop();
					stack.push(new FunctionCallNode(function,args));
					return true;
				} else
					throw new SyntaxError("Missing comma",
											tokens.peek().getStart());
			}
		} else
			return false;
	}

	private boolean argument() throws SyntaxError {
		// named arg
		if(variable()) {
			Token name = ((VariableNode)stack.pop()).getOrigin();
			if(symbol("=")) {
				Token equals = getLastToken();
				if(expression()) {
					ExpressionNode expr = getLastExpression();
					stack.push(new ArgNode(name.getValue(),expr));
					return true;
				} else
					throw new SyntaxError("No expression after \"=\"",
											equals.getEnd());
			} else
				tokens.addFirst(name);
		}
		// *arg and **arg
		if(symbol("*")) {
			Token starOne = getLastToken();
			if(symbol("*")) {
				Token starTwo = getLastToken();
				ArgType type = ArgType.KEYWORDS;
				if(expression()) {
					ExpressionNode expr = getLastExpression();
					stack.push(new ArgNode(expr,type));
					return true;
				} else
					throw new SyntaxError("No expression after \"**\"",
											starTwo.getEnd());
			} else {
				ArgType type = ArgType.SPLAT;
				if(expression()) {
					ExpressionNode expr = getLastExpression();
					stack.push(new ArgNode(expr,type));
					return true;
				} else
					throw new SyntaxError("No expression after \"*\"",
											starOne.getEnd());
			}
		} else if(expression()) {
			ArgType type = ArgType.NORMAL;
			ExpressionNode expr = getLastExpression();
			stack.push(new ArgNode(expr,type));
			return true;
		} else
			return false;
	}
	
	private boolean atom() throws SyntaxError {
		return variable() || number() || string() || regex() || list() ||
				dictOrSet() || character() || anonymousFunction();
	}

	private boolean character() {
		if(token(TokenType.CHAR)) {
			Token charToken = getLastToken();
			stack.push(new CharNode(charToken));
			return true;
		} else
			return false;
	}

	private boolean variable() {
		if(wordWithPattern(variablePattern)) {
			Token variableToken = getLastToken();
			if(reservedWords.contains(variableToken.getValue())) {
				tokens.addFirst(variableToken);
				return false;
			} else {
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
				elements.add(getLastExpression());
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol("]")) {
					stack.pop();
					throw getSyntaxError("Trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("]")) {
					stack.pop();
					stack.push(new ListNode(elements));
					return true;
				} else
					throw new SyntaxError("Missing comma",
											tokens.peek().getStart());
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
				ExpressionNode key = getLastExpression();
				if(!symbol(":"))
					return set(key);
				else {
					Token colon = lastToken();
					stack.pop();
					if(expression()) {
						ExpressionNode value = getLastExpression();
						elements.put(key, value);
					} else
						throw new SyntaxError("No expression after \":\"",
												colon.getEnd());
				}
			}
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol("}")) {
					stack.pop();
					throw getSyntaxError("Trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("}")) {
					stack.pop();
					stack.push(new DictNode(elements));
					return true;
				} else
					throw new SyntaxError("Missing comma",
											tokens.peek().getStart());
			}
		}
	}
	
	private boolean set(ExpressionNode firstElement) throws SyntaxError {
		HashSet<ExpressionNode> elements = new HashSet<ExpressionNode>();
		elements.add(firstElement);
		if(symbol(",")) {
			if(symbol(","))
				throw getSyntaxError("Double comma");
			else if(symbol("}")) {
				stack.pop();
				throw getSyntaxError("Trailing comma");
			} else
				stack.pop();
		} else
			throw new SyntaxError("Missing comma",tokens.peek().getStart());
		while(true) {
			if(expression())
				elements.add(getLastExpression());
			// if not?
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol("}")) {
					stack.pop();
					throw getSyntaxError("Trailing comma");
				} else
					stack.pop();
			} else {
				if(symbol("}")) {
					stack.pop();
					stack.push(new SetNode(elements));
					return true;
				} else
					throw new SyntaxError("Missing comma",
											tokens.peek().getStart());
			}
		}
	}
	
	private boolean number() {
		if(token(TokenType.NUMBER)) {
			Token numberToken = getLastToken();
			stack.push(new NumberNode(numberToken));
			return true;
		} else
			return false;
	}

	private boolean string() {
		if(token(TokenType.STRING)) {
			Token stringToken = getLastToken();
			stack.push(new StringNode(stringToken));
			return true;
		} else
			return false;
	}

	private boolean regex() {
		if(token(TokenType.REGEX)) {
			Token regexToken = getLastToken();
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
		boolean splatAlready = false;
		boolean kwargsAlready = false;
		ArrayList<ArgSpecNode> args = new ArrayList<ArgSpecNode>();
		while(true) {
			if(argSpec()) {
				ArgSpecNode arg = (ArgSpecNode)stack.pop();
				if(arg.getType() == ArgType.SPLAT) {
					if(splatAlready)
						throw new SyntaxError(
								"There can only be one splat argument",
								arg.getNameToken().getStart());								
					else
						splatAlready = true;
				} else if(arg.getType() == ArgType.KEYWORDS) {
					if(kwargsAlready)
						throw new SyntaxError(
								"There can only be one keyword argument",
								arg.getNameToken().getStart());
					else
						kwargsAlready = true;
				} else if(splatAlready)
					throw new SyntaxError("There can't be normal " +
											"arguments after splat arguments",
											arg.getNameToken().getStart());
				args.add(arg);
				if(symbol(",")) {
					if(symbol(","))
						throw getSyntaxError("Double comma");
					else if(symbol(close))
						throw getSyntaxError("Trailing comma");
					else
						stack.pop();
				} else if(symbol(close)) {
					symbol(close);
					Token closingToken = getLastToken();
					stack.push(new ArgsSpecNode(args,closingToken));
					return true;
				} else {
					Token unexpected = tokens.peek();
					throw new SyntaxError("Unexpected \""+
											unexpected.getValue()+"\"",
											unexpected.getStart());
				}
			}
		}
	}
	
	private boolean argSpec() throws SyntaxError {
		ArgType type = ArgType.NORMAL;
		if(symbol("*")) {
			stack.pop();
			if(symbol("*")) {
				stack.pop();
				type = ArgType.KEYWORDS;
			} else
				type = ArgType.SPLAT;
		}
		if(variable()) {
			Token argName = ((VariableNode)stack.pop()).getOrigin();
			Token className = null;
			if(classSpec())
				className = ((VariableNode)stack.pop()).getOrigin();
			AtomNode defaultValue = null;
			if(defaultSpec())
				defaultValue = (AtomNode)stack.pop();
			stack.push(new ArgSpecNode(argName,type,className,defaultValue));
			return true;
		} else
			return false;
	}
	
	private boolean classSpec() throws SyntaxError {
		if(!symbol(":"))
			return false;
		else {
			Token colon = getLastToken();
			if(className())
				return true;
			else if(word("nil")) {
				stack.push(new VariableNode(getLastToken()));
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
			Token equals = getLastToken();
			if(atom())
				return true;
			else
				throw new SyntaxError("no default value specified after =",
										equals.getStart());
		}
	}

	private boolean className() {
		if(wordWithPattern(classNamePattern)) {
			Token classNameToken = getLastToken();
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
	
	private Token getLastToken() {
		return ((TempNode)stack.pop()).getToken();
	}
	
	private ExpressionNode getLastExpression() {
		return (ExpressionNode)stack.pop();
	}
	
	private void makeOperation() {
		ExpressionNode right = getLastExpression();
		Token operator = getLastToken();
		ExpressionNode left = getLastExpression();
		makeOperation(left,operator,right);
	}
	
	private void makeOperation(ExpressionNode left, Token operator,
									ExpressionNode right) {
		ArrayList<ArgNode> args = new ArrayList<ArgNode>();
		args.add(new ArgNode(right,ArgType.NORMAL));
		stack.push(new MethodCallNode(left,operator,args));
	}
	
	private SyntaxError getSyntaxError(String message) {
		return new SyntaxError(message,lastToken().getEnd());
	}
	
}
