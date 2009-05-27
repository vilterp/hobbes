package hobbes.ast;

import java.util.ArrayList;

import hobbes.parser.Token;

public class FunctionDefNode implements StatementNode {
	
	private Token name;
	private ArrayList<VariableNode> args;
	private BlockNode block;
	
	public FunctionDefNode(Token n, ArrayList<VariableNode> a, BlockNode b) {
		name = n;
		args = a;
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
