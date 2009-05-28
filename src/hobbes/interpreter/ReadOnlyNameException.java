package hobbes.interpreter;

public class ReadOnlyNameException extends Exception {
	
	private String theName;
	
	public ReadOnlyNameException(String n) {
		theName = n;
	}
	
	public String getNameInQuestion() {
		return theName;
	}
	
}
