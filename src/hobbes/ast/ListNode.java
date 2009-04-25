package hobbes.ast;

import java.util.ArrayList;

public class ListNode implements ObjectNode {
	
	private ArrayList<ExpressionNode> elements;
	
	public ListNode(ArrayList<ExpressionNode> elems) {
		elements = elems;
	}
	
	public ListNode() {
		elements = new ArrayList<ExpressionNode>();
	}
	
	public String toString() {
		return elements.toString();
	}
	
}
