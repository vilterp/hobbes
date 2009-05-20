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
		return "["+ sanitizedValue() + ":" + type + "@" + location + "]";
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
	
	public SourceSpan getSourceSpan() {
		return location;
	}
	
	public SourceLocation getStart() {
		return location.getStart();
	}

	public SourceLocation getEnd() {
		return location.getEnd();
	}
	
	public Token mergeWith(Token other) {
		return new Token(value + other.getValue(),type,
							new SourceSpan(location.getStart(),other.getEnd()));
	}
	
}
