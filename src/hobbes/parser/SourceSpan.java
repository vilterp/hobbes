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
	
	public String show() {
		if(!start.getLine().equals(end.getLine()))
			throw new UnsupportedOperationException("only works with same line");
		String ans = start.getLine().getCode() + "\n";
		for(int i=0; i < start.getPosition(); i++)
			ans += " ";
		for(int i=0; i < end.getPosition() - start.getPosition(); i++)
			ans += "*";
		return ans;
	}
	
	public SourceLocation getStart() {
		return start;
	}
	
	public SourceLocation getEnd() {
		return end;
	}
	
}
