package hobbes.ast;

import hobbes.parser.SourceLine;

import java.util.HashMap;
import java.util.Iterator;

public class DictNode implements AtomNode {
	
	private SourceLine line;
	private HashMap<ExpressionNode,ExpressionNode> elements;
	
	public DictNode(SourceLine l, HashMap<ExpressionNode,ExpressionNode> elems) {
		line = l;
		elements = elems;
	}
	
	public DictNode(SourceLine l) {
		this(l,new HashMap<ExpressionNode,ExpressionNode>());
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
	
	public SourceLine getLine() {
		return line;
	}
	
	public HashMap<ExpressionNode,ExpressionNode> getElements() {
		return elements;
	}
	
}
