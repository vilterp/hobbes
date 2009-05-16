package hobbes.ast;

public class ReturnNode implements StatementNode {
	
	private ExpressionNode expr;
	
	public ReturnNode(ExpressionNode e) {
		expr = e;
	}
	
	public String toString() {
		return "return(" + expr + ")";
	}
	
}
