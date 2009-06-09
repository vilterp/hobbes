package hobbes.interpreter;

import hobbes.parser.SourceLine;

public class ModuleFrame extends ExecutionFrame {
	
	private String name;
	private SourceLine currentLine;
	
	public ModuleFrame(Interpreter i, String n) {
		super(new Scope(i));
		getScope().addBasics();
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public String show() {
		return "  in " + name + "\n"
				+ "    " + currentLine.show();
	}

	public void setCurrentLine(SourceLine line) {
		currentLine = line;
	}

}
