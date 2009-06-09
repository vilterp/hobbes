package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class RegexNode implements AtomNode {
	
	private Token origin;
	
	public RegexNode(Token t) {
		origin = t;
	}
	
	public String toString() {
		return "/" + sanitizedValue() + "/";
	}
	
	public String sanitizedValue() {
		return origin.getValue()
				.replaceAll("\n", "\\\\n")
				.replaceAll("\t", "\\\\t")
				.replaceAll("\"", "\\\"");
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
