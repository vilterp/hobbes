package hobbes.parser.rules;

public class OtherRuleSegment implements RuleSegment {
	
	private String name;
	private Rule rule;
	
	public OtherRuleSegment(String n, Rule r) {
		name = n;
		rule = r;
	}
	
	public String toString() {
		return "OtherRuleSegment["+name+"]";
	}
	
}
