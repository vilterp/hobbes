package hobbes.parser.syntaxtree;

import hobbes.lang.HbObject;

public class NumberNode implements SyntaxNode {
	
	private String value;
	
	public NumberNode(String val) {
		value = val;
	}
	
	public String toString() {
		return value;
	}
	
	public HbObject evaluate() {
		// TODO Auto-generated method stub
		return null;
	}

}
