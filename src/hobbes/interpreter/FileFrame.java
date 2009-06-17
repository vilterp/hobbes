package hobbes.interpreter;

public class FileFrame extends ExecutionFrame {
	
	private String name;
	
	public FileFrame(Interpreter i, String n) {
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
