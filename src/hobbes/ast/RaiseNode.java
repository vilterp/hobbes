package hobbes.ast;

public class RaiseNode implements StatementNode {
	
	private StringNode errorName;
	private ExpressionNode errorDesc;
	
	public RaiseNode(StringNode n, ExpressionNode d) {
		errorName = n;
		errorDesc = d;
	}
	
	public RaiseNode(StringNode n) {
		errorName = n;
		errorDesc = null;
	}
	
	public String toString() {
		return "raise(" + errorName
				+ (errorDesc == null ? "" : "," + errorDesc)
				+ ")";
	}
	
}
