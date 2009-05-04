package hobbes.parser;

import java.io.Serializable;

public class SourceLocation implements Serializable {
	
	// TODO: seperate SourcePoint & SourceSpan classes
	// TODO: save file names, line numbers
	
	private int start;
	private int end;
	
	public SourceLocation(int s, int e) {
		start = s;
		end = e;
	}
	
	public String toString() {
		return "("+start+","+end+")";
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
	
}
