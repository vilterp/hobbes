package hobbes.values;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Error")
public class HbError extends HbObject {
	
	private String errorMessage;
	
	public HbError(ObjectSpace o) {
		super(o);
		errorMessage = null;
	}
	
	public HbError(ObjectSpace o, String m) {
		super(o);
		errorMessage = m;
	}
	
	protected HbArgumentError getNoMessageError() {
		return new HbArgumentError(getObjSpace(),
				getHbClass().getName() + " needs a message");
	}

	public String getMessage() {
		return errorMessage;
	}
	
	@HobbesMethod(name="toString")
	public HbString hbToString() {
		StringBuilder ans = new StringBuilder("<");
		ans.append(getHbClass().getName());
		if(getMessage() != null) {
			ans.append(": \"");
			ans.append(getMessage());
			ans.append("\"");
		}
		ans.append(">");
		return new HbString(getObjSpace(),ans);
	}
	
}
