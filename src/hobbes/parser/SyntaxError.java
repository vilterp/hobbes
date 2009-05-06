package hobbes.parser;

public class SyntaxError extends Exception {
	
	private SourceLocation location;
	
	public SyntaxError(String msg, SourceLocation loc) {
		super(msg);
		location = loc;
	}
	
	public SourceLocation getLocation() {
		return location;
	}
	
}
