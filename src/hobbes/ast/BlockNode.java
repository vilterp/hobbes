package hobbes.ast;

import hobbes.parser.SourceLine;

import java.util.ArrayList;
import java.util.Iterator;

public class BlockNode implements SyntaxNode, Iterable<SyntaxNode> {
	
	private SourceLine firstLine;
	private ArrayList<SyntaxNode> lines;
	
	public BlockNode(SourceLine fl, ArrayList<SyntaxNode> ls) {
		lines = ls;
		if(lines.size() > 0)
			firstLine = lines.get(0).getLine();
		else
			firstLine = fl;
	}
	
	public BlockNode(SyntaxNode l) {
		ArrayList<SyntaxNode> ls = new ArrayList<SyntaxNode>();
		ls.add(l);
		firstLine = l.getLine();
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
	
	public SourceLine getLine() {
		return firstLine;
	}
	
}
