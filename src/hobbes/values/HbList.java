package hobbes.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="List")
public class HbList extends HbObject {
	
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
	
	@HobbesMethod(name="clone")
	public HbList clone() {
		return new HbList(getInterp(),(ArrayList<HbObject>)elements.clone());
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
			throw new HbArgumentError(getInterp(),"[]",index,"HbInt");
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
			throw new HbArgumentError(getInterp(),"get",index,"HbInt");
	}
	
	@HobbesMethod(name="clear")
	public void clear() {
		elements.clear();
	}
	
	@HobbesMethod(name="add",numArgs=1)
	public void add(HbObject obj) {
		elements.add(obj);
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
								joiner,
								"String");
	}
	
	@HobbesMethod(name="toSet")
	public HbSet toSet() {
		HbSet set = new HbSet(getInterp());
		for(HbObject elem: elements)
			set.add(elem);
		return set;
	}
	
	@HobbesMethod(name="sort")
	public HbList sort() throws ErrorWrapper {
		HbList newList = clone();
		newList.sortInPlace();
		return newList;
	}
	
	@HobbesMethod(name="sort!")
	public void sortInPlace() throws ErrorWrapper {
		sort(0,elements.size()-1);
	}
	
	private void sort(int start, int end) throws ErrorWrapper {
		if(start < end) {
			int pivotIndex = partition(start,end);
			sort(start,pivotIndex-1);
			sort(pivotIndex+1,end);
		}
	}
	
	private int partition(int start, int end) throws ErrorWrapper {
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
