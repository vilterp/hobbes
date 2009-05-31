package hobbes.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.*;
import hobbes.parser.*;
import hobbes.values.*;

public class Interpreter {
	
	public static void main(String[] args) {
		if(args.length == 0) { // interactive console
			Scanner s = new Scanner(System.in);
			Interpreter i = new Interpreter("<console>");
			while(true) {
				if(i.needsMore())
					System.out.print(i.getLastOpener() + "> ");
				else
					System.out.print(">> ");
				try {
					i.add(s.nextLine());
					if(!i.needsMore()) {
						HbInstance result = i.getResult();
						if(result != null)
							System.out.println("=> " + result.show());
					}
				} catch(NoSuchElementException e) {
					System.out.println();
					break;
				}
			}
		} else if(args.length == 1) {
			if(args[0].equals("-h")) {
				System.out.println("Run with no args to use the interactive console,\n"
									+ "or with a file name to run that file");
			} else { // run file
				File f = new File(args[0]);
				Scanner s = null;
				try {
					s = new Scanner(f);
				} catch (FileNotFoundException e) {
					System.err.println("File \"" + args[0] + "\" not found.");
					System.exit(0);
				}
				Interpreter i = new Interpreter(args[0]);
				while(s.hasNext()) {
					i.add(s.nextLine());
					if(!i.needsMore())
						i.getResult();
				}
				if(i.needsMore())
					System.err.println("Unexpected end of file in \"" + args[0] + "\": "+
										"still waiting to close " + i.getLastOpener());
			}
		} else {
			System.out.print("Too many args. Run hobbes -h for help.");
		}
	}
	
	private Stack<ExecutionFrame> stack;
	private ObjectSpace objSpace;
	private int lineNo;
	private String fileName;
	private Parser parser;
	private Tokenizer tokenizer;
	
	private static final int MAX_STACK_SIZE = 500;
	
	public Interpreter(String fn) {
		objSpace = new ObjectSpace();
		stack = new Stack<ExecutionFrame>();
		lineNo = 1;
		fileName = fn;
		stack.push(new ModuleFrame(objSpace,fileName));
		parser = new Parser();
		tokenizer = new Tokenizer();
	}
	
	public void add(String line) {
		try {
			tokenizer.addLine(new SourceLine(line,fileName,lineNo));
			lineNo++;
		} catch (SyntaxError e) {
			handleSyntaxError(e);
		}
	}
	
	public boolean needsMore() {
		return !tokenizer.isReady();
	}
	
	public String getLastOpener() {
		return tokenizer.getLastOpener();
	}
	
	public HbInstance getResult() {
		if(needsMore())
			throw new IllegalStateException("More code needed");
		else {
			try {
				return interpret(parser.parse(tokenizer.getTokens()));
			} catch (SyntaxError e) {
				handleSyntaxError(e);
				return null;
			}
		}
	}
	
	private void handleSyntaxError(SyntaxError e) {
		HbError error = new HbError("Syntax Error",e.getMessage(),e.getLocation());
		error.addFrame((ModuleFrame)getCurrentFrame());
		error.printStackTrace();
		tokenizer.reset();
		parser.reset();
	}
	
