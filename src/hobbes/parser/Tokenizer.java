package hobbes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Scanner;

public class Tokenizer {
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		Scanner s = new Scanner(System.in);
		
		int lineNo = 1;
		while(true) {
			System.out.print(lineNo + ":");
			if(t.isReady())
				System.out.print(">> ");
			else
				System.out.print(t.getLastOpener()+"> ");
			try {
				t.addLine(new SourceLine(s.nextLine(),lineNo));
				if(t.isReady()) {
					ArrayList<Token> tokens = t.getTokens();
					System.out.println(tokens);
					for(Token token: tokens)
						System.out.println(token.getSourceSpan().show());
				}
			} catch(SyntaxError e) {
				t.reset();
				System.err.println(e.getMessage());
				System.err.println(e.getLocation().show());
			}
			lineNo++;
		}
		
//		try {
//			t.addLine(new SourceLine("a = 2+2.b(/oh yeah/)",1));
//		} catch (SyntaxError e) {
//			System.err.println(e.getMessage());
//			System.err.println(e.getLocation().show());
//		}
//		if(t.isReady()) {
//			ArrayList<Token> tokens = t.getTokens();
//			System.out.println(tokens);
//			for(Token token: tokens)
//				System.out.println(token.getLocation().show());
//		} else
//			System.out.println("waiting to close "+t.getLastOpener());
		
	}
	
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
		// multiCharSymbols.add("++");
		// multiCharSymbols.add("--"); // breaks 2--2 (=> [2, --, 2])
	}
	
	private final static HashMap<String,String> pairs = new HashMap<String,String>();
	static {
		pairs.put("(", ")");
		pairs.put("[", "]");
		pairs.put("{", "}");
		pairs.put("|", "|");
	}
	
	private SourceLine line;
	private String code;
	private ArrayList<Token> tokens;
	private String buffer;
	private Stack<Token> depth;
	private SourceLocation pos;
	private SourceLocation startPos;
	
	public Tokenizer() {
		line = null;
		code = "";
		tokens = new ArrayList<Token>();
		buffer = "";
		depth = new Stack<Token>();
		pos = startPos = null;
	}
	
	public void reset() {
		tokens.clear();
		depth.clear();
		pos = startPos = null;
	}

	public void addLine(SourceLine l) throws SyntaxError {
		code = l.getCode();
		line = l;
		pos = new SourceLocation(line,0);
		if(isReady() || getLastOpener().equals("\\"))
			startPos = pos;
		tokenize();
	}
	
	public boolean isReady() {
		return depth.isEmpty() || (depth.peek().getValue() == "\\");
	}
	
	public String getLastOpener() {
		if(isReady()) {
			return null;
		} else {
			return depth.peek().getValue();
		}
	}
	
	private String getWaitingFor() {
		if(isReady())
			return null;
		else
			return pairs.get(getLastOpener());
	}
	
	public ArrayList<Token> getTokens() {
		if(isReady()) {
			ArrayList<Token> temp = (ArrayList<Token>)tokens.clone();
			reset();
			return temp;
		} else
			throw new IllegalStateException("can't get tokens, still waiting to close "
																+getLastOpener());
	}
	
	public int numTokens() {
		return tokens.size();
	}
	
	private void tokenize() throws SyntaxError {
		while(moreCode()) {
			if(!isReady()) {
				if(getLastOpener().equals("\""))
					getString();
				else if(getLastOpener().equals("/"))
					getRegex();
				else
					getToken();
			} else
				getToken();
		}
	}
	
	private void getToken() throws SyntaxError {
		if(peek() == '\\') {
			read();
			depth.push(makeToken(TokenType.SYMBOL));
			if(moreCode())
				throw getUnexpectedTokenError(depth.pop());
		} else {
			if(peek() == '#')
					code = "";
			else if(peek() == '\t') {
				read();
				tokens.add(makeToken(TokenType.TAB));
			} else if(peek() == ' ') {
				if(moreCode(4) && peek(1) == ' ' && peek(2) == ' ' && peek(3) == ' ') {
					read();
					read();
					read();
					read();
					tokens.add(makeToken(TokenType.TAB));
				} else if(moreCode(2) && peek(1) == ' ') {
					read();
					read();
					tokens.add(makeToken(TokenType.TAB));
				} else {
					advance();
					advanceStart();
				}
			} else if(Character.isLetter(peek()) || peek() == '_')
				getWord();
			else if(peek() == '"') {
				read();
				Token startingQuote = makeToken(TokenType.SYMBOL);
				depth.push(startingQuote);
				startPos = startingQuote.getStart();
				getString();
			} else if(peek() == '\'') {
				advance();
				getChar();
			} else if(Character.isDigit(peek()))
				getNumber();
			else if(peek() == '.') {
				if(peek(1) != null && Character.isDigit(peek(1))) {
					// .5
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
					Token openingSlash = makeToken(TokenType.SYMBOL);
					depth.push(openingSlash);
					startPos = openingSlash.getStart();
					getRegex();
				}
			} else
				getSymbol();
			if(!isReady() && getLastOpener().equals("\\"))
				depth.pop();
		}
	}

	private void getString() throws SyntaxError {
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} else if(peek() == '"') {
				advance();
				depth.pop();
				tokens.add(makeToken(TokenType.STRING));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer += "\n";
						advance();
						advance();
					} else if(peek(1) == 't') {
						buffer += "\t";
						advance();
						advance();
					} else if(peek(1) == '"') {
						buffer += '"';
						advance();
						advance();
					} else if(peek(1) == '\\') {
						buffer += "\\";
						advance();
						advance();
					} else
						throw new SyntaxError("invalid escape sequence " +
												"(only \\t, \\n, \\\" and \\\\)",
												pos.next().next());
				} else
					read();
			} else
				read();
		}
	}
	
	private void getChar() throws SyntaxError {
		if(peek() == '\'')
			throw new SyntaxError("empty character literal",pos.next());
		else if(peek() == '\\') {
			if(peek(1) == 'n') {
				buffer += "\n";
				advance();
				advance();
			} else if(peek(1) == 't') {
				buffer += "\t";
				advance();
				advance();
			} else if(peek(1) == '\'') {
				buffer += "'";
				advance();
				advance();
			} else if(peek(1) == '\\') {
				buffer += "\\";
				advance();
				advance();
			} else
				throw new SyntaxError("not a valid escape sequence " +
										"(only \\t, \\n, \\' and \\\\)",
										pos.next().next());
		} else
			read();
		if(peek() != '\'')
			throw new SyntaxError("can only put one character " +
					"in a character literal (put strings in \"'s)",
					pos.next());
		else {
			advance();
			tokens.add(makeToken(TokenType.CHAR));
			return;
		}
	}

	private void getRegex() throws SyntaxError {
		while(true) {
			if(!moreCode()) {
				buffer += "\n";
				return;
			} if(peek() == '/' && (lastChar() == null || lastChar() != '\\')) {
				advance();
				depth.pop();
				tokens.add(makeToken(TokenType.REGEX));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer += "\n";
						advance();
						advance();
					} else if(peek(1) == 't') {
						buffer += "\t";
						advance();
						advance();
					} else if(peek(1) == '/') {
						buffer += '/';
						advance();
						advance();
					} else if(peek(1) == '\\') {
						buffer += "\\";
						advance();
						advance();
					} else
						throw new SyntaxError("not a valid escape sequence " +
												"(only \\n, \\t, \\/ and \\\\)",
												pos.next().next());
				} else
					read();
			} else
				read();
		}
	}
	
	private void getWord() throws SyntaxError {
		read();
		while(moreCode() && (Character.isLetterOrDigit(peek()) || peek() == '_'))
			read();
		if(moreCode() && (peek() == '?' || peek() == '!'))
			read();
		tokens.add(makeToken(TokenType.WORD));
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
	
	private void getSymbol() throws SyntaxError {
		read();
		if(moreCode()) {
			if(peek(1) != null && multiCharSymbols.contains(
			   lastChar().toString() + peek().toString() + peek(1).toString())) {
				read();
				read();
			} else if(multiCharSymbols.contains(
							lastChar().toString()+peek().toString()))
				read();
		}
		Token symbol = makeToken(TokenType.SYMBOL);
		// this symbol is the opening of a pair
		if(pairs.containsKey(symbol.getValue())) {
			if(symbol.getValue().equals("|")) {
				if(!"|".equals(getLastOpener())) // |'s can't be nested
					depth.push(symbol);
				else
					depth.pop();
			} else
				depth.push(symbol);
		} else if(pairs.containsValue(symbol.getValue())) {
			if(isReady())
				throw getUnexpectedTokenError(symbol);
			else if(getWaitingFor().equals(symbol.getValue()))
				depth.pop();
			else
				throw getMismatchError(symbol,pairs.get(getLastOpener()));
		}
		tokens.add(symbol);
	}
	
	private Character peek() {
		return peek(0);
	}
	
	private Character peek(int ahead) {
		try {
			return code.charAt(pos.getPosition() + ahead);
		} catch(StringIndexOutOfBoundsException e) {
			return null;
		}
	}
	
	private Character read() {
		char next = nextChar();
		buffer += next;
		return next;
	}
	
	private char nextChar() {
		char temp = code.charAt(pos.getPosition());
		advance();
		return temp;
	}
	
	private void advance() {
		pos = pos.next();
		// FIXME: eww too many objects!
	}
	
	private void advanceStart() {
		startPos = startPos.next();
	}
	
	private boolean moreCode() {
		return moreCode(1);
	}
	
	private boolean moreCode(int howMuchMore) {
		return pos.getPosition() + howMuchMore <= code.length();
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
		SourceSpan loc = new SourceSpan(startPos,pos);
		return new Token(flush(),type,loc);
	}
	
	private SyntaxError getMismatchError(Token found, String expected) {
		return new SyntaxError("Expected "+expected+", found "+found.getValue(),
									found.getStart());
	}
	
	private SyntaxError getUnexpectedTokenError(Token theUnexpected) {
		return getUnexpectedTokenError(theUnexpected,null);
	}
	
	private SyntaxError getUnexpectedTokenError(Token theUnexpected, String note) {
		String message = "Unexpected "+theUnexpected.getValue() +
								(note == null ? "" : "(" + note + ")");
		return new SyntaxError(message,theUnexpected.getStart());
	}
	
}