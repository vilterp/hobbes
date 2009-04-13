package hobbes.parser;

import java.util.ArrayList;

public class RepeatSegment implements RuleSegment {
	
	private Rule repeated;
	
	public RepeatSegment(Rule r) {
		repeated = r;
	}
	
	public String toString() {
		return "RepeatSegment"+repeated.getSegments().toString();
	}

}
