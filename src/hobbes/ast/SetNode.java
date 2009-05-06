package hobbes.ast;

import java.util.HashSet;
import java.util.Iterator;

public class SetNode implements ObjectNode {
	
	private HashSet<ExpressionNode> elements;
	
	public SetNode(HashSet<ExpressionNode> elems) {
		elements = elems;
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
	
}
