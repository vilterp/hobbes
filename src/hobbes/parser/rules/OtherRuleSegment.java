package hobbes.parser.rules;

public class OtherRuleSegment implements RuleSegment {
	
	private String name;
	
	public OtherRuleSegment(String n) {
		name = n;
	}
	
	public String getRuleName() {
		return name;
	}
	
	public String toString() {
		return "OtherRuleSegment["+name+"]";
	}
	
}
