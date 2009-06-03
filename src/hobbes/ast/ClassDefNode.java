package hobbes.ast;

import hobbes.parser.Token;

public class ClassDefNode implements DefNode {
	
	private Token nameToken;
	private ObjectNode superclass;
	private ArgsSpecNode args;
	private BlockNode body;
	
	public ClassDefNode(Token n, ArgsSpecNode a, ObjectNode sc, BlockNode b) {
		nameToken = n;
		superclass = sc;
		args = a;
		body = b;
	}
	
	public String toString() {
		return "class "
				+ nameToken.getValue()
				+ "("
				+ "(" + (args == null ? "" : args) + ")" + ","
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
	
	public ObjectNode getSuperclass() {
		return superclass;
	}
	
	public Token getClassNameToken() {
		return nameToken;
	}
	
}
