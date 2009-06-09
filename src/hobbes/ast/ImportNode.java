package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ImportNode implements StatementNode {
	
	private Token origin;
	private ArrayList<VariableNode> path;
	private HashSet<VariableNode> names;
	
	public ImportNode(Token o, ArrayList<VariableNode> p, HashSet<VariableNode> n) {
		origin = o;
		path = p;
		names = n;
	}
	
	public ImportNode(ArrayList<VariableNode> p) {
		path = p;
		names = null;
	}
	
	public String toString() {
		String ans = "import(";
		Iterator<VariableNode> it = path.iterator();
		while(it.hasNext()) {
			ans += it.next();
			if(it.hasNext())
				ans += ".";
		}
		if(names != null) {
			if(ans.charAt(ans.length()-1) != '(')
				ans += ".";
			ans += names;
		}
		ans += ")";
		return ans;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
