package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="List")
public class HbList extends HbObject {
	
	private ArrayList<HbObject> elements;
	private static final String COMMA_SPACE = ", ";
	
	public HbList(Interpreter o) {
		this(o,new ArrayList<HbObject>());
	}
	
	public HbList(Interpreter o, ArrayList<HbObject> initValues) {
		super(o);
		elements = initValues;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper {
		StringBuilder repr = new StringBuilder("[");
		Iterator<HbObject> it = elements.iterator();
		while(it.hasNext()) {
			repr.append(getInterp().show(it.next()));
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append(']');
		return new HbString(getInterp(),repr);
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject get(HbObject index) throws HbError {
		if(index instanceof HbInt) {
			int ind = ((HbInt)index).getValue();
			if(ind < elements.size()) {
				return elements.get(ind);
			} else
				throw new HbKeyError(getInterp(),
						new Integer(ind).toString()
						+ " (size: " + elements.size() + ")");
		} else
			throw new HbArgumentError(getInterp(),
									"[]",
									index.getHbClass().getName(),
									"HbInt");
	}
	
	@HobbesMethod(name="[]set",numArgs=2)
	public void set(HbObject index, HbObject value) throws HbError {
		if(index instanceof HbInt) {
			int ind = ((HbInt)index).getValue();
			if(ind < elements.size()) {
				elements.set(ind, value);
			} else
				throw new HbKeyError(getInterp(),new Integer(ind).toString());
		} else
			throw new HbArgumentError(getInterp(),
									"get",
									index.getHbClass().getName(),
									"HbInt");
	}
	
	@HobbesMethod(name="length")
	public HbInt getLength() {
		return getObjSpace().getInt(elements.size());
	}
	
	@HobbesMethod(name="empty?")
	public HbObject isEmpty() {
		return getObjSpace().getBool(elements.isEmpty());
	}
	
	@HobbesMethod(name="join",numArgs=1,defaults={"\"\""})
	public HbString join(HbObject joiner) throws HbArgumentError, ErrorWrapper {
		if(joiner instanceof HbString) {
			String j = ((HbString)joiner).getValue().toString();
			StringBuilder ans = new StringBuilder();
			Iterator<HbObject> it = elements.iterator();
			while(it.hasNext()) {
				ans.append(getInterp().callToString(it.next()));
				if(it.hasNext())
					ans.append(j);
			}
			return new HbString(getInterp(),ans);
		} else
			throw new HbArgumentError(getInterp(),"join",
								joiner.getHbClass().getName(),
								"String");
	}

}
