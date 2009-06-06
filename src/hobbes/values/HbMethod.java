package hobbes.values;

import java.util.HashMap;

import hobbes.ast.ExpressionNode;

public abstract class HbMethod {
	
	private HashMap<Integer,ExpressionNode> defaults;
	
	public HbMethod() {
		defaults = new HashMap<Integer,ExpressionNode>();
	}
	
	public abstract String getName();
	public abstract String getDeclaringClassName();
	public abstract int getNumArgs();

	public ExpressionNode getDefault(int argInd) {
		return defaults.get(argInd);
	}

	public void setDefault(int argInd, ExpressionNode expr) {
		defaults.put(argInd, expr);
	}
	
}
