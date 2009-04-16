package hobbes.parser;

public class UnexpectedEOL extends Exception {
	
	public Character expected;
	
	public UnexpectedEOL(char exp) {
		super();
		expected = exp;
	}
	
	public char getExpected() {
		return expected;
	}
	
}
