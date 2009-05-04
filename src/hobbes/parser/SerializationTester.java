package hobbes.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import hobbes.ast.*;

public class SerializationTester {
	
	public static void main(String[] args) {
		SerializationTester t = new SerializationTester();
		String[] tests = {"(8*19^7)+-.321 or a*39+28 to 36+(18*ab.c[d](e,12+42*a).c)" +
				"to (8*19^7)+-.321 or a*39+28 to 36+(18*ab.c[d](e,12+42*a).c)" +
				"to (8*19^7)+-.321 or a*39+28 to 36+(18*ab.c[d](e,12+42*a).c) " +
				"to (8*19^7)+-.321 or a*39+28 to 36+(18*ab.c[d](e,12+42*a).c)"};
		for(int i=0; i < tests.length; i++) {
			t.writeTree(t.parse(tests[i]), "test"+i+".hbt");
		}
//		for(int i=0; i < tests.length; i++) {
//			System.out.println(t.readTree("test"+i+".hbt"));
//		}
		final int NUM_TRIALS = 10000;
		
		System.out.println("\nPARSING\n");
		
		for(String test: tests) {
			long start = System.currentTimeMillis();
			for(int trial=0; trial<NUM_TRIALS; trial++) {
				SyntaxNode tree = t.parse(test);
			}
			long end = System.currentTimeMillis();
			int diff = (int)(end-start);
			double time = ((double)diff)/((double)NUM_TRIALS);
			System.out.println(test + ": " + time + "ms");
		}
		
		System.out.println("\nREADING\n");
		
		for(int test=0; test < tests.length; test++) {
			long start = System.currentTimeMillis();
			for(int trial=0; trial < NUM_TRIALS; trial++) {
				SyntaxNode tree = t.readTree("test"+test+".hbt");
			}
			long end = System.currentTimeMillis();
			int diff = (int)(end-start);
			double time = ((double)diff)/((double)NUM_TRIALS);
			System.out.println(test + ": " + time + "ms");
		}
	}
	
	private Tokenizer tokenizer;
	private Parser parser;
	
	public SerializationTester() {
		tokenizer = new Tokenizer();
		parser = new Parser();
	}
	
	public SyntaxNode parse(String code) {
		try {
			tokenizer.addCode(code);
		} catch (MismatchException e) {
			e.printStackTrace();
		} catch (UnexpectedTokenException e) {
			e.printStackTrace();
		}
		try {
			return parser.parse(tokenizer.getTokens(), code);
		} catch (SyntaxError e) {
			System.err.println(e.getMessage());
			System.err.println(e.show());
		}
		return null;
	}
	
	public void writeTree(SyntaxNode tree, String path) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.writeObject(tree);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SyntaxNode readTree(String path) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return (SyntaxNode)in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
