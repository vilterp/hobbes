package hobbes.values;

import java.util.ArrayList;

import hobbes.ast.ArgSpecNode;
import hobbes.ast.BlockNode;
import hobbes.ast.MethodDefNode;

public class HbNormalMethod extends HbMethod {
	
	private String name;
	private String className;
	private BlockNode block;
	private ArrayList<ArgSpecNode> args;
	
	public HbNormalMethod(String cn, MethodDefNode def) {
		name = def.getName();
		className = cn;
		block = def.getBlock();
		args = def.getArgs();
		for(int i=0; i < args.size(); i++) {
			ArgSpecNode argSpec = args.get(i);
			if(argSpec.getDefault() != null)
				setDefault(i,argSpec.getDefault());
		}
	}

	public String getName() {
		return name;
	}
	
	public String getDeclaringClassName() {
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
