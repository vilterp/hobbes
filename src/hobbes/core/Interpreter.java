package hobbes.core;

import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.*;
import hobbes.core.builtins.*;
import hobbes.parser.*;

public class Interpreter {
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		Interpreter i = new Interpreter();
		
		int lineNo = 1;
		String fileName = "<console>"; 
		
		while(true) {
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener() + "> ");
			try {
				t.addLine(new SourceLine(s.nextLine(),lineNo,fileName));
			} catch(SyntaxError e) {
				// FIXME: shouldn't syntax errors use the same exception throwing mechanism
							// used normally?
			}
		}
		
	}
	
	private ObjectSpace objSpace;
	private Stack<ExecutionFrame> callStack;
	
	public Interpreter() {
		objSpace = new ObjectSpace();
		callStack = new Stack<ExecutionFrame>();
	}
	
	public HbObject interpret(SyntaxNode ast) {
		// Scala's pattern matching would shine here...
		if(ast instanceof AtomNode) {
			if(ast instanceof NumberNode) {
				try {
					return new HbInt(objSpace,Integer.parseInt(((NumberNode)ast).getValue()));
				} catch(NumberFormatException e) {
					System.err.println("only integers for now");
				}
			} else if(ast instanceof VariableNode) {
				return objSpace.get(((VariableNode)ast).getValue());
			}
		}
		return null;
	}
	
}
