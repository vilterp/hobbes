package hobbes.parser;

import java.util.ArrayList;
import java.util.Iterator;

public class SourceFile implements Iterable<SourceLine> {
	
	private String path;
	private ArrayList<SourceLine> lines;
	
	public SourceFile(String p) {
		path = p;
		lines = new ArrayList<SourceLine>();
	}
	
	public SourceLine addLine(String code) {
		SourceLine newLine = new SourceLine(this,code,getNumLines()+1);
		lines.add(newLine);
		return newLine;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getNumLines() {
		return lines.size();
	}
	
	public SourceLine getLine(int lineNo) {
		try {
			return lines.get(lineNo-1);
		} catch(IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("No such line: " + lineNo + " in " + path);
		}
	}
	
	public ArrayList<SourceLine> getPrecedingLines(int start, int num) {
		ArrayList<SourceLine> toReturn = new ArrayList<SourceLine>();
		for(int i=start-1; i > 0 && start-i <= num; i--)
			toReturn.add(0,getLine(i));
		return toReturn;
	}
	
	public ArrayList<SourceLine> getFollowingLines(int start, int num) {
		ArrayList<SourceLine> toReturn = new ArrayList<SourceLine>();
		for(int i=start+1; i <= getNumLines() && i-start <= num; i++)
			toReturn.add(getLine(i));
		return toReturn;
	}
	
	public Iterator<SourceLine> iterator() {
		return lines.iterator();
	}
	
}
