package hobbes.core;

import hobbes.parser.*;

public class Interpreter {
	
	private Tokenizer tokenizer;
	private Parser parser;
	private ObjectSpace objSpace;
	
	public Interpreter() {
		tokenizer = new Tokenizer();
		parser = new Parser();
		objSpace = new ObjectSpace();
	}
	
}
