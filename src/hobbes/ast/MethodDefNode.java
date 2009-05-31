package hobbes.ast;

import hobbes.parser.Token;

public class MethodDefNode implements SyntaxNode {
	
	private Token name;
	private ArgsSpecNode args;
	private BlockNode block;
	
	public MethodDefNode(Token n, ArgsSpecNode a, BlockNode b) {
		name = n;
		args = a;
		block = b;
	}
	
	public String toString() {
		return "def "
				+ name.getValue()
				+ "(" + "(" + args + ")" + ","
				+ block + ")";
	}
	
}
