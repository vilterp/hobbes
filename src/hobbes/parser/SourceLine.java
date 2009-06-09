package hobbes.parser;

public class SourceLine {
	
	private String code;
	private int lineNo;
	private SourceFile file;
	
	public SourceLine(SourceFile f, String c, int line) {
		code = c;
		lineNo = line;
		file = f;
	}
	
	public String toString() {
		return "SourceLine["
				+ file + ":"
				+ lineNo + ":"
				+ code + "@"
				+ hashCode()
				+"]";
	}
	
	public boolean equals(SourceLine other) {
		return lineNo == other.getLineNo() &&
			   code.equals(other.getCode());
	}
	
	public String getCode() {
		return code;
	}
	
	public int getLineNo() {
		return lineNo;
	}
	
	public SourceLocation getEnd() {
		return new SourceLocation(this,code.length()-1);
	}
	
	public SourceFile getFile() {
		return file;
	}
	
	public String show() {
		return lineNo + ": " + code;
	}
	
}
