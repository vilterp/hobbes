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
			
//			try {
//				t.addLine(new SourceLine("try {",1));
//				t.addLine(new SourceLine("  print(bla)",2));
//				t.addLine(new SourceLine("} catch {",3));
//				t.addLine(new SourceLine("  print(\"whoops!\")",4));
//				t.addLine(new SourceLine("}",5));
//				LinkedList<Token> tokens = t.getTokens();
//				System.out.println(p.parse(tokens));
//			} catch (SyntaxError e) {
//				System.err.println(e.getMessage());
//				System.err.println(e.getLocation().show());
//				e.printStackTrace();
//			}
			
	}

	private static final Pattern variablePattern =
					Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*(\\?|!)?");
	private static final Pattern instanceVarPattern = 
					Pattern.compile("@[a-zA-Z_][a-zA-Z0-9_]*");
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
					throw new SyntaxError("Parser error: stack not empty: " + stack,
											tokens.peek().getStart());
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
				methodDef() || classDef() || tryCatch()) {
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
		return assignment() || deletionStatement() || importStatement() ||
				returnStatement();
	}

	private boolean assignment() throws SyntaxError {
		if(tokenAhead(TokenType.SYMBOL,"=") && object()) {
			ObjectNode leftObj = (ObjectNode)stack.pop();
			if(symbol("=")) {
				Token equals = getLastToken();
				if(expression()) {
					ExpressionNode rightExpr = getLastExpression();
					if(leftObj instanceof VarNode) {
						stack.push(new AssignmentNode(
											(VarNode)leftObj,rightExpr));
						return true;
					}
					// make sure it's assignable
					if(leftObj instanceof AtomNode)
						throw new SyntaxError("Can't assign to an atom",
												equals.getStart());
					if(leftObj instanceof FunctionCallNode)
						throw new SyntaxError("Can't assign to the result of a " +
												"function call",
												equals.getStart());
					// ok, it's a method call
					MethodCallNode left = (MethodCallNode)leftObj;
					// foo[bar] = baz
					if(left.getMethodName().equals("[]")) {
						ArrayList<ArgNode> args = new ArrayList<ArgNode>();
						args.add(left.getArgs().get(0));
						args.add(new ArgNode(rightExpr,ArgType.NORMAL));
						stack.push(new MethodCallNode(left.getReceiver(),
														equals,"[]set",args));
						return true;
					} else if(left.hasArgs())
						throw new SyntaxError("Can't assign to the result of " +
												"a method with arguments",
												equals.getStart());
					else {
						ArrayList<ArgNode> args  = new ArrayList<ArgNode>();
						args.add(new ArgNode(
								new StringNode(left.getOrigin()),ArgType.NORMAL));
						args.add(new ArgNode(rightExpr,ArgType.NORMAL));
						stack.push(new MethodCallNode(left.getReceiver(),
														equals,"setattr",args));
						return true;
					}
				} else
					throw new SyntaxError("No object after =",
											equals.getEnd());
			} else {
				return true;
			}
		} else
			return false;
	}

	private boolean deletionStatement() throws SyntaxError {
		if(word("del")) {
			Token delWord = getLastToken();
			if(object()) {
				ObjectNode delObj = (ObjectNode)stack.pop();
				if(delObj instanceof AtomNode) {
					if(delObj instanceof VarNode) {
						stack.push(new DeletionNode((VarNode)delObj));
						return true;
					} else
						throw new SyntaxError("Can't delete an atom",
												delWord.getEnd().next());
				} else if(delObj instanceof FunctionCallNode)
					throw new SyntaxError("Can't delete the result " +
											"of a function call",
											delWord.getEnd());
				else { // it's a MethodCallNode
					MethodCallNode obj = (MethodCallNode)delObj;
					if(obj.getMethodName().equals("[]")) {
						ArrayList<ArgNode> args = new ArrayList<ArgNode>();
						args.add(obj.getArgs().get(0));
						stack.push(new MethodCallNode(obj.getReceiver(),
														delWord,"[]del",args));
						return true;
					} else if(obj.hasArgs())
						throw new SyntaxError("Can't delete the result of " +
												"a method call with arguments",
												obj.getOrigin().getEnd());
					else {
						ArrayList<ArgNode> args = new ArrayList<ArgNode>();
						args.add(new ArgNode(new StringNode(obj.getOrigin()),
												ArgType.NORMAL));
						stack.push(new MethodCallNode(obj.getReceiver(),
														delWord,"delattr",args));
						return true;
					}
				}
			} else
				throw new SyntaxError("No object after \"del\"",
										delWord.getEnd());
		} else
			return false;
	}

	private boolean importStatement() throws SyntaxError {
		if(word("import")) {
			Token importWord = getLastToken();
			ArrayList<VariableNode> path = new ArrayList<VariableNode>();
			while(true) {
				if(variable()) {
					VariableNode var = (VariableNode)stack.pop();
					path.add(var);
					if(var.getValue().equals("_")) {
						stack.push(new ImportNode(path));
						return true;
					}
				} else if(symbol("{")) {
					stack.pop();
					if(symbol("}"))
						throw getSyntaxError("Nothing inside {}s");
					HashSet<VariableNode> names = new HashSet<VariableNode>();
					while(true) {
						if(variable())
							names.add((VariableNode)stack.pop());
						if(symbol(",")) {
							if(symbol(","))
								throw getSyntaxError("Double comma");
							if(symbol(","))
								throw getSyntaxError("Trailing comma");
							else
								stack.pop();
						} else if(symbol("}")) {
							stack.pop();
							stack.push(new ImportNode(path,names));
							return true;
						} else
							throw new SyntaxError("Missing comma",
													tokens.peek().getStart());
					}
				}
				if(symbol(".")) {
					if(symbol("."))
						throw getSyntaxError("Double dot");
					else if(tokens.isEmpty())
						throw getSyntaxError("Trailing dot");
					else
						stack.pop();
				} else if(tokens.isEmpty()) {
					if(path.isEmpty())
						throw new SyntaxError("No name after \"import\"",
												importWord.getEnd().next());
					stack.push(new ImportNode(path));
					return true;
				} else
					throw new SyntaxError("Unexpected " + tokens.peek().getValue(),
											tokens.peek().getStart());
			}
		} else
			return false;
	}
	
	private boolean returnStatement() throws SyntaxError {
		if(word("return")) {
			Token returnWord = getLastToken();
			if(expression()) {
				stack.push(new ReturnNode(getLastExpression()));
				return true;
			} else
				throw new SyntaxError("No expression after \"return\"",
										returnWord.getEnd().next());
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
	
	private boolean classDef() throws SyntaxError {
		if(word("class")) {
			Token classWord = getLastToken();
			if(wordWithPattern(classNamePattern)) {
				Token name = getLastToken();
				ArgsSpecNode args = null;
				if(argsSpec("(",")"))
					args = (ArgsSpecNode)stack.pop();
				ObjectNode superclass = null;
				if(superclassDef())
					superclass = (ObjectNode)stack.pop();
				BlockNode block = null;
				if(block())
					block = (BlockNode)stack.pop();
				stack.push(new ClassNode(name,args,superclass,block));
				return true;
			} else
				throw new SyntaxError("No class name after \"class\" " +
										"(must start with a capital letter)",
										classWord.getEnd().next());
		} else
			return false;
	}

	private boolean methodDef() throws SyntaxError {
		if(word("def")) {
			Token defWord = getLastToken();
			if(variable()) {
				Token name = ((VariableNode)stack.pop()).getOrigin();
				ArgsSpecNode args = null;
				if(argsSpec("(",")"))
					args = (ArgsSpecNode)stack.pop();
				if(block()) {
					BlockNode block = (BlockNode)stack.pop();
					stack.push(new MethodDefNode(name,args,block));
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
	
	private boolean superclassDef() throws SyntaxError {
		if(symbol("[")) {
			Token opener = getLastToken();
			if(object()) {
				if(symbol("]")) {
					stack.pop();
					return true;
				} else
					throw new SyntaxError("Expected ], found "
											+ tokens.peek().getValue(),
											tokens.peek().getStart());
			} else
				throw new SyntaxError("No class expression after [",
										opener.getStart());
		} else
			return false;
	}
	
	private boolean tryCatch() throws SyntaxError {
		if(word("try")) {
			Token tryWord = getLastToken();
			if(block()) {
				BlockNode tryBlock = (BlockNode)stack.pop();
				ArrayList<CatchNode> catches = new ArrayList<CatchNode>();
				while(word("catch")) {
					Token catchWord = getLastToken();
					if(variable()) {
						ObjectNode exceptionClass = (ObjectNode)stack.pop();
						if(block()) {
							BlockNode catchBlock = (BlockNode)stack.pop();
							catches.add(new CatchNode(exceptionClass,catchBlock));
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
						BlockNode ifBlock = new BlockNode(theIf);
						BlockNode elseBlock = new BlockNode(theElse);
						stack.push(new IfStatementNode(condition,ifBlock,elseBlock));
						return true;
					} else
						throw new SyntaxError("No expression after \"else\"",
												elseWord.getEnd());
				} else {
					BlockNode ifBlock = new BlockNode(theIf);
					stack.push(new IfStatementNode(condition,ifBlock));
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
				throw new SyntaxError("No expression after "+getLastToken().getValue(),
									  	getLastToken().getEnd());
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
				throw new SyntaxError("No expression after "+getLastToken().getValue(),
									  	getLastToken().getEnd());
		} else
			return true;
	}
	
	private boolean negative() throws SyntaxError {
		if(symbol("-")) {
			Token negative = getLastToken();
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
		if(variable()) {
			attr = ((VariableNode)stack.pop()).getOrigin();
		} else
			throw new SyntaxError("no attribute name after \".\"",dot.getEnd());
		if(!symbol("(")) {
			stack.push(new MethodCallNode(getLastExpression(),attr));
			return true;
		} else
			stack.pop();
		destroyEOLsUntil(")");
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
			destroyEOLsUntil(")");
			ArrayList<ArgNode> args = new ArrayList<ArgNode>();
			while(true) {
				if(argument())
					args.add((ArgNode)stack.pop());
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
		return variable() || instanceVar() || number() || string() ||
				regex() || list() || dictOrSet() || character() ||
				anonymousFunction();
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

	private boolean instanceVar() {
		if(wordWithPattern(instanceVarPattern)) {
			stack.push(new InstanceVarNode(getLastToken()));
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

	private boolean anonymousFunction() throws SyntaxError {
		ArgsSpecNode args = null;
		if(!argsSpec("|","|"))
			return false;
		args = (ArgsSpecNode)stack.pop();
		if(block()) {
			BlockNode funcBlock = (BlockNode)stack.pop();
			stack.push(new AnonymousFunctionNode(args,funcBlock));
			return true;
		} else
			throw new SyntaxError("No block after anonymous function header",
									args.getClosingToken().getEnd());
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
			if(expression() || statement())
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
	
	private boolean argsSpec(String open, String close) throws SyntaxError {
		if(!symbol(open))
			return false;
		else
			stack.pop();
		boolean splatAlready = false;
		boolean kwargsAlready = false;
		ArrayList<ArgSpecNode> args = new ArrayList<ArgSpecNode>();
		destroyEOLsUntil(close);
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
			}
			if(symbol(",")) {
				if(symbol(","))
					throw getSyntaxError("Double comma");
				else if(symbol(close)) {
					stack.pop();
					throw getSyntaxError("Trailing comma");
				} else
					stack.pop();
			} else if(symbol(close)) {
				Token closingToken = getLastToken();
				stack.push(new ArgsSpecNode(args,closingToken));
				return true;
			} else {
				throw new SyntaxError("Missing comma",tokens.peek().getStart());
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
			AtomNode defaultValue = null;
			if(defaultSpec())
				defaultValue = (AtomNode)stack.pop();
			stack.push(new ArgSpecNode(argName,type,defaultValue));
			return true;
		} else
			return false;
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
	
	private boolean testOp() {
		if(symbols("=="))
			return true;
		if(symbols("!="))
			return true;
		if(symbols(">="))
			return true;
		if(symbols("<="))
			return true;
		if(symbols("<"))
			return true;
		if(symbols(">"))
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
	
	private boolean symbols(String value) {
		int beginSize = stack.size();
		for(int i=0; i < value.length(); i++) {
			Character c = value.charAt(i);
			if(!symbol(c.toString())) {
				while(stack.size() > beginSize)
					tokens.addFirst(getLastToken());
				return false;
			}
		}
		return true;
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
	
	private boolean tokenAhead(TokenType type, String value) {
		for(Token t: tokens)
			if(t.getType() == type && value.equals(t.getValue()))
				return true;
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
		return new SyntaxError(message,getLastToken().getStart());
	}
	
	private void destroyEOLsUntil(String val) {
		Iterator<Token> it = tokens.iterator();
		while(it.hasNext()) {
			Token t = it.next();
			if(t.getType() == TokenType.SYMBOL && t.getValue().equals(val))
				return;
			else if(t.getType() == TokenType.EOL)
				it.remove();
		}
	}
	
}
