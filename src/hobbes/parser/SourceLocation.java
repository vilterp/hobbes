package hobbes.parser;

public class SourceLocation {
	
	private SourceLine line;
	private int position;
	private static final int INDENTATION = 4;
	
	public SourceLocation(SourceLine l, int pos) {
		line = l;
		position = pos;
	}
	
	public String toString() {
		return line.getLineNo() + ":" + position;
	}
	
	public String show() {
		StringBuilder ans = new StringBuilder();
		// indentation
		for(int i=0; i < INDENTATION; i++)
			ans.append(" ");
		// line no and code
		String lineNo = new Integer(line.getLineNo()).toString();
		ans.append(lineNo + ": " + line.getCode() + "\n");
		// spaces before pointer to account for line no
		ans.append("  ");
		for(int i=0; i < lineNo.length(); i++)
			ans.append(" ");
		// indentation
		for(int i=0; i < INDENTATION; i++)
				ans.append(" ");
		// pointer
		for(int i=0; i < position; i++)
			ans.append(" ");
		ans.append("^");
		return ans.toString();
	}
	
	public int getPosition() {
		return position;
	}
	
	public SourceLine getLine() {
		return line;
	}
	
	public SourceLocation next() {
		return new SourceLocation(line,position+1);
	}
	
}
