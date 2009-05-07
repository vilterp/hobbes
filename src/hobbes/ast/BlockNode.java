package hobbes.ast;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockNode implements SyntaxNode {
	
	private ArrayList<SyntaxNode> lines;
	
	public BlockNode(ArrayList<SyntaxNode> ls) {
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
	
}
