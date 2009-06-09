package hobbes.ast;

import hobbes.parser.SourceLine;

import java.util.ArrayList;

public class AnonymousFunctionNode implements AtomNode {
	
	private SourceLine line;
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;
	
	public AnonymousFunctionNode(ArgsSpecNode a, BlockNode b) {
		args = a.getArgs();
		line = a.getLine();
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
	
	public SourceLine getLine() {
		return line;
	}
	
}
