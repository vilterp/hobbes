package hobbes.interpreter;

import java.util.ArrayList;

import hobbes.parser.SourceLocation;
import hobbes.values.HbError;

public class ErrorWrapper extends Exception {
	
	private HbError error;
	private SourceLocation loc;
	private ArrayList<ExecutionFrame> trace;
	
	public ErrorWrapper(HbError e, SourceLocation l) {
		error = e;
		loc = l;
		trace = new ArrayList<ExecutionFrame>();
	}
	
	public HbError getError() {
		return error;
	}
	
	public SourceLocation getLocation() {
		return loc;
	}
	
	public void addFrame(ExecutionFrame f) {
		trace.add(f);
	}

	public void printStackTrace() {
		System.err.print(error.getClassInstance().getName());
		if(error.getMessage() != null)
			System.err.print(": " + error.getMessage());
		System.err.println();
		System.err.println(loc.show());
		for(ExecutionFrame f: trace)
			System.err.println(f.show());
	}
	
}
