package hobbes.ast;

import hobbes.parser.SourceLine;

import java.util.HashSet;
import java.util.Iterator;

public class SetNode implements AtomNode {
	
	private SourceLine line;
	private HashSet<ExpressionNode> elements;
	
	public SetNode(SourceLine l, HashSet<ExpressionNode> elems) {
		elements = elems;
		line = l;
	}
	
	public String toString() {
		String ans = "{";
		Iterator<ExpressionNode> it = elements.iterator();
		while(it.hasNext()) {
			ans += it.next();
			if(it.hasNext())
				ans += ", ";
		}
		ans += "}";
		return ans;
	}
	
	public HashSet<ExpressionNode> getElements() {
		return elements;
	}
	
	public SourceLine getLine() {
		return line;
	}
	
}
