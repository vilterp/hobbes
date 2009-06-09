package hobbes.ast;

import hobbes.parser.SourceLine;

public class GlobalNode implements StatementNode {
	
	private VariableNode var;
	
	public GlobalNode(VariableNode v) {
		var = v;
	}
	
	public String toString() {
		return "global(" + var + ")";
	}
	
	public SourceLine getLine() {
		return var.getLine();
	}
	
}
