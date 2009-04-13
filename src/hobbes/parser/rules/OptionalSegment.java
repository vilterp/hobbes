package hobbes.parser.rules;

public class OptionalSegment implements RuleSegment {
	
	private Rule optional;
	
	public OptionalSegment(Rule r) {
		optional = r;
	}
	
	public String toString() {
		return "OptionalSegment"+optional.getSegments().toString();
	}
	
}
