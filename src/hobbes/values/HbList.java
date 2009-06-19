package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.values.HbRange.IterImp;

@HobbesClass(name="List")
public class HbList extends HbObject {
	
	private ArrayList<HbObject> elements;
	private int iterPos = 0;
	private static Random r = new Random();
	private static final String COMMA_SPACE = ", ";
	
	public HbList(Interpreter o) {
		this(o,new ArrayList<HbObject>());
	}
	
	public HbList(Interpreter o, ArrayList<HbObject> initValues) {
		super(o);
		iterPos = 0;
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
		HbList newList = new HbList(getInterp());
		for(HbObject elem: elements)
			newList.add(elem); // this incRefs each object
		return newList;
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("[");
		Iterator<HbObject> it = elements.iterator();
		while(it.hasNext()) {
			repr.append(it.next().realShow());
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append(']');
		return getObjSpace().getString(repr);
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(!isEmpty());
	}
	
	@HobbesMethod(name="==",numArgs=1)
	public HbObject equalTo(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		if(other instanceof HbList) {
			for(int i=0; i < length(); i++) {
				try {
					if(((HbList)other).get(i).eq(get(i)))
						return getObjSpace().getFalse();
				} catch(HbKeyError e) {
					return getObjSpace().getFalse();
				}
			}
			return getObjSpace().getTrue();
		} else
			throw new HbArgumentError(getInterp(),"==",other,"List");
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject hbGet(HbObject index) throws HbError, ErrorWrapper, Continue, Break {
		if(index instanceof HbInt)
			return get(((HbInt)index).getValue());
		else if(index instanceof HbRange && ((HbRange)index).getStart() instanceof HbInt) {
			HbList subList = new HbList(getInterp());
			IterImp it = ((HbRange)index).iterator();
			while(it.hasNext())
				subList.add(get(((HbInt)it.getNext()).getValue()));
			return subList;
		} else
			throw new HbArgumentError(getInterp(),"[]",index,"Int or Range of Int");
	}
	
	public HbObject get(int ind) throws HbKeyError {
		if(ind >= 0 && ind < elements.size()) {
			return elements.get(ind);
		} else if(ind < 0) {
			if(-ind <= length())
				return get(length()+ind);
			else
				throw new HbKeyError(getInterp(),
						new Integer(ind).toString()
						+ " (size: " + elements.size() + ")");
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
	
	@HobbesMethod(name="[]=",numArgs=2)
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
				ans.append(it.next().realToString());
				if(it.hasNext())
					ans.append(j);
			}
			return getObjSpace().getString(ans);
		} else
			throw new HbArgumentError(getInterp(),"join",
								joiner,
								"String");
	}
	
	@HobbesMethod(name="+",numArgs=1)
	public HbList merge(HbObject other) throws HbArgumentError {
		if(other instanceof HbList) {
			HbList newList = hbClone();
			for(HbObject elem: ((HbList)other).getElements())
				newList.add(elem);
			return newList;
		} else
			throw new HbArgumentError(getInterp(),"+",other,"List");
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
	
	@HobbesMethod(name="map",numArgs=1)
	public HbList map(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			HbList newList = new HbList(getInterp());
			for(int i=0; i < length(); i++) {
				HbObject val = elements.get(i);
				HbObject result = getInterp().callFunc((HbFunction)func,
															new HbObject[]{val},null);
				newList.add(result);
			}
			return newList;
		} else
			throw new HbArgumentError(getInterp(),"map",func,"AnonymousFunction");
	}
	
	@HobbesMethod(name="filter",numArgs=1)
	public HbList filter(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			HbList newList = new HbList(getInterp());
			for(int i=0; i < length(); i++) {
				HbObject val = elements.get(i);
				HbObject result = getInterp().callFunc(
										(HbFunction)func,new HbObject[]{val},null);
				if(result.call("toBool") == getObjSpace().getTrue())
					newList.add(val);
			}
			return newList;
		} else
			throw new HbArgumentError(getInterp(),"filter",func,"AnonymousFunction");
	}
	
	@HobbesMethod(name="flatten")
	public HbList flatten() throws ErrorWrapper, HbError, Continue, Break {
		HbList newList = new HbList(getInterp());
		for(HbObject element: elements)
			if(element.call("toBool") == getObjSpace().getTrue())
				newList.add(element);
		return newList;
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
	
	@HobbesMethod(name="each",numArgs=1)
	public void each(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			for(HbObject elem: elements)
				getInterp().callFunc((HbFunction)func,new HbObject[]{elem},null);
		} else
			throw new HbArgumentError(getInterp(),"each",func,
						"AnonymousFunction, Function, or NativeFunction");
	}
	
	@HobbesMethod(name="iter_has_next")
	public HbObject iterHasNext() {
		return getObjSpace().getBool(iterPos < length());
	}
	
	@HobbesMethod(name="iter_next")
	public HbObject iterNext() throws HbKeyError {
		HbObject elem = get(iterPos);
		iterPos++;
		return elem;
	}
	
	@HobbesMethod(name="iter_index")
	public HbInt iterIndex() {
		return getObjSpace().getInt(iterPos);
	}
	
	@HobbesMethod(name="iter_rewind")
	public void iterRewind() {
		iterPos = 0;
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
