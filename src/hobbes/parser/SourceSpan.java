package hobbes.parser;

public class SourceSpan {
	
	private SourceLocation start;
	private SourceLocation end;
	
	public SourceSpan(SourceLocation s, SourceLocation e) {
		start = s;
		end = e;
	}
	
	public String toString() {
		return "(" + start + "-" + end + ")";
	}
	
	public SourceLocation getStart() {
		return start;
	}
	
	public SourceLocation getEnd() {
		return end;
	}
	
}
