package hobbes.core;

import java.util.HashMap;
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
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			SyntaxNode tree = null;
			try {
				t.addLine(new SourceLine(s.nextLine(),lineNo,fileName));
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
	private HashMap<String,HbValue> variables;
	
	public Interpreter(String fileName) {
		frame = new ModuleFrame(fileName);
		variables = new HashMap<String,HbValue>();
	}
	
	public ExecutionFrame getCurrentFrame() {
		return frame;
	}
	
	public HbError convertSyntaxError(SyntaxError t) {
		return new HbError("Syntax Error",t.getMessage(),t.getLocation());
	}
	
	public HbValue interpret(SyntaxNode tree) {
		try {
			return exec(tree);
		} catch(HbError e) {
			// ... pop frames, etc
		}
	}
	
	public HbValue exec(SyntaxNode tree) throws HbError {
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
				return new HbInt(Integer.parseInt(((NumberNode)tree).getValue()));
			} catch(NumberFormatException e) {
				System.err.println("Only integers for the time being");
			}
		} else if(tree instanceof VariableNode) {
			String varName = ((VariableNode)tree).getValue();
			HbValue result = variables.get(varName);
			if(result != null)
				return result;
			else
				throw new HbError("Name Error",
									"name \"" + varName + "\" isn't defined",
									((VariableNode)tree).getOrigin().getStart());
		}
		return null;
	}
	
	public void delete(String varName) {
		variables.remove(varName);
	}
	
	public void assign(String var, HbValue val) {
		variables.put(var, val);
	}
	
}
