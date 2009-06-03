package hobbes.values;

import java.util.ArrayList;

import hobbes.ast.BlockNode;
import hobbes.ast.MethodDefNode;
import hobbes.ast.VariableNode;

public class HbNormalMethod implements HbMethod {
	
	private String name;
	private BlockNode block;
	private ArrayList<VariableNode> args;
	
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
	
	public ArrayList<VariableNode> getArgs() {
		return args;
	}
	
}
