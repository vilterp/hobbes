package hobbes.values;

import java.util.HashMap;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Object")
public class HbObject extends Throwable {
	
	private int id;
	private int numRefs;
	private HbClass hobbesClass;
	private Interpreter interp;
	
	public HbObject(Interpreter i) {
		interp = i;
		id = getObjSpace().add(this);
		numRefs = 0;
		// get HbClass instance
		if(getClass().isAnnotationPresent(HobbesClass.class)) {
			String className = getClass().getAnnotation(HobbesClass.class).name();
			if(!className.equals("Class"))
				hobbesClass = getObjSpace().getClass(className);
		} else
			throw new IllegalArgumentException("\"" + getClass().getName()
					+ "\" extends HbObject but doesn't have a HbClass annotation");
	}
	
	public void setClass(HbClass c) {
		hobbesClass = c;
	}
	
	public ObjectSpace getObjSpace() {
		return interp.getObjSpace();
	}
	
	public Interpreter getInterp() {
		return interp;
	}
	
	public int[] contentAddrs() {
		return new int[0];
	}
	
	public String toString() {
		return "<" + getHbClass().getName() + "@" + getId() + ">";
	}
	
	public boolean gt(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		return call(">",new HbObject[]{other}) == getObjSpace().getTrue();
	}
	
	public boolean lt(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		return call("<",new HbObject[]{other}) == getObjSpace().getTrue();
	}
	
	public boolean eq(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		return call("==",new HbObject[]{other}) == getObjSpace().getTrue();
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder("<");
		repr.append(getHbClass().getName());
		repr.append("@");
		repr.append(getId());
		repr.append(">");
		return getObjSpace().getString(repr.toString());
	}
	
	@HobbesMethod(name="is_a?",numArgs=1)
	public HbObject isA(HbObject klass) throws HbArgumentError {
		if(klass instanceof HbClass) {
			return getHbClass().descendsFrom(klass);
		} else
			throw new HbArgumentError(getInterp(),"is_a?",klass,"Class");
	}
	
	public String realToString() throws ErrorWrapper, HbError, Continue, Break {
		HbObject repr = call("toString");
		if(repr instanceof HbString)
			return ((HbString)repr).getValue();
		else
			throw new HbTypeError(getInterp(),"toString must return a String");
	}
	
	public String show() throws ErrorWrapper, HbError, Continue, Break {
		return realToString();
	}
	
	public HbObject call(String methodName, HbObject[] args) throws ErrorWrapper, HbError,
																				Continue, Break {
		if(getHbClass().hasMethod(methodName))
			return getInterp().callMethod(this,methodName,args,null);
		else
			throw new HbMissingMethodError(getInterp(),methodName,getHbClass().getName());
	}
	
	public HbObject call(String methodName) throws ErrorWrapper, HbError, Continue, Break {
		return call(methodName, new HbObject[]{});
	}
	
	@HobbesMethod(name="call",numArgs=2)
	public HbObject hbCall(HbObject methodName, HbObject args)
									throws ErrorWrapper, HbError, Continue, Break {
		if(methodName instanceof HbString) {
			if(args instanceof HbList)
				return call(((HbString)methodName).getValue(),
										((HbList)args).toArray());
			else
				throw new HbArgumentError(getInterp(),"call",args,"List");
		} else
			throw new HbArgumentError(getInterp(),"call",methodName,"String");
			
	}
	
	@HobbesMethod(name="to",numArgs=1)
	public HbRange to(HbObject other) throws ErrorWrapper, HbError, Continue, Break {
		return new HbRange(getInterp(),this,other);
	}

	@HobbesMethod(name="hash_code")
	public HbInt defaultHashCode() {
		return getObjSpace().getInt(getId());
	}
	
	public int realHashCode() throws ErrorWrapper, HbError, Continue, Break {
		HbObject result = call("hash_code");
		if(result instanceof HbInt)
			return ((HbInt)result).getValue();
		else
			throw new HbTypeError(getInterp(),"hash_code must return an Int");
	}
	
	public void incRefs() {
		numRefs++;
	}
	
	public void decRefs() {
		numRefs--;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isReferenced() {
		return numRefs > 0;
	}
	
	@HobbesMethod(name="init")
	public void init() {}
	
	@HobbesMethod(name="class")
	public HbClass getHbClass() {
		return hobbesClass;
	}
	
	@HobbesMethod(name="object_id")
	public HbInt objectId() {
		return new HbInt(getInterp(),id);
	}
	
	@HobbesMethod(name="methods")
	public HbSet getMethods() throws ErrorWrapper, HbError, Continue, Break {
		HbSet temp = new HbSet(getInterp());
		for(String methodName: getHbClass().getMethodNames())
			temp.add(getObjSpace().getString(methodName));
		return temp;
	}
	
	@HobbesMethod(name="is",numArgs=1)
	public HbObject is(HbObject other) {
		return getObjSpace().getBool(getId() == other.getId());
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getTrue();
	}
	
	@HobbesMethod(name="or",numArgs=1)
	public HbObject or(HbObject other) {
		if(toBool() == getObjSpace().getTrue())
			return this;
		else if(other.toBool() == getObjSpace().getTrue())
			return other;
		else
			return getObjSpace().getFalse();
	}
	
	@HobbesMethod(name="and",numArgs=1)
	public HbObject and(HbObject other) {
		return getObjSpace().getBool(toBool() == getObjSpace().getTrue()
				&& other.toBool() == getObjSpace().getTrue());
	}
	
}
