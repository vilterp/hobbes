package hobbes.interpreter;

import hobbes.parser.Token;

public class Break extends LoopControlException {
	
	private Token origin;
	
	public Break(Token o) {
		origin = o;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
