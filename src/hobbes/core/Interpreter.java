package hobbes.core;

import java.util.NoSuchElementException;
import java.util.Scanner;

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
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			String line = null;
			try {
				line = s.nextLine();
			} catch(NoSuchElementException e) {
				break;
			}
			SyntaxNode tree = null;
			try {
				t.addLine(new SourceLine(line,lineNo,fileName));
				tree = p.parse(t.getTokens());
			} catch(SyntaxError e) {
				HbError error = i.convertSyntaxError(e);
				error.addFrame((ModuleFrame)i.getCurrentFrame());
				error.printStackTrace();
				t.reset();
				p.reset();
			}
			HbValue result = i.interpret(tree);
			if(result != null)
				System.out.println("=> " + result.show());
		}
	}
	
	private ExecutionFrame frame;
	private ObjectSpace objSpace;
	
	public Interpreter(String fileName) {
		objSpace = new ObjectSpace();
		frame = new ModuleFrame(objSpace,fileName);
	}
	
	public ExecutionFrame getCurrentFrame() {
		return frame;
	}
	
	public HbError convertSyntaxError(SyntaxError t) {
		return new HbError("Syntax Error",t.getMessage(),t.getLocation());
	}
	
	public HbValue interpret(SyntaxNode tree) {
		try {
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
		} catch(HbError e) {
			while(getCurrentFrame().getEnclosing() != null) {
				ExecutionFrame f = getCurrentFrame();
				if(f instanceof ShowableFrame)
					e.addFrame((ShowableFrame)f);
				frame = f.getEnclosing();
			}
			e.addFrame((ShowableFrame)getCurrentFrame()); // top-level module frame
			e.printStackTrace();
		}
		return null;
	}
	
	public HbValue exec(StatementNode tree) throws HbError {
		if(tree instanceof ExpressionNode) {
			return evaluate((ExpressionNode)tree);
		} else if(tree instanceof DeletionNode) {
			delete(((DeletionNode)tree).getVarName());
			return null;
		} else if(tree instanceof AssignmentNode) {
			AssignmentNode a = (AssignmentNode)tree;
			assign(a.getVar().getValue(),evaluate(a.getExpr()));
			return null;
		} else
			return null;
	}
	
	public HbValue evaluate(ExpressionNode tree) throws HbError {
		if(tree instanceof NumberNode) {
			try {
				int value = Integer.parseInt(((NumberNode)tree).getValue());
				return objSpace.getInt(value);
			} catch(NumberFormatException e) {
				float value = Float.parseFloat(((NumberNode)tree).getValue());
				return objSpace.getFloat(value);
			}
		} else if(tree instanceof StringNode) {
			return new HbString(objSpace,((StringNode)tree).getValue());
		} else if(tree instanceof VariableNode) {
			String varName = ((VariableNode)tree).getValue();
			try {
				return getCurrentFrame().getScope().get(varName);
			} catch (UndefinedNameException e) {
				throw new HbError("Undefined Variable",
									"the variable \"" + varName + "\" is undefined",
									((VariableNode)tree).getOrigin().getStart());
			}
		} else if(tree instanceof OperationNode) {
			OperationNode op = (OperationNode)tree;
			String o = op.getOperator().getValue();
			if(o.equals("+") || o.equals("-") || o.equals("*") || o.equals("/") ||
					o.equals("%") || o.equals("^")) {
				HbNumber left = null;
				try {
					left = (HbNumber)evaluate(op.getLeft());
				} catch(ClassCastException e) {
					throw new HbError("Type Error","expression on left of + isn't a number",
										op.getOperator().getStart());
				}
				HbNumber right = null;
				try {
					right = (HbNumber)evaluate(op.getRight());
				} catch(ClassCastException e) {
					throw new HbError("Type Error","expression on right of + isn't a number",
										op.getOperator().getEnd());
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
			}
		}
		System.err.println("only numbers for the time being");
		return null;
	}
	
	public void delete(String varName) {
		getCurrentFrame().getScope().delete(varName);
	}
	
	public void assign(String var, HbValue val) {
		getCurrentFrame().getScope().set(var, val);
	}
	
}
