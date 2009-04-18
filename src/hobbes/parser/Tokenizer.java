package hobbes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Tokenizer {
	
	private Queue<Character> code;
	private ArrayList<Token> tokens;
	private String buffer;
	private Stack<Token> depth;
	
	private final static ArrayList<Character> trailingEquals = new ArrayList<Character>();
	static {
		trailingEquals.add('=');
		trailingEquals.add('+');
		trailingEquals.add('-');
		trailingEquals.add('*');
		trailingEquals.add('/');
		trailingEquals.add('>');
		trailingEquals.add('<');
	}
	private final static HashMap<String,String> pairs = new HashMap<String,String>();
	static {
		pairs.put("(", ")");
		pairs.put("[", "]");
		pairs.put("{", "}");
		pairs.put("class", "end");
		pairs.put("def", "end");
		pairs.put("if", "end");
		pairs.put("for", "end");
		pairs.put("while", "end");
	}
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		try {
			t.addCode("class Bla end");
		} catch (MismatchException e) {
			e.printStackTrace();
		}
		if(t.isReady())
			System.out.println(t.getTokens());
		else
			System.out.println("waiting to close "+t.getLastOpener());
	}
	
	public Tokenizer() {
		code = new LinkedList<Character>();
		tokens = new ArrayList<Token>();
		buffer = "";
		depth = new Stack<Token>();
	}
	
	public void addCode(String c) throws MismatchException {
		for(int i=0; i < c.length(); i++)
			code.add(c.charAt(i));
		tokenize();
	}
	
	public boolean isReady() {
		return depth.isEmpty();
	}
	
	public String getLastOpener() {
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
	
	private void tokenize() throws MismatchException {
		while(moreCode()) {
			if(!isReady() && getLastOpener().equals("\"")) {
				if(getLastOpener().equals("\""))
					getString();
			} else {
				if(code.peek() == '#')
					code.clear();
				else if(Character.isWhitespace(code.peek()))
					code.poll();
				else if(Character.isLetter(code.peek()))
					getWord();
				else if(code.peek() == '"') {
					depth.push(new Token(code.poll().toString(),TokenType.SYMBOL));
					getString();
				} else if(Character.isDigit(code.peek()))
					// TODO: number tokens can start with a '-'
					// TODO: number tokens can start with a '.'
					getNumber();
				else
					getSymbol();
			}
		}
	}

	private void getString() {
		// TODO backslashed stuff: \n, \t
		// TODO unicode stuff: \u4564 or whatever
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} if(code.peek() == '"' && lastChar() != '\\') {
				code.poll();
				depth.pop();
				tokens.add(new Token(flush(),TokenType.STRING));
				return;
			} else
				read();
		}
	}
	
	private void getWord() {
		read();
		while(moreCode() && (Character.isLetter(code.peek()) || Character.isDigit(code.peek())))
			read();
		Token word = new Token(flush(),TokenType.WORD);
		if(pairs.containsKey(word.getValue()))
			depth.push(word);
		else if(pairs.containsValue(word.getValue())) {
			depth.pop(); // "end" is the only closing word
		}
		tokens.add(word);
	}
	
	private void getNumber() {
		while(moreCode() && Character.isDigit(code.peek()))
			read();
		if(moreCode() && code.peek() == '.') {
			Character point = code.poll();
			if(Character.isDigit(code.peek())) {
				buffer += point;
				while(moreCode() && Character.isDigit(code.peek()))
					read();
			} else
				tokens.add(new Token(point.toString(),TokenType.SYMBOL));
		}
		tokens.add(new Token(flush(),TokenType.NUMBER));
	}
	
	private void getSymbol() throws MismatchException {
		read();
		// if this symbol might have a trailing = and there is one, read it
		if(trailingEquals.indexOf(lastChar()) > 0 && code.peek() == '=')
			read();
		Token symbol = new Token(flush(),TokenType.SYMBOL);
		// this symbol is the opening of a pair
		if(pairs.containsKey(symbol.getValue()))
			depth.push(symbol);
		else if(pairs.containsValue(symbol.getValue())) {
			if(getLastOpener() == symbol.getValue())
				depth.pop();
			else
				throw new MismatchException(symbol,pairs.get(getLastOpener()));
		}	
		tokens.add(symbol);
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