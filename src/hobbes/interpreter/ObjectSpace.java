package hobbes.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import hobbes.values.*;

public class ObjectSpace {

	private HashMap<Integer,ValueRecord> objects;
	private HashMap<Integer,HbInt> intConstants;
	private HashMap<Float,HbFloat> floatConstants;
	private HashMap<String,HbClass> classes;
	private HashSet<String> builtinClasses;
	private HashMap<String,HbNativeFunction> functions;
	private HashSet<Integer> alive;
	private HashSet<Integer> collected;
	private int nextId;
	private int trueId;
	private int falseId;
	private int nilId;
	private boolean verboseGC;
	private Interpreter interp;

	public ObjectSpace(Interpreter i, boolean vgc) {
		interp = i;
		verboseGC = vgc;
		objects = new HashMap<Integer,ValueRecord>();
		builtinClasses = new HashSet<String>();
		classes = new HashMap<String,HbClass>();
		functions = new HashMap<String,HbNativeFunction>();
		alive = new HashSet<Integer>();
		collected = new HashSet<Integer>();
		intConstants = new HashMap<Integer,HbInt>();
		floatConstants = new HashMap<Float,HbFloat>();
		nextId = 0;
	}
	
	public void addBuiltins() {
		// add builtin classes
		addNativeClass(HbClass.class);
		addNativeClass(HbObject.class);
		getClass("Class").setSuperclass(getClass("Object"));
		addNativeClass(HbTrue.class);
		addNativeClass(HbFalse.class);
		addNativeClass(HbNil.class);
		addNativeClass(HbInt.class);
		addNativeClass(HbFloat.class);
		addNativeClass(HbString.class);
		addNativeClass(HbAnonymousFunction.class);
		addNativeClass(HbNativeFunction.class);
		addNativeClass(HbNormalFunction.class);
		// collections
		addNativeClass(HbList.class);
		addNativeClass(HbDict.class);
		addNativeClass(HbSet.class);
		// errors
		addNativeClass(HbError.class);
		addNativeClass(HbSyntaxError.class);
		addNativeClass(HbMissingMethodError.class);
		addNativeClass(HbUndefinedNameError.class);
		addNativeClass(HbArgumentError.class);
		addNativeClass(HbReadOnlyError.class);
		addNativeClass(HbKeyError.class);
		addNativeClass(HbTypeError.class);
		addNativeClass(HbNotAClassError.class);
		addNativeClass(HbNotIterableError.class);
		// set builtin classes
		for(String className: classes.keySet())
			builtinClasses.add(className);
		// add builtin globals
		nilId = new HbNil(interp).getId();
		trueId = new HbTrue(interp).getId();
		falseId = new HbFalse(interp).getId();
		// add native functions
		addNativeFunction("print",new String[]{"object"});
		addNativeFunction("get_input",new String[]{"prompt"});
		addNativeFunction("eval",new String[]{"code"});
	}
	
	private void addNativeFunction(String name, String[] args) {
		ArrayList<String> a = new ArrayList<String>();
		for(String s: args)
			a.add(s);
		functions.put(name,new HbNativeFunction(interp,name,a));
	}
	
	private void addNativeClass(Class<? extends HbObject> klass) {
		if(klass.isAnnotationPresent(HobbesClass.class)) {
			String name = ((HobbesClass)klass
							.getAnnotation(HobbesClass.class)).name();
			String superclass = ((HobbesClass)klass
						.getAnnotation(HobbesClass.class)).superClass();
			classes.put(name,new HbClass(interp,klass,superclass));
			classes.get(name).setClass(classes.get("Class"));
		} else
			throw new IllegalArgumentException("Supplied class \""+ klass.getName()
												+ "\" has no HobbesClass annotation");
	}
	
	public void addClass(HbClass klass) {
		classes.put(klass.getName(),klass);
	}
	
	public HashMap<String,HbClass> getClasses() {
		return classes;
	}
	
	public HashSet<String> getBuiltinClasses() {
		return builtinClasses;
	}
	
	public HbClass getClass(String name) {
		return (HbClass)classes.get(name);
	}
	
	public HashMap<String,HbNativeFunction> getNativeFunctions() {
		return functions;
	}

	private int getId() {
		int id = nextId;
		nextId++;
		return id;
	}

	public HbObject get(int id) {
		return objects.get(id).getValue();
	}

	public int add(HbObject val) {
		int id = getId();
		set(id, val);
		alive.add(id);
		return id;
	}

	private void set(int id, HbObject val) {
		objects.put(id, new ValueRecord(val));
	}
	
	public HbObject getBool(boolean condition) {
		if(condition)
			return getTrue();
		else
			return getFalse();
	}

	public HbTrue getTrue() {
		return (HbTrue)get(trueId);
	}

	public HbFalse getFalse() {
		return (HbFalse)get(falseId);
	}

	public HbNil getNil() {
		return (HbNil)get(nilId);
	}
	
	public HbObject nilIfNull(HbObject obj) {
		if(obj == null)
			return getNil();
		else
			return obj;
	}

	public HbInt getInt(Integer val) {
		if(intConstants.containsKey(val))
			return intConstants.get(val);
		else {
			HbInt newConstant = new HbInt(interp,val);
			intConstants.put(val,newConstant);
			return newConstant;
		}
	}

	public HbFloat getFloat(Float val) {
		if (intConstants.containsKey(val))
			return floatConstants.get(val);
		else {
			HbFloat newConstant = new HbFloat(interp,val);
			floatConstants.put(val,newConstant);
			return newConstant;
		}
	}
	
	public void incRefs(int id) {
		objects.get(id).incRefs();
	}
	
	public void decRefs(int id) {
		objects.get(id).decRefs();
	}
	
	private void garbageCollect(int id){
		HbObject victim = get(id);
		if(!isReferenced(id)) {
			if(victim instanceof HbInt)
				intConstants.remove(((HbInt)victim).getValue());
			for(int addr: victim.contentAddrs()) {
				if(objects.containsKey(addr)) {
					objects.get(addr).decRefs();
					garbageCollect(addr);
				}
			}
			if(verboseGC)
				System.out.println("Collected " + victim);
			objects.remove(id);
			collected.add(id);
		}
	}
	
	public void garbageCollect() {
		for(int id: alive)
			if(!collected.contains(id))
				garbageCollect(id);
		for(int id: collected)
			alive.remove(id);
		collected.clear();
	}
	
	private boolean isReferenced(int id) {
		return objects.get(id).isReferenced();
	}

	public void resetAlive() {
		alive.clear();
	}
	
	public int size() {
		return objects.size();
	}

}
