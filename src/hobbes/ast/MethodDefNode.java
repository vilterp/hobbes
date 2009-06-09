package hobbes.ast;

import java.util.ArrayList;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

public class MethodDefNode implements DefNode {
	
	private Token name;
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;
	
	public MethodDefNode(Token n, ArgsSpecNode a, BlockNode b) {
		name = n;
		if(a != null)
			args = a.getArgs();
		else
			args = new ArrayList<ArgSpecNode>();
		block = b;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("def ");
		sb.append(name.getValue());
		sb.append('(');
		sb.append(args);
		sb.append(',');
		sb.append(block);
		sb.append(')');
		return sb.toString();
	}
	
	public String getName() {
		return name.getValue();
	}
	
	public Token getNameToken() {
		return name;
	}
	
	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
	public SourceLine getLine() {
		return name.getLine();
	}
	
}
