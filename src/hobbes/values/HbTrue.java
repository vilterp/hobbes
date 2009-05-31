package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbTrue extends HbBoolean {

	public HbTrue(ObjectSpace o) {
		super(o);
	}

	public HbString getType() {
		return new HbString(getObjSpace(), "True");
	}

	@Override
	public HbBoolean is(HbInstance other) {
		if (other instanceof HbTrue)
			return getObjSpace().getTrue();
		else
			return getObjSpace().getFalse();
	}

	public HbString show() {
		return new HbString(getObjSpace(), "true");
	}

}
