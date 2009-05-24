package hobbes.core;

import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.*;
import hobbes.core.builtins.*;
import hobbes.parser.*;

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
				HbSyntaxError error = i.convertSyntaxError(e);
				error.addFrame((ModuleFrame)i.getCurrentFrame());
				error.printStackTrace();
				t.reset();
				p.reset();
			}
			System.out.println(i.interpret(tree).toString());
		}
		
	}
	
	private ObjectSpace objSpace;
	private ExecutionFrame frame;
	
	public Interpreter(String fileName) {
		objSpace = new ObjectSpace();
		frame = new ModuleFrame(fileName);
	}
	
	public HbObject interpret(SyntaxNode tree) {
		try {
			// Scala's pattern matching would shine here...
			if(tree instanceof AtomNode) {
				if(tree instanceof NumberNode) {
					try {
						return new HbInt(objSpace,Integer.parseInt(((NumberNode)tree).getValue()));
					} catch(NumberFormatException e) {
						System.err.println("only integers for now");
					}
				} else if(tree instanceof VariableNode) {
					return objSpace.get(((VariableNode)tree).getValue());
				}
			}
		} catch(HbError e) {
			
		}
		return null;
	}
	
	public ExecutionFrame getCurrentFrame() {
		return frame;
	}
	
	public HbSyntaxError convertSyntaxError(SyntaxError t) {
		return new HbSyntaxError(objSpace, t.getLocation(), t.getMessage());
	}
	
}
