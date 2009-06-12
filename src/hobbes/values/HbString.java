package hobbes.values;

import hobbes.interpreter.Break;
import hobbes.interpreter.Continue;
import hobbes.interpreter.ErrorWrapper;
import hobbes.interpreter.Interpreter;

@HobbesClass(name="String")
public class HbString extends HbObject {
	
	private StringBuilder value;
	
	public HbString(Interpreter i) {
		super(i);
		value = new StringBuilder();
	}
	
	public HbString(Interpreter i, String val) {
		super(i);
		value = new StringBuilder(val);
	}
	
	public HbString(Interpreter i, StringBuilder val) {
		this(i,val.toString());
	}
	
	public HbString(Interpreter i, Character val) {
		this(i,val.toString());
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
		return new HbString(getInterp(),value.toString());
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
	public HbInt length() {
		return getObjSpace().getInt(value.length());
	}
	
	@HobbesMethod(name="+",numArgs=1)
	public HbString plus(HbObject other) throws HbError, ErrorWrapper, Continue, Break {
		return new HbString(getInterp(),getValue() + other.realToString());
	}
	
	@HobbesMethod(name="*",numArgs=1)
	public HbString times(HbObject other) throws HbArgumentError {
		if(other instanceof HbInt) {
			StringBuilder newString = new StringBuilder();
			for(int i=0; i < ((HbInt)other).getValue(); i++)
				newString.append(getValue());
			return new HbString(getInterp(),newString);
		} else
			throw new HbArgumentError(getInterp(),"*",other,"Int");
	}
	
	@HobbesMethod(name="chars")
	public HbList chars() {
		HbList toReturn = new HbList(getInterp());
		for(int i=0; i < value.length(); i++)
			toReturn.add(new HbString(getInterp(),value.charAt(i)));
		return toReturn;
	}
	
	@HobbesMethod(name="words")
	public HbList words() throws HbArgumentError {
		return split(new HbString(getInterp()," "));
	}
	
	@HobbesMethod(name="split",numArgs=1)
	public HbList split(HbObject delimeter) throws HbArgumentError {
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
					toReturn.add(new HbString(getInterp(),buf.toString()));
					buf = new StringBuilder();
				} else {
					buf.append(value.charAt(pos));
					pos++;
				}
			}
			if(buf.length() > 0)
				toReturn.add(new HbString(getInterp(),buf.toString()));
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
	
	@HobbesMethod(name="lstrip!")
	public void lstripInPlace() {
		int pos = 0;
		while(pos < value.length() && Character.isWhitespace(value.charAt(pos)))
			pos++;
		value.delete(0,pos);
	}
	
	@HobbesMethod(name="rstrip!")
	public void rstripInPlace() {
		int pos = value.length()-1;
		while(pos >= 0 && Character.isWhitespace(value.charAt(pos)))
			pos--;
		value.delete(pos+1,value.length());
	}
	
	@HobbesMethod(name="strip!")
	public void stripInPlace() {
		lstripInPlace();
		rstripInPlace();
	}
	
	@HobbesMethod(name="lstrip")
	public HbString lstrip() {
		HbString newString = hbClone();
		newString.lstripInPlace();
		return newString;
	}
	
	@HobbesMethod(name="rstrip")
	public HbString rstrip() {
		HbString newString = hbClone();
		newString.rstripInPlace();
		return newString;
	}
	
	@HobbesMethod(name="strip")
	public HbString strip() {
		HbString newString = hbClone();
		newString.stripInPlace();
		return newString;
	}

}
