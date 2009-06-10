package hobbes.values;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

import java.util.HashSet;
import java.util.Iterator;

@HobbesClass(name="Set")
public class HbSet extends HbObject {

	private HbDict elements;
	private static final String COMMA_SPACE = ", ";
	
	public HbSet(Interpreter i) {
		super(i);
		elements = new HbDict(getInterp());
	}
	
	@HobbesMethod(name="size")
	public HbInt size() {
		return getObjSpace().getInt(elements.size());
	}
	
	@HobbesMethod(name="empty?")
	public HbObject isEmpty() {
		return getObjSpace().getBool(elements.size() == 0);
	}
	
	@HobbesMethod(name="contains?",numArgs=1)
	public HbObject contains(HbObject obj) throws ErrorWrapper {
		return getObjSpace().getBool(elements.containsKey(obj));
	}
	
	@HobbesMethod(name="remove")
	public HbObject remove(HbObject obj) throws ErrorWrapper {
		try {
			elements.remove(obj);
			return getObjSpace().getTrue();
		} catch (HbKeyError e) {
			return getObjSpace().getFalse();
		}
	}
	
	@HobbesMethod(name="add")
	public void add(HbObject obj) throws ErrorWrapper {
		elements.put(obj,getObjSpace().getTrue());
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper {
		StringBuilder repr = new StringBuilder("{");
		Iterator<HbObject> it = elements.getKeys().iterator();
		while(it.hasNext()) {
			repr.append(getInterp().show(it.next()));
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append('}');
		return new HbString(getInterp(),repr);
	}
	
	@HobbesMethod(name="toList")
	public HbList toList() {
		HbList list = new HbList(getInterp());
		for(HbObject element: elements.getKeys())
			list.add(element);
		return list;
	}
	
}
