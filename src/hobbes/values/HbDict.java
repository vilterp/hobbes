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
	private ArrayList<HbObject> keys;
	private static final int INIT_SIZE = 11;
	private static final String COMMA_SPACE = ", ";
	private static final String COLON_SPACE = ": ";
	
	public HbDict(Interpreter i) {
		this(i,new Bucket[INIT_SIZE],new ArrayList<HbObject>());
	}
	
	public HbDict(Interpreter i, Bucket[] b, ArrayList<HbObject> k) {
		super(i);
		keys = k;
		buckets = b;
	}
	
	public int[] contentAddrs() {
		int[] addrs = new int[size()];
		for(int i=0; i < size(); i++)
			addrs[i] = keys.get(i).getId();
		return addrs;
	}
	
	public ArrayList<HbObject> getKeys() {
		return keys;
	}
	
	public int size() {
		return keys.size();
	}
	
	@HobbesMethod(name="clone")
	public HbDict hbClone() {
		return new HbDict(getInterp(),buckets.clone(),keys);
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
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("{");
		Iterator<HbObject> it = keys.iterator();
		while(it.hasNext()) {
			HbObject key = it.next();
			repr.append(key.show());
			repr.append(COLON_SPACE);
			try {
				repr.append(get(key).show());
			} catch (HbKeyError e) {
				e.printStackTrace();
			}
			if(it.hasNext())
				repr.append(COMMA_SPACE);
		}
		repr.append('}');
		return new HbString(getInterp(),repr);
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbObject get(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		Bucket bucket = buckets[map(key.realHashCode())];
		if(bucket == null)
			throw new HbKeyError(getInterp(),key.show());
		HbObject ans = bucket.get(key);
		if(ans == null)
			throw new HbKeyError(getInterp(),key.show());
		return ans;
	}
	
	@HobbesMethod(name="[]set",numArgs=2)
	public void put(HbObject key, HbObject value) throws ErrorWrapper, HbError, Continue, Break {
		int bucketIndex = map(key.realHashCode());
		Bucket bucket = buckets[bucketIndex];
		if(bucket == null) {
			buckets[bucketIndex] = new Bucket();
			bucket = buckets[bucketIndex];
		}
		if(bucket.addEntry(new Entry(key,value))) {
			keys.add(key);
			key.incRefs();
			value.incRefs();
		}
	}
	
	@HobbesMethod(name="[]del",numArgs=1)
	public void remove(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
		Bucket bucket = buckets[map(key.realHashCode())];
		if(bucket == null)
			throw new HbKeyError(getInterp(),key.show());
		if(!bucket.removeEntry(key))
			throw new HbKeyError(getInterp(),key.show());
	}
	
	@HobbesMethod(name="contains_key?",numArgs=1)
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
		for(HbObject key: keys) {
			try {
				remove(key);
			} catch (HbKeyError e) {
				e.printStackTrace();
			}
		}
	}
	
	@HobbesMethod(name="keys")
	public HbSet keySet() throws ErrorWrapper, HbError, Continue, Break {
		HbSet toReturn = new HbSet(getInterp());
		for(HbObject key: keys)
			toReturn.add(key);
		return toReturn;
	}
	
	@HobbesMethod(name="values")
	public HbSet valueSet() throws ErrorWrapper, HbError, Continue, Break {
		HbSet toReturn = new HbSet(getInterp());
		for(HbObject key: keys)
			toReturn.add(get(key));
		return toReturn;
	}
	
	private int map(int val) {
		return Math.abs(val) % buckets.length;
	}
	
	private class Bucket {
		
		private ArrayList<Entry> entries;
		
		public Bucket() {
			entries = new ArrayList<Entry>();
		}
		
		/*
		 * true: new
		 * false: overwrite
		 */
		public boolean addEntry(Entry e) throws ErrorWrapper, HbError, Continue, Break {
			for(int i=0; i < entries.size(); i++) {
				Entry entry = entries.get(i);
				if(entry.getKey().realHashCode()
						== e.getKey().realHashCode()) {
					entries.get(i).getKey().decRefs();
					entries.get(i).getValue().decRefs();
					entries.remove(i);
					entries.add(e);
					return false;
				}
			}
			entries.add(e);
			return true;
		}
		
		public boolean removeEntry(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
			for(int i=0; i < entries.size(); i++) {
				Entry entry = entries.get(i);
				if(entry.getKey().realHashCode() == key.realHashCode()) {
					entry.getKey().decRefs();
					entry.getValue().decRefs();
					return true;
				}
			}
			return false;
		}
		
		public HbObject get(HbObject key) throws ErrorWrapper, HbError, Continue, Break {
			for(Entry entry: entries)
				if(key.realHashCode() == entry.getKey().realHashCode())
					return entry.getValue();
			return null;
		}
		
	}
	
	private class Entry {
		
		private HbObject key;
		private HbObject value;
		
		public Entry(HbObject k, HbObject v) {
			key = k;
			value = v;
		}
		
		public HbObject getKey() {
			return key;
		}
		
		public HbObject getValue() {
			return value;
		}
		
	}

}
