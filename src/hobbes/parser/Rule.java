package hobbes.parser;

import java.util.ArrayList;
import java.util.Iterator;

public class Rule implements Iterable<RuleSegment> {
	
	private ArrayList<RuleSegment> segments;
	
	public Rule(ArrayList<RuleSegment> segs) {
		segments = segs;
	}
	
	public Rule() {
		segments = new ArrayList<RuleSegment>();
	}
	
	public void addSegment(RuleSegment segment) {
		segments.add(segment);
	}

	public Iterator<RuleSegment> iterator() {
		return segments.iterator();
	}
	
}
