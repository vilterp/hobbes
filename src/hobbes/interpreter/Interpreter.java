package hobbes.interpreter;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.*;
import hobbes.parser.*;
import hobbes.values.*;

public class Interpreter {
	
	public static void main(String[] args) {
		
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
					HbValue result = i.getResult();
					if(result != null)
						System.out.println("=> " + result.show());
				}
			} catch(NoSuchElementException e) {
				System.out.println();
				break;
			}
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
	
	public HbValue getResult() {
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
	
	private HbValue interpret(SyntaxNode tree) {
		if(tree != null) {
			try {
				return run(tree);
			} catch(HbError e) {
				while(canPop()) {
					ExecutionFrame f = getCurrentFrame();
					if(f instanceof ShowableFrame)
						e.addFrame((ShowableFrame)f);
					popFrame();
				}
				e.addFrame((ShowableFrame)getCurrentFrame()); // top-level frame
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}
	
	private HbValue run(SyntaxNode tree) throws HbError {
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
		} else { 
			System.err.println("not doing that control structure yet");
			return null;
		}
	}
	
	private void exec(StatementNode stmt) throws HbError {
		if(stmt instanceof DeletionNode)
			delete((DeletionNode)stmt);
		else if(stmt instanceof AssignmentNode)
			assign((AssignmentNode)stmt);
		else if(stmt instanceof FunctionDefNode)
			define((FunctionDefNode)stmt);
		else if(stmt instanceof ReturnNode)
			throw new HbError("Unexpected Return Statement","not inside a function",
								((ReturnNode)stmt).getOrigin().getStart());
		else
			System.err.println("doesn't do that kind of statement yet");
	}
	
	private void execIfStatement(IfStatementNode stmt) throws HbError {
		if(evaluateCondition(stmt.getCondition())) {
			for(SyntaxNode item: stmt.getIfBlock())
				run(item);
		} else if(stmt.getElseBlock() != null) {
			for(SyntaxNode item: stmt.getElseBlock())
				run(item);
		}
	}
	
	private void execWhileLoop(WhileLoopNode wl) throws HbError {
		while(evaluateCondition(wl.getCondition())) {
			for(SyntaxNode item: wl.getBlock()) {
				if(item instanceof BreakNode)
					return;
				else if(item instanceof ContinueNode)
					break;
				else
					run(item);
			}
		}
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

	private HbValue evaluate(ExpressionNode expr) throws HbError {
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
	
	private HbValue evaluateNumber(NumberNode num) {
		try {
			int value = Integer.parseInt(num.getValue());
			return objSpace.getInt(value);
		} catch(NumberFormatException e) {
			float value = Float.parseFloat(num.getValue());
			return objSpace.getFloat(value);
		}
	}

	private HbValue evaluateVariable(VariableNode var) throws HbError {
		String varName = var.getValue();
		try {
			return getCurrentFrame().getScope().get(varName);
		} catch (UndefinedNameException e) {
			throw new HbError("Undefined Variable",varName,
								((VariableNode)var).getOrigin().getStart());
		}
	}

	private HbValue evaluateInlineIf(InlineIfStatementNode ifStatement) throws HbError {
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
	
	private HbValue evaluateOperation(OperationNode op) throws HbError {
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
			HbValue leftResult = evaluate(op.getLeft());
			if(leftResult.toBool() == objSpace.getTrue())
				return leftResult;
			else {
				HbValue rightResult = evaluate(op.getRight());
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
	
	private HbValue evaluateFunctionCall(FunctionCallNode funcCall)
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
	
	private HbValue evaluateNormalFunctionCall(FunctionCallNode funcCall,
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
		ArrayList<HbValue> argValues = new ArrayList<HbValue>();
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
		HbValue lastResult = null;
		for(SyntaxNode blockItem: func.getBlock()) {
			if(blockItem instanceof ReturnNode) {
				HbValue result = run(((ReturnNode)blockItem).getExpr());
				popFrame();
				return result;
			} else
				lastResult = run(blockItem);
		}
		popFrame();
		return lastResult;
	}

	private HbValue evaluateNativeFunctionCall(FunctionCallNode funcCall,
													HbNativeFunction func)
																throws HbError {
		// ensure correct # args
		if(funcCall.getArgs().size() != func.getArgs().length)
			throw new HbError("Wrong Number of Arguments",
								"Function \"" + func.getName() + "\""
								+ "takes " + func.getArgs().length
								+ ", got " + funcCall.getArgs().size(),
								funcCall.getParenLoc());
		// evaluate arguments
		ArrayList<HbValue> argValues = new ArrayList<HbValue>();
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
	
	private HbValue evaluateNegative(NegativeNode neg) throws HbError {
		HbValue expr = evaluate(neg.getExpr());
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
	
	private HbValue evaluateNot(NotNode not) throws HbError {
		if(evaluate(not.getExpr()).toBool() == objSpace.getTrue())
			return objSpace.getFalse();
		else
			return objSpace.getTrue();
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
