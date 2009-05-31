package hobbes.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ImportNode implements StatementNode {
	
	private ArrayList<VariableNode> path;
	private HashSet<VariableNode> names;
	
	public ImportNode(ArrayList<VariableNode> p, HashSet<VariableNode> n) {
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
	
}
