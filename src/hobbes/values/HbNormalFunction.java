package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.ast.ArgSpecNode;
import hobbes.ast.ArgsSpecNode;
import hobbes.ast.BlockNode;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="Function")
public class HbNormalFunction extends HbNamedFunction {
	
	private String name;
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;
	
	public HbNormalFunction(Interpreter o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getInterp(),
				"Can't make a function with no parameters");
	}
	
	public HbNormalFunction(Interpreter i, String n, ArrayList<ArgSpecNode> a, BlockNode b) {
		super(i);
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
		return new HbString(getInterp(),repr);
	}
	
	public int getNumArgs() {
		return args.size();
	}
	
	public String getRepr() {
		return name;
	}
	
}
