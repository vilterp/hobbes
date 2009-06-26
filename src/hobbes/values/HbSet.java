package hobbes.values;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.values.HbDict.Entry;

import java.util.ArrayList;
import java.util.Iterator;

@HobbesClass(name="Set")
public class HbSet extends HbObject implements Iterable<HbObject> {

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
	
	@HobbesMethod(name="clone")
	public HbSet hbClone() throws ErrorWrapper, HbError, Continue, Break {
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
	public HbObject hbIsEmpty() {
		return getObjSpace().getBool(isEmpty());
	}
	
	public boolean isEmpty() {
		return elements.size() == 0;
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(!isEmpty());
	}
	
	@HobbesMethod(name="contains?",numArgs=1)
	public HbObject hbContains(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		return getObjSpace().getBool(elements.containsKey(obj));
	}
	
	public boolean contains(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		return elements.containsKey(obj);
	}
	
	@HobbesMethod(name="remove",numArgs=1)
	public HbObject remove(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		try {
			elements.remove(obj);
			return getObjSpace().getTrue();
		} catch (HbKeyError e) {
			return getObjSpace().getFalse();
		}
	}
	
	@HobbesMethod(name="add",numArgs=1)
	public void add(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		elements.put(obj,getObjSpace().getTrue());
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("{");
		Iterator<HbObject> it = this.iterator();
		while(it.hasNext()) {
			repr.append(it.next().realShow());
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append('}');
		return getObjSpace().getString(repr);
	}
	
	@HobbesMethod(name="toList")
	public HbList toList() {
		HbList list = new HbList(getInterp());
		for(HbObject elem: this)
			list.add(elem);
		return list;
	}
	
	@HobbesMethod(name="map",numArgs=1)
	public HbSet map(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			HbSet newSet = new HbSet(getInterp());
			for(HbObject elem: this) {
				HbObject result = getInterp().callFunc((HbFunction)func,
															new HbObject[]{elem},null);
				newSet.add(result);
			}
			return newSet;
		} else
			throw new HbArgumentError(getInterp(),"map",func,"AnonymousFunction");
	}
	
	@HobbesMethod(name="filter",numArgs=1)
	public HbSet filter(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			HbSet newSet = new HbSet(getInterp());
			for(HbObject elem: this) {
				HbObject result = getInterp().callFunc((HbFunction)func,
															new HbObject[]{elem},null);
				if(result == getObjSpace().getTrue())
					newSet.add(result);
			}
			return newSet;
		} else
			throw new HbArgumentError(getInterp(),"filter",func,"AnonymousFunction");
	}
	
	@HobbesMethod(name="each",numArgs=1)
	public void each(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			for(HbObject elem: this)
				getInterp().callFunc((HbFunction)func,new HbObject[]{elem},null);
		} else
			throw new HbArgumentError(getInterp(),"each",func,
					"AnonymousFunction, Function, or NativeFunction");
	}
	
	@HobbesMethod(name="iter_has_next")
	public HbObject iterHasNext() {
		return elements.iterHasNext();
	}
	
	@HobbesMethod(name="iter_next")
	public HbObject iterNext() throws ErrorWrapper, HbError, Continue, Break {
		HbObject next = elements.iterIndex();
		elements.iterAdvance();
		return next;
	}
	
	@HobbesMethod(name="iter_rewind")
	public void iterRewind() {
		elements.iterRewind();
	}
	
	@HobbesMethod(name="union",numArgs=1)
	public HbSet union(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		if(other instanceof HbSet) {
			HbSet union = hbClone();
			for(HbObject elem: (HbSet)other)
				union.add(elem);
			return union;
		} else
			throw new HbArgumentError(getInterp(),"+",other,"Set");
	}
	
	@HobbesMethod(name="intersection",numArgs=1)
	public HbSet intersection(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		if(other instanceof HbSet) {
			HbSet intersection = new HbSet(getInterp());
			for(HbObject elem: this)
				if(((HbSet)other).contains(elem))
					intersection.add(elem);
			return intersection;
		} else
			throw new HbArgumentError(getInterp(),"-",other,"Set");
	}
	
	@HobbesMethod(name="-",numArgs=1)
	public HbSet difference(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		if(other instanceof HbSet) {
			HbSet diff = hbClone();
			for(HbObject elem: this)
				if(((HbSet)other).contains(elem))
					diff.remove(elem);
			return diff;
		} else
			throw new HbArgumentError(getInterp(),"-",other,"Set");
	}
	
	@HobbesMethod(name="clear")
	public void clear() throws ErrorWrapper, HbError, Continue, Break {
		elements.clear();
	}
	
	public Iterator<HbObject> iterator() {
		return new IterImp(elements.getEntries());
	}
	
	public class IterImp implements Iterator<HbObject> {
		
		public Iterator<Entry> entries;
		
		public IterImp(ArrayList<Entry> e) {
			entries = e.iterator();
		}
		
		public boolean hasNext() {
			return entries.hasNext();
		}

		public HbObject next() {
			return entries.next().getKey();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
