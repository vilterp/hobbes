package hobbes.parser;

public class SourceLine {
	
	private String code;
	private int lineNo;
	private String filePath;
	
	public SourceLine(String c, int line, String file) {
		code = c;
		lineNo = line;
		filePath = file;
	}
	
	public String toString() {
		return "SourceLine["
				+ filePath + ":"
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
	
}
