package hobbes.values;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="Int")
public class HbInt extends HbObject {
	
	private int value;
	
	public HbInt(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),
				"Can't make an Int with no parameters");
	}
	
	public HbInt(Interpreter i, int val) {
		super(i);
		value = val;
	}
	
	public int getValue() {
		return value;
	}
	
	public String toString() {
		return "<Int val=" + getValue() + ">";
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() {
		return getObjSpace().getString(String.valueOf(value));
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

	@HobbesMethod(name="<",numArgs=1)
	public HbObject lessThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getBool(getValue() < ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getBool(getValue() < ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"<",other,"Int or Float");
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
	
	@HobbesMethod(name=">=",numArgs=1)
	public HbObject gtEq(HbObject other) throws HbArgumentError {
		return getObjSpace().getBool(greaterThan(other) == getObjSpace().getTrue() ||
				equalTo(other) == getObjSpace().getTrue());
	}
	
	@HobbesMethod(name="<=",numArgs=1)
	public HbObject ltEq(HbObject other) throws HbArgumentError {
		return getObjSpace().getBool(lessThan(other) == getObjSpace().getTrue() ||
				equalTo(other) == getObjSpace().getTrue());
	}

	@HobbesMethod(name="+",numArgs=1)
	public HbObject plus(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getInt(getValue() + ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() + ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"+",other,"Int or Float");
	}
	
	@HobbesMethod(name="-",numArgs=1)
	public HbObject minus(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getInt(getValue() - ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() - ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"-",other,"Int or Float");
	}
	
	@HobbesMethod(name="*",numArgs=1)
	public HbObject times(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getInt(getValue() * ((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() * ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"*",other,"Int or Float");
	}

	@HobbesMethod(name="/",numArgs=1)
	public HbObject dividedBy(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat((float)getValue()
							/ (float)((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() / ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"/",other,"Int or Float");
	}

	@HobbesMethod(name="%",numArgs=1)
	public HbObject mod(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getFloat((float)getValue()
								% (float)((HbInt)other).getValue());
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() % ((HbFloat)other).getValue());
		else
			throw new HbArgumentError(getInterp(),"%",other,"Int or Float");
	}

	@HobbesMethod(name="^",numArgs=1)
	public HbObject toThePowerOf(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt)
			return getObjSpace().getInt((int)Math.pow((double)getValue(),
											(double)((HbInt)other).getValue()));
		else if(other instanceof HbFloat)
			return getObjSpace().getFloat((float)Math.pow((double)getValue(),
											(double)((HbInt)other).getValue()));
		else
			throw new HbArgumentError(getInterp(),"^",other,"Int or Float");
	}
	
	@HobbesMethod(name="abs")
	public HbInt abs() {
		if(getValue() < 0)
			return getObjSpace().getInt(-getValue());
		else
			return this;
	}
	
	@HobbesMethod(name="even?")
	public HbObject isEven() {
		return getObjSpace().getBool(value % 2 == 0);
	}
	
	@HobbesMethod(name="odd?")
	public HbObject isOdd() {
		return getObjSpace().getBool(value % 2 != 0);
	}
	
	@HobbesMethod(name="succ")
	public HbInt succ() {
		return getObjSpace().getInt(value+1);
	}
	
	@HobbesMethod(name="pred")
	public HbInt pred() {
		return getObjSpace().getInt(value-1);
	}
	
	@HobbesMethod(name="times",numArgs=1)
	public void doNumTimes(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			for(int i=0; i < getValue(); i++)
				getInterp().callFunc((HbFunction)func,new HbObject[]{},null);
		} else
			throw new HbArgumentError(getInterp(),"times",func,"AnonymousFunction");
	}
	
}
