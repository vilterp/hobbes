package hobbes.values;

import hobbes.interpreter.Interpreter;

@HobbesClass(name="Error")
public class HbError extends HbObject {
	
	private String errorMessage;
	
	public HbError(Interpreter i) {
		super(i);
		errorMessage = null;
	}
	
	public HbError(Interpreter i, String m) {
		super(i);
		errorMessage = m;
	}
	
	protected HbArgumentError getNoMessageError() {
		return new HbArgumentError(getInterp(),
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
		return new HbString(getInterp(),ans);
	}
	
}
