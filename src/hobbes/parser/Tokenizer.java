package hobbes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Scanner;

public class Tokenizer {
	
	// TODO: location info for multiline tokens (sep. SourcePoint & SourceSpan classes?)
	// TODO: give each token the line as a String (what about multilines?)
	// TODO: if statements, etc on a single line with no "end"
	
	private LinkedList<Character> code;
	private ArrayList<Token> tokens;
	private String buffer;
	private Stack<Token> depth;
	private int pos;
	private int startPos;
	
	private final static HashSet<String> multiCharSymbols = new HashSet<String>();
	static {
		multiCharSymbols.add("==");
		multiCharSymbols.add("!=");
		multiCharSymbols.add("+=");
		multiCharSymbols.add("-=");
		multiCharSymbols.add("*=");
		multiCharSymbols.add("/=");
		multiCharSymbols.add(">=");
		multiCharSymbols.add("<=");
		multiCharSymbols.add("++");
		multiCharSymbols.add("--");
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
		Scanner s = new Scanner(System.in);
		
		while(true) {
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener()+"> ");
			try {
				t.addCode(s.nextLine());
				if(t.isReady())
					System.out.println(t.getTokens());
			} catch(MismatchException e) {
				t.clear();
				System.out.println(e.getMessage());
			} catch (UnexpectedTokenException e) {
				t.clear();
				System.out.println(e.getMessage());
			}
		}
		
//		try {
//			t.addCode("==");
//		} catch (MismatchException e) {
//			e.printStackTrace();
//		}
//		if(t.isReady())
//			System.out.println(t.getTokens());
//		else
//			System.out.println("waiting for "+t.getWaitingFor());
	}
	
	public Tokenizer() {
		code = new LinkedList<Character>();
		tokens = new ArrayList<Token>();
		buffer = "";
		depth = new Stack<Token>();
		pos = startPos = 0;
	}
	
	public void addCode(String c) throws MismatchException, UnexpectedTokenException {
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
	
	public String getWaitingFor() {
		if(isReady())
			return null;
		else
			return pairs.get(getLastOpener());
	}
	
	public ArrayList<Token> getTokens() {
		if(isReady()) {
			ArrayList<Token> temp = (ArrayList<Token>)tokens.clone();
			clear();
			return temp;
		} else
			throw new IllegalStateException("can't get tokens, still waiting to close "
																+getLastOpener());
	}
	
	public int numTokens() {
		return tokens.size();
	}
	
	public void clear() {
		tokens.clear();
		code.clear();
		depth.clear();
		pos = startPos = 0;
	}
	
	private void tokenize() throws MismatchException, UnexpectedTokenException {
		while(moreCode()) {
			if(!isReady()) {
				if(getLastOpener().equals("\"") || getLastOpener().equals("'"))
					getString(getLastOpener().charAt(0));
				else if(getLastOpener().equals("/"))
					getRegex();
				else
					getToken();
			} else
				getToken();
		}
	}
	
	private void getToken() throws MismatchException, UnexpectedTokenException {
		if(peek() == '#')
			code.clear();
		else if(Character.isWhitespace(peek())) {
			code.poll();
			pos++;
			startPos++;
		} else if(Character.isLetter(peek()) || peek() == '_')
			getWord();
		else if(peek() == '"' || peek() == '\'') {
			char start = read();
			depth.push(makeToken(TokenType.SYMBOL));
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
		} else if(peek() == '-') {
			if(peek(1) != null && Character.isDigit(peek(1))) {
				// -5
				read();
				getNumber();
			} else if((peek(1) != null && peek(1) == '.') &&
					  (peek(2) != null && Character.isDigit(peek(2)))) {
				read();
				read();
				getNumber();
			} else
				getSymbol();
		} else if(peek() == '/') {
			Token lastToken = lastToken();
			if(lastToken != null &&
				(lastToken.getType() == TokenType.NUMBER || 
				 lastToken.getType() == TokenType.WORD ||
				 (lastToken.getType() == TokenType.SYMBOL &&
					lastToken.getValue().equals(")"))))
				getSymbol();
			else {
				read();
				depth.push(makeToken(TokenType.SYMBOL));
				getRegex();
			}
		} else
			getSymbol();
	}

	private void getString(char start) {
		// TODO unicode stuff: \u4564 or whatever
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} else if(peek() == start && lastChar() != '\\') {
				code.poll();
				pos++;
				depth.pop();
				tokens.add(makeToken(TokenType.STRING));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer += "\n";
						pos += 2;
						code.poll();
						code.poll();
					} else if(peek(1) == 't') {
						buffer += "\t";
						pos += 2;
						code.poll();
						code.poll();
					} else if(peek(1) == start) {
						buffer += start;
						pos += 2;
						code.poll();
						code.poll();
					} else
						read();
				} else
					read();
			} else
				read();
		}
	}
	
	private void getRegex() {
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} if(peek() == '/' && lastChar() != '\\') {
				code.poll();
				pos++;
				depth.pop();
				tokens.add(makeToken(TokenType.REGEX));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer += "\n";
						pos += 2;
						code.poll();
						code.poll();
					} else if(peek(1) == 't') {
						buffer += "\t";
						pos += 2;
						code.poll();
						code.poll();
					} else if(peek(1) == '/') {
						buffer += '/';
						pos += 2;
						code.poll();
						code.poll();
					} else
						read();
				} else
					read();
			} else
				read();
		}
	}
	
	private void getWord() throws UnexpectedTokenException {
		read();
		while(moreCode() && (Character.isLetterOrDigit(peek()) || peek() == '_'))
			read();
		if(moreCode() && (peek() == '?' || peek() == '!'))
			read();
		Token word = makeToken(TokenType.WORD);
		if(pairs.containsKey(word.getValue()))
			depth.push(word);
		else if(pairs.containsValue(word.getValue())) {
			if(depth.isEmpty())
				throw new UnexpectedTokenException(word);
			else if(getWaitingFor().equals(word.getValue()))
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
			tokens.add(makeToken(TokenType.NUMBER));
		} else
			tokens.add(makeToken(TokenType.NUMBER));
	}
	
	private void getSymbol() throws MismatchException, UnexpectedTokenException {
		read();
		if(moreCode()) {
			if(peek(1) != null && multiCharSymbols.contains(
			   lastChar().toString() + peek().toString() + peek(1).toString())) {
				read();
				read();
			} else if(multiCharSymbols.contains(lastChar().toString() + peek().toString()))
				read();
		}
		Token symbol = makeToken(TokenType.SYMBOL);
		// this symbol is the opening of a pair
		if(pairs.containsKey(symbol.getValue()))
			depth.push(symbol);
		else if(pairs.containsValue(symbol.getValue())) {
			if(depth.isEmpty())
				throw new UnexpectedTokenException(symbol);
			else if(getWaitingFor().equals(symbol.getValue()))
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
	
	private Token lastToken() {
		if(tokens.isEmpty())
			return null;
		else
			return tokens.get(tokens.size()-1);
	}
	
	private String flush() {
		String temp = buffer;
		buffer = "";
		startPos = pos;
		return temp;
	}
	
	private Token makeToken(TokenType type) {
		SourceLocation loc = new SourceLocation(startPos,pos);
		return new Token(flush(),type,loc);
	}
	
}