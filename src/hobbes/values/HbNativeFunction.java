package hobbes.values;

import hobbes.interpreter.ObjectSpace;

public class HbNativeFunction extends HbFunction {
	
	private String name;
	private String[] args;
	
	public HbNativeFunction(ObjectSpace o, String n, String[] p) {
		super(o);
		name = n;
		args = p;
	}

	public HbString getType() {
		return new HbString(getObjSpace(),"NativeFunction");
	}
	
	public HbBoolean is(HbInstance other) {
		if(other.getId() == getId())
			return getObjSpace().getTrue();
		else
			return getObjSpace().getFalse();
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),"<NativeFunction "
				+ name
				+ "("
				+ joinArgsWithCommas()
				+ ")>");
	}
	
	private String joinArgsWithCommas() {
		StringBuilder ans = new StringBuilder();
		for(int i=0; i < args.length; i++)
			ans.append(args[i] + ",");
		return ans.toString().substring(0,ans.toString().length()-1);
	}
	
	public String getName() {
		return name;
	}
	
	public String[] getArgs() {
		return args;
	}

}
