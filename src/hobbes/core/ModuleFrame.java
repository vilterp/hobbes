package hobbes.core;

public class ModuleFrame extends ExecutionFrame implements ShowableFrame {
	
	private String name;
	
	public ModuleFrame(ObjectSpace o, String n) {
		super(new Scope(o));
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public String show() {
		return "  in " + name;
	}

}
