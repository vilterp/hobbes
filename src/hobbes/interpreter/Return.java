package hobbes.interpreter;

import hobbes.parser.Token;
import hobbes.values.HbInstance;

public class Return extends Throwable {
	
	private Token origin;
	private HbInstance toReturn;
	
	public Return(Token o, HbInstance tr) {
		toReturn = tr;
		origin = o;
	}
	
	public HbInstance getToReturn() {
		return toReturn;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
