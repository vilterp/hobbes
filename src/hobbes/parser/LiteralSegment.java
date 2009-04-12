package hobbes.parser;

public class LiteralSegment implements RuleSegment {
	
	private String value;
	
	public LiteralSegment(String val) {
		value = val;
	}
	
	public String toString() {
		return "LiteralSegment[\""+value+"\"]";
	}
	
	public boolean matches() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
