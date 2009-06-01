package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbClass extends HbObject {
	
	String name;

	public HbClass(ObjectSpace o, String n) {
		super(o);
		name = n;
	}
	
	public HbClass(ObjectSpace o) {
		super(o);
		name = "Class";
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public HbString show() {
		StringBuilder ans = new StringBuilder("<Class ");
		ans.append(name);
		ans.append(">");
		return new HbString(getObjSpace(),ans);
	}

}
