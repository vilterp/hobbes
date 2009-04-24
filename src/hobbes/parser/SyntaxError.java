package hobbes.parser;

public class SyntaxError extends Exception {
	
	private int pos;
	private String line;
	private String message;
	
	public SyntaxError(String msg, int at, String code) {
		super(msg);
		pos = at;
		line = code;
		message = msg;
	}

	public int getPos() {
		return pos;
	}

	public String getLine() {
		return line;
	}
	
	public String show() {
		String ans = line + "\n";
		for(int i=0; i < pos; i++)
			ans += ' ';
		ans += '^';
		return ans;
	}
	
}