	private HbInstance interpret(SyntaxNode tree) {
		if(tree != null) {
			try {
				try {
					return run(tree);
				} catch(Return r) {
					throw new HbError("Unexpected Return Statement",
										"not inside a function",
										r.getOrigin().getStart());
				} catch(Break b) {
					throw new HbError("Unexpected Break Statement",
										"not inside a loop",
										b.getOrigin().getStart());
				} catch(Continue c) {
					throw new HbError("Unexpected Continue Statement",
										"not inside a loop",
										c.getOrigin().getStart());
				}
			} catch(HbError e) {
				while(canPop()) {
					ExecutionFrame f = getCurrentFrame();
					e.addFrame(f);
					popFrame();
				}
				e.addFrame(getCurrentFrame()); // top-level frame
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}
	
	private HbInstance run(SyntaxNode tree) throws HbError, Return, Continue, Break {
		if(tree instanceof ExpressionNode)
			return evaluate((ExpressionNode)tree);
		else if(tree instanceof StatementNode) {
			exec((StatementNode)tree);
			return null;
		} else if(tree instanceof IfStatementNode) {
			execIfStatement((IfStatementNode)tree);
			return null;
		} else if(tree instanceof WhileLoopNode) {
			execWhileLoop((WhileLoopNode)tree);
			return null;
		} else if(tree instanceof TryNode) {
			execTry((TryNode)tree);
			return null;
		} else { 
			System.err.println("not doing that control structure yet");
			return null;
		}
	}
	
	private void exec(StatementNode stmt) throws HbError, Return, Continue, Break {
		if(stmt instanceof DeletionNode)
			delete((DeletionNode)stmt);
		else if(stmt instanceof AssignmentNode)
			assign((AssignmentNode)stmt);
		else if(stmt instanceof FunctionDefNode)
			define((FunctionDefNode)stmt);
		else if(stmt instanceof ReturnNode)
			execReturn((ReturnNode)stmt);
		else if(stmt instanceof ThrowNode)
			execThrow((ThrowNode)stmt);
		else if(stmt instanceof ReturnNode)
			execReturn((ReturnNode)stmt);
		else if(stmt instanceof ContinueNode)
			throw new Continue(((ContinueNode)stmt).getOrigin());
		else if(stmt instanceof BreakNode)
			throw new Break(((BreakNode)stmt).getOrigin());
		else
			System.err.println("doesn't do that kind of statement yet");
	}
	
	private void execIfStatement(IfStatementNode stmt) throws HbError, Return, Continue, Break {
		if(evaluateCondition(stmt.getCondition())) {
			runBlock(stmt.getIfBlock());
		} else if(stmt.getElseBlock() != null) {
			runBlock(stmt.getElseBlock());
		}
	}
	
	private void execWhileLoop(WhileLoopNode wl) throws HbError, Return {
		while(evaluateCondition(wl.getCondition())) {
			for(SyntaxNode item: wl.getBlock()) {
				try {
					run(item);
				} catch (Continue e) {
					break;
				} catch (Break e) {
					return;
				}
			}
		}
	}
	
	private void execTry(TryNode t) throws HbError, Return, Continue, Break {
		try {
			runBlock(t.getTryBlock());
		} catch(HbError e) {
			for(CatchNode c: t.getCatches()) {
				if(((HbString)evaluate(c.getName())).getValue().equals(e.getName())) {
					runBlock(c.getBlock());
					return;
				}
			}
			throw e;
		}
		if(t.getFinally() != null)
			runBlock(t.getFinally());
	}
	
	private void execThrow(ThrowNode t) throws HbError {
		throw new HbError(evaluate(t.getName()).toString(),
							(t.getDesc() == null ? null : evaluate(t.getDesc()).toString()),
							t.getOrigin().getStart());
	}
	
	private void execReturn(ReturnNode r) throws Return, HbError {
		throw new Return(r.getOrigin(),evaluate(r.getExpr()));
	}
	
	private void define(FunctionDefNode func) throws HbError {
		try {
			getCurrentFrame().getScope().setGlobal(func.getName(),
										new HbNormalFunction(objSpace,func));
		} catch(ReadOnlyNameException e) {
			throw new HbError("Read Only Error",
								e.getNameInQuestion(),
								func.getNameToken().getStart());
		}
	}

	private void assign(AssignmentNode a) throws HbError {
		try {
			getCurrentFrame().getScope().set(a.getVar().getValue(),evaluate(a.getExpr()));
		} catch (ReadOnlyNameException e) {
			throw new HbError("Read Only Error",
								e.getNameInQuestion(),
								a.getEqualsToken().getStart());
		}
	}

	private void delete(DeletionNode d) throws HbError {
		try {
			getCurrentFrame().getScope().delete(d.getVarName());
		} catch (ReadOnlyNameException e) {
			throw new HbError("Read Only Error",
					e.getNameInQuestion(),
					d.getOrigin().getEnd().next());
		}
	}

	private HbInstance evaluate(ExpressionNode expr) throws HbError {
		if(expr instanceof NumberNode)
			return evaluateNumber((NumberNode)expr);
		else if(expr instanceof StringNode)
			return new HbString(objSpace,((StringNode)expr).getValue());
		else if(expr instanceof VariableNode)
			return evaluateVariable((VariableNode)expr);
		else if(expr instanceof InlineIfStatementNode)
			return evaluateInlineIf((InlineIfStatementNode)expr);
		else if(expr instanceof OperationNode)
			return evaluateOperation((OperationNode)expr);
		else if(expr instanceof FunctionCallNode)
			return evaluateFunctionCall((FunctionCallNode)expr);
		else if(expr instanceof NegativeNode)
			return evaluateNegative((NegativeNode)expr);
		else if(expr instanceof NotNode)
			return evaluateNot((NotNode)expr);
		else
			System.err.println("doesn't do that kind of expression yet");
		return null;
	}
	
	private HbInstance evaluateNumber(NumberNode num) {
		try {
			int value = Integer.parseInt(num.getValue());
			return objSpace.getInt(value);
		} catch(NumberFormatException e) {
			float value = Float.parseFloat(num.getValue());
			return objSpace.getFloat(value);
		}
	}

	private HbInstance evaluateVariable(VariableNode var) throws HbError {
		String varName = var.getValue();
		try {
			return getCurrentFrame().getScope().get(varName);
		} catch (UndefinedNameException e) {
			throw new HbError("Undefined Variable",varName,
								((VariableNode)var).getOrigin().getStart());
		}
	}

	private HbInstance evaluateInlineIf(InlineIfStatementNode ifStatement) throws HbError {
		if(evaluateCondition(ifStatement.getCondition())) {
			return evaluate(ifStatement.getTheIf());
		} else if(ifStatement.getTheElse() != null) {
			return evaluate(ifStatement.getTheElse());
		} else
			return objSpace.getNil();
	}

	private boolean evaluateCondition(ExpressionNode expr) throws HbError {
		return evaluate(expr).toBool() == objSpace.getTrue();
	}
	
	private HbInstance evaluateOperation(OperationNode op) throws HbError {
		String o = op.getOperator().getValue();
		if(o.equals("+") || o.equals("-") || o.equals("*") || o.equals("/") ||
				o.equals("%") || o.equals("^")) {
			HbNumber left = null;
			try {
				left = (HbNumber)evaluate(op.getLeft());
			} catch(ClassCastException e) {
				throw new HbError("Type Error",
									"expression on left of + isn't a number",
									op.getOperator().getStart());
			}
			HbNumber right = null;
			try {
				right = (HbNumber)evaluate(op.getRight());
			} catch(ClassCastException e) {
				throw new HbError("Type Error",
									"expression on right of + isn't a number",
									op.getOperator().getStart());
			}
			if(o.equals("+"))
				return left.plus(right);
			else if(o.equals("-"))
				return left.minus(right);
			else if(o.equals("*"))
				return left.times(right);
			else if(o.equals("/"))
				return left.dividedBy(right);
			else if(o.equals("%"))
				return left.mod(right);
			else if(o.equals("^"))
				return left.toThePowerOf(right);
		} else if(o.equals("is")) {
			return evaluate(op.getLeft()).is(evaluate(op.getRight()));
		} else if(o.equals("==")) {
			if(evaluate(op.getLeft()).getId() ==
						evaluate(op.getRight()).getId())
				return objSpace.getTrue();
			else
				return objSpace.getFalse();
		} else if(o.equals("and")) {
			if(evaluateCondition(op.getLeft()) && evaluateCondition(op.getRight()))
				return objSpace.getTrue();
			else
				return objSpace.getFalse();
		} else if(o.equals("or")) {
			HbInstance leftResult = evaluate(op.getLeft());
			if(leftResult.toBool() == objSpace.getTrue())
				return leftResult;
			else {
				HbInstance rightResult = evaluate(op.getRight());
				if(rightResult.toBool() == objSpace.getTrue())
					return rightResult;
				else
					return objSpace.getFalse();
			}
		} else {
			System.err.println("Can't do that operator yet");
		}
		return null;
	}
	
	private HbInstance evaluateFunctionCall(FunctionCallNode funcCall)
																throws HbError {
		// get function object
		HbFunction func = null;
		try {
			func = (HbFunction)evaluate(funcCall.getFunction());
		} catch(ClassCastException e) {
			throw new HbError("Not a Function",
								"tried to call something that's not a function",
								funcCall.getParenLoc());
		}
		if(func instanceof HbNativeFunction)
			return evaluateNativeFunctionCall(funcCall,(HbNativeFunction)func);
		else
			return evaluateNormalFunctionCall(funcCall,(HbNormalFunction)func);
	}
	
	private HbInstance evaluateNormalFunctionCall(FunctionCallNode funcCall,
													HbNormalFunction func)
															throws HbError {
		// ensure correct # args
		ArrayList<VariableNode> argNames = func.getArgs();
		ArrayList<ExpressionNode> argExprs = funcCall.getArgs();
		if(argExprs.size() != argNames.size())
			throw new HbError("Wrong Number of Arguments",
						"Function \"" + func.getName() +
						"\" takes " + argNames.size() +
						", got " + argExprs.size(),
						funcCall.getParenLoc());
		// evaluate param expressions
		ArrayList<HbInstance> argValues = new ArrayList<HbInstance>();
		for(ExpressionNode expr: argExprs)
			argValues.add(evaluate(expr));
		// push function frame
		try {
			pushFrame(new FunctionFrame(objSpace,getCurrentFrame().getScope(),
									func.getName(),
									funcCall.getParenLoc(),false));
		} catch (StackOverflow e1) {
			throw new HbError("Stack Overflow",funcCall.getParenLoc());
		}
		// set arg names to arg values
		for(int i=0; i < argNames.size(); i++) {
			try {
				getCurrentFrame().getScope().set(argNames.get(i).getValue(),
													argValues.get(i));
			} catch (ReadOnlyNameException e) {
				throw new HbError("Read Only Error",e.getNameInQuestion(),
									argNames.get(i).getOrigin().getStart());
			}
		}
		// execute each block item
		HbInstance lastResult = null;
		for(SyntaxNode blockItem: func.getBlock()) {
			try {
				lastResult = run(blockItem);
			} catch(Break b) {
				throw new HbError("Unexpected Break Statement",
									"not inside a loop",
									b.getOrigin().getStart());
			} catch(Continue c) {
				throw new HbError("Unexpected Continue Statement",
									"not inside a loop",
									c.getOrigin().getStart());
			} catch(Return r) {
				popFrame();
				return r.getToReturn();
			}
		}
		popFrame();
		return lastResult;
	}

	private HbInstance evaluateNativeFunctionCall(FunctionCallNode funcCall,
													HbNativeFunction func)
																throws HbError {
		// ensure correct # args
		if(funcCall.getArgs().size() != func.getArgs().length)
			throw new HbError("Wrong Number of Arguments",
								"Function \"" + func.getName() + "\""
								+ " takes " + func.getArgs().length
								+ ", got " + funcCall.getArgs().size(),
								funcCall.getParenLoc());
		// evaluate arguments
		ArrayList<HbInstance> argValues = new ArrayList<HbInstance>();
		for(ExpressionNode expr: funcCall.getArgs())
			argValues.add(evaluate(expr));
		// run
		if(func.getName().equals("print")) {
			System.out.println(argValues.get(0));
			return objSpace.getNil();
		} else if(func.getName().equals("get_input")) {
			Scanner in = new Scanner(System.in);
			if(argValues.get(0) instanceof HbString)
				System.out.print(argValues.get(0));
			else
				throw new HbError("Type Error","get_input takes a String",
									funcCall.getParenLoc().next());
			return new HbString(objSpace,in.nextLine());
		} else {
			System.err.println("doesn't do that native function yet");
			return objSpace.getNil();
		}
	}
	
	private HbInstance evaluateNegative(NegativeNode neg) throws HbError {
		HbInstance expr = evaluate(neg.getExpr());
		if(expr instanceof HbFloat)
			return objSpace.getFloat(-((HbFloat)expr).getValue());
		else if(expr instanceof HbInt)
			return objSpace.getInt(-((HbInt)expr).getValue());
		else {
			if(expr.toBool() == objSpace.getTrue())
				return objSpace.getFalse();
			else
				return objSpace.getTrue();
		}
	}
	
	private HbInstance evaluateNot(NotNode not) throws HbError {
		if(evaluate(not.getExpr()).toBool() == objSpace.getTrue())
			return objSpace.getFalse();
		else
			return objSpace.getTrue();
	}
	
	private HbInstance runBlock(BlockNode b) throws HbError, Return, Continue, Break {
		HbInstance lastResult = null;
		for(SyntaxNode blockItem: b)
			lastResult = run(blockItem);
		return lastResult;
	}

	private ExecutionFrame getCurrentFrame() {
		return stack.peek();
	}

	private void pushFrame(ExecutionFrame f) throws StackOverflow {
		if(stack.size() < MAX_STACK_SIZE)
			stack.push(f);
		else
			throw new StackOverflow();
	}

	private void popFrame() {
		if(canPop())
			stack.pop();
		else
			throw new IllegalStateException("Can't pop the top level frame");
	}

	private boolean canPop() {
		return stack.size() > 1;
	}
	
}
