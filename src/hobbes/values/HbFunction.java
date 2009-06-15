package hobbes.values;

import java.util.HashMap;

import hobbes.ast.ExpressionNode;
import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

public abstract class HbFunction extends HbObject implements HbCallable {
	
	private HashMap<Integer,ExpressionNode> defaults;
	
	public HbFunction(Interpreter o) {
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
	
	public abstract String getRepr() throws ErrorWrapper, HbError, Continue, Break;
	
}
