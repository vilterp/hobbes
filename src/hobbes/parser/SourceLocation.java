package hobbes.parser;

public class SourceLocation {
	
	private SourceLine line;
	private int position;
	
	public SourceLocation(SourceLine l, int pos) {
		line = l;
		position = pos;
	}
	
	public String toString() {
		return line.getLineNo() + ":" + position;
	}
	
	public String show() {
		String ans = " " + line.getCode() + "\n";
		for(int i=0; i < position; i++)
			ans += " ";
		ans += "^";
		return ans;
	}
	
	public int getPosition() {
		return position;
	}
	
}
