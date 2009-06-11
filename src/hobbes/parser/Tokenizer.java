package hobbes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Scanner;

public class Tokenizer {
	
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
	private LinkedList<Token> tokens;
	private StringBuilder buffer;
	private Stack<Token> depth;
	private SourceLocation pos;
	private SourceLocation startPos;
	
	public Tokenizer() {
		line = null;
		code = "";
		tokens = new LinkedList<Token>();
		buffer = new StringBuilder();
		depth = new Stack<Token>();
		pos = startPos = null;
	}
	
	public void reset() {
		tokens.clear();
		depth.clear();
		buffer = new StringBuilder();
		pos = startPos = null;
	}

	public void addLine(SourceLine l) throws SyntaxError {
		code = l.getCode();
		line = l;
		pos = new SourceLocation(line,0);
		if(isReady())
			startPos = pos;
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
	
	private String getWaitingFor() {
		if(isReady())
			return null;
		else
			return pairs.get(getLastOpener());
	}
	
	public LinkedList<Token> getTokens() {
		if(isReady()) {
			LinkedList<Token> temp = (LinkedList<Token>)tokens.clone();
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
		if(!moreCode()) {
			if("\"".equals(getLastOpener()) || "/".equals(getLastOpener()))
				buffer.append("\n");
			return;
		}
		while(moreCode()) {
			if(!isReady()) {
				if(getLastOpener().equals("\""))
					getString('"');
				if(getLastOpener().equals("\'"))
					getString('\'');
				else if(getLastOpener().equals("/"))
					getRegex();
				else
					getToken();
			} else
				getToken();
		}
		if(isReady() || getLastOpener().equals("{"))
			tokens.add(new Token("",TokenType.EOL,new SourceSpan(pos,pos)));
	}
	
	private void getToken() throws SyntaxError {
		if(peek() == '#')
			code = "";
		else if(peek() == ' ' || peek() == '\t') {
			advance();
			startPos = pos;
		} else if(Character.isLetter(peek()) || peek() == '_' || peek() == '@')
				getWord();
		else if(peek() == '"' || peek() == '\'') {
			char start = read();
			Token startingQuote = makeToken(TokenType.SYMBOL);
			depth.push(startingQuote);
			startPos = startingQuote.getStart();
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
	}

	private void getString(char start) throws SyntaxError {
		while(true) {
			if(!moreCode()) {
				buffer.append("\n");
				return;
			} else if(peek() == start) {
				advance();
				depth.pop();
				tokens.add(makeToken(TokenType.STRING));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer.append("\n");
						advance();
						advance();
					} else if(peek(1) == 't') {
						buffer.append("\t");
						advance();
						advance();
					} else if(peek(1) == start) {
						buffer.append(start);
						advance();
						advance();
					} else if(peek(1) == '\\') {
						buffer.append("\\");
						advance();
						advance();
					} else
						throw new SyntaxError("invalid escape sequence " +
												"(only \\t, \\n, \\"
												+ (start == '"' ? '\'' : '"' )
												+ " and \\\\)",
												pos.next());
				} else
					read();
			} else
				read();
		}
	}
	
	private void getRegex() throws SyntaxError {
		while(true) {
			if(!moreCode()) {
				buffer.append("\n");
				return;
			} if(peek() == '/' && (lastChar() == null || lastChar() != '\\')) {
				advance();
				depth.pop();
				tokens.add(makeToken(TokenType.REGEX));
				return;
			} else if(peek() == '\\') {
				if(peek(1) != null) {
					if(peek(1) == 'n') {
						buffer.append("\n");
						advance();
						advance();
					} else if(peek(1) == 't') {
						buffer.append("\t");
						advance();
						advance();
					} else if(peek(1) == '/') {
						buffer.append('/');
						advance();
						advance();
					} else if(peek(1) == '\\') {
						buffer.append("\\");
						advance();
						advance();
					} else
						throw new SyntaxError("Not a valid escape sequence " +
												"(only \\n, \\t, \\/ and \\\\)",
												pos.next());
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
		Token word = makeToken(TokenType.WORD);
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
		buffer.append(next);
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
		String temp = buffer.toString();
		buffer = new StringBuilder();
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