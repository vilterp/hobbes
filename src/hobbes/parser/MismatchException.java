package hobbes.parser;

public class MismatchException extends Exception {
	
	private Token found;
	private String expected;
	
	public MismatchException(Token f, String e) {
		super("expected "+e+", found "+f.getValue());
		found = f;
		expected = e;
		// TODO: show line with pointer
		// this should really be some kind of HobbesException
	}
	
}
