package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class Parser extends AbstractParser {
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		p.parse("2+2");
		System.out.println(p.stack);
	}
	
	public void expression() {
		if(stack.size() >= 3) {
			Stack<SyntaxNode> frozenStack = (Stack<SyntaxNode>)stack.clone();
			try {
				TermNode right = (TermNode)stack.pop();
				AddOpNode op = (AddOpNode)stack.pop();
				TermNode left = (TermNode)stack.pop();
				stack.push(new ExpressionNode(op,left,right));
			} catch(ClassCastException e) {
				stack = frozenStack;
			}
		} else if(stack.peek() instanceof TermNode) {
			stack.push(new ExpressionNode((TermNode)stack.pop()));
		}
	}
	
	public void term() {
		if(stack.size() >= 3) {
			Stack<SyntaxNode> frozenStack = (Stack<SyntaxNode>)stack.clone();
			try {
				NumberNode right = (NumberNode)stack.pop();
				MultOpNode op = (MultOpNode)stack.pop();
				NumberNode left = (NumberNode)stack.pop();
				stack.push(new TermNode(op,left,right));
			} catch(ClassCastException e) {
				stack = frozenStack;
			}
		} else if(stack.peek() instanceof NumberNode) {
			stack.push(new TermNode((NumberNode)stack.pop()));
		}
	}
	
	public void multOp(String value) {
		stack.push(new MultOpNode(value));
	}
	
	public void addOp(String value) {
		stack.push(new AddOpNode(value));
	}
	
	public void number(String value) {
		stack.push(new NumberNode(value));
	}
	
}
