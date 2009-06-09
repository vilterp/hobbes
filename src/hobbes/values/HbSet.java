package hobbes.values;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

import java.util.HashSet;
import java.util.Iterator;

@HobbesClass(name="Set")
public class HbSet extends HbObject {

	private HashSet<HbObject> elements;
	private static final String COMMA_SPACE = ", ";
	
	public HbSet(Interpreter i) {
		super(i);
		elements = new HashSet<HbObject>();
	}
	
	public HbSet(Interpreter i, HashSet<HbObject> e) {
		super(i);
		elements = e;
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
	public HbObject contains(HbObject obj) {
		return getObjSpace().getBool(elements.contains(obj));
	}
	
	@HobbesMethod(name="remove")
	public void remove(HbObject obj) {
		elements.remove(obj);
	}
	
	@HobbesMethod(name="add")
	public void add(HbObject obj) {
		elements.add(obj);
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper {
		StringBuilder repr = new StringBuilder("{");
		Iterator<HbObject> it = elements.iterator();
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
		for(HbObject element: elements)
			list.add(element);
		return list;
	}
	
}
