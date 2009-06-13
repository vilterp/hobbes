package hobbes.values;

import java.util.Iterator;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="Range")
public class HbRange extends HbObject implements Iterable<HbObject> {
	
	private HbObject start;
	private HbObject end;
	
	public HbRange(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),"the Range constructor " +
				"needs start and end parameters");
	}
	
	public HbRange(Interpreter i, HbObject s, HbObject e) {
		super(i);
		start = s;
		end = e;
		start.incRefs();
		end.incRefs();
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
	
	public Iterator<HbObject> iterator() {
		return new IterImp(this);
	}
	
	private class IterImp implements Iterator<HbObject> {
		
		private HbRange range;
		private HbObject current;
		
		public IterImp(HbRange r) {
			range = r;
			current = r.getStart();
		}
		
		public boolean hasNext() {
			try {
				return current.call("succ").eq(range.getEnd());
			} catch (ErrorWrapper e) {
				e.printStackTrace();
			} catch (HbError e) {
				e.printStackTrace();
			} catch (Continue e) {
				e.printStackTrace();
			} catch (Break e) {
				e.printStackTrace();
			} finally {
				System.exit(1);
				return false;
			}
		}
		
		public HbObject next() {
			try {
				return current.call("succ");
			} catch (ErrorWrapper e) {
				e.printStackTrace();
			} catch (HbError e) {
				e.printStackTrace();
			} catch (Continue e) {
				e.printStackTrace();
			} catch (Break e) {
				e.printStackTrace();
			} finally {
				System.exit(1);
				return null;
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}
