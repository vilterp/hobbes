package hobbes.ast;

import hobbes.parser.Token;

public class AttributeNode implements ObjectNode {
	
	private ExpressionNode object;
	private Token attribute;
	
	public AttributeNode(ExpressionNode obj, Token attr) {
		object = obj;
		attribute = attr;
	}
	
	public String toString() {
		return "attr(" + attribute.getValue() + "," + object + ")";
	}
	
}
