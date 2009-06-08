package hobbes.interpreter;

public class ModuleFrame extends ExecutionFrame {
	
	private String name;
	
	public ModuleFrame(Interpreter i, String n) {
		super(new Scope(i));
		getScope().addBasics();
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public String show() {
		return "  in " + name;
	}

}
