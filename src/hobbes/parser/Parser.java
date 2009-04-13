package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.util.Stack;

public class Parser extends BaseParser {
	
	private Stack<SyntaxNode> stack;
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		System.out.println(p);
		p.parse("hello");
		System.out.println(p.stack);
	}
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
	}
	
	public void number(String value) {
		stack.push(new NumberNode(value));
	}
	
	public void helloGoodbye(String value) {
		stack.push(new NumberNode(value));
	}
	
	public void helloGoodbye(String value, String otherValue) {
		stack.push(new NumberNode(value));
		stack.push(new NumberNode(otherValue));
	}
	
}
