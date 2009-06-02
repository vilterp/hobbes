package hobbes.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
		HbSyntaxError error =
				new HbSyntaxError(objSpace,e.getMessage(),e.getLocation());
		error.addFrame((ModuleFrame)getCurrentFrame());
		error.printStackTrace();
		tokenizer.reset();
		parser.reset();
	}
	
	private HbObject interpret(SyntaxNode tree) {
		if(tree != null) {
			try {
				return run(tree);
			} catch (HbError e) {
				while(canPop())
					e.addFrame(popFrame());
				e.addFrame(getCurrentFrame());
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}
	
	private HbObject run(SyntaxNode tree) throws HbError {
		if(tree instanceof ExpressionNode)
			return eval((ExpressionNode)tree);
		else if(tree instanceof StatementNode) {
			exec((StatementNode)tree);
			return null;
		} else
			return null;
	}
	
	private HbObject eval(ExpressionNode expr) throws HbError {
		if(expr instanceof NumberNode) {
			try {
				int value = Integer.parseInt(((NumberNode)expr).getValue());
				return objSpace.getInt(value);
			} catch(NumberFormatException e) {
				System.err.println("only ints for now");
				return null;
			}
		} else if(expr instanceof VariableNode)
			return evalVariable((VariableNode)expr);
		else if(expr instanceof MethodCallNode)
			return evalMethodCall((MethodCallNode)expr);
		else {
			System.err.println("doesn't do that kind of expression");
			return null;
		}
	}
	
	private HbObject evalVariable(VariableNode var) throws HbError {
		try {
			return getCurrentFrame().getScope().get(var.getName());
		} catch (UndefinedNameException e) {
			throw new HbUndefinedNameError(objSpace,var.getName(),
											var.getOrigin().getStart());
		}
	}

	private HbObject evalMethodCall(MethodCallNode call) throws HbError {
		HbObject receiver = eval(call.getReceiver());
		HbMethod method = receiver.getMethod(call.getMethodName());
		// check that method exists
		if(method == null)
			throw new HbMissingMethodError(objSpace,call.getMethodName(),
											call.getOrigin().getStart());
		// check correct # args
		if(method.getNumArgs() != call.getNumArgs()) {
			StringBuilder msg = new StringBuilder(call.getMethodName());
			msg.append(" takes ");
			msg.append(method.getNumArgs());
			msg.append(" args, but got ");
			msg.append(call.getNumArgs());
			throw new HbArgumentError(objSpace,msg,call.getOrigin().getStart());
		}
		// eval args
		HbObject[] args = new HbObject[call.getNumArgs()];
		for(int i=0; i < args.length; i++)
			args[i] = eval(call.getArgs().get(i));
		if(method instanceof HbNativeMethod)
			return evalNativeMethodCall(receiver,(HbNativeMethod)method,args);
		else
			return evalNormalMethodCall(receiver,(HbNormalMethod)method,args);
	}
	
	private HbObject evalNormalMethodCall(HbObject receiver,
			HbNormalMethod method, HbObject[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	private HbObject evalNativeMethodCall(HbObject receiver,
						HbNativeMethod method, HbObject[] args) {
		try {
			return (HbObject)method.getMethod().invoke(receiver,args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void exec(StatementNode stmt) throws HbError {
		if(stmt instanceof AssignmentNode)
			assign((AssignmentNode)stmt);
		else
			System.out.println("doesn't do that statement yet");
	}

	private void assign(AssignmentNode stmt) throws HbError {
		try {
			getCurrentFrame().getScope().set(stmt.getVar().getName(),eval(stmt.getExpr()));
		} catch (ReadOnlyNameException e) {
			throw new HbReadOnlyError(objSpace,e.getNameInQuestion(),
												stmt.getEqualsToken().getStart());
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

	private ExecutionFrame popFrame() {
		if(canPop())
			return stack.pop();
		else
			throw new IllegalStateException("Can't pop the top level frame");
	}

	private boolean canPop() {
		return stack.size() > 1;
	}
	
}
