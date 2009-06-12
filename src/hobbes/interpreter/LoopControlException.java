package hobbes.interpreter;

import hobbes.parser.Token;

public abstract class LoopControlException extends Throwable {
	
	public abstract Token getOrigin();
	
}
