package hobbes.core;

public class ModuleFrame extends ExecutionFrame implements ShowableFrame {
	
	private String name;
	
	public ModuleFrame(String n) {
		super(null);
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public String show() {
		return "  in " + name;
	}

}
