package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="NativeFunction")
public class HbNativeFunction extends HbNamedFunction {
	
	private String name;
	private ArrayList<String> args;
	
	public HbNativeFunction(Interpreter o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getInterp(),
				"Can't make a new native function");
	}
	
	public HbNativeFunction(Interpreter i, String n, ArrayList<String> a) {
		super(i);
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
		return new HbString(getInterp(),repr);
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
}
