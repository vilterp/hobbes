package hobbes.ast;

import hobbes.parser.SourceLine;

import java.util.ArrayList;

public class ListNode implements AtomNode {
	
	private SourceLine line;
	private ArrayList<ExpressionNode> elements;
	
	public ListNode(SourceLine l, ArrayList<ExpressionNode> elems) {
		line = l;
		elements = elems;
	}
	
	public ListNode() {
		elements = new ArrayList<ExpressionNode>();
	}
	
	public String toString() {
		return elements.toString();
	}
	
	public ArrayList<ExpressionNode> getElements() {
		return elements;
	}
	
	public SourceLine getLine() {
		return line;
	}
	
}
