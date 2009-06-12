package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="List")
public class HbList extends HbObject {
	
	// TODO: []del
	// TODO: find (== or is?)
	
	private ArrayList<HbObject> elements;
	private static Random r = new Random();
	private static final String COMMA_SPACE = ", ";
	
	public HbList(Interpreter o) {
		this(o,new ArrayList<HbObject>());
	}
	
	public HbList(Interpreter o, ArrayList<HbObject> initValues) {
		super(o);
		elements = initValues;
	}
	
	public ArrayList<HbObject> getElements() {
		return elements;
	}
	
	public int[] contentAddrs() {
		int[] addrs = new int[length()];
		for(int i=0; i < length(); i++)
			addrs[i] = elements.get(i).getId();
		return addrs;
	}
	
	@HobbesMethod(name="clone")
	public HbList hbClone() {
		return new HbList(getInterp(),(ArrayList<HbObject>)elements.clone());
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("[");
		Iterator<HbObject> it = elements.iterator();
		while(it.hasNext()) {
			repr.append(it.next().show());
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append(']');
		return new HbString(getInterp(),repr);
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(!isEmpty());
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject hbGet(HbObject index) throws HbError {
		if(index instanceof HbInt) {
			return get(((HbInt)index).getValue());
		} else
			throw new HbArgumentError(getInterp(),"[]",index,"Int");
	}
	
	public HbObject get(int ind) throws HbKeyError {
		if(ind >= 0 && ind < elements.size()) {
			return elements.get(ind);
		} else
			throw new HbKeyError(getInterp(),
					new Integer(ind).toString()
					+ " (size: " + elements.size() + ")");
	}
	
	@HobbesMethod(name="first")
	public HbObject first() throws HbKeyError {
		return get(0);
	}
	
	@HobbesMethod(name="last")
	public HbObject last() throws HbKeyError {
		return get(length()-1);
	}
	
	@HobbesMethod(name="[]set",numArgs=2)
	public void set(HbObject index, HbObject value) throws HbError {
		if(index instanceof HbInt) {
			int ind = ((HbInt)index).getValue();
			if(ind >= 0 && ind < elements.size()) {
				if(elements.get(ind) != null)
					elements.get(ind).decRefs();
				elements.set(ind,value);
				value.incRefs();
			} else
				throw new HbKeyError(getInterp(),new Integer(ind).toString());
		} else
			throw new HbArgumentError(getInterp(),"[]set",index,"Int");
	}
	
	@HobbesMethod(name="add",numArgs=1)
	public void add(HbObject obj) {
		elements.add(obj);
		obj.incRefs();
	}

	@HobbesMethod(name="[]del",numArgs=1)
	public void removeAtIndex(HbObject index) throws HbArgumentError {
		if(index instanceof HbInt) {
			index.decRefs();
			elements.remove(((HbInt)index).getValue());
		} else
			throw new HbArgumentError(getInterp(),"[]del",index,"Int");
	}
	
	@HobbesMethod(name="clear")
	public void clear() {
		for(HbObject element: elements)
			element.decRefs();
		elements.clear();
	}
	
	public int length() {
		return elements.size();
	}
	
	@HobbesMethod(name="length")
	public HbInt hbLength() {
		return getObjSpace().getInt(elements.size());
	}
	
	@HobbesMethod(name="empty?")
	public HbObject hbIsEmpty() {
		return getObjSpace().getBool(isEmpty());
	}
	
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	@HobbesMethod(name="join",numArgs=1,defaults={"\"\""})
	public HbString join(HbObject joiner) throws ErrorWrapper, HbError, Continue, Break {
		if(joiner instanceof HbString) {
			String j = ((HbString)joiner).getValue().toString();
			StringBuilder ans = new StringBuilder();
			Iterator<HbObject> it = elements.iterator();
			while(it.hasNext()) {
				ans.append(it.next().show());
				if(it.hasNext())
					ans.append(j);
			}
			return new HbString(getInterp(),ans);
		} else
			throw new HbArgumentError(getInterp(),"join",
								joiner,
								"String");
	}
	
	@HobbesMethod(name="merge!")
	public void mergeInPlace(HbObject other) throws HbArgumentError {
		if(other instanceof HbList) {
			for(HbObject elem: ((HbList)other).getElements())
				add(elem);
		} else
			throw new HbArgumentError(getInterp(),"merge",other,"List");
	}
	
	@HobbesMethod(name="merge")
	public HbList merge(HbObject other) throws HbArgumentError {
		HbList newList = hbClone();
		newList.mergeInPlace(other);
		return newList;
	}
	
	@HobbesMethod(name="find",numArgs=1)
	public HbObject find(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		for(int i=0; i < length(); i++)
			if(obj.call("==",new HbObject[]{get(i)}) == getObjSpace().getTrue())
				return getObjSpace().getInt(i);
		return getObjSpace().getNil();				
	}
	
	@HobbesMethod(name="contains?",numArgs=1)
	public HbObject contains(HbObject obj) throws ErrorWrapper, HbError, Continue, Break {
		return getObjSpace().getBool(find(obj) instanceof HbInt);
	}
	
	@HobbesMethod(name="toSet")
	public HbSet toSet() throws ErrorWrapper, HbError, Continue, Break {
		HbSet set = new HbSet(getInterp());
		for(HbObject elem: elements)
			set.add(elem);
		return set;
	}
	
	@HobbesMethod(name="uniq")
	public HbList uniq() throws ErrorWrapper, HbError, Continue, Break {
		return toSet().toList();
	}
	
	public HbObject[] toArray() {
		HbObject[] toReturn = new HbObject[elements.size()];
		for(int i=0; i < elements.size(); i++)
			toReturn[i] = elements.get(i);
		return toReturn;
	}
	
	@HobbesMethod(name="reverse!")
	public void reverseInPlace() {
		for(int i=0; i < elements.size() / 2; i++)
			swap(i,elements.size()-1-i);
	}
	
	@HobbesMethod(name="reverse")
	public HbList reverse() {
		HbList newList = hbClone();
		newList.reverseInPlace();
		return newList;
	}
	
	@HobbesMethod(name="sort")
	public HbList sort() throws ErrorWrapper, HbError, Continue, Break {
		HbList newList = hbClone();
		newList.sortInPlace();
		return newList;
	}
	
	@HobbesMethod(name="sort!")
	public void sortInPlace() throws ErrorWrapper, HbError, Continue, Break {
		sort(0,elements.size()-1);
	}
	
	private void sort(int start, int end) throws ErrorWrapper, HbError, Continue, Break {
		if(start < end) {
			int pivotIndex = partition(start,end);
			sort(start,pivotIndex-1);
			sort(pivotIndex+1,end);
		}
	}
	
	private int partition(int start, int end) throws ErrorWrapper, HbError, Continue, Break {
		int r1 = r.nextInt(end-start) + start;
		int r2 = r.nextInt(end-start) + start;
		int r3 = r.nextInt(end-start) + start;
		if(elements.get(r1).gt(elements.get(r2))) {
			int temp = r1;
			r1 = r2;
			r2 = temp;
		}
		if(elements.get(r2).gt(elements.get(r3))) {
			int temp = r2;
			r2 = r3;
			r3 = temp;
		}
		if(elements.get(r1).gt(elements.get(r2))) {
			int temp = r1;
			r1 = r2;
			r2 = temp;
		}
		swap(r2,end);
		int pivotIndex = end;
		HbObject pivot = elements.get(pivotIndex);
		end--;
		boolean goingRight = true;
		while(start <= end) {
			if(goingRight) {
				if(elements.get(start).gt(pivot))
					goingRight = false;
				else
					start++;
			} else {
				if(elements.get(end).lt(pivot)) {
					swap(start,end);
					start++;
					goingRight = true;
				} else {
					end--;
				}
			}
		}
		if(elements.get(start).gt(pivot)) {
			swap(start,pivotIndex);
			return start;
		} else {
			return pivotIndex;
		}
	}
	
	private void swap(int a, int b) {
		HbObject temp = elements.get(a);
		elements.set(a,elements.get(b));
		elements.set(b,temp);
	}

}
