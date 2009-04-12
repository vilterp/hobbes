package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.util.Stack;

public class Parser extends BaseParser {
	
	private Stack<SyntaxNode> stack;
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		p.parse("2");
		System.out.println(p.stack);
	}
	
	public Parser() {
		stack = new Stack<SyntaxNode>();
	}
	
	public void number(String value) {
		stack.push(new NumberNode(value));
	}
	
}
