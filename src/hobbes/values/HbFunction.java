package hobbes.values;

import java.util.ArrayList;

import hobbes.ast.FunctionDefNode;
import hobbes.ast.BlockNode;
import hobbes.ast.VariableNode;
import hobbes.interpreter.ObjectSpace;

public class HbFunction extends HbValue {
	
	private String name;
	private BlockNode block;
	private ArrayList<VariableNode> args;
	
	public HbFunction(ObjectSpace o, FunctionDefNode f) {
		super(o);
		name = f.getName();
		block = f.getBlock();
		args = f.getArgs();
	}
	
	public BlockNode getBlock() {
		return block;
	}
	
	public ArrayList<VariableNode> getArgs() {
		return args;
	}
	
	public String getName() {
		return name;
	}

	public HbString getType() {
		return new HbString(getObjSpace(),"Function");
	}
	
	public HbBoolean is(HbValue other) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public HbString show() {
		return new HbString(getObjSpace(),"<Function " + name
				+ "(" + args.toString().substring(1,args.toString().length()-1) + ")"
				+ ">");
	}
	
}
