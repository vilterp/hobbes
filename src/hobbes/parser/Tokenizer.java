package hobbes.parser;

import java.util.ArrayList;

public class Tokenizer {
	
	// TODO: handle multiline tokens (strings, regexps, etc)
	
	private String code;
	private int pos;
	private String buffer;
	private int startPos;
	private ArrayList<Character> trailingEquals;
	
	public static void main(String[] args) {
		Tokenizer t = new Tokenizer();
		try {
			System.out.println(t.tokenize("a = /my_regex/"));
		} catch (EOL e) {
			e.printStackTrace();
		}
	}
	
	public Tokenizer() {
		code = "";
		pos = 0;
		startPos = pos;
		buffer = "";
		trailingEquals = new ArrayList<Character>();
		trailingEquals.add('=');
		trailingEquals.add('>');
		trailingEquals.add('<');
		trailingEquals.add('+');
		trailingEquals.add('-');
		trailingEquals.add('*');
		trailingEquals.add('/');
	}
	
	public ArrayList<Token> tokenize(String c) throws EOL {
		code = c;
		pos = 0;
		ArrayList<Token> tokens = new ArrayList<Token>();
		while(pos < code.length()) {
			Character next = peek();
			if(Character.isSpaceChar(next)) {
				startPos++;
				advance();
			} else if(Character.isLetter(next))
				tokens.add(getWord());
			else if(Character.isDigit(next))
				// TODO: number tokens can start with "-" signs
				tokens.add(getNumber());
			else if(next == '"' || next == '\'')
				tokens.add(getString(next));
			else if(next == '/') {
				if(tokens.isEmpty())
					tokens.add(getSymbol());
				else {
					Token last = tokens.get(tokens.size()-1);
					if(last.getType() == TokenType.WORD ||
					   last.getType() == TokenType.NUMBER ||
					   (last.getType() == TokenType.SYMBOL && last.getValue().equals(")")))
						tokens.add(getSymbol());
					else
						tokens.add(getRegex());
				}
			} else
				tokens.add(getSymbol());
		}
		return tokens;
	}

	private Token getWord() {
		while(Character.isLetter(peek()) || Character.isDigit(peek()))
			read();
		return getToken(TokenType.WORD);
	}
	
	private Token getNumber() {
		boolean point = false;
		while(true) {
			Character next = peek();
			if(next == null)
				break;
			else if(Character.isDigit(next))
				read();
			else if(peek() == '.') {
				if(!point) {
					read();
					point = true;
				} else
					break;
			} else
				break;
		}
		return getToken(TokenType.NUMBER);
	}
	
	private Token getString(char startChar) throws EOL {
		// TODO: backslashed stuff (newlines, tabs, etc); unicode "\u1234" things?
		advance();
		while(true) {
			Character next = peek();
			if(next == null)
				throw new EOL();
			else if(next == startChar && endOfBuffer() != '\\') {
				break;
			} else
				read();
		}
		advance();
		return getToken(TokenType.STRING);
	}
	
	private Token getRegex() throws EOL {
		advance();
		while(true) {
			Character next = peek();
			if(next == null)
				throw new EOL();
			else if(next == '/' && endOfBuffer() != '\\')
				break;
			else
				read();
		}
		advance();
		return getToken(TokenType.REGEX);
	}
	
	private Token getSymbol() {
		Character next = read();
		if(trailingEquals.indexOf(next) > -1 && peek() == '=') {
			read();
			return getToken(TokenType.SYMBOL);
		} else {
			return getToken(TokenType.SYMBOL);
		}
	}
	
	private Token getToken(TokenType type) {
		return new Token(new SourceLocation(startPos,pos),flush(),type);
	}
	
	private char endOfBuffer() {
		return buffer.charAt(buffer.length()-1);
	}
	
	private String flush() {
		String buf = buffer;
		buffer = "";
		startPos = pos;
		return buf;
	}
	
	private Character read() {
		Character next = peek();
		if(next != null) {
			buffer += next;
			advance();
			return next;
		} else
			return null;
	}
	
	private Character peek() {
		try {
			return code.charAt(pos);
		} catch(StringIndexOutOfBoundsException e) {
			return null;
		}
		
	}
	
	private void advance() {
		pos++;
	}
	
	private void rewind() {
		rewind(1);
	}
	
	private void rewind(int num) {
		pos -= num;
	}
	
	private String getRemainder() {
		return code.substring(pos);
	}
	
}
