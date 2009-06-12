package hobbes.interpreter;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
					System.out.print(" " + i.getLastOpener() + " ");
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
	private SourceFile file;
	private FileFrame topLevelFrame;
	private ObjectSpace objSpace;
	private String fileName;
	private Parser parser;
	private Tokenizer tokenizer;
	private boolean verboseGC;
	
	private static final int MAX_STACK_SIZE = 500;
	
	public Interpreter(String fn, boolean vgc) {
		verboseGC = vgc;
		file = new SourceFile(fn);
		objSpace = new ObjectSpace(this,vgc);
		objSpace.addBuiltins();
		objSpace.resetAlive();
		if(verboseGC)
			System.out.println("initial object space size: " + objSpace.size());
		stack = new Stack<ExecutionFrame>();
		fileName = fn;
		topLevelFrame = new FileFrame(this,fileName);
		stack.push(topLevelFrame);
		parser = new Parser();
		tokenizer = new Tokenizer();
	}
	
	public Interpreter(String fn) {
		this(fn,false);
	}
	
	public void add(String line) {
		try {
			tokenizer.addLine(file.addLine(line));
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
	
	public ObjectSpace getObjSpace() {
		return objSpace;
	}
	
	private void handleError(ErrorWrapper e) {
		while(canPop())
			e.addFrame(popFrame());
		e.addFrame(getCurrentFrame());
		e.printStackTrace();
	}
	
	private String interpret(SyntaxNode tree) {
		if(tree != null) {
			topLevelFrame.setCurrentLine(tree.getLine());
			try {
				HbObject result = run(tree);
				String toReturn = null;
				if(result != null)
					toReturn = result.show();
				return toReturn;
			} catch(ErrorWrapper e) {
				handleError(e);
				return null;
			} catch (HbError e) {
				handleError(new ErrorWrapper(e,null));
				return null;
			} catch(Return r) {
				handleUnexpectedReturn(r);
				return null;
			} catch (Continue e) {
				handleUnexpectedBreakOrContinue(e);
				return null;
			} catch (Break e) {
				handleUnexpectedBreakOrContinue(e);
				return null;
			} finally {
				objSpace.garbageCollect();
				if(verboseGC)
					System.out.println("object space size: " + objSpace.size());
			}
		} else
			return null;
	}

	private HbObject run(SyntaxNode tree) throws ErrorWrapper, Return, HbError, Continue, Break {
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
	
	private HbObject eval(ExpressionNode expr) throws ErrorWrapper, HbError, Continue, Break {
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
			return new HbString(this,((StringNode)expr).getValue());
		else if(expr instanceof NegativeNode)
			return evalNegative((NegativeNode)expr);
		else if(expr instanceof NotNode)
			return evalNot((NotNode)expr);
		else if(expr instanceof SetNode)
			return evalSet((SetNode)expr);
		else if(expr instanceof DictNode)
			return evalDict((DictNode)expr);
		else if(expr instanceof InlineIfStatementNode)
			return evalInlineIf((InlineIfStatementNode)expr);
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
	
	private HbObject evalInlineIf(InlineIfStatementNode expr)
									throws ErrorWrapper, HbError, Continue, Break {
		if(evalCond(expr.getCond()))
			return eval(expr.getIf());
		else if(expr.getElse() != null)
			return eval(expr.getElse());
		else
			return objSpace.getNil();
	}

	private boolean evalCond(ExpressionNode cond) throws ErrorWrapper, HbError, Continue, Break {
		return eval(cond) == objSpace.getTrue();
	}

	private HbObject evalNot(NotNode expr) throws ErrorWrapper, HbError, Continue, Break {
		HbObject result = eval(expr.getExpr());
		if(result.call("toBool") == objSpace.getTrue())
			return objSpace.getFalse();
		else
			return objSpace.getTrue();
	}

	private HbObject evalNegative(NegativeNode neg) throws ErrorWrapper {
		if(neg.getExpr() instanceof NumberNode)
			return evalNumber((NumberNode)neg.getExpr(),true);
		else
			throw new ErrorWrapper(new HbSyntaxError(this,
					"Only numbers can be negative."),
					neg.getOrigin().getStart());
	}

	private HbObject evalDict(DictNode expr) throws ErrorWrapper, HbError, Continue, Break {
		HbDict newDict = new HbDict(this);
		for(ExpressionNode key: expr.getElements().keySet())
			newDict.put(eval(key),eval(expr.getElements().get(key)));
		return newDict;
	}

	private HbObject evalSet(SetNode expr) throws ErrorWrapper, HbError, Continue, Break {
		HbSet newSet = new HbSet(this);
		for(ExpressionNode elem: expr.getElements())
			newSet.add(eval(elem));
		return newSet;
	}

	private HbObject evalFunctionCall(FunctionCallNode call)
										throws ErrorWrapper, HbError, Continue, Break {
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
			HbObject[] args, SourceLocation parenLoc)
											throws ErrorWrapper, HbError, Continue, Break {
		// push frame
		try {
			pushFrame(new FunctionFrame(new Scope(this,getCurrentFrame().getScope()),
								func.realToString(),parenLoc));
		} catch (HbStackOverflow e) {
			throw new ErrorWrapper(e,parenLoc);
		}
		// set args in scope
		for(int i=0; i < args.length; i++) {
			try {
				getCurrentFrame().getScope().assign(func.getArgs().get(i).getVar().getName(),
																					args[i]);
			} catch (HbReadOnlyError e) {
				throw new ErrorWrapper(e,parenLoc);
			}
		}
		// run block
		HbObject lastResult = null;
		for(SyntaxNode item: func.getBlock()) {
			try {
				lastResult = run(item);
			} catch(Return r) {
				popFrame();
				return r.getValue();
			}
		}
		popFrame();
		return lastResult;
	}
	
	public HbObject callAnonFunc(HbAnonymousFunction func,
								HbObject[] args, SourceLocation parenLoc)
									throws ErrorWrapper, HbError, Continue, Break {
		if(func.getNumArgs() != args.length)
			throw getArgumentError(func.realToString(),args.length,func.getNumArgs(),parenLoc);
		else
			return evalAnonFuncCall(func,args,parenLoc);
	}

	private HbObject evalNormalFuncCall(HbNormalFunction func, HbObject[] args,
												SourceLocation parenLoc)
												throws ErrorWrapper, HbError, Continue, Break {
		// push frame
		try {
			pushFrame(new FunctionFrame(new Scope(this,getCurrentFrame().getScope()),
								func.getName(),parenLoc));
		} catch (HbStackOverflow e) {
			throw new ErrorWrapper(e,parenLoc);
		}
		// set args in scope
		for(int i=0; i < args.length; i++) {
			try {
				getCurrentFrame().getScope().assign(func.getArgs().get(i).getVar().getName(),
																					args[i]);
			} catch (HbReadOnlyError e) {
				throw new ErrorWrapper(e,parenLoc);
			}
		}
		// run block
		HbObject lastResult = null;
		for(SyntaxNode item: func.getBlock()) {
			try {
				lastResult = run(item);
			} catch(Return r) {
				popFrame();
				return r.getValue();
			}
		}
		popFrame();
		return lastResult;
	}

	private HbObject evalNativeFuncCall(HbNativeFunction func, HbObject[] args,
			SourceLocation parenLoc) throws ErrorWrapper, HbError, Continue, Break {
		try {
			pushFrame(new NativeFunctionFrame(getCurrentFrame().getScope(),
													func.getName(),parenLoc));
		} catch (HbStackOverflow e) {
			throw new ErrorWrapper(e,parenLoc);
		}
		if(func.getName().equals("print")) {
			System.out.println(args[0].realToString());
			popFrame();
			return objSpace.getNil();
		} else if(func.getName().equals("get_input")) {
			System.out.print(args[0].realToString());
			Scanner in = new Scanner(System.in);
			popFrame();
			return new HbString(this,in.nextLine());
		} else if(func.getName().equals("eval")) {
			try {
				pushFrame(new FileFrame(this,"<eval>"));
			} catch (HbStackOverflow e1) {
				throw new ErrorWrapper(e1,parenLoc);
			}
			SourceFile f = new SourceFile("<eval>");
			Scanner s = new Scanner(((HbString)args[0]).getValue());
			HbObject lastResult = null;
			while(s.hasNext()) {
				try {
					tokenizer.addLine(f.addLine(s.next()));
					if(tokenizer.isReady()) {
						SyntaxNode tree = parser.parse(tokenizer.getTokens());
						lastResult = run(tree);
					}
				} catch (SyntaxError e) {
					throw new ErrorWrapper(new HbSyntaxError(this,e.getMessage()),
											e.getLocation());
				} catch (Return r) {
					handleUnexpectedReturn(r);
				}
			}
			popFrame(); // FileFrame
			popFrame(); // NativeFunctionFrame
			return lastResult;
		} else
			throw new IllegalArgumentException("Nonexistent native function");
	}
	
	private void handleUnexpectedReturn(Return r) {
		SyntaxError e = new SyntaxError("Unexpected return statment - " +
					"not inside a function or method",
					r.getOrigin().getStart());
		handleSyntaxError(e);
	}
	
	private void handleUnexpectedBreakOrContinue(LoopControlException lc) {
		SyntaxError e = new SyntaxError("Unexpected "
				+ (lc instanceof Continue ? "continue" : "break")
				+ " statment - not inside a loop",
				lc.getOrigin().getStart());
		handleSyntaxError(e);
	}
	
	private HbObject evalAnonFunc(AnonymousFunctionNode func) {
		return new HbAnonymousFunction(this,func.getArgs(),func.getBlock());
	}

	private HbObject evalInstanceVar(InstanceVarNode expr) throws ErrorWrapper {
		if(getCurrentFrame() instanceof NormalMethodFrame) {
			return ((NormalMethodFrame)getCurrentFrame())
											.getReceiver().getInstVar(expr.getName());
		} else
			throw new ErrorWrapper(new HbSyntaxError(this,
					"Unexpected instance variable: not inside a method"),
									expr.getOrigin().getStart());
	}

	private HbObject evalNumber(NumberNode num, boolean negative) {
		try {
			int value = Integer.parseInt(num.getValue());
			if(negative)
				return objSpace.getInt(-value);
			else
				return objSpace.getInt(value);
		} catch(NumberFormatException e) {
			float value = Float.parseFloat(num.getValue());
			if(negative)
				return objSpace.getFloat(-value);
			else
				return objSpace.getFloat(value);
		}
	}
	
	private HbObject evalNumber(NumberNode num) {
		return evalNumber(num,false);
	}

	private HbObject evalList(ListNode expr) throws ErrorWrapper, HbError, Continue, Break {
		HbList newList = new HbList(this);
		for(ExpressionNode elem: expr.getElements())
			newList.add(eval(elem));
		return newList;
	}

	private HbObject evalNewInstance(NewInstanceNode expr)
												throws ErrorWrapper, HbError, Continue, Break {
		String className = expr.getClassVar().getName();
		// get HbClass instance
		HbClass hobbesClass = null;
		try {
			hobbesClass = (HbClass)eval(expr.getClassVar());
		} catch(ClassCastException e) {
			throw new ErrorWrapper(new HbNotAClassError(this,className),
									expr.getClassVar().getOrigin().getStart());
		}
		// get Java class
		Class<?extends HbObject> javaClass =
								objSpace.getClass(className).getJavaClass();
		Constructor constructor = null;
		HbObject newObj = null;
		try {
			// get one-arg (ObjectSpace) constructor
			constructor = javaClass.getConstructor(Interpreter.class);
			// make new instance
			newObj = (HbObject)constructor.newInstance(this);
		} catch (SecurityException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (NoSuchMethodException e) {
			System.err.println("Class " + javaClass.getName()
					+ " has no constructor " +
					"that takes an Interpreter as the sole parameter");
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
	
	private HbObject evalMethodCall(MethodCallNode call)
											throws ErrorWrapper, HbError, Continue, Break {
		HbObject receiver = eval(call.getReceiver());
		return evalMethodCall(receiver,call.getMethodName(),call.getArgs(),
									call.getOrigin().getStart());
	}

	private HbObject evalMethodCall(HbObject receiver, String methodName,
				ArrayList<ExpressionNode> args, SourceLocation origin)
											throws ErrorWrapper, HbError, Continue, Break {
		HbCallable method = null;
		if(receiver.getHbClass().hasMethod(methodName))
			method = receiver.getHbClass().getMethod(methodName);
		else
			throw new ErrorWrapper(new HbMissingMethodError(this,methodName,
								receiver.getHbClass().getName()),origin);
		HbObject[] argValues = evalArgs(args,method,methodName,origin);
		return callMethod(receiver,methodName,argValues,origin);
	}
	
	public HbObject callMethod(HbObject receiver, String methodName,
										HbObject[] args, SourceLocation loc)
												throws ErrorWrapper, HbError, Continue, Break {
		HbMethod method = receiver.getHbClass().getMethod(methodName);
		if(method instanceof HbNormalMethod)
			return evalNormalMethodCall(receiver,(HbNormalMethod)method,args,loc);
		else
			return evalNativeMethodCall(receiver,(HbNativeMethod)method,args,loc);
	}
	
	private HbObject[] evalArgs(ArrayList<ExpressionNode> args, HbCallable func,
												String name, SourceLocation loc)
												throws ErrorWrapper, HbError, Continue, Break {
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
				throw getArgumentError(name,i,func.getNumArgs(),loc);
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
		msg.append(" but takes ");
		msg.append(needed);
		return new ErrorWrapper(new HbArgumentError(this,msg),loc);
	}
	
	private HbObject evalNormalMethodCall(HbObject receiver, HbNormalMethod method,
													HbObject[] args,
													SourceLocation origin)
												throws ErrorWrapper, HbError, Continue, Break {
		// add frame
		try {
			pushFrame(new NormalMethodFrame(this,getCurrentFrame().getScope(),
										receiver,method.getName(),origin));
		} catch (HbStackOverflow e) {
			throw new ErrorWrapper(e,origin);
		}
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
				return r.getValue();
			}
		}
		popFrame();
		return objSpace.nilIfNull(lastResult);
	}

	private HbObject evalNativeMethodCall(HbObject receiver, HbNativeMethod method,
						HbObject[] args, SourceLocation loc) throws ErrorWrapper {
		try {
			pushFrame(new NativeMethodFrame(getCurrentFrame().getScope(),
					receiver.getHbClass().getName(),method.getName(),loc));
		} catch (HbStackOverflow e) {
			throw new ErrorWrapper(e,loc);
		}
		try {
			HbObject temp = (HbObject)method.getMethod().invoke(receiver,args);
			popFrame();
			return temp;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if(e.getCause() instanceof HbError)
				throw new ErrorWrapper((HbError)e.getCause(),loc);
			else if(e.getCause() instanceof ErrorWrapper)
				throw (ErrorWrapper)e.getCause();
			else
				e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	
	private void exec(StatementNode stmt) throws ErrorWrapper, Return, HbError, Continue, Break {
		if(stmt instanceof AssignmentNode)
			assign((AssignmentNode)stmt);
		else if(stmt instanceof DeletionNode)
			delete((DeletionNode)stmt);
		else if(stmt instanceof ReturnNode)
			execReturn((ReturnNode)stmt);
		else if(stmt instanceof IfStatementNode)
			execIf((IfStatementNode)stmt);
		else if(stmt instanceof WhileLoopNode)
			execWhile((WhileLoopNode)stmt);
		else if(stmt instanceof ContinueNode)
			throw new Continue(((ContinueNode)stmt).getOrigin());
		else if(stmt instanceof BreakNode)
			throw new Break(((BreakNode)stmt).getOrigin());
		else
			System.out.println("doesn't do that statement yet");
	}

	private void execWhile(WhileLoopNode stmt)
								throws ErrorWrapper, HbError, Return, Continue, Break {
		while(evalCond(stmt.getCondition())) {
			try {
				runBlock(stmt.getBlock());
			} catch(Continue c) {
				continue;
			} catch(Break b) {
				return;
			}
		}
	}

	private void execIf(IfStatementNode stmt)
									throws ErrorWrapper, HbError, Return, Continue, Break {
		if(evalCond(stmt.getCondition()))
			runBlock(stmt.getIfBlock());
		else if(stmt.getElseBlock() != null)
			runBlock(stmt.getElseBlock());
	}

	private void runBlock(BlockNode block)
									throws ErrorWrapper, HbError, Return, Continue, Break {
		for(SyntaxNode item: block)
			run(item);
	}

	private void execReturn(ReturnNode stmt)
										throws ErrorWrapper, Return, HbError, Continue, Break {
		throw new Return(stmt.getOrigin(),eval(stmt.getExpr()));
	}

	private void assign(AssignmentNode stmt) throws ErrorWrapper, HbError, Continue, Break {
		if(stmt.getVar() instanceof VariableNode) {
			try {
				getCurrentFrame().getScope().assign(stmt.getVar().getName(),
														eval(stmt.getExpr()));
			} catch (HbReadOnlyError e) {
				throw new ErrorWrapper(e,stmt.getEqualsToken().getStart());
			}
		} else if(getCurrentFrame() instanceof NormalMethodFrame) {
			((NormalMethodFrame)getCurrentFrame()).getReceiver()
									.putInstVar(stmt.getVar().getName(),eval(stmt.getExpr()));
		} else
			throw new ErrorWrapper(new HbSyntaxError(this,
					"Unexpected instance variable: not inside a method"),
							stmt.getVar().getOrigin().getStart());
	}
	
	private void delete(DeletionNode del) throws ErrorWrapper {
		try {
			getCurrentFrame().getScope().delete(del.getVar().getName());
		} catch(ReadOnlyNameException e) {
			throw new ErrorWrapper(
					new HbReadOnlyError(this,e.getNameInQuestion()),
					del.getOrigin().getStart());
		} catch (UndefinedNameException e) {
			throw new ErrorWrapper(
					new HbUndefinedNameError(this,del.getVar().getName()),
					del.getVar().getOrigin().getStart());
		}
	}
	
	private void defineClass(ClassDefNode def) throws ErrorWrapper {
		// get superclass
		String superclass = null;
		if(def.getSuperclass() == null)
			superclass = "Object";
		else {
			HbObject sc = evalVariable(def.getSuperclass());
			if(sc instanceof HbClass)
				superclass = ((HbClass)sc).getName();
			else
				throw new ErrorWrapper(new HbNotAClassError(this,def.getSuperclass().getName()),
									def.getSuperclass().getOrigin().getStart());
		}
		if(!superclass.equals("Object") && objSpace.getBuiltinClasses().contains(superclass))
			throw new ErrorWrapper(new HbArgumentError(this,
							"Can't currently extend the builtin classes."),
							def.getSuperclass().getOrigin().getStart());
		// make HbClass instance
		HbClass newClass = new HbClass(this,def.getName(),superclass);
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
					new HbNormalFunction(this,def.getName(),def.getArgs(),def.getBlock()));
		} catch (HbReadOnlyError e) {
			throw new ErrorWrapper(e,def.getNameToken().getStart());
		}
	}

	private ExecutionFrame getCurrentFrame() {
		return stack.peek();
	}

	private void pushFrame(ExecutionFrame f) throws HbStackOverflow {
		if(stack.size() < MAX_STACK_SIZE)
			stack.push(f);
		else
			throw new HbStackOverflow(this);
	}

	private ExecutionFrame popFrame() {
		if(canPop()) {
			ExecutionFrame temp = stack.pop();
			return temp;
		} else
			throw new IllegalStateException("Can't pop the top level frame");
	}

	private boolean canPop() {
		return stack.size() > 1;
	}

	private void handleSyntaxError(SyntaxError e) {
		ErrorWrapper error = new ErrorWrapper(
				new HbSyntaxError(this,e.getMessage()),e.getLocation());
		handleError(error);
		tokenizer.reset();
		parser.reset();
	}
	
}
