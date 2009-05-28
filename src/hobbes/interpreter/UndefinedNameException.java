package hobbes.interpreter;

public class UndefinedNameException extends Exception {
	
	private String theName;
	
	public UndefinedNameException(String name) {
		theName = name;
	}
	
	public String getNameInQuestion() {
		return theName;
	}
	
}
