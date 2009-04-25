package hobbes.ast;

import java.util.ArrayList;

public class ArrayNode implements ObjectNode {
	
	private ArrayList<ExpressionNode> elements;
	
	public ArrayNode(ArrayList<ExpressionNode> elems) {
		elements = elems;
	}
	
	public ArrayNode() {
		elements = new ArrayList<ExpressionNode>();
	}
	
	public String toString() {
		return elements.toString();
	}
	
}
