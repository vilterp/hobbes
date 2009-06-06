package hobbes.interpreter;

import hobbes.values.HbClass;
import hobbes.values.HbObject;
import hobbes.values.HbReadOnlyError;
import hobbes.values.HbUndefinedNameError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Scope {
	
	private ObjectSpace objSpace;
	private HashMap<String,Integer> names;
	private HashSet<String> globals;
	
	private static HashSet<String> readOnlys = new HashSet<String>();
	static {
		readOnlys.add("true");
		readOnlys.add("false");
		readOnlys.add("nil");
		readOnlys.add("self");
		readOnlys.add("print");
		readOnlys.add("get_input");
	}
	
	public Scope(ObjectSpace o) {
		this(o,null);
	}
	
	public Scope(ObjectSpace o, Scope inheritGlobalsFrom) {
		objSpace = o;
		names = new HashMap<String,Integer>();
		if(inheritGlobalsFrom == null) {
			globals = new HashSet<String>();
		} else {
			globals = inheritGlobalsFrom.getGlobals();
			for(String global: inheritGlobalsFrom.getGlobals()) {
				try {
					assignGlobalForce(global,inheritGlobalsFrom.get(global));
				} catch (HbUndefinedNameError e) {
					throw new IllegalArgumentException("name \"" + global
							+ "\" in globals not in scope object");
				}
			}
		}
	}
	
	public void addBasics() {
		// classes
		for(String className: objSpace.getClasses().keySet())
			assignGlobalForce(className,objSpace.getClasses().get(className));
		// functions
		for(String funcName: objSpace.getNativeFunctions().keySet())
			assignGlobalForce(funcName,objSpace.getNativeFunctions().get(funcName));
		// variables
		assignGlobalForce("true",objSpace.getTrue());
		assignGlobalForce("false",objSpace.getFalse());
		assignGlobalForce("nil",objSpace.getNil());
	}
	
	public HashSet<String> getGlobals() {
		return globals;
	}
	
	public HbObject get(String name) throws HbUndefinedNameError {
		if(names.containsKey(name))
			return objSpace.get(names.get(name));
		else
			throw new HbUndefinedNameError(objSpace,name);
	}
	
	public void assign(String name, HbObject val) throws HbReadOnlyError {
		// ensure this name is not read-only
		if(isReadOnly(name))
			throw new HbReadOnlyError(objSpace,name);
		else
			doAssign(name,val);
	}
	
	public void assignForce(String name, HbObject val) {
		doAssign(name,val);
	}
	
	private void doAssign(String name, HbObject val) {
		// get id of whatever is already there
		Integer prevId = null;
		if(names.containsKey(name))
			prevId = names.get(name);
		// set
		names.put(name,val.getId());
		objSpace.incRefs(val.getId());
		// garbage collect on overwritten object
		if(prevId != null)
			objSpace.garbageCollect(prevId);
	}
	
	public void assignGlobal(String name, HbObject val) throws HbReadOnlyError {
		assign(name,val);
		globals.add(name);
	}
	
	public void assignGlobalForce(String name, HbObject val) {
		assignForce(name,val);
		globals.add(name);
	}
	
	public void delete(String name) throws ReadOnlyNameException,
												UndefinedNameException {
		if(readOnlys.contains(name))
			throw new ReadOnlyNameException(name);
		else if(isDefined(name)) {
			int deletedId = names.get(name);
			objSpace.decRefs(deletedId);
			objSpace.garbageCollect(deletedId);
			names.remove(name);
		} else
			throw new UndefinedNameException(name);
	}
	
	public boolean isDefined(String name) {
		return names.containsKey(name);
	}
	
	private boolean isReadOnly(String name) {
		return readOnlys.contains(name) ||
				objSpace.getBuiltinClasses().contains(name);
	}
	
}
