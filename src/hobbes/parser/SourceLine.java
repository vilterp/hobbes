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
	
	public String getCode() {
		return code;
	}
	
	public int getLineNo() {
		return lineNo;
	}
	
}
