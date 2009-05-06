package hobbes.parser;

public class UnexpectedTokenException extends Exception {
	
	Token token;
	
	public UnexpectedTokenException(Token tok) {
		super("Unexpected \""+tok.getValue()+"\"");
		token = tok;
	}
	
	// TODO: show
	
}
