package hobbes.ast;

public class SubscriptNode implements ObjectNode {
	
	private ExpressionNode object;
	private ExpressionNode subscript;
	
	public SubscriptNode(ExpressionNode obj, ExpressionNode subscr) {
		object = obj;
		subscript = subscr;
	}
	
	public String toString() {
		return "subscr(" + subscript + "," + object + ")"; 
	}
	
}
