package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class ContinueNode implements StatementNode {
	
	Token origin;
	
	public ContinueNode(Token o) {
		origin = o;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}