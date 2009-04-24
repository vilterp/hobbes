package hobbes.ast;

import hobbes.parser.Token;

public class TermNode implements SyntaxNode {
	
	private PowerResultNode left;
	private String operator;
	private TermNode right;
	
	public TermNode(PowerResultNode l, String o, TermNode r) {
		left = l;
		operator = o;
		right = r;
	}
	
	public TermNode(PowerResultNode l) {
		left = l;
		operator = null;
		right = null;
	}
	
	public String toString() {
		if(operator == null && right == null)
			return left.toString();
		else
			return operator + "(" + left + "," + right + ")";
	}
	
}
