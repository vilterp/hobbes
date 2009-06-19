package hobbes.interpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.SyntaxNode;
import hobbes.parser.SourceFile;
import hobbes.parser.SourceLine;
import hobbes.values.HbError;
import hobbes.values.HbObject;

public class Debugger extends Interpreter {
	
	private Scanner in;
	private String lastCommand;
	private SourceFile file;
	private HashSet<Integer> breakPoints;
	private boolean stopping;
	
	private static final int NUM_SURR_LINES = 5;
	private static final String HELP = "commands (parenthesized arguments optional):\n" +
						"h(elp)        this help message\n" +
						"s             step into\n" +
						"o             step over\n" +
						"n             run until next breakpoint\n" +
						"v             show variables in current scope\n" +
						"l (<lineNo>)  list source\n" +
						"w             show current call stack\n" +
						"b <lineNo>    toggle breakpoint at lineNo\n" +
						"If the command doesn't match any of these,\n" +
						"it will be evaluated as code.";
	
	public Debugger(String fn, boolean vgc) {
		super(fn,vgc,true);
		file = new SourceFile(fn);
		in = new Scanner(System.in);
		lastCommand = "s";
		breakPoints = new HashSet<Integer>();
		stopping = true;
	}
	
	public void addLine(String code) {
		file.addLine(code);
	}
	
	public void go() {
		for(SourceLine l: file) {
			add(l.getCode());
			if(!needsMore()) {
				String result = getResult();
				if(result != null)
					System.out.println(result);
			}	
		}
	}
	
	public HbObject run(SyntaxNode tree) throws ErrorWrapper, HbError, Continue, Break, Return {
		if(!stopping) {
			if(breakPoints.contains(tree.getLine().getLineNo()))
				stopping = true;
			else
				return super.run(tree);
		}
		while(true) {
			System.out.print("======= ");
			// print trace
			Iterator<ExecutionFrame> it = getStackClone().iterator();
			while(it.hasNext()) {
				ExecutionFrame frame = it.next();
				if(frame instanceof FileFrame)
					System.out.print(((FileFrame)frame).getName());
				else if(frame instanceof NormalFunctionFrame)
					System.out.print(((FunctionFrame)frame).getName());
				else if(frame instanceof MethodFrame) {
					System.out.print(((MethodFrame)frame).getName());
				}
				if(it.hasNext())
					System.out.print(" > ");
			}
			System.out.println();
			// print current line
			showLine(tree.getLine());
			// print prompt
			System.out.print(">>> ");
			// get next line
			String command = null;
			try {
				command = in.nextLine().trim();
			} catch(NoSuchElementException e) {
				System.exit(0);
			}
			// save last command
			if(command.equals("")) {
				if(lastCommand != null)
					command = lastCommand;
				else
					continue;
			}
			lastCommand = command;
			// run command
			if(command.equals("h") || command.equals("help")) {
				System.out.println(HELP);
			} else if(command.equals("s")) {
				return super.run(tree);
			} else if(command.equals("n")) {
				stopping = false;
				return super.run(tree);
			} else if(command.equals("o")) {
				stopping = false;
				HbObject result = super.run(tree);
				stopping = true;
				return result;
			} else if(command.equals("w")) {
				showStack();
			} else if(command.equals("v")) {
				showScope();
			} else if(command.startsWith("b")) {
				if(command.length() >= 3) {
					try {
						setBreakpoint(Integer.parseInt(command.substring(2)));
					} catch(NumberFormatException e) {
						System.out.println("not a number");
					}
				} else
					System.out.println("Supply a line number, eg \"b 42\"");
			} else if(command.startsWith("l")) {
				if(command.length() >= 3) {
					try {
						showSurroundingLines(file.getLine(Integer.parseInt(command.substring(2))));
					} catch(NumberFormatException e) {
						System.out.println("\"" + command.substring(2) + "\" is not a valid line number");
					}
				} else
					showSurroundingLines(tree.getLine());
			} else {
				evalCode(command);
			}
		}
	}
	
	private void showSurroundingLines(SourceLine focus) {
		for(SourceLine l: file.getPrecedingLines(focus.getLineNo(),NUM_SURR_LINES))
			showLine(l);
		showLine(focus,true);
		for(SourceLine l: file.getFollowingLines(focus.getLineNo(),NUM_SURR_LINES))
			showLine(l);
	}

	private void evalCode(String command) throws ErrorWrapper, HbError, Continue, Break {
		// FIXME: error handling? don't want it to bubble to top
		stopping = false;
		System.out.println("==> " + evalFunc(command,null).realShow());
		stopping = true;
	}

	private void showStack() {
		Stack<ExecutionFrame> stack = getStackClone();
		while(!stack.isEmpty())
			System.out.println(stack.pop().show());
	}

	private void setBreakpoint(int lineNo) {
		if(checkLineNo(lineNo)) {
			if(breakPoints.contains(lineNo))
				breakPoints.remove(lineNo);
			else
				breakPoints.add(lineNo);
			showLine(file.getLine(lineNo));
		} 
	}
	
	private boolean checkLineNo(int lineNo) {
		if(lineNo <= file.getNumLines() && lineNo > 0)
			return true;
		else {
			System.out.println("Breakpoint not within file (" + file.getNumLines() + " lines)");
			return false;
		}
	}

	private void showScope() throws ErrorWrapper, HbError, Continue, Break {
		stopping = false;
		HashMap<String,HbObject> contents = getCurrentFrame().getScope().getContents();
		HashMap<String,HbObject> instVars = null;
		if(getCurrentFrame() instanceof NormalMethodFrame)
			instVars = ((NormalMethodFrame)getCurrentFrame()).getReceiver().getInstVars();
		if(contents.isEmpty() && (instVars == null || instVars.isEmpty()))
			System.out.println("<no local variables>");
		else {
			for(String var: contents.keySet())
				if(!var.equals("self"))
					System.out.println(var + " = " + contents.get(var).realShow());
			if(instVars != null) {
				for(String var: instVars.keySet())
					if(!var.equals("self"))
						System.out.println("@" + var + " = " + instVars.get(var).realShow());
			}
		}
		stopping = true;
	}

	private void showLine(SourceLine l) {
		showLine(l,false);
	}
	
	private void showLine(SourceLine l, boolean focus) {
		if(breakPoints.contains(l.getLineNo()))
			System.out.print('B');
		else
			System.out.print(' ');
		if(focus)
			System.out.print('>');
		else
			System.out.print(' ');
		System.out.print(' ');
		System.out.println(l.show());
	}

}
