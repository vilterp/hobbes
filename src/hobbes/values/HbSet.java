package hobbes.values;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

import java.util.ArrayList;
import java.util.Iterator;

@HobbesClass(name="Set")
public class HbSet extends HbObject {

	private HbDict elements;
	private static final String COMMA_SPACE = ", ";
	
	public HbSet(Interpreter i) {
		super(i);
		elements = new HbDict(getInterp());
		elements.incRefs();
	}
	
	public HbSet(Interpreter i, HbDict d) {
		super(i);
		elements = d;
	}
	
	public int[] contentAddrs() {
		return new int[]{elements.getId()};
	}
	
	public ArrayList<HbObject> getElements() {
		return elements.getKeys();
	}
	
	@HobbesMethod(name="clone")
	public HbSet hbClone() {
		HbDict newElements = elements.hbClone();
		newElements.incRefs();
		return new HbSet(getInterp(),newElements);
	}
	
	public int size() {
		return elements.size();
	}
	
	@HobbesMethod(name="size")
	public HbInt hbSize() {
		return elements.hbSize();
	}
	
	@HobbesMethod(name="empty?")
	public HbObject isEmpty() {
		return getObjSpace().getBool(elements.size() == 0);
	}
	
	@HobbesMethod(name="contains?",numArgs=1)
	public HbObject hbContains(HbObject obj) throws ErrorWrapper, HbError {
		return getObjSpace().getBool(elements.containsKey(obj));
	}
	
	public boolean contains(HbObject obj) throws ErrorWrapper, HbError {
		return elements.containsKey(obj);
	}
	
	@HobbesMethod(name="remove")
	public HbObject remove(HbObject obj) throws ErrorWrapper, HbError {
		try {
			elements.remove(obj);
			return getObjSpace().getTrue();
		} catch (HbKeyError e) {
			return getObjSpace().getFalse();
		}
	}
	
	@HobbesMethod(name="add")
	public void add(HbObject obj) throws ErrorWrapper, HbError {
		elements.put(obj,getObjSpace().getTrue());
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper, HbError {
		StringBuilder repr = new StringBuilder("{");
		Iterator<HbObject> it = elements.getKeys().iterator();
		while(it.hasNext()) {
			repr.append(it.next().show());
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
	
	@HobbesMethod(name="+",numArgs=1)
	public HbSet union(HbObject other) throws ErrorWrapper, HbError {
		if(other instanceof HbSet) {
			HbSet union = hbClone();
			for(HbObject key: ((HbSet)other).getElements())
				union.add(key);
			return union;
		} else
			throw new HbArgumentError(getInterp(),"+",other,"Set");
	}
	
	@HobbesMethod(name="intersection",numArgs=1)
	public HbSet intersection(HbObject other) throws ErrorWrapper, HbError {
		if(other instanceof HbSet) {
			HbSet intersection = new HbSet(getInterp());
			for(HbObject key: elements.getKeys())
				if(((HbSet)other).contains(key))
					intersection.add(key);
			return intersection;
		} else
			throw new HbArgumentError(getInterp(),"-",other,"Set");
	}
	
	public void clear() throws ErrorWrapper, HbError {
		elements.clear();
	}
	
}