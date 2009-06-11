package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="Float")
public class HbFloat extends HbObject {
	
	private float value;
	
	public HbFloat(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),
			"Can't make an Int with no parameters");
	}
	
	public HbFloat(Interpreter i, float val) {
		super(i);
		value = val;
	}
	
	public float getValue() {
		return value;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return new HbString(getInterp(),String.valueOf(value));
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(getValue() != 0);
	}
	
	@HobbesMethod(name="==",numArgs=1)
	public HbObject equalTo(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() == ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getBool(getValue() == ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"==",other,"Int or Float");
	}

	@HobbesMethod(name=">",numArgs=1)
	public HbObject greaterThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() > ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getBool(getValue() > ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),">",other,"Int or Float");
	}

	@HobbesMethod(name="<",numArgs=1)
	public HbObject lessThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() < ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getBool(getValue() < ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"<",other,"Int or Float");
	}
	
	@HobbesMethod(name="abs")
	public HbObject abs() {
		if(getValue() < 0)
			return getObjSpace().getFloat(-getValue());
		else
			return this;
	}
	
	@HobbesMethod(name="/",numArgs=1)
	public HbObject dividedBy(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat(getValue() / ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() / ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"/",other,"Int or Float");
	}
	
	@HobbesMethod(name="-",numArgs=1)
	public HbObject minus(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat(getValue() - ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() - ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"-",other,"Int or Float");
	}
	
	@HobbesMethod(name="%",numArgs=1)	
	public HbObject mod(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat(getValue() % ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() % ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"%",other,"Int or Float");
	}
	
	@HobbesMethod(name="+",numArgs=1)
	public HbObject plus(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat(getValue() + ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() + ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"+",other,"Int or Float");
	}
	
	@HobbesMethod(name="*",numArgs=1)
	public HbObject times(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat(getValue() * ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() * ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"*",other,"Int or Float");
	}
	
	@HobbesMethod(name="^",numArgs=1)
	public HbObject toThePowerOf(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat((float)Math.pow((double)getValue(),
											(double)((HbInt)other).getValue()));
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat((float)Math.pow((double)getValue(),
											(double)((HbFloat)other).getValue()));
		else
			throw new HbArgumentError(getInterp(),"-",other,"Int or Float");
	}
	
}
