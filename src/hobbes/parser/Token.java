package hobbes.parser;

public class Token {
	
	private String value;
	private TokenType type;
	private SourceSpan location;
	
	public Token(String val, TokenType t, SourceSpan loc) {
		value = val;
		type = t;
		location = loc;
	}
	
	public String toString() {
		return "["+ (type == TokenType.TAB ? "" : sanitizedValue() + ":")
				+ type + "@" + location + "]";
	}
	
	public String sanitizedValue() {
		return value.replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t");
	}
	
	public String getValue() {
		return value;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public SourceSpan getLocation() {
		return location;
	}
	
	public SourceLocation getStart() {
		return location.getStart();
	}

	public SourceLocation getEnd() {
		return location.getEnd();
	}
	
}
