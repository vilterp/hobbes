package hobbes.ast;

import hobbes.parser.Token;

public class NumberNode implements SyntaxNode {
	
	public Token origin;
	
	public NumberNode(Token token) {
		origin = token;
	}
	
	public String toString() {
		return origin.getValue();
	}
	
}
