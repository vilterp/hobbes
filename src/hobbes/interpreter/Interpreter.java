package hobbes.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
						String result = i.getResult();
						if(result != null)
							System.out.println("=> " + result);
					}
				} catch(NoSuchElementException e) {
					System.out.println();
					break;
				}
			}
		} else if(args.length == 1) {
			if(args[0].equals("-h")) {
				System.out.println("Run with no args to "
									+ "use the interactive console,\n"
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
					System.err.println("Unexpected end of file in "
							+ "\"" + args[0] + "\": "
							+ "still waiting to close " + i.getLastOpener());
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
	private boolean verboseGC;
	private ArrayList<ExpressionNode> emptyArgs;
	
	private static final int MAX_STACK_SIZE = 500;
	
	public Interpreter(String fn, boolean vgc) {
		verboseGC = vgc;
		objSpace = new ObjectSpace(vgc);
		objSpace.resetCreated();
		if(verboseGC)
			System.out.println("initial object space size: " + objSpace.size());
		stack = new Stack<ExecutionFrame>();
		lineNo = 1;
		fileName = fn;
		stack.push(new ModuleFrame(objSpace,fileName));
		parser = new Parser();
		tokenizer = new Tokenizer();
		emptyArgs = new ArrayList<ExpressionNode>();
	}
	
	public Interpreter(String fn) {
		this(fn,false);
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
	
	public String getResult() {
		if(needsMore())
			throw new IllegalStateException("More code needed");
		else {
			try {
				return interpret(parser.parse(tokenizer.getTokens()));
			} catch(SyntaxError e) {
				handleSyntaxError(e);
				return null;
			}
		}
	}
	
	private String show(HbObject obj) throws ErrorWrapper, SyntaxError {
		return ((HbString)evalMethodCall(obj,"toString",emptyArgs,null)).getValue();
	}
	
	private String interpret(SyntaxNode tree) {
		if(tree != null) {
			try {
				HbObject result = run(tree);
				// collect the garbage
				objSpace.garbageCollectCreated();
				if(verboseGC)
					System.out.println("object space size: " + objSpace.size());
				if(result == null)
					return null;
				if(result instanceof HbString)
					return "\"" + ((HbString)result).sanitizedValue() + "\"";
				else
					return ((HbString)evalMethodCall(result,"toString",emptyArgs,null))
									.sanitizedValue();
			} catch(ErrorWrapper e) {
				while(canPop())
					e.addFrame(popFrame());
				e.addFrame(getCurrentFrame());
				e.printStackTrace();
				return null;
			} catch(Return r) {
				SyntaxError e = new SyntaxError("Unexpected return statment - " +
												"not inside function or method",
													r.getOrigin().getStart());
				handleSyntaxError(e);
				return null;
			} catch (SyntaxError e) {
				handleSyntaxError(e);
				return null;
			}
		} else
			return null;
	}
	
	private HbObject run(SyntaxNode tree) throws ErrorWrapper, Return, SyntaxError {
		if(tree instanceof ExpressionNode)
			return eval((ExpressionNode)tree);
		else if(tree instanceof StatementNode) {
			exec((StatementNode)tree);
			return null;
		} else if(tree instanceof ClassDefNode) {
			defineClass((ClassDefNode)tree);
			return null;
		} else if(tree instanceof MethodDefNode) {
			defineFunction((MethodDefNode)tree);
			return null;
		} else {
			System.out.println("doesn't do " + tree.getClass().getName() + "s yet");
			return null;
		}
	}
	
	private HbObject eval(ExpressionNode expr) throws ErrorWrapper, SyntaxError {
		// atoms
		if(expr instanceof NumberNode)
			return evalNumber((NumberNode)expr);
		else if(expr instanceof VariableNode)
			return evalVariable((VariableNode)expr);
		else if(expr instanceof InstanceVarNode)
			return evalInstanceVar((InstanceVarNode)expr);
		else if(expr instanceof NewInstanceNode)
			return evalNewInstance((NewInstanceNode)expr);
		else if(expr instanceof ListNode)
			return evalList((ListNode)expr);
		else if(expr instanceof AnonymousFunctionNode)
			return evalAnonFunc((AnonymousFunctionNode)expr);
		else if(expr instanceof StringNode)
			return new HbString(objSpace,((StringNode)expr).getValue());
		// calls
		else if(expr instanceof MethodCallNode)
			return evalMethodCall((MethodCallNode)expr);
		else if(expr instanceof FunctionCallNode)
			return evalFunctionCall((FunctionCallNode)expr);
		else {
			System.err.println("doesn't do that kind of expression");
			return null;
		}
	}
	
	private HbObject evalFunctionCall(FunctionCallNode call) throws ErrorWrapper, SyntaxError {
		HbFunction func = (HbFunction)eval(call.getFuncExpr());
		HbObject[] args = evalArgs(call.getArgs(),func,
							func.hbToString().getValue(),call.getParenLoc());
		if(func instanceof HbNativeFunction)
			return evalNativeFuncCall((HbNativeFunction)func,args,call.getParenLoc());
		else if(func instanceof HbNormalFunction)
			return evalNormalFuncCall((HbNormalFunction)func,args,call.getParenLoc());
		else
			return evalAnonFuncCall((HbAnonymousFunction)func,args,call.getParenLoc());
	}

	private HbObject evalAnonFuncCall(HbAnonymousFunction func,
			HbObject[] args, SourceLocation parenLoc) {
		// TODO Auto-generated method stub
		return null;
	}

	private HbObject evalNormalFuncCall(HbNormalFunction func, HbObject[] args,
			SourceLocation parenLoc) {
		// TODO Auto-generated method stub
		return null;
	}

	private HbObject evalNativeFuncCall(HbNativeFunction func, HbObject[] args,
			SourceLocation parenLoc) throws ErrorWrapper, SyntaxError {
		if(func.getName().equals("print")) {
			System.out.println(show(args[0]));
			return objSpace.getNil();
		} else if(func.getName().equals("get_input")) {
			System.out.print(show(args[0]));
			Scanner in = new Scanner(System.in);
			return new HbString(objSpace,in.nextLine());
		} else if(func.getName().equals("eval")) {
			System.err.println("eval hasn't been implemented yet");
			return objSpace.getNil();
		} else
			throw new IllegalArgumentException("Nonexistent native function");
	}

	private HbObject evalAnonFunc(AnonymousFunctionNode func) {
		return new HbAnonymousFunction(objSpace,func.getArgs(),func.getBlock());
	}

	private HbObject evalInstanceVar(InstanceVarNode expr) throws SyntaxError {
		if(getCurrentFrame() instanceof MethodFrame) {
			return ((MethodFrame)getCurrentFrame()).getReceiver().getInstVar(expr.getName());
		} else
			throw new SyntaxError("Unexpected instance variable: not inside a method",
									expr.getOrigin().getStart());
	}

	private HbObject evalNumber(NumberNode num) {
		try {
			int value = Integer.parseInt(num.getValue());
			return objSpace.getInt(value);
		} catch(NumberFormatException e) {
			System.err.println("only ints for now");
			return null;
		}
	}

	private HbObject evalList(ListNode expr) throws ErrorWrapper, SyntaxError {
		ArrayList<HbObject> elements = new ArrayList<HbObject>();
		for(ExpressionNode elem: expr.getElements())
			elements.add(eval(elem));
		return new HbList(objSpace,elements);
	}

	private HbObject evalNewInstance(NewInstanceNode expr) throws ErrorWrapper, SyntaxError {
		String className = expr.getClassVar().getName();
		// get HbClass instance
		HbClass hobbesClass = null;
		try {
			hobbesClass = (HbClass)eval(expr.getClassVar());
		} catch(ClassCastException e) {
			throw new ErrorWrapper(new HbNotAClassError(objSpace,className),
									expr.getClassVar().getOrigin().getStart());
		}
		// get Java class
		Class<?extends HbObject> javaClass =
								objSpace.getClass(className).getJavaClass();
		Constructor constructor = null;
		HbObject newObj = null;
		try {
			// get one-arg (ObjectSpace) constructor
			constructor = javaClass.getConstructor(ObjectSpace.class);
			// make new instance
			newObj = (HbObject)constructor.newInstance(objSpace);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchMethodException e) {
			System.err.println("Class " + javaClass.getName() + " has no constructor " +
					"that takes an ObjectSpace as the sole parameter");
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvocationTargetException e) {
			if(e.getCause() instanceof HbError)
				throw new ErrorWrapper((HbError)e.getCause(),
										expr.getNewWord().getStart());
			else {
				e.printStackTrace();
				System.exit(1);
			}
		}
		newObj.setClass(hobbesClass);
		// call init
		SourceLocation loc = expr.getNewWord().getStart();
		evalMethodCall(newObj,"init",expr.getArgs(),loc);
		return newObj;
	}

	private HbObject evalVariable(VariableNode var) throws ErrorWrapper {
		try {
			return getCurrentFrame().getScope().get(var.getName());
		} catch (HbUndefinedNameError e) {
			throw new ErrorWrapper(e,var.getOrigin().getStart());
		}
	}
	
	private HbObject evalMethodCall(MethodCallNode call) throws ErrorWrapper, SyntaxError {
		HbObject receiver = eval(call.getReceiver());
		HbClass receiverClass = receiver.getHbClass();
		return evalMethodCall(receiver,call.getMethodName(),call.getArgs(),
									call.getOrigin().getStart());
	}

	private HbObject evalMethodCall(HbObject receiver, String methodName,
				ArrayList<ExpressionNode> args, SourceLocation origin)
													throws ErrorWrapper, SyntaxError {
		HbClass receiverClass = receiver.getHbClass();
		HbMethod method = receiverClass.getMethod(methodName);
		// check that method exists
		if(method == null)
			throw new ErrorWrapper(
						new HbMissingMethodError(objSpace,methodName),
						origin);
		HbObject[] argValues = evalArgs(args,method,
									method.getDeclaringClassName() + "#" + method.getName(),
									origin);
		if(method instanceof HbNativeMethod)
			return evalNativeMethodCall(receiver,(HbNativeMethod)method,argValues,
													origin);
		else {
			try {
				return evalNormalMethodCall(receiver,(HbNormalMethod)method,
															argValues,origin);
			} catch (StackOverflow e) {
				throw new ErrorWrapper(new HbStackOverflow(objSpace),
										origin);
			}
		}
	}
	
	private HbObject[] evalArgs(ArrayList<ExpressionNode> args, HbCallable func, String name,
									SourceLocation loc) throws ErrorWrapper, SyntaxError {
		// check not too many args
		if(args.size() > func.getNumArgs())
			throw getArgumentError(name,args.size(),func.getNumArgs(),loc);
		// eval args
		HbObject[] argValues = new HbObject[func.getNumArgs()];
		for(int i=0; i < argValues.length; i++) {
			if(i < args.size())
				argValues[i] = eval(args.get(i));
			else if(func.getDefault(i) != null)
				argValues[i] = eval(func.getDefault(i));
			else
				throw getArgumentError(name,i+1,func.getNumArgs(),loc);
		}
		return argValues;
	}
	
	private ErrorWrapper getArgumentError(String name, int gotten, int needed,
																SourceLocation loc) {
		StringBuilder msg = new StringBuilder(name);
		msg.append(" got ");
		msg.append(gotten);
		if(gotten == 1)
			msg.append(" arg,");
		else
			msg.append(" args,");
		msg.append(" but needs ");
		msg.append(needed);
		return new ErrorWrapper(new HbArgumentError(objSpace,msg),loc);
	}
	
	private HbObject evalNormalMethodCall(HbObject receiver, HbNormalMethod method,
													HbObject[] args,
													SourceLocation origin)
												throws StackOverflow, ErrorWrapper, SyntaxError {
		// add frame
		pushFrame(new MethodFrame(objSpace,getCurrentFrame().getScope(),
									receiver,method.getName(),origin));
		// set variables in scope
		for(int i=0; i < args.length; i++) {
			try {
				getCurrentFrame().getScope().assign(method.getArgName(i),args[i]);
			} catch (HbReadOnlyError e) {
				throw new ErrorWrapper(e,
						method.getArgs().get(i).getVar().getOrigin().getStart());
			}
		}
		getCurrentFrame().getScope().assignForce("self",receiver);
		// run method
		HbObject lastResult = null;
		for(SyntaxNode item: method.getBlock()) {
			try {
				lastResult = run(item);
			} catch(Return r) {
				popFrame();
				return r.getToReturn();
			}
		}
		popFrame();
		return objSpace.nilIfNull(lastResult);
	}

	private HbObject evalNativeMethodCall(HbObject receiver, HbNativeMethod method,
						HbObject[] args, SourceLocation loc) throws ErrorWrapper {
		try {
			return (HbObject)method.getMethod().invoke(receiver,args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			if(e.getCause() instanceof HbError) {
				throw new ErrorWrapper((HbError)e.getCause(),loc);
			} else
				e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	
	private void exec(StatementNode stmt) throws ErrorWrapper, Return, SyntaxError {
		if(stmt instanceof AssignmentNode)
			assign((AssignmentNode)stmt);
		else if(stmt instanceof DeletionNode)
			delete((DeletionNode)stmt);
		else if(stmt instanceof ReturnNode)
			execReturn((ReturnNode)stmt);
		else
			System.out.println("doesn't do that statement yet");
	}

	private void execReturn(ReturnNode stmt) throws ErrorWrapper, Return, SyntaxError {
		throw new Return(stmt.getOrigin(),eval(stmt.getExpr()));
	}

	private void assign(AssignmentNode stmt) throws ErrorWrapper, SyntaxError {
		if(stmt.getVar() instanceof VariableNode) {
			try {
				getCurrentFrame().getScope().assign(stmt.getVar().getName(),
														eval(stmt.getExpr()));
			} catch (HbReadOnlyError e) {
				throw new ErrorWrapper(e,stmt.getEqualsToken().getStart());
			}
		} else if(getCurrentFrame() instanceof MethodFrame) {
			((MethodFrame)getCurrentFrame()).getReceiver().putInstVar(stmt.getVar().getName(),
																eval(stmt.getExpr()));
		} else
			throw new SyntaxError("Unexpected instance variable: not inside a method",
							stmt.getVar().getOrigin().getStart());
	}
	
	private void delete(DeletionNode del) throws ErrorWrapper {
		try {
			getCurrentFrame().getScope().delete(del.getVar().getName());
		} catch(ReadOnlyNameException e) {
			throw new ErrorWrapper(
					new HbReadOnlyError(objSpace,e.getNameInQuestion()),
					del.getOrigin().getStart());
		} catch (UndefinedNameException e) {
			throw new ErrorWrapper(
					new HbUndefinedNameError(objSpace,del.getVar().getName()),
					del.getVar().getOrigin().getStart());
		}
	}
	
	private void defineClass(ClassDefNode def) throws ErrorWrapper {
		// make HbClass instance
		HbClass newClass = new HbClass(objSpace,def.getName());
		// add class to ObjectSpace's classes HashMap
		objSpace.addClass(newClass);
		try {
			getCurrentFrame().getScope().assignGlobal(def.getName(),newClass);
		} catch (HbReadOnlyError e) {
			throw new ErrorWrapper(e,def.getClassNameToken().getStart());
		}
		// define new class
		for(SyntaxNode item: def.getBody()) {
			if(item instanceof MethodDefNode) {
				MethodDefNode methodDef = (MethodDefNode)item;
				newClass.addMethod(methodDef.getName(),
						new HbNormalMethod(def.getName(),methodDef));
			}
		}
	}
	
	private void defineFunction(MethodDefNode def) throws ErrorWrapper {
		try {
			getCurrentFrame().getScope().assignGlobal(def.getName(),
					new HbNormalFunction(objSpace,def.getName(),def.getArgs(),def.getBlock()));
		} catch (HbReadOnlyError e) {
			throw new ErrorWrapper(e,def.getNameToken().getStart());
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
		if(canPop()) {
			ExecutionFrame temp = stack.pop();
			objSpace.garbageCollectCreated();
			return temp;
		} else
			throw new IllegalStateException("Can't pop the top level frame");
	}

	private boolean canPop() {
		return stack.size() > 1;
	}

	private void handleSyntaxError(SyntaxError e) {
		ErrorWrapper error = new ErrorWrapper(
				new HbSyntaxError(objSpace,e.getMessage()),e.getLocation());
		error.addFrame((ModuleFrame)getCurrentFrame());
		error.printStackTrace();
		tokenizer.reset();
		parser.reset();
	}
	
}
