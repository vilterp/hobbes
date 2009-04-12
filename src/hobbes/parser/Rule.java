package hobbes.parser;

import java.util.ArrayList;
import java.util.Iterator;

public class Rule implements Iterable<RuleSegment> {
	
	private ArrayList<RuleSegment> segments;
	
	public Rule() {
		segments = new ArrayList<RuleSegment>();
	}
	
	public String toString() {
		return "Rule" + segments.toString();
	}
	
	public void addSegment(RuleSegment segment) {
		segments.add(segment);
	}
	
	public ArrayList<RuleSegment> getSegments() {
		return segments;
	}

	public Iterator<RuleSegment> iterator() {
		return segments.iterator();
	}
	
}
