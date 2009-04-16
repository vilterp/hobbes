package hobbes.parser;

public class Token {
	
	private String value;
	private SourceLocation location;
	private TokenType type;
	
	public Token(SourceLocation loc, String val, TokenType t) {
		value = val;
		type = t;
		location = loc;
	}
	
	public String toString() {
		return "token["+type+","+value+","+location+"]";
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
