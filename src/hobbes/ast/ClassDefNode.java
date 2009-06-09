package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class ClassDefNode implements DefNode {
	
	private Token nameToken;
	private VariableNode superclass;
	private BlockNode body;
	
	public ClassDefNode(Token n, VariableNode sc, BlockNode b) {
		nameToken = n;
		superclass = sc;
		body = b;
	}
	
	public String toString() {
		return "class "
				+ nameToken.getValue()
				+ "("
				+ (superclass == null ? "" : "[" + superclass + "]") + ","
				+ (body == null ? "" : body)
				+ ")";
	}
	
	public BlockNode getBody() {
		return body;
	}
	
	public String getName() {
		return nameToken.getValue();
	}
	
	public VariableNode getSuperclass() {
		return superclass;
	}
	
	public Token getClassNameToken() {
		return nameToken;
	}
	
	public SourceLine getLine() {
		return nameToken.getLine();
	}
	
}
