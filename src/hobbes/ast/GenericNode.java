package hobbes.ast;

import java.util.ArrayList;

public class GenericNode implements ObjectNode {
	
	private ExpressionNode type;
	private ArrayList<ObjectNode> generics;
	
	public GenericNode(ExpressionNode t, ArrayList<ObjectNode> g) {
		type = t;
		generics = g;
	}
	
	public String toString() {
		return type + "<"
				+ generics.toString().substring(1, generics.toString().length()-1)
				+ ">";
	}
	
}
