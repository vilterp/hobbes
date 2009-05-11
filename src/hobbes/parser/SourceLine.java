package hobbes.parser;

public class SourceLine {
	
	private String code;
	private int lineNo;
	private String filePath;
	
	public SourceLine(String c, int line) {
		code = c;
		lineNo = line;
		filePath = null;
	}
	
	public String toString() {
		return "SourceLine[" + lineNo + ":" + code + "]";
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
	
}
