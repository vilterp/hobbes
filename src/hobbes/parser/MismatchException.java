package hobbes.parser;

public class MismatchException extends Exception {
	
	private Token found;
	private String expected;
	private String line;
	
	public MismatchException(Token f, String e, String l) {
		super("expected "+e+", found "+f.getValue());
		found = f;
		expected = e;
		line = l;
		// TODO: show line with pointer
		// this should really be some kind of HobbesException
	}
	
}
