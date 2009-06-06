package hobbes.values;

import hobbes.ast.ExpressionNode;

public interface HbCallable {
	
	int getNumArgs();
	void setDefault(int argInd, ExpressionNode val);
	ExpressionNode getDefault(int argInd);
	
}
