package hobbes.interpreter;

public class ModuleFrame extends ExecutionFrame {
	
	private String name;
	
	public ModuleFrame(ObjectSpace o, String n) {
		super(new Scope(o));
		// getScope().addBasics();
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public String show() {
		return "  in " + name;
	}

}
