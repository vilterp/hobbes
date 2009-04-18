package hobbes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Tokenizer {
	
	private LinkedList<Character> code;
	private ArrayList<Token> tokens;
	private String buffer;
	private Stack<Token> depth;
	private int pos;
	private int startPos;
	
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
			t.addCode("a = \"hello");
		} catch (MismatchException e) {
			e.printStackTrace();
		}
		try {
			t.addCode("goodbye\"");
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
		pos = startPos = 0;
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
		code.clear();
		pos = startPos = 0;
		return temp;
	}
	
	private void tokenize() throws MismatchException {
		// TODO regex literals
		while(moreCode()) {
			if(!isReady()) {
				if(getLastOpener().equals("\"") || getLastOpener().equals("'"))
					getString(getLastOpener().charAt(0));
			} else {
				if(peek() == '#')
					code.clear();
				else if(Character.isWhitespace(peek())) {
					code.poll();
					pos++;
					startPos++;
				} else if(Character.isLetter(peek()))
					getWord();
				else if(peek() == '"' || peek() == '\'') {
					char start = read();
					depth.push(getToken(TokenType.SYMBOL));
					getString(start);
				} else if(Character.isDigit(peek()))
					getNumber();
				else if(peek() == '.') {
					if(peek(1) != null && Character.isDigit(peek(1))) {
						// .5
						read();
						getNumber();
					} else
						getSymbol();
				} else
					getSymbol();
			}
		}
	}

	private void getString(char start) {
		// TODO backslashed stuff: \n, \t
		// TODO unicode stuff: \u4564 or whatever
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} if(peek() == start && lastChar() != '\\') {
				code.poll();
				pos++;
				depth.pop();
				tokens.add(getToken(TokenType.STRING));
				return;
			} else
				read();
		}
	}
	
	private void getWord() {
		read();
		while(moreCode() && (Character.isLetter(peek()) || Character.isDigit(peek())))
			read();
		Token word = getToken(TokenType.WORD);
		if(pairs.containsKey(word.getValue()))
			depth.push(word);
		else if(pairs.containsValue(word.getValue())) {
			if(pairs.get(getLastOpener()).equals(word.getValue()))
				depth.pop();
		}
		tokens.add(word);
	}
	
	private void getNumber() {
		while(moreCode() && Character.isDigit(peek()))
			read();
		if(moreCode() && peek() == '.') {
			if(peek(1) != null && Character.isDigit(peek(1))) {
				read();
				while(moreCode() && Character.isDigit(peek()))
					read();
			}
			tokens.add(getToken(TokenType.NUMBER));
		} else
			tokens.add(getToken(TokenType.NUMBER));
	}
	
	private void getSymbol() throws MismatchException {
		read();
		// if this symbol might have a trailing = and there is one, read it
		if(trailingEquals.indexOf(lastChar()) > 0 && peek() == '=')
			read();
		Token symbol = getToken(TokenType.SYMBOL);
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
	
	private Character peek() {
		return peek(0);
	}
	
	private Character peek(int ahead) {
		try {
			return code.get(ahead);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private Character read() {
		buffer += code.poll();
		pos++;
		return lastChar();
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
		startPos = pos;
		return temp;
	}
	
	private Token getToken(TokenType type) {
		SourceLocation loc = new SourceLocation(startPos,pos);
		return new Token(flush(),type,loc);
	}
	
}