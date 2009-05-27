package hobbes.parser;

import hobbes.ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
					t.addLine(new SourceLine(line,lineNo,"<console>"));
					if(t.isReady() && t.numTokens() > 0)
						System.out.println(p.parse(t.getTokens()));
				} catch (SyntaxError e) {
					System.err.println("line "
										+ e.getLocation().getLine().getLineNo() + ": " 
										+ e.getMessage());
					System.err.println(e.getLocation().show());
					p.reset();
					t.reset();
				}
				lineNo++;
			}
			
//			try {
//				t.addLine(new SourceLine("5.times(||{",1));
//				t.addLine(new SourceLine("  print(\"odelay!\")",2));
//				t.addLine(new SourceLine("})",3));
//				LinkedList<Token> tokens = t.getTokens();
//				System.out.println(tokens);
//				System.out.println(p.parse(tokens));
//			} catch (SyntaxError e) {
//				System.err.println(e.getMessage());
//				System.err.println(e.getLocation().show());
//				e.printStackTrace();
//			}
			
	}

	private static final Pattern variablePattern =
					Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*(\\?|!)?");
	private static final Pattern classNamePattern = 
					Pattern.compile("[A-Z][a-zA-Z0-9]*");
	private static final HashSet<String> reservedWords = new HashSet<String>();
	static {
		reservedWords.add("not");
		reservedWords.add("class");
		reservedWords.add("trait");
		reservedWords.add("def");
		reservedWords.add("while");
		reservedWords.add("until");
		reservedWords.add("for");
		reservedWords.add("if");
		reservedWords.add("else");
		reservedWords.add("elif");
		reservedWords.add("match");
		reservedWords.add("case");
		reservedWords.add("try");
		reservedWords.add("catch");
		reservedWords.add("finally");
		reservedWords.add("unless");
		reservedWords.add("return");
		reservedWords.add("del");
		reservedWords.add("import");
	}
	
	private Stack<SyntaxNode> stack;
	private LinkedList<Token> tokens;
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
		tokens = new LinkedList<Token>();
	}
	
	public SyntaxNode parse(LinkedList<Token> tokenList) throws SyntaxError {
		if(tokenList.isEmpty())
			return null;
		Token firstToken = tokenList.getFirst();
		tokens = tokenList;
		if(blockItem()) {
			if(tokens.isEmpty()) {
				if(stack.size() == 1)
					return stack.pop();
				else
					throw new SyntaxError("Parser error: " + stack.size()
											+ " items on stack: " + stack,
											firstToken.getStart());
			} else
				throw new SyntaxError("Unexpected \"" + tokens.peek().getValue() + "\"",
										tokens.peek().getStart());
		} else
			throw new SyntaxError("Invalid syntax",firstToken.getStart());
	}
	
	public void reset() {
		stack.clear();
		tokens.clear();
	}
	
	private boolean blockItem() throws SyntaxError {
		if(statement() || expression() || forLoop() || whileLoop() ||
				functionDef() || tryCatch()) {
			if(eol())
				return true;
			else
				throw new SyntaxError("Expected end of line, found "
										+ tokens.peek().getValue(),
										tokens.peek().getStart());
		} else
			return false;
	}
	
	private boolean statement() throws SyntaxError {
		return assignment() || deletionStatement() || returnStatement() ||
				globalStatement() || raiseStatement();
	}

	private boolean assignment() throws SyntaxError {
		if(variable()) {
			VariableNode var = (VariableNode)stack.pop();
			if(symbol("=")) {
				Token equals = getLastToken();
				if(expression()) {
					ExpressionNode expr = getLastExpression();
					stack.push(new AssignmentNode(var,equals,expr));
					return true;
				} else
					throw new SyntaxError("No expression after \"=\"",
											equals.getEnd());
			} else {
				tokens.addFirst(var.getOrigin());
				return false;
			}
		} else
			return false;
	}

	private boolean deletionStatement() throws SyntaxError {
		if(word("del")) {
			Token delWord = getLastToken();
			if(variable()) {
				VariableNode var = (VariableNode)stack.pop();
				stack.push(new DeletionNode(var));
				return true;
			} else
				throw new SyntaxError("No variable after \"del\"",
										delWord.getEnd());
		} else
			return false;
	}
	
	private boolean returnStatement() throws SyntaxError {
		if(word("return")) {
			Token returnWord = getLastToken();
			if(expression()) {
				stack.push(new ReturnNode(returnWord,getLastExpression()));
				return true;
			} else
				throw new SyntaxError("No expression after \"return\"",
										returnWord.getEnd().next());
		} else
			return false;
	}
	
	private boolean raiseStatement() throws SyntaxError {
		if(word("raise")) {
			Token raiseWord = getLastToken();
			if(string()) {
				StringNode errorName = (StringNode)stack.pop();
				if(symbol(",")) {
					Token comma = getLastToken();
					if(expression()) {
						ExpressionNode errorDesc = getLastExpression();
						stack.push(new RaiseNode(errorName,errorDesc));
						return true;
					} else
						throw new SyntaxError("No description \",\"",
												comma.getEnd());
				} else {
					stack.push(new RaiseNode(errorName));
					return true;
				}
			} else
				throw new SyntaxError("No error name after \"raise\"",
										raiseWord.getEnd().next());
		} else
			return false;
	}
	
	private boolean globalStatement() throws SyntaxError {
		if(word("global")) {
			Token globalWord = getLastToken();
			if(variable()) {
				stack.push(new GlobalNode((VariableNode)stack.pop()));
				return true;
			} else
				throw new SyntaxError("No variable name after \"global\"",
										globalWord.getEnd().next());
		} else
			return false;
	}

	private boolean whileLoop() throws SyntaxError {
		if(word("while") || word("until")) {
			Token iou = getLastToken();
			if(expression()) {
				ExpressionNode cond = getLastExpression();
				if(iou.getValue().equals("until"))
					cond = new NotNode(cond);
				if(block()) {
					BlockNode block = (BlockNode)stack.pop();
					stack.push(new WhileLoopNode(cond,block));
					return true;
				} else
					throw new SyntaxError("No block inside while loop",
											iou.getSourceSpan().getEnd()
											.getLine().getEnd().next());
			} else
				throw new SyntaxError("No expression after \""
										+ iou.getValue() + "\"",
										iou.getEnd().next());
		} else
			return false;
	}
	
	private boolean forLoop() throws SyntaxError {
		if(word("for")) {
			Token forWord = getLastToken();
			if(variable()) {
				VariableNode loopVar = (VariableNode)stack.pop();
				VariableNode indexVar = null;
				if(symbol(":")) {
					Token colon = getLastToken();
					if(variable()) {
						VariableNode temp = loopVar;
						loopVar = (VariableNode)stack.pop();
						indexVar = temp;
					} else
						throw new SyntaxError("No variable after \":\"",
												colon.getEnd());
				}
				if(word("in")) {
					Token inWord = getLastToken();
					if(expression()) {
						ExpressionNode collection = getLastExpression();
						if(block()) {
							BlockNode block = (BlockNode)stack.pop();
							stack.push(new ForLoopNode(indexVar,loopVar,collection,block));
							return true;
						} else
							throw new SyntaxError("No block after for loop heading",
										inWord.getSourceSpan().getEnd()
										.getLine().getEnd().next());
					} else
						throw new SyntaxError("No expression after \"in\"",
												inWord.getEnd().next());
				} else
					throw new SyntaxError("No \"in\" after loop variable",
											loopVar.getOrigin().getEnd().next());
			} else
				throw new SyntaxError("No variable after \"for\"",forWord.getEnd().next());
		} else
			return false;
	}

	private boolean ifStatement() throws SyntaxError {
		if(word("if") || word("unless")) {
			getIf(getLastToken());
			return true;
		} else
			return false;
	}
	
	private boolean getIf(Token startWord) throws SyntaxError {
		if(expression()) {
			ExpressionNode cond = getLastExpression();
			if(startWord.getValue().equals("unless"))
				cond = new NotNode(cond);
			if(block()) {
				BlockNode block = (BlockNode)stack.pop();
				if(word("else")) {
					Token elseWord = getLastToken();
					if(block()) {
						BlockNode elseBlock = (BlockNode)stack.pop();
						stack.push(new IfStatementNode(cond,block,elseBlock));
						return true;
					} else
						throw new SyntaxError("No block after \"else\"",
												elseWord.getEnd().next());
				} else if(word("elif")) {
					Token elifWord = getLastToken();
					getIf(elifWord);
					BlockNode elseBlock = new BlockNode((IfStatementNode)stack.pop());
					stack.push(new IfStatementNode(cond,block,elseBlock));
					return true;
				} else {
					stack.push(new IfStatementNode(cond,block));
					return true;
				}
			} else
				throw new SyntaxError("No block inside if statement",
										startWord.getSourceSpan().getEnd()
										.getLine().getEnd().next());
		} else
			throw new SyntaxError("No condition after \""
									+ startWord.getValue() + "\"",
									startWord.getEnd().next());
	}

	private boolean functionDef() throws SyntaxError {
		if(word("def")) {
			Token defWord = getLastToken();
			// name
			if(variable()) {
				Token name = ((VariableNode)stack.pop()).getOrigin();
				// args spec
				ArrayList<VariableNode> args = argsSpec();
				if(block()) {
					BlockNode block = (BlockNode)stack.pop();
					stack.push(new FunctionDefNode(name,args,block));
					return true;
				} else
					throw new SyntaxError("No block after method heading",
											defWord.getSourceSpan().getEnd()
											.getLine().getEnd().next());
			} else
				throw new SyntaxError("No method name after \"def\"",
										defWord.getEnd().next());
		} else
			return false;
	}
	
	private ArrayList<VariableNode> argsSpec() throws SyntaxError {
		ArrayList<VariableNode> args = new ArrayList<VariableNode>();
		if(symbol("(")) {
			stack.pop();
			while(true) {
				if(variable())
					args.add((VariableNode)stack.pop());
				if(symbol(",")) {
					if(symbol(","))
						throw getSyntaxError("Double comma");
					else if(symbol(")")) {
						stack.pop();
						throw getSyntaxError("Trailing comma");
					} else
						stack.pop();
				} else if(symbol(")")) {
					stack.pop();
					return args;
				}
			}
		} else
			return args;
	}
	
	private boolean tryCatch() throws SyntaxError {
		if(word("try")) {
			Token tryWord = getLastToken();
			if(block()) {
				BlockNode tryBlock = (BlockNode)stack.pop();
				ArrayList<CatchNode> catches = new ArrayList<CatchNode>();
				while(word("catch")) {
					Token catchWord = getLastToken();
					if(string()) {
						StringNode errorName = (StringNode)stack.pop();
						if(block()) {
							BlockNode catchBlock = (BlockNode)stack.pop();
							catches.add(new CatchNode(errorName,catchBlock));
						} else
							throw new SyntaxError("No block after \"catch\"",
											catchWord.getSourceSpan().getEnd()
											.getLine().getEnd().next());
					} else
						throw new SyntaxError("Catch what? " +
												"(Use \"finally\" to catch everything)",
												catchWord.getEnd().next());
				}
				BlockNode finallyBlock = null;
				if(word("finally")) {
					Token finallyWord = getLastToken();
					if(block())
						finallyBlock = (BlockNode)stack.pop();
					else
						throw new SyntaxError("No block after \"finally\"",
												finallyWord.getEnd().next());
				}
				stack.push(new TryNode(tryBlock,catches,finallyBlock));
				return true;
			} else
				throw new SyntaxError("No block after \"try\"",
										tryWord.getSourceSpan().getEnd()
										.getLine().getEnd().next());
		} else
			return false;
	}
	
	private boolean expression() throws SyntaxError {
		return inlineIfStatement() || parenthesizedExpression() || ifStatement();
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
		if(symbol(")")) {
			stack.pop();
			return true;
		} else
			throw new SyntaxError("Expected ), found " + tokens.peek().getValue(),
									tokens.peek().getStart());
	}

	private boolean inlineIfStatement() throws SyntaxError {
		if(!or())
			if(!parenthesizedExpression())
				return false;
		if(word("if") || word("unless")) {
			Token iou = getLastToken();
			ExpressionNode theIf = getLastExpression();
			ExpressionNode condition = null;
			if(or()) {
				condition = getLastExpression();
				if(iou.getValue().equals("unless"))
					condition = new NotNode(condition);
				if(word("else")) {
					Token elseWord = getLastToken();
					if(or()) {
						ExpressionNode theElse = getLastExpression();
						stack.push(new InlineIfStatementNode(
											condition,theIf,theElse));
						return true;
					} else
						throw new SyntaxError("No expression after \"else\"",
												elseWord.getEnd());
				} else {
					stack.push(new InlineIfStatementNode(condition,theIf,null));
					return true;
				}
			} else
				throw new SyntaxError("No expression after \"" + 
										iou.getValue() + "\"",
										iou.getEnd());
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
				throw new SyntaxError("No expression after "+getLastToken().getValue(),
						  			  	getLastToken().getEnd());
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
			Token negative = getLastToken();
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
		if(!atom())
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
	
	private boolean functionCall() throws SyntaxError {
		if(variable()) {
			VariableNode function = (VariableNode)stack.pop();
			if(symbol("(")) {
				Token opener = getLastToken();
				destroyEOLsUntil(")");
				ArrayList<ExpressionNode> args = new ArrayList<ExpressionNode>();
				while(true) {
					if(expression())
						args.add(getLastExpression());
					if(symbol(",")) {
						if(symbol(","))
							throw getSyntaxError("Double comma");
						if(symbol(")")) {
							stack.pop();
							throw getSyntaxError("Trailing comma");
						} else
							stack.pop();
					} else if(symbol(")")) {
						stack.pop();
						stack.push(new FunctionCallNode(function,args,opener.getStart()));
						return true;
					} else
						throw new SyntaxError("Missing comma",
												tokens.peek().getStart());
				}
			} else {
				tokens.addFirst(function.getOrigin());
				return false;
			}
		} else
			return false;
	}
	
	private boolean atom() throws SyntaxError {
		return functionCall() || variable() || number() || string() ||
				regex() || list() || dictOrSet() || character();
	}

	private boolean variable() {
		if(wordWithPattern(variablePattern) || addOp() ||
				multOp() || powerOp() || symbol("%")) {
			Token variableToken = getLastToken();
			if(reservedWords.contains(variableToken.getValue())) {
				tokens.addFirst(variableToken);
				return false;
			} else {
				stack.push(new VariableNode(variableToken));
				return true;
			}
		} else if(symbol("[")) {
			if(symbol("]")) {
				if(word("set")) {
					Token eq = getLastToken();
					Token cl = getLastToken();
					Token op = getLastToken();
					stack.push(new VariableNode(op.mergeWith(cl).mergeWith(eq)));
					return true;
				} else if(word("del")) {
					Token delWord = getLastToken();
					Token cl = getLastToken();
					Token op = getLastToken();
					stack.push(new VariableNode(op.mergeWith(cl).mergeWith(delWord)));
					return true;
				} else {
					Token cl = getLastToken();
					Token op = getLastToken();
					stack.push(new VariableNode(op.mergeWith(cl)));
					return true;
				}
			} else {
				tokens.addFirst(getLastToken());
				return false;
			}
		} else
			return false;
	}

	private boolean character() {
		if(token(TokenType.CHAR)) {
			Token charToken = getLastToken();
			stack.push(new CharNode(charToken));
			return true;
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
		destroyEOLsUntil("]");
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
		destroyEOLsUntil("}");
		HashMap<ExpressionNode,ExpressionNode> elements =
							new HashMap<ExpressionNode,ExpressionNode>();
		while(true) {
			if(expression()) {
				ExpressionNode key = getLastExpression();
				if(!symbol(":"))
					return set(key);
				else {
					Token colon = getLastToken();
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
	
	private boolean block() throws SyntaxError {
		if(!symbol("{"))
			return false;
		else
			stack.pop();
		ArrayList<SyntaxNode> lines = new ArrayList<SyntaxNode>();
		if(eol()) {
			while(true) {
				if(blockItem()) {
					lines.add(stack.pop());
				} else
					break;
			}
			if(symbol("}")) {
				stack.pop();
				stack.push(new BlockNode(lines));
				return true;
			} else {
				Token theUnexpected = tokens.peek();
				throw new SyntaxError("Expected }, found "
										+ theUnexpected.getValue(),
										theUnexpected.getStart());
			}
		} else {
			if(statement() || expression())
				lines.add(stack.pop());
			if(symbol("}")) {
				stack.pop();
				stack.push(new BlockNode(lines));
				return true;
			} else {
				Token theUnexpected = tokens.peek();
				throw new SyntaxError("Expected }, found "
										+ theUnexpected.getValue(),
										theUnexpected.getStart());
			}
		}
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
	
	private boolean eol() {
		if(token(TokenType.EOL)) {
			stack.pop();
			return true;
		} else
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
			Token word = ((TempNode)stack.peek()).getToken();
			if(pattern.matcher(word.getValue()).matches())
				return true;
			else {
				tokens.addFirst(word);
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
	
	private Token getLastToken() {
		return ((TempNode)stack.pop()).getToken();
	}
	
	private Token lastToken() {
		return ((TempNode)stack.peek()).getToken();
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
		stack.push(new OperationNode(left,operator,right));
	}
	
	private SyntaxError getSyntaxError(String message) {
		return new SyntaxError(message,getLastToken().getStart());
	}
	
	private void destroyEOLsUntil(String val) {
		Iterator<Token> it = tokens.iterator();
		int depth = 0;
		while(it.hasNext()) {
			Token t = it.next();
			if(depth == 0 && t.getType() == TokenType.SYMBOL &&
					t.getValue().equals(val))
				return;
			else if(depth == 0 && 
					t.getType() == TokenType.EOL)
				it.remove();
			else if(t.getType() == TokenType.SYMBOL && t.getValue().equals("{"))
				depth++;
			else if(t.getType() == TokenType.SYMBOL && t.getValue().equals("}"))
				depth--;
		}
	}
	
}
