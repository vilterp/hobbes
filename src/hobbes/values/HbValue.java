package hobbes.values;

import hobbes.core.ObjectSpace;

public abstract class HbValue {
	
	private int id;
	private ObjectSpace objSpace;
	
	public HbValue(ObjectSpace o) {
		objSpace = o;
		id = objSpace.add(this);
	}
	
	public int getId() {
		return id;
	}
	
	public ObjectSpace getObjSpace() {
		return objSpace;
	}
	
	public abstract HbString show();
	public abstract HbString getType();
	public abstract HbBoolean is(HbValue other);
	
}
