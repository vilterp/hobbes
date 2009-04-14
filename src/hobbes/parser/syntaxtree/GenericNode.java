package hobbes.parser.syntaxtree;

import hobbes.lang.HbObject;

public class GenericNode implements SyntaxNode {

	private String value;
	private GenericNode left;
	private GenericNode right;
	
	public GenericNode(String v, GenericNode l, GenericNode r) {
		value = v;
		left = l;
		right = r;
	}
	
	public GenericNode(String v) {
		value = v;
		left = null;
		right = null;
	}
	
	public String toString() {
		String ans = value + "(";
		if(left != null && right != null)
			ans += left + ", " + right;
		else if(left != null)
			ans += left;
		else if(right != null)
			ans += right;
		return ans + ")";
	}
	
	public GenericNode getLeft() {
		return left;
	}

	public void setLeft(GenericNode left) {
		this.left = left;
	}

	public GenericNode getRight() {
		return right;
	}

	public void setRight(GenericNode right) {
		this.right = right;
	}

	public String getValue() {
		return value;
	}

	public HbObject evaluate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
