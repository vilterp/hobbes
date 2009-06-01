package hobbes.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
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
						HbObject result = i.getResult();
						if(result != null)
							System.out.println("=> " + result.toString());
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
	
	public HbObject getResult() {
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
	
	private HbObject interpret(SyntaxNode tree) {
		if(tree != null) {
			try {
				return evaluate((ExpressionNode)tree);
			} catch(ClassCastException e) {
				System.err.println("just expressions for now");
				return null;
			}
		} else
			return null;
	}
	
	private HbObject evaluate(ExpressionNode expr) {
		if(expr instanceof NumberNode) {
			try {
				int value = Integer.parseInt(((NumberNode)expr).getValue());
				return objSpace.getInt(value);
			} catch(NumberFormatException e) {
				System.err.println("only ints for now");
				return null;
			}
		} else {
			System.err.println("doesn't do that kind of expression");
			return null;
		}
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
