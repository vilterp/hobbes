package hobbes.parser;

public class UnexpectedTokenException extends Exception {
	
	Token symbol;
	
	public UnexpectedTokenException(Token sym) {
		super("Unexpected \""+sym.getValue()+"\"");
		symbol = sym;
	}
	
}
