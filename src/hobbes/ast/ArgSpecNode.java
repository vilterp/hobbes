package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class ArgSpecNode implements SyntaxNode {
	
	private VariableNode var;
	private Token equalsToken;
	private ExpressionNode def;
	
	public ArgSpecNode(VariableNode var) {
		this(var,null,null);
	}
	
	public ArgSpecNode(VariableNode v, Token eq, ExpressionNode d) {
		var = v;
		equalsToken = eq;
		def = d;
	}
	
	public String toString() {
		return getVar().getName()
		+ (equalsToken == null ? "" : equalsToken.getValue() + def);
	}
	
	public VariableNode getVar() {
		return var;
	}
	
	public Token getEqualsToken() {
		return equalsToken;
	}
	
	public ExpressionNode getDefault() {
		return def;
	}
	
	public SourceLine getLine() {
		return equalsToken.getLine();
	}
	
}
