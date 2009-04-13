package hobbes.parser.rules;

import java.util.regex.*;

public class RegexSegment implements RuleSegment {
	
	private Pattern pattern;
	
	public RegexSegment(Pattern patt) {
		pattern = patt;
	}
	
	public String toString() {
		return "RegexSegment["+pattern.toString()+"]";
	}

	public MatchResult matchAgainst(String string) {
		Matcher matcher = pattern.matcher(string);
		if(matcher.lookingAt())
			return matcher.toMatchResult();
		else
			return null;
	}

}
