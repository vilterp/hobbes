package hobbes.ast;

import java.util.ArrayList;

import hobbes.parser.Token;

public class MethodDefNode implements DefNode {
	
	private Token name;
	private ArrayList<VariableNode> args;
	private BlockNode block;
	
	public MethodDefNode(Token n, ArgsSpecNode a, BlockNode b) {
		name = n;
		args = a.getVars();
		block = b;
	}
	
	public String toString() {
		return "def "
				+ name.getValue()
				+ "(" + "(" + args + ")" + ","
				+ block + ")";
	}
	
	public String getName() {
		return name.getValue();
	}
	
	public Token getNameToken() {
		return name;
	}
	
	public ArrayList<VariableNode> getArgs() {
		return args;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
}
