package hobbes.interpreter;

import java.util.ArrayList;

import hobbes.parser.SourceLocation;

public class HbError extends Throwable {
	
	private String errorName;
	private SourceLocation location;
	private ArrayList<ShowableFrame> trace;
	
	public HbError(String n, String m, SourceLocation l) {
		super(m);
		errorName = n;
		location = l;
		trace = new ArrayList<ShowableFrame>();
	}
	
	public HbError(String n, SourceLocation l) {
		this(n,null,l);
	}
	
	public void addFrame(ShowableFrame f) {
		trace.add(f);
	}
	
	public void printStackTrace() {
		System.err.print(errorName);
		if(getMessage() != null)
			System.err.print(": " + getMessage());
		System.err.println();
		System.err.println(location.show());
		for(ShowableFrame f: trace)
			System.err.println(f.show());
	}
	
	public String getName() {
		return errorName;
	}
	
}
