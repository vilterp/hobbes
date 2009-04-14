package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class Parser extends AbstractParser {
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		p.parse("-24234234.01");
		System.out.println(p.stack);
	}
	
	public void number(String value) {
		stack.push(new GenericNode(value,null,null));
	}
	
	public void operator(String value) {
		stack.push(new GenericNode(value,null,null));
	}
	
	public void expression() {
		GenericNode right = (GenericNode) stack.pop();
		String operator = ((GenericNode) stack.pop()).getValue();
		GenericNode left = (GenericNode) stack.pop();
		stack.push(new GenericNode(operator,left,right));
	}
	
}
