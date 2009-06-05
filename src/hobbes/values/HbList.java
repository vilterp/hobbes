package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="List")
public class HbList extends HbObject {
	
	private ArrayList<HbObject> elements;
	
	public HbList(ObjectSpace o) {
		this(o,new ArrayList<HbObject>());
	}
	
	public HbList(ObjectSpace o, ArrayList<HbObject> initValues) {
		super(o);
		elements = initValues;
	}
	
	public String toString() {
		return elements.toString();
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject get(HbObject index) throws HbError {
		if(index instanceof HbInt) {
			int ind = ((HbInt)index).getValue();
			if(ind < elements.size()) {
				return elements.get(ind);
			} else
				throw new HbKeyError(getObjSpace(),
						new Integer(ind).toString()
						+ " (size: " + elements.size() + ")");
		} else
			throw new HbArgumentError(getObjSpace(),
									"[]",
									index.getHbClass().getName(),
									"HbInt");
	}
	
	@HobbesMethod(name="[]set",numArgs=1)
	public void set(HbObject index, HbObject value) throws HbError {
		if(index instanceof HbInt) {
			int ind = ((HbInt)index).getValue();
			if(ind < elements.size()) {
				elements.set(ind, value);
			} else
				throw new HbKeyError(getObjSpace(),new Integer(ind).toString());
		} else
			throw new HbArgumentError(getObjSpace(),
									"get",
									index.getHbClass().getName(),
									"HbInt");
	}
	
	@HobbesMethod(name="join",numArgs=1,defaults={"\"\""})
	public HbString join(HbObject joiner) throws HbArgumentError {
		if(joiner instanceof HbString) {
			String j = ((HbString)joiner).getValue().toString();
			StringBuilder ans = new StringBuilder();
			Iterator<HbObject> it = elements.iterator();
			while(it.hasNext()) {
				HbObject next = it.next();
				if(next instanceof HbString)
					ans.append(((HbString)next).getValue());
				else
					ans.append(next);
				if(it.hasNext())
					ans.append(j);
			}
			return new HbString(getObjSpace(),ans);
		} else
			throw new HbArgumentError(getObjSpace(),"join",
								joiner.getHbClass().getName(),
								"String");
	}

}