package hobbes.interpreter;

import hobbes.parser.Token;

public class Continue extends Throwable {
	
	private Token origin;
	
	public Continue(Token o) {
		origin = o;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
