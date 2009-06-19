package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="Dict")
public class HbDict extends HbObject {
	
	private Bucket[] buckets;
	private ArrayList<Entry> entries;
	private int iterPos;
	
	private static final int INIT_SIZE = 11;
	private static final String COMMA_SPACE = ", ";
	private static final String COLON_SPACE = ": ";
	
	public HbDict(Interpreter i) {
		this(i,new Bucket[INIT_SIZE],new ArrayList<HbObject>(),new ArrayList<HbObject>());
	}
	
	public HbDict(Interpreter i, Bucket[] b, ArrayList<HbObject> k, ArrayList<HbObject> v) {
		super(i);
		buckets = b;
		entries = new ArrayList<Entry>();
		iterPos = 0;
	}
	
	public int[] contentAddrs() {
		int[] addrs = new int[entries.size()*2];
		int i = 0;
		for(Entry e: entries) {
			addrs[i] = e.getKey().getId();
			i++;
			addrs[i] = e.getValue().getId();
			i++;
		}
		return addrs;
	}
	
	public int size() {
		return entries.size();
	}
	
	@HobbesMethod(name="clone")
	public HbDict hbClone() throws ErrorWrapper, HbError, Continue, Break {
		HbDict newDict = new HbDict(getInterp());
		for(Entry entry: entries)
			newDict.put(entry.getKey(),entry.getValue());
		return newDict;
	}
	
	@HobbesMethod(name="size")
	public HbInt hbSize() {
		return getObjSpace().getInt(size());
	}
	
	@HobbesMethod(name="empty?")
	public HbObject hbIsEmpty() {
		return getObjSpace().getBool(isEmpty());
	}
	
	public boolean isEmpty() {
		return size() == 0;
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(!isEmpty());
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("{");
		Iterator<Entry> it = entries.iterator();
		while(it.hasNext()) {
			Entry entry = it.next();
			repr.append(entry.getKey().realShow());
			repr.append(COLON_SPACE);
			try {
				repr.append(entry.getValue().realShow());
			} catch (HbKeyError e) {
				e.printStackTrace();
			}
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append('}');
		return getObjSpace().getString(repr);
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject get(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		Bucket bucket = buckets[map(key.realHashCode())];
		if(bucket == null)
			throw new HbKeyError(getInterp(),key.realShow());
		Entry e = bucket.getEntry(key);
		if(e != null)
			return e.getValue();
		else
			throw new HbKeyError(getInterp(),key.realShow());
	}
	
	@HobbesMethod(name="[]=",numArgs=2)
	public void put(HbObject key, HbObject value) throws ErrorWrapper, HbError, Continue, Break {
		int bucketIndex = map(key.realHashCode());
		Bucket bucket = buckets[bucketIndex];
		if(bucket == null) {
			buckets[bucketIndex] = new Bucket(this);
			bucket = buckets[bucketIndex];
		}
		bucket.addEntry(key,value);
	}
	
	@HobbesMethod(name="[]del",numArgs=1)
	public void remove(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		Bucket bucket = buckets[map(key.realHashCode())];
		if(bucket == null)
			throw new HbKeyError(getInterp(),key.realShow());
		if(!bucket.removeEntry(key))
			throw new HbKeyError(getInterp(),key.realShow());
	}
	
	@HobbesMethod(name="has_key?",numArgs=1)
	public HbObject hbContainsKey(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		return getObjSpace().getBool(containsKey(key));
	}
	
	public boolean containsKey(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		try {
			get(key);
			return true;
		} catch (HbKeyError e) {
			return false;
		}
	}
	
	@HobbesMethod(name="clear")
	public void clear() throws ErrorWrapper, HbError, Continue, Break {
		for(HbObject key: keySet()) {
			try {
				remove(key);
			} catch (HbKeyError err) {
				err.printStackTrace();
			}
		}
	}
	
	@HobbesMethod(name="keys")
	public HbSet keySet() throws ErrorWrapper, HbError, Continue, Break {
		HbSet toReturn = new HbSet(getInterp());
		for(Entry e: entries)
			toReturn.add(e.getKey());
		return toReturn;
	}
	
	@HobbesMethod(name="values")
	public HbSet valueSet() throws ErrorWrapper, HbError, Continue, Break {
		HbSet toReturn = new HbSet(getInterp());
		for(Entry e: entries)
			toReturn.add(e.getValue());
		return toReturn;
	}
	
	@HobbesMethod(name="iter_has_next")
	public HbObject iterHasNext() {
		return getObjSpace().getBool(iterPos < entries.size());
	}
	
	@HobbesMethod(name="iter_next")
	public HbObject iterNext() throws ErrorWrapper, HbError, Continue, Break {
		HbObject temp = entries.get(iterPos).getValue();
		iterAdvance();
		return temp;
	}
	
	@HobbesMethod(name="iter_index")
	public HbObject iterIndex() {
		return entries.get(iterPos).getKey();
	}
	
	public void iterAdvance() {
		iterPos++;
	}
	
	@HobbesMethod(name="iter_rewind")
	public void iterRewind() {
		iterPos = 0;
	}
	
	public ArrayList<Entry> getEntries() {
		return entries;
	}
	
	private int map(int val) {
		return Math.abs(val) % buckets.length;
	}
	
	private class Bucket {
		
		private HbDict origin;
		private ArrayList<Entry> entries;
		
		public Bucket(HbDict o) {
			origin = o;
			entries = new ArrayList<Entry>(2);
		}
		
		public String toString() {
			return "Bucket" + entries.toString();
		}
		
		public void addEntry(HbObject key, HbObject value)
											throws ErrorWrapper, HbError, Continue, Break {
			for(int i=0; i < entries.size(); i++) {
				Entry oldEntry = entries.get(i);
				if(oldEntry.getKey().realHashCode()
						== key.realHashCode()) { // same key: overwrite value
					// dec refs on old key, value
					oldEntry.getValue().decRefs();
					oldEntry.getKey().decRefs();
					// inc refs on new key, value
					value.incRefs();
					key.incRefs();
					// overwrite entry
					Entry e = new Entry(key,value,oldEntry.getOriginInd());
					entries.set(i,e);
					origin.getEntries().add(e);
					return;
				}
			}
			// add new entries
			key.incRefs();
			value.incRefs();
			Entry e = new Entry(key,value,origin.getEntries().size());
			entries.add(e);
			origin.getEntries().add(e);
		}
		
		public boolean removeEntry(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
			for(int i=0; i < entries.size(); i++) {
				Entry entry = entries.get(i);
				if(entry.getKey().realHashCode() == key.realHashCode()) {
					entry.getKey().decRefs();
					entry.getValue().decRefs();
					origin.getEntries().remove(entry.getOriginInd());
					for(int j=entry.getOriginInd(); j < origin.getEntries().size(); j++)
						origin.getEntries().get(j).decrOriginInd();
					return true;
				}
			}
			return false;
		}
		
		public Entry getEntry(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
			for(Entry entry: entries)
				if(key.realHashCode() == entry.getKey().realHashCode())
					return entry;
			return null;
		}
		
	}
	
	public class Entry {
		
		private HbObject key;
		private HbObject value;
		private int originInd;
		
		public Entry(HbObject k, HbObject v, int oi) {
			key = k;
			value = v;
			originInd = oi;
		}
		
		public String toString() {
			return originInd + ":Entry[" + key + " = " + value + "]";
		}
		
		public HbObject getKey() {
			return key;
		}
		
		public HbObject getValue() {
			return value;
		}

		public int getOriginInd() {
			return originInd;
		}
		
		public void decrOriginInd() {
			originInd--;
		}
		
	}

}
