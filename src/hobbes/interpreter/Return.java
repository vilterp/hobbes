package hobbes.interpreter;

import hobbes.parser.Token;
import hobbes.values.HbValue;

public class Return extends Throwable {
	
	private Token origin;
	private HbValue toReturn;
	
	public Return(Token o, HbValue tr) {
		toReturn = tr;
		origin = o;
	}
	
	public HbValue getToReturn() {
		return toReturn;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
