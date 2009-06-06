package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.ast.ArgSpecNode;
import hobbes.ast.ArgsSpecNode;
import hobbes.ast.BlockNode;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Function")
public class HbNormalFunction extends HbFunction {
	
	private String name;
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;
	
	public HbNormalFunction(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getObjSpace(),
				"Can't make a function with no parameters");
	}
	
	public HbNormalFunction(ObjectSpace o, String n, ArrayList<ArgSpecNode> a, BlockNode b) {
		super(o);
		name = n;
		args = a;
		block = b;
	}

	public String getName() {
		return name;
	}

	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}

	public BlockNode getBlock() {
		return block;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		StringBuilder repr = new StringBuilder("<Function ");
		repr.append(name);
		repr.append("(");
		Iterator<ArgSpecNode> it = args.iterator();
		while(it.hasNext()) {
			repr.append(it.next());
			if(it.hasNext())
				repr.append(",");
		}
		repr.append(")>");
		return new HbString(getObjSpace(),repr);
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
}
