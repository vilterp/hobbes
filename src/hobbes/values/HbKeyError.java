package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="KeyError")
public class HbKeyError extends HbError {
	
	public HbKeyError(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw getNoMessageError();
	}
	
	public HbKeyError(ObjectSpace o, String m) {
		super(o, m);
	}

}
