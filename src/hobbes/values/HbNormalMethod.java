package hobbes.values;

import hobbes.ast.BlockNode;
import hobbes.ast.MethodDefNode;

public class HbNormalMethod {
	
	private String name;
	private BlockNode block;
	private MethodDefNode def;
	
	public HbNormalMethod(String n, BlockNode b, MethodDefNode d) {
		name = n;
		block = b;
		def = d;
	}

	public String getName() {
		return name;
	}

	public BlockNode getBlock() {
		return block;
	}

	public MethodDefNode getDef() {
		return def;
	}
	
}
