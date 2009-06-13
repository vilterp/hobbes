package hobbes.interpreter;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;

import hobbes.ast.SyntaxNode;
import hobbes.parser.Parser;
import hobbes.parser.Tokenizer;
import hobbes.values.HbError;
import hobbes.values.HbObject;

public class Debugger extends Interpreter {
	
	public static void main(String[] args) {
		Debugger d = new Debugger("<somefile>");
		String code = "def add(a,b){a+b}\n" +
				"def add3(a,b,c){add(add(a,b),c)}\n" +
				"add3(1,2,3)\n" +
				"";
		Scanner s = new Scanner(code);
		while(s.hasNext()) {
			d.add(s.nextLine());
			if(!d.needsMore()) {
				String res = d.getResult();
				if(res != null)
					System.out.println(d.getResult());
			}
		}
	}
	
	private Scanner in;
	private String lastCommand;
	
	public Debugger(String fn) {
		super(fn);
		in = new Scanner(System.in);
		lastCommand = null;
	}
	
	public HbObject run(SyntaxNode tree) throws ErrorWrapper, HbError, Continue, Break, Return {
		while(true) {
			System.out.println(tree.getLine().show());
			System.out.print("> ");
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
			} else {
				continue;
			}
		}
	}

}
