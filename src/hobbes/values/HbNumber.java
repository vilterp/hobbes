package hobbes.values;

import hobbes.core.ObjectSpace;

public abstract class HbNumber extends HbValue {

	public HbNumber(ObjectSpace o) {
		super(o);
	}

	public abstract HbNumber plus(HbNumber other);
	public abstract HbNumber minus(HbNumber other);
	public abstract HbNumber dividedBy(HbNumber other);
	public abstract HbNumber times(HbNumber other);
	public abstract HbNumber toThePowerOf(HbNumber other);
	public abstract HbNumber mod(HbNumber other);

}
