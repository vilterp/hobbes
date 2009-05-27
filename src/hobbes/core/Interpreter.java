package hobbes.core;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.*;
import hobbes.parser.*;
import hobbes.values.*;

public class Interpreter {
	
	public static void main(String[] args) {
		
		int lineNo = 1;
		String fileName = "<console>"; 
		
		Scanner s = new Scanner(System.in);
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		Interpreter i = new Interpreter(fileName);
		
		while(true) {
			// print prompt
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			// get input
			SourceLine line = null;
			try {
				line = new SourceLine(s.nextLine(),lineNo,fileName);
				lineNo++;
			} catch(NoSuchElementException e) {
				break;
			}
			SyntaxNode tree = null;
			try {
				// tokenize
				t.addLine(line);
				if(t.isReady()) {
					// parse
					tree = p.parse(t.getTokens());
					if(tree != null) {
						// interpret
						HbValue result = i.interpret(tree);
						if(result != null)
							System.out.println("=> " + result.show());
					}
				}
			} catch(SyntaxError e) {
				HbError error = i.convertSyntaxError(e);
				error.addFrame((ModuleFrame)i.getCurrentFrame());
				error.printStackTrace();
				t.reset();
				p.reset();
			}
		}
	}
	
	private Stack<ExecutionFrame> stack;
	private ObjectSpace objSpace;
	
	public Interpreter(String fileName) {
		objSpace = new ObjectSpace();
		stack = new Stack<ExecutionFrame>();
		stack.push(new ModuleFrame(objSpace,fileName));
	}
	
	public ExecutionFrame getCurrentFrame() {
		return stack.peek();
	}
	
	private void pushFrame(ExecutionFrame f) {
		stack.push(f);
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
	
	public HbValue interpret(SyntaxNode tree) {
		try {
			return run(tree);
		} catch(HbError e) {
			while(canPop()) {
				ExecutionFrame f = getCurrentFrame();
				if(f instanceof ShowableFrame)
					e.addFrame((ShowableFrame)f);
				popFrame();
			}
			e.addFrame((ShowableFrame)getCurrentFrame()); // top-level module frame
			e.printStackTrace();
		}
		return null;
	}
	
	private HbValue run(SyntaxNode tree) throws HbError {
		if(tree instanceof ExpressionNode)
			return evaluate((ExpressionNode)tree);
		else if(tree instanceof StatementNode) {
			exec((StatementNode)tree);
			return null;
		} else {
			// ControlStructureNode
			System.err.println("not doing control structures yet");
			return null;
		}
	}
	
	private void exec(StatementNode stmt) throws HbError {
		if(stmt instanceof DeletionNode)
			delete(((DeletionNode)stmt).getVarName());
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
	
	private void define(FunctionDefNode func) throws HbError {
		try {
			getCurrentFrame().getScope().set(func.getName(),new HbFunction(objSpace,func));
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

	private void delete(String varName) {
		getCurrentFrame().getScope().delete(varName);
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
		}
		return null;
	}
	
	private HbValue evaluateFunctionCall(FunctionCallNode funcCall) throws HbError {
		// get function object
		HbFunction func = null;
		try {
			func = (HbFunction)evaluate(funcCall.getFunction());
		} catch(ClassCastException e) {
			throw new HbError("Not a function",funcCall.getParenLoc());
		}
		// ensure correct # args
		ArrayList<VariableNode> argNames = func.getArgs();
		ArrayList<ExpressionNode> argValues = funcCall.getArgs();
		if(argValues.size() != argNames.size())
			throw new HbError("Wrong Number of Arguments",
						"Function \"" + func.getName() +
						"\" takes " + argNames.size() +
						", got " + argValues.size(),
						funcCall.getParenLoc());
		// push function frame
		pushFrame(new FunctionFrame(objSpace,func.getName(),
										funcCall.getParenLoc(),false));
		// set arg names to arg values
		for(int i=0; i < argNames.size(); i++) {
			try {
				getCurrentFrame().getScope().set(argNames.get(i).getValue(),
									 	evaluate(argValues.get(i)));
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
	
	private HbError convertSyntaxError(SyntaxError t) {
		return new HbError("Syntax Error",t.getMessage(),t.getLocation());
	}
	
}
