package hobbes.ast;

import java.util.HashMap;
import java.util.Iterator;

public class DictNode implements ObjectNode {
	
	private HashMap<ExpressionNode,ExpressionNode> elements;
	
	public DictNode(HashMap<ExpressionNode,ExpressionNode> elems) {
		elements = elems;
	}
	
	public DictNode() {
		elements = new HashMap<ExpressionNode,ExpressionNode>();
	}
	
	public String toString() {
		String ans = "{";
		Iterator<ExpressionNode> it = elements.keySet().iterator();
		while(it.hasNext()) {
			ExpressionNode key = it.next();
			ans += key;
			ans += ": ";
			ans += elements.get(key);
			if(it.hasNext())
				ans += ", ";
		}
		ans += "}";
		return ans;
	}
	
}
