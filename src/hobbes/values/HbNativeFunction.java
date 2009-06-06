package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="NativeFunction")
public class HbNativeFunction extends HbFunction {
	
	private String name;
	private ArrayList<String> args;
	
	public HbNativeFunction(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getObjSpace(),
				"Can't make a new native function");
	}
	
	public HbNativeFunction(ObjectSpace o, String n, ArrayList<String> a) {
		super(o);
		name = n;
		args = a;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getArgs() {
		return args;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		StringBuilder repr = new StringBuilder("<NativeFunction ");
		repr.append(name);
		repr.append("(");
		Iterator<String> it = args.iterator();
		while(it.hasNext()) {
			repr.append(it.next());
			if(it.hasNext())
				repr.append(",");
		}
		repr.append(")>");
		return new HbString(getObjSpace(),repr);
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
}
