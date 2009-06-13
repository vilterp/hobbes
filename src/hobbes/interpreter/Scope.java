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
	private Interpreter interp;
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
	
	public Scope(Interpreter i) {
		this(i,null);
	}
	
	public Scope(Interpreter i, Scope inheritGlobalsFrom) {
		interp = i;
		objSpace = interp.getObjSpace();
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
			throw new HbUndefinedNameError(interp,name);
	}
	
	public void assign(String name, HbObject val) throws HbReadOnlyError {
		// ensure this name is not read-only
		if(isReadOnly(name))
			throw new HbReadOnlyError(interp,name);
		else
			doAssign(name,val);
	}
	
	public void assignForce(String name, HbObject val) {
		doAssign(name,val);
	}
	
	private void doAssign(String name, HbObject val) {
		// get id of whatever is already there
		if(names.containsKey(name))
			objSpace.get(names.get(name)).decRefs();
		// set
		names.put(name,val.getId());
		val.incRefs();
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
	
	public HashMap<String,HbObject> getContents() {
		HashMap<String,HbObject> contents = new HashMap<String,HbObject>();
		for(String name: names.keySet())
			if(!globals.contains(name))
				contents.put(name,objSpace.get(names.get(name)));
		return contents;
	}
	
}
