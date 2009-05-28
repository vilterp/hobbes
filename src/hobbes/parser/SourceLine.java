package hobbes.parser;

public class SourceLine {
	
	private String code;
	private int lineNo;
	private String fileName;
	
	public SourceLine(String c, String file, int line) {
		code = c;
		lineNo = line;
		fileName = file;
	}
	
	public String toString() {
		return "SourceLine["
				+ fileName + ":"
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
	
	public String getFileName() {
		return fileName;
	}
	
}
