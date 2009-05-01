package hobbes.ast;

public class AttributeNode implements ObjectNode {
	
	private ExpressionNode object;
	private String attribute;
	
	public AttributeNode(ExpressionNode obj, String attr) {
		object = obj;
		attribute = attr;
	}
	
	public String toString() {
		return "attr(" + attribute + "," + object + ")";
	}
	
}
