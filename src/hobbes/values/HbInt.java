package hobbes.values;

import hobbes.core.ObjectSpace;

public class HbInt extends HbNumber {
	
	private int value;
	
	public HbInt(ObjectSpace o, int v) {
		super(o);
		value = v;
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),new Integer(value).toString());
	}
	
	public HbString getType() {
		return new HbString(getObjSpace(),"Int");
	}
	
	public int getValue() {
		return value;
	}
	
	public HbBoolean is(HbValue other) {
		if(other instanceof HbInt) {
			if(((HbInt)other).getValue() == getValue())
				return getObjSpace().getTrue();
			else
				return getObjSpace().getFalse();
		} else
			return getObjSpace().getFalse();
	}

	public HbNumber plus(HbNumber other) {
		if(other instanceof HbInt) {
			return getObjSpace().getInt(getValue() + ((HbInt)other).getValue());
		} else {
			return getObjSpace().getFloat(getValue() + ((HbFloat)other).getValue());
		}
	}

	public HbNumber minus(HbNumber other) {
		if(other instanceof HbInt) {
			return getObjSpace().getInt(getValue() + ((HbInt)other).getValue());
		} else
			return getObjSpace().getFloat(getValue() + ((HbFloat)other).getValue());
	}

	public HbNumber times(HbNumber other) {
		if(other instanceof HbInt) {
			return getObjSpace().getInt(getValue() * ((HbInt)other).getValue());
		} else
			return getObjSpace().getFloat(getValue() * ((HbFloat)other).getValue());
	}

	public HbNumber dividedBy(HbNumber other) {
		if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() / ((HbFloat)other).getValue());
		else
			return getObjSpace().getFloat(getValue() / (float)((HbInt)other).getValue());
	}

	public HbNumber toThePowerOf(HbNumber other) {
		if(other instanceof HbInt) {
			int result = (int)Math.pow((double)getValue(), (double)((HbInt)other).getValue());
			return getObjSpace().getInt(result);
		} else {
			float result = (float)Math.pow((double)getValue(),
										(double)((HbFloat)other).getValue());
			return getObjSpace().getFloat(result);
		}
	}
	
	public HbNumber mod(HbNumber other) {
		if(other instanceof HbFloat)
			return getObjSpace().getFloat(getValue() % ((HbFloat)other).getValue());
		else
			return getObjSpace().getFloat(getValue() % (float)((HbInt)other).getValue());
	}

}