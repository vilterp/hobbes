package hobbes.parser;

import hobbes.ast.SyntaxNode;

public class TimeTester {
	
	public static void main(String[] args) {
		final int NUM_TRIALS = 100000;
		final String[] examples = {"'four score and seven years ago, our fathers brought forth on this continent a nation, concieved in liberty and dedicated...'.words.reverse().join(' ')"};
		Tokenizer t = new Tokenizer();
		Parser p = new Parser();
		
		for(String example: examples) {
			System.out.print(example + ": ");
			long start = System.currentTimeMillis();
			for(int trial=0; trial < NUM_TRIALS; trial++) {
				try {
					t.addLine(new SourceLine(example,1));
					SyntaxNode tree = p.parse(t.getTokens());
				} catch (SyntaxError e) {
					System.err.println(e.getMessage());
					System.err.println(e.getLocation().show());
				}
			}
			long end = System.currentTimeMillis();
			int diff = (int)(end-start);
			float perTrial = (float)diff/NUM_TRIALS;
			System.out.println(perTrial + "ms");
		}
		
	}

}
