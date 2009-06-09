package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class DeletionNode implements StatementNode {
	
	private Token origin;
	private VarNode var;
	
	public DeletionNode(Token o, VarNode v) {
		origin = o;
		var = v;
	}
	
	public String toString() {
		return "del(" + var + ")";
	}
	
	public VarNode getVar() {
		return var;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
