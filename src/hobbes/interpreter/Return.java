package hobbes.interpreter;

import hobbes.parser.Token;
import hobbes.values.HbObject;

public class Return extends Throwable {
	
	private Token origin;
	private HbObject toReturn;
	
	public Return(Token o, HbObject tr) {
		toReturn = tr;
		origin = o;
	}
	
	public HbObject getValue() {
		return toReturn;
	}
	
	public Token getOrigin() {
		return origin;
	}
	
}
