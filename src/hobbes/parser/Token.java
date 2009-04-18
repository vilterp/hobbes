package hobbes.parser;

public class Token {
	
	private String value;
	private TokenType type;
	
	public Token(String val, TokenType t) {
		value = val;
		type = t;
	}
	
	public String toString() {
		return "token["+type+","+value.replaceAll("\n", "\\\\n")+"]";
	}
	
	public String getValue() {
		return value;
	}
	
	public TokenType getType() {
		return type;
	}
	
}
