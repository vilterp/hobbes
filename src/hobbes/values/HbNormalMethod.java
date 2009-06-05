package hobbes.values;

import java.util.ArrayList;

import hobbes.ast.ArgSpecNode;
import hobbes.ast.BlockNode;
import hobbes.ast.MethodDefNode;

public class HbNormalMethod extends HbMethod {
	
	private String name;
	private BlockNode block;
	private ArrayList<ArgSpecNode> args;
	
	public HbNormalMethod(MethodDefNode def) {
		name = def.getName();
		block = def.getBlock();
		args = def.getArgs();
	}

	public String getName() {
		return name;
	}

	public BlockNode getBlock() {
		return block;
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}
	
	public String getArgName(int ind) {
		return args.get(ind).getVar().getName();
	}
	
}
