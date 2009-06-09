package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="Int")
public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(Interpreter o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getInterp(),
				"Can't make an Int with no parameters");
	}
	
	public HbInt(Interpreter o, int val) {
		super(o);
		value = val;
	}
	
	public int getValue() {
		return value;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return new HbString(getInterp(),new Integer(value).toString());
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(getValue() != 0);
	}
	
	@HobbesMethod(name="abs")
	public HbInt abs() {
		if(getValue() < 0)
			return getObjSpace().getInt(-getValue());
		else
			return this;
	}
	
	@HobbesMethod(name=">")
	public HbObject greaterThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() > ((HbInt)other).getValue());
		else
			throw new HbArgumentError(getInterp(),">",other,"Int");
	}
	
	@HobbesMethod(name="<")
	public HbObject lessThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() < ((HbInt)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"<",other,"Int");
	}
	
	@HobbesMethod(name="==")
	public HbObject equalTo(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() == ((HbInt)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"==",other,"Int");
	}
	
}
