package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class StringNode implements AtomNode {
	
	private Token origin;
	
	public StringNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "\"" + sanitizedValue() + "\"";
	}
	
	public String sanitizedValue() {
		return origin.getValue()
				.replaceAll("\n", "\\\\n")
				.replaceAll("\t", "\\\\t")
				.replaceAll("\"", "\\\"");
	}
	
	public String getValue() {
		return origin.getValue();
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
