package hobbes.ast;

import java.util.ArrayList;

public class AnonymousFunctionNode implements AtomNode {
	
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;
	
	public AnonymousFunctionNode(ArgsSpecNode a, BlockNode b) {
		args = a.getArgs();
		block = b;
	}
	
	public String toString() {
		return "|" + args + "|"
				+ "{" + block + "}";
	}
	
	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
}
