package hobbes.ast;

import java.util.LinkedList;

public class ArrayNode implements ObjectNode {
	
	private LinkedList<ExpressionNode> elements;
	// linked list cuz the parser has to add them in reverse order
	
	public ArrayNode(LinkedList<ExpressionNode> elems) {
		elements = elems;
	}
	
	public ArrayNode() {
		elements = new LinkedList<ExpressionNode>();
	}
	
	public String toString() {
		return elements.toString();
	}
	
}
