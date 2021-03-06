package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class TempNode implements SyntaxNode {
	
	public Token token;
	
	public TempNode(Token t) {
		token = t;
	}
	
	public Token getToken() {
		return token;
	}
	
	public String toString() {
		return token.getValue();
	}
	
	public SourceLine getLine() {
		return token.getLine();
	}
	
}
