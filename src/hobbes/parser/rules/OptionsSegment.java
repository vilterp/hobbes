package hobbes.parser.rules;

import java.util.ArrayList;
import java.util.Iterator;

public class OptionsSegment implements RuleSegment {
	
	private ArrayList<Rule> options;
	
	public OptionsSegment() {
		options = new ArrayList<Rule>();
	}
	
	public void addOption(Rule option) {
		options.add(option);
	}
	
	public String toString() {
		String ans = "OptionsSegment[";
		Iterator<Rule> it = options.iterator();
		while(it.hasNext()) {
			ans += it.next().getSegments().toString();
			if(it.hasNext())
				ans += "|";
		}
		return ans + "]";
	}
	
}
