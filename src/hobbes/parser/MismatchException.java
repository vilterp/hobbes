package hobbes.parser;

public class MismatchException extends Exception {
	
	private Token found;
	private String expected;
	
	public MismatchException(Token f, String e) {
		super("expected "+e+", found "+f.getValue());
		found = f;
		expected = e;
		// TODO: show line with pointer - will require capturing line...
	}
	
	public Token getFound() {
		return found;
	}
	
	public String getExpected() {
		return expected;
	}
	
}
