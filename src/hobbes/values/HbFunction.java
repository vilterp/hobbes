package hobbes.values;

import java.util.HashMap;

import hobbes.ast.ExpressionNode;
import hobbes.interpreter.ObjectSpace;

public abstract class HbFunction extends HbObject implements HbCallable {
	
	private HashMap<Integer,ExpressionNode> defaults;
	
	public HbFunction(ObjectSpace o) {
		super(o);
		defaults = new HashMap<Integer,ExpressionNode>();
	}
	
	public abstract int getNumArgs();

	public ExpressionNode getDefault(int argInd) {
		return defaults.get(argInd);
	}

	public void setDefault(int argInd, ExpressionNode expr) {
		defaults.put(argInd, expr);
	}
	
}
