package hobbes.interpreter;

import java.util.HashMap;
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
	private static final int SURR_LINES = 5;
	
	public Debugger(String fn) {
		super(fn);
		file = new SourceFile(fn);
		in = new Scanner(System.in);
		lastCommand = "s";
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
		if(tree.getLine().getFile().getPath().equals("<eval>"))
			return super.run(tree);
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
			System.out.println(tree.getLine().show());
			// print prompt
			System.out.print(">>> ");
			// get next line
			String command = null;
			try {
				command = in.nextLine();
			} catch(NoSuchElementException e) {
				System.exit(0);
			}
			if(command.equals("")) {
				if(lastCommand != null)
					command = lastCommand;
				else
					continue;
			}
			lastCommand = command;
			if(command.equals("s")) {
				return super.run(tree);
			} if(command.equals("w")) {
				Stack<ExecutionFrame> stack = getStackClone();
				while(!stack.isEmpty())
					System.out.println(stack.pop().show());
				continue;
			} if(command.equals("v")) {
				HashMap<String,HbObject> contents = getCurrentFrame().getScope().getContents();
				if(contents.isEmpty())
					System.out.println("<no local variables>");
				else {
					for(String var: contents.keySet())
						System.out.println(var + " = " + contents.get(var).show());
				}
				continue;
			} if(command.equals("l")) {
				for(SourceLine l: file.getPrecedingLines(tree.getLine().getLineNo(),SURR_LINES))
					System.out.println("  " + l.show());
				System.out.println("- " + tree.getLine().show());
				for(SourceLine l: file.getFollowingLines(tree.getLine().getLineNo(),SURR_LINES))
					System.out.println("  " + l.show());
			} else {
				System.out.println(evalFunc(command,null).show());
			}
		}
	}

}
