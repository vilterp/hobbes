package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.util.Stack;

public class Parser extends BaseParser {
	
	private Stack<GenericNode> stack;
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		System.out.println(p);
		p.parse("2+2");
		System.out.println(p.stack);
	}
	
	public Parser() {
		stack = new Stack<GenericNode>();
	}
	
	public void number(String value) {
		stack.push(new GenericNode(value,null,null));
	}
	
	public void operator(String value) {
		stack.push(new GenericNode(value,null,null));
	}
	
	public void expression() {
		GenericNode right = stack.pop();
		String operator = stack.pop().getValue();
		GenericNode left = stack.pop();
		stack.push(new GenericNode(operator,left,right));
	}
	
}
