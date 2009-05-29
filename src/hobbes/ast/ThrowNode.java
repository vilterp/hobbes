package hobbes.ast;

public class ThrowNode implements StatementNode {
	
	private StringNode errorName;
	private ExpressionNode errorDesc;
	
	public ThrowNode(StringNode n, ExpressionNode d) {
		errorName = n;
		errorDesc = d;
	}
	
	public ThrowNode(StringNode n) {
		errorName = n;
		errorDesc = null;
	}
	
	public String toString() {
		return "raise(" + errorName
				+ (errorDesc == null ? "" : "," + errorDesc)
				+ ")";
	}
	
}
