package hobbes.parser;

public class UnexpectedTokenException extends Exception {
	
	Token token;
	
	public UnexpectedTokenException(Token tok) {
		super("Unexpected \""+tok.getValue()+"\"");
		token = tok;
	}
	
	public SourceLocation getLocation() {
		return token.getEnd();
	}
	
}
