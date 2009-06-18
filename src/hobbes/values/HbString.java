package hobbes.values;

import java.util.ArrayList;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;
import hobbes.values.HbRange.IterImp;

@HobbesClass(name="String")
public class HbString extends HbObject {
	
	private String value;
	private int iterPos;
	
	public HbString(Interpreter i) {
		this(i,"");
	}
	
	public HbString(Interpreter i, Character val) {
		this(i,val.toString());
	}
	
	public HbString(Interpreter i, String val) {
		super(i);
		value = val;
		iterPos = 0;
	}
	
	public String sanitizedValue() {
		// backslash craziness!
		return value.toString()
				.replaceAll("\n", "\\\\n")
				.replaceAll("\t", "\\\\t")
				.replaceAll("\'", "\\\\'");
	}
	
	public String getValue() {
		return value.toString();
	}
	
	@HobbesMethod(name="clone")
	public HbString hbClone() {
		return getObjSpace().getString(value.toString());
	}
	
	public String toString() {
		return "<String@" + getId() + " val=" + getValue() + ">";
	}
	
	public String show() {
		return "'" + sanitizedValue() + "'";
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		return this;
	}
	
	@HobbesMethod(name="hash_code")
	public HbInt defaultHashCode() {
		return getObjSpace().getInt(value.toString().hashCode());
	}
	
	@HobbesMethod(name="==",numArgs=1)
	public HbObject equalTo(HbObject other) {
		if(other instanceof HbString)
			return getObjSpace().getBool(value.toString().equals(((HbString)other).getValue()));
		else
			return getObjSpace().getFalse();
	}
	
