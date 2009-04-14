package hobbes.parser;

import hobbes.parser.syntaxtree.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

public class Parser extends AbstractParser {
	
	public static void main(String[] args) throws MatchError {
		Parser p = new Parser();
		p.parse("24234234");
		System.out.println(p.stack);
		int[] numbers = {6,2,5,8,3,2,4,1};
	}
	
	public int[] test(int... numbers) {
		return numbers;
	}
	
	public void number(String d1, String d2, String d3, String d4, String d5, String d6, String d7, String d8) {
		String number = "";
		//for(String digit: digits)
		//	number += digit;
		stack.push(new GenericNode(number,null,null));
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
