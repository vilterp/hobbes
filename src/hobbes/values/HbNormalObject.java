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
	
	public Set<String> getInstVarNames() {
		return instVars.keySet();
	}

}
