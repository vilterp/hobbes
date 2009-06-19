package hobbes.values;

import java.util.ArrayList;
import java.util.Iterator;

import hobbes.values.HbArgumentError;
import hobbes.values.HbObject;
import hobbes.ast.BlockNode;
import hobbes.ast.ArgSpecNode;
import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="AnonymousFunction")
public class HbAnonymousFunction extends HbFunction {
	
	private ArrayList<ArgSpecNode> args;
	private BlockNode block;

	public HbAnonymousFunction(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),
				"Can't make an anonymous function with no parameters");
	}
	
	public HbAnonymousFunction(Interpreter i, ArrayList<ArgSpecNode> a, BlockNode b) {
		super(i);
		args = a;
		block = b;
	}

	public ArrayList<ArgSpecNode> getArgs() {
		return args;
	}

	public BlockNode getBlock() {
		return block;
	}
	
	public String getRepr() throws ErrorWrapper, HbError, Continue, Break {
		return realShow();
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		StringBuilder repr = new StringBuilder("<AnonymousFunction(");
		Iterator<ArgSpecNode> it = args.iterator();
		while(it.hasNext()) {
			repr.append(it.next());
			if(it.hasNext())
				repr.append(",");
		}
		repr.append(')');
		repr.append('@');
		repr.append(getId());
		repr.append('>');
		return getObjSpace().getString(repr);
	}
	
	public int getNumArgs() {
		return args.size();
	}

}
