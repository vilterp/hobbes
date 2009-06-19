package hobbes.values;

import java.util.HashMap;
import java.util.Set;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="Object")
public class HbNormalObject extends HbObject {
	
	private HashMap<String,Integer> instVars;
	
	public HbNormalObject(Interpreter i) {
		super(i);
		instVars = new HashMap<String,Integer>();
	}
	
	public int[] contentAddrs() {
		int[] addrs = new int[instVars.size()];
		int counter = 0;
		for(int i: instVars.values()) {
			addrs[counter] = i;
			counter++;
		}
		return addrs;
	}
	
	public HbObject getInstVar(String name) throws HbUndefinedNameError {
		if(instVars.containsKey(name))
			return getObjSpace().get(instVars.get(name));
		else
			throw new HbUndefinedNameError(getInterp(),name);
	}
	
	public void putInstVar(String name, HbObject obj) {
		if(instVars.containsKey(name))
			getObjSpace().get(instVars.get(name)).decRefs();
		instVars.put(name,obj.getId());
	}
	
	public HashMap<String,HbObject> getInstVars() {
		HashMap<String,HbObject> toReturn = new HashMap<String,HbObject>();
		for(String name: instVars.keySet())
			toReturn.put(name,getObjSpace().get(instVars.get(name)));
		return toReturn;
	}

}
