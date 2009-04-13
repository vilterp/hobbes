package hobbes.parser.syntaxtree;

import java.util.ArrayList;
import hobbes.lang.HbObject;

public class MethodCallNode implements SyntaxNode {
	
	private String methodName;
	private ArrayList<ArgumentNode> arguments;
	
	public MethodCallNode(String name) {
		methodName = name;
	}
	
	// TODO: interface to add arguments
	
	public HbObject evaluate() {
		// TODO Auto-generated method stub
		return null;
	}

}
