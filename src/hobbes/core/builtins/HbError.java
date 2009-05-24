package hobbes.core.builtins;

import java.util.ArrayList;

import hobbes.core.ObjectSpace;
import hobbes.core.ShowableFrame;
import hobbes.parser.SourceLocation;

public class HbError extends HbObject {
	
	private String message;
	private SourceLocation location;
	private ArrayList<ShowableFrame> trace;
	
	public HbError(ObjectSpace o, SourceLocation l, String msg) {
		super(o);
		message = msg;
		location = l;
		trace = new ArrayList<ShowableFrame>();
	}
	
	public String getName() {
		return "Error";
	}
	
	public String toString() {
		return "<Error message=\"" + message + "\">";
	}
	
	public String getMessage() {
		return message;
	}
	
	public void printStackTrace() {
		System.err.println(getName() + ": " + getMessage());
		System.err.println(location.show());
		for(ShowableFrame frame: trace) {
			System.err.println(frame.show());
		}
	}
	
	public void addFrame(ShowableFrame e) {
		trace.add(e);
	}
	
}
