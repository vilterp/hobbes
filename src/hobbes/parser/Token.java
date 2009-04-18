package hobbes.parser;

public class Token {
	
	private String value;
	private TokenType type;
	private SourceLocation location;
	
	public Token(String val, TokenType t, SourceLocation loc) {
		value = val;
		type = t;
		location = loc;
	}
	
	public String toString() {
		return "token["+type+":"+value.replaceAll("\n", "\\\\n")+"@"+location+"]";
	}
	
	public String getValue() {
		return value;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public SourceLocation getLocation() {
		return location;
	}
	
}
