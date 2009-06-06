package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.values.HbArgumentError;
import hobbes.values.HbObject;
import hobbes.ast.BlockNode;
import hobbes.ast.ArgSpecNode;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="AnonymousFunction")
public class HbAnonymousFunction extends HbFunction {
	
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;

	public HbAnonymousFunction(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getObjSpace(),
				"Can't make an anonymous function with no parameters");
	}
	
	public HbAnonymousFunction(ObjectSpace o, ArrayList<ArgSpecNode> a, BlockNode b) {
		super(o);
		args = a;
		block = b;
	}

	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}

	public BlockNode getBlock() {
		return block;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		StringBuilder repr = new StringBuilder("<AnonymousFunction (");
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
