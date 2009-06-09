package hobbes.parser;

import java.util.ArrayList;

public class SourceFile {
	
	private String path;
	private ArrayList<SourceLine> lines;
	
	public SourceFile(String p) {
		path = p;
		lines = new ArrayList<SourceLine>();
	}
	
	public SourceLine addLine(String code) {
		SourceLine newLine = new SourceLine(this,code,numLines()+1);
		lines.add(newLine);
		return newLine;
	}
	
	private void addLine(SourceLine l) {
		lines.add(l);
	}
	
	public String getPath() {
		return path;
	}
	
	public int numLines() {
		return lines.size();
	}
	
	public SourceLine getLine(int lineNo) {
		try {
			return lines.get(lineNo-1);
		} catch(IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("No such line: " + lineNo + " in " + path);
		}
	}
	
	public SourceLine[] getPrecedingLines(int start, int num) {
		SourceLine[] toReturn = new SourceLine[num];
		for(int i=start-1; i >= 0 && start-i <= num; i--)
			toReturn[start-1-i] = getLine(i);
		return toReturn;
	}
	
	public SourceLine[] getFollowingLines(int start, int num) {
		SourceLine[] toReturn = new SourceLine[num];
		for(int i=start+1; i < numLines() && i-start <= num; i++)
			toReturn[i-1-start] = getLine(i);
		return toReturn;
	}
	
}