	@HobbesMethod(name=">",numArgs=1)
	public HbObject greaterThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbString)
			return getObjSpace().getBool(value.toString()
						.compareTo(((HbString)other).getValue()) > 0);
		else
			throw new HbArgumentError(getInterp(),">",other,"String");
	}
	
	@HobbesMethod(name="<",numArgs=1)
	public HbObject lessThan(HbObject other) throws HbArgumentError {
		if(other instanceof HbString)
			return getObjSpace().getBool(value.toString()
										.compareTo(((HbString)other).getValue()) < 0);
		else
			throw new HbArgumentError(getInterp(),"<",other,"String");
	}
	
	@HobbesMethod(name="length")
	public HbInt hbLength() {
		return getObjSpace().getInt(length());
	}
	
	public int length() {
		return value.length();
	}
	
	@HobbesMethod(name="+",numArgs=1)
	public HbString plus(HbObject other) throws HbError, ErrorWrapper, Continue, Break {
		return getObjSpace().getString(getValue() + other.realToString());
	}
	
	@HobbesMethod(name="*",numArgs=1)
	public HbString times(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt) {
			StringBuilder newString = new StringBuilder();
			for(int i=0; i < ((HbInt)other).getValue(); i++)
				newString.append(getValue());
			return getObjSpace().getString(newString);
		} else
			throw new HbArgumentError(getInterp(),"*",other,"Int");
	}
	
	@HobbesMethod(name="[]",numArgs=1)
	public HbString hbGet(HbObject index) throws HbError, ErrorWrapper, Continue, Break {
		if(index instanceof HbInt)
			return get(((HbInt)index).getValue());
		else if(index instanceof HbRange && ((HbRange)index).getStart() instanceof HbInt) {
			StringBuilder subString = new StringBuilder();
			IterImp it = ((HbRange)index).iterator();
			while(it.hasNext())
				subString.append(get(((HbInt)it.getNext()).getValue()).getValue());
			return getObjSpace().getString(subString);
		} else
			throw new HbArgumentError(getInterp(),"[]",index,"Int or Range of Int");
	}
	
	public HbString get(int ind) throws HbKeyError {
		if(ind >= 0 && ind < length())
			return getObjSpace().getString(new String(new char[]{value.charAt(ind)}));
		else if(ind < 0) {
			if(-ind <= length())
				return get(length() + ind);
			else
				throw new HbKeyError(getInterp(),ind + " (length: " + length() + ")");
		} else
			throw new HbKeyError(getInterp(),ind + " (length: " + length() + ")");
	}
	
	@HobbesMethod(name="each",numArgs=1)
	public void each(HbObject func) throws ErrorWrapper, HbError, Continue, Break {
		if(func instanceof HbFunction) {
			for(int i=0; i < value.length(); i++)
				getInterp().callFunc((HbFunction)func,new HbObject[]{get(i)},null);
		} else
			throw new HbArgumentError(getInterp(),"each",func,
					"AnonymousFunction, Function, or NativeFunction");
	}
	
	@HobbesMethod(name="iter_has_next")
	public HbObject iterHasNext() {
		return getObjSpace().getBool(iterPos < length());
	}
	
	@HobbesMethod(name="iter_next")
	public HbString iterNext() throws HbKeyError {
		HbString next = get(iterPos);
		iterPos++;
		return next;
	}
	
	@HobbesMethod(name="iter_index")
	public HbInt iterIndex() throws HbKeyError {
		return getObjSpace().getInt(iterPos);
	}
	
	@HobbesMethod(name="iter_rewind")
	public void iterRewind() {
		iterPos = 0;
	}
	
	@HobbesMethod(name="succ")
	public HbString succ() {
		if(isEmpty())
			return this;
		ArrayList<Integer> codePoints = new ArrayList<Integer>(length());
		for(int i=length()-1; i >= 0; i--)
			codePoints.add(value.codePointAt(i));
		succ(codePoints);
		char[] chars = new char[length()];
		for(int i=0; i < length(); i++)
			chars[i] = Character.toChars(codePoints.get(i))[0];
		return new HbString(getInterp(),new String(chars));
	}
	
	@HobbesMethod(name="pred")
	public HbString pred() {
		if(isEmpty())
			return this;
		ArrayList<Integer> codePoints = new ArrayList<Integer>(length());
		for(int i=length()-1; i >= 0; i--)
			codePoints.add(value.codePointAt(i));
		pred(codePoints);
		char[] chars = new char[length()];
		for(int i=0; i < length(); i++)
			chars[i] = Character.toChars(codePoints.get(i))[0];
		return new HbString(getInterp(),new String(chars));
	}
	
	private void succ(ArrayList<Integer> values) {
		succ(values,0);
	}
	
	private void succ(ArrayList<Integer> values, int ind) {
		values.set(ind,values.get(ind)+1);
		if(values.get(ind) > Character.MAX_CODE_POINT) {
			values.set(ind,Character.MIN_CODE_POINT);
			succ(values,1);
		}
	}
	
	private void pred(ArrayList<Integer> values) {
		pred(values,0);
	}
	
	private void pred(ArrayList<Integer> values, int ind) {
		values.set(ind,values.get(ind)-1);
		if(values.get(ind) > Character.MAX_CODE_POINT) {
			values.set(ind,Character.MIN_CODE_POINT);
			succ(values,1);
		}
	}
	
	@HobbesMethod(name="chars")
	public HbList chars() throws HbKeyError {
		HbList toReturn = new HbList(getInterp());
		for(int i=0; i < value.length(); i++)
			toReturn.add(get(i));
		return toReturn;
	}
	
	@HobbesMethod(name="words")
	public HbList words() throws HbArgumentError, HbKeyError {
		return split(getObjSpace().getString(" "));
	}
	
	@HobbesMethod(name="split",numArgs=1)
	public HbList split(HbObject delimeter) throws HbArgumentError, HbKeyError {
		if(delimeter instanceof HbString) {
			String d = ((HbString)delimeter).getValue();
			if(d.equals(""))
				return chars();
			HbList toReturn = new HbList(getInterp());
			int pos = 0;
			StringBuilder buf = new StringBuilder();
			while(pos < value.length()) {
				if(value.substring(pos).startsWith(d)) {
					pos += d.length();
					toReturn.add(getObjSpace().getString(buf.toString()));
					buf = new StringBuilder();
				} else {
					buf.append(value.charAt(pos));
					pos++;
				}
			}
			if(buf.length() > 0)
				toReturn.add(getObjSpace().getString(buf.toString()));
			return toReturn;
		} else
			throw new HbArgumentError(getInterp(),"split",delimeter,"String");
	}
	
	@HobbesMethod(name="empty?")
	public HbObject hbIsEmpty() {
		return getObjSpace().getBool(isEmpty());
	}
	
	public boolean isEmpty() {
		return value.length() == 0;
	}
	
	@HobbesMethod(name="toBool")
	public HbObject toBool() {
		return getObjSpace().getBool(!isEmpty());
	}
	
	@HobbesMethod(name="lstrip")
	public HbString lstrip() {
		if(value.length() == 0)
			return this;
		int pos = 0;
		while(Character.isWhitespace(value.charAt(pos)))
			pos++;
		return getObjSpace().getString(value.substring(pos));
	}
	
	@HobbesMethod(name="rstrip")
	public HbString rstrip() {
		if(value.length() == 0)
			return this;
		int pos = value.length()-1;
		while(Character.isWhitespace(value.charAt(pos)))
			pos--;
		return getObjSpace().getString(value.substring(0,pos+1));
	}
	
	@HobbesMethod(name="strip")
	public HbString strip() {
		return lstrip().rstrip();
	}
	
	@HobbesMethod(name="starts_with?",numArgs=1)
	public HbObject startsWith(HbObject str) throws HbArgumentError {
		if(str instanceof HbString)
			return getObjSpace().getBool(value.startsWith(((HbString)str).getValue()));
		else
			throw new HbArgumentError(getInterp(),"starts_with?",str,"String");	
	}
	
	@HobbesMethod(name="ends_with?",numArgs=1)
	public HbObject endsWith(HbObject str) throws HbArgumentError {
		if(str instanceof HbString)
			return getObjSpace().getBool(value.endsWith(((HbString)str).getValue()));
		else
			throw new HbArgumentError(getInterp(),"ends_with?",str,"String");	
	}
	
}
