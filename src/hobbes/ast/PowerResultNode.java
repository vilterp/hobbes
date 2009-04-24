package hobbes.ast;

public class PowerResultNode implements SyntaxNode {
	
	public NumberNode left;
	public PowerResultNode right;
	
	public PowerResultNode(NumberNode l, PowerResultNode r) {
		left = l;
		right = r;
	}
	
	public PowerResultNode(NumberNode l) {
		left = l;
		right = null;
	}
	
	public String toString() {
		if(right == null)
			return left.toString();
		else
			return "^" + "(" + left + "," + right + ")";
	}
	
}
