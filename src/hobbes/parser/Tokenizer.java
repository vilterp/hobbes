package hobbes.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Tokenizer {
	
	private Queue<Character> code;
	private ArrayList<Token> tokens;
	private String buffer;
	private Stack<Token> depth;
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		t.addCode("\"hello world");
		System.out.println(t.getTokens());
		t.addCode("oh yeah\"");
		System.out.println(t.getTokens());
	}
	
	public Tokenizer() {
		code = new LinkedList<Character>();
		tokens = new ArrayList<Token>();
		buffer = "";
		depth = new Stack<Token>();
	}
	
	public void addCode(String c) {
		for(int i=0; i < c.length(); i++)
			code.add(c.charAt(i));
		tokenize();
	}
	
	public boolean isReady() {
		return depth.isEmpty();
	}
	
	public String getWaitingOn() {
		if(isReady()) {
			return null;
		} else {
			return depth.peek().getValue();
		}
	}
	
	public ArrayList<Token> getTokens() {
		ArrayList<Token> temp = (ArrayList<Token>)tokens.clone();
		tokens.clear();
		return temp;
	}
	
	private void tokenize() {
		if(isReady()) {
			if(code.peek() == '"') {
				depth.push(new Token(code.poll().toString(),TokenType.SYMBOL));
				readStringContents();
				if(moreCode()) {
					depth.pop();
					tokens.add(new Token(flush(),TokenType.STRING));
				} else
					buffer += "\n";
			}
		} else {
			if(getWaitingOn().equals("\"")) {
				readStringContents();
				if(moreCode()) {
					depth.pop();
					tokens.add(new Token(flush(),TokenType.STRING));
				} else
					buffer += "\n";
			}
		}
	}

	private void readStringContents() {
		while(true) {
			if(code.peek() == null)
				return;
			if(code.peek() == '"' && lastChar() != '\\')
				return;
			read();
		}
	}
	
	private void read() {
		buffer += code.poll();
	}
	
	private boolean moreCode() {
		return !code.isEmpty();
	}
	
	private Character lastChar() {
		if(buffer.length() > 0)
			return buffer.charAt(buffer.length()-1);
		else
			return null;
	}
	
	private String flush() {
		String temp = buffer;
		buffer = "";
		return temp;
	}
	
}