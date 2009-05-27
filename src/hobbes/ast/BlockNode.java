package hobbes.ast;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockNode implements SyntaxNode, Iterable<SyntaxNode> {
	
	private ArrayList<SyntaxNode> lines;
	
	public BlockNode(ArrayList<SyntaxNode> ls) {
		lines = ls;
	}
	
	public BlockNode(SyntaxNode l) {
		ArrayList<SyntaxNode> ls = new ArrayList<SyntaxNode>();
		ls.add(l);
		lines = ls;
	}
	
	public String toString() {
		if(lines.size() == 0)
			return "";
		else if(lines.size() == 1)
			return lines.get(0).toString();
		else {
			String ans = "\n";
			Iterator<SyntaxNode> it = lines.iterator();
			while(it.hasNext()) {
				ans += "  " + it.next();
				if(it.hasNext())
					ans += "\n";
			}
			ans += "\n";
			return ans;
		}
	}
	
	public Iterator<SyntaxNode> iterator() {
		return lines.iterator();
	}
	
}
