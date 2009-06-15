package hobbes.values;

import java.util.Iterator;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="Range")
public class HbRange extends HbObject {
	
	private HbObject start;
	private HbObject end;
	
	public HbRange(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),"the Range constructor " +
				"needs start and end parameters");
	}
	
	public HbRange(Interpreter i, HbObject s, HbObject e)
									throws ErrorWrapper, HbError, Continue, Break {
		super(i);
		// these should be before the errors.
		// weird GC problems otherwise
		start = s;
		end = e;
		start.incRefs();
		end.incRefs();
		if(s.getHbClass() != e.getHbClass())
			throw new HbArgumentError(getInterp(),"start and end are of different classes ("
					+ s.getHbClass().getName() + " and " + e.getHbClass().getName()
					+ ", respectively)");
		if(!s.getHbClass().hasMethod("succ"))
			// woo duck typing (...?)
			throw new HbArgumentError(getInterp(),s.getHbClass().getName()
					+ "s don't have a \"succ\" method, which Range needs");
		if(s.gt(end))
			throw new HbArgumentError(getInterp(),"starting value greater than ending value");
	}
	
	public int[] contentAddrs() {
		return new int[]{start.getId(),end.getId()};
	}
	
	@HobbesMethod(name="start")
	public HbObject getStart() {
		return start;
	}
	
	@HobbesMethod(name="end")
	public HbObject getEnd() {
		return end;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() throws ErrorWrapper, HbError, Continue, Break {
		StringBuilder repr = new StringBuilder();
		repr.append("<Range ");
		repr.append(start.realToString());
		repr.append(" to ");
		repr.append(end.realToString());
		repr.append(">");
		return new HbString(getInterp(),repr);
	}
	
	@HobbesMethod(name="each",numArgs=1)
	public void each(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			for(HbObject cur=start; cur.lt(end) || cur.eq(end); cur=cur.call("succ"))
				getInterp().callFunc((HbFunction)func,new HbObject[]{cur},null);
		} else
			throw new HbArgumentError(getInterp(),"each",func,
					"AnonymousFunction, Function, or NativeFunction");
	}
	
	@HobbesMethod(name="toList")
	public HbList toList() throws ErrorWrapper, HbError, Continue, Break {
		HbList toReturn = new HbList(getInterp());
		for(HbObject cur=start; cur.lt(end) || cur.eq(end); cur=cur.call("succ"))
			toReturn.add(cur);
		return toReturn;
	}
	
	public IterImp iterator() {
		return new IterImp(start,end);
	}
	
	public class IterImp {
		
		private HbObject cur;
		private HbObject end;
		
		public IterImp(HbObject c, HbObject e) {
			cur = c;
			end = e;
		}
		
		public boolean hasNext() throws ErrorWrapper, HbError, Continue, Break {
			return cur.lt(end) || cur.eq(end);
		}
		
		public HbObject getNext() throws ErrorWrapper, HbError, Continue, Break {
			HbObject temp = cur;
			cur = cur.call("succ");
			return temp;
		}
		
	}
	
}
