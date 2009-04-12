package hobbes.parser;

import java.util.regex.Pattern;

public class RegexSegment implements RuleSegment {
	
	private Pattern pattern;
	
	public RegexSegment(Pattern patt) {
		pattern = patt;
	}
	
	public String toString() {
		return "RegexSegment["+pattern.toString()+"]";
	}
	
	public boolean matches() {
		// TODO Auto-generated method stub
		return false;
	}

}
