package hobbes.ast;

import hobbes.parser.Token;

public class ClassNode implements SyntaxNode {
	
	private Token name;
	private ObjectNode superclass;
	private ArgsSpecNode args;
	private BlockNode body;
	
	public ClassNode(Token n, ArgsSpecNode a, ObjectNode sc, BlockNode b) {
		name = n;
		superclass = sc;
		args = a;
		body = b;
	}
	
	public String toString() {
		return "class "
				+ name.getValue()
				+ "("
				+ "(" + (args == null ? "" : args) + ")" + ","
				+ (superclass == null ? "" : "[" + superclass + "]") + ","
				+ (body == null ? "" : body)
				+ ")";
	}
	
}
