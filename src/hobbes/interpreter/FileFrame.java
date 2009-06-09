package hobbes.interpreter;

import hobbes.parser.SourceLine;

public class FileFrame extends ExecutionFrame {
	
	private String name;
	private SourceLine currentLine;
	
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

	public void setCurrentLine(SourceLine line) {
		currentLine = line;
	}

}
