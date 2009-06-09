package hobbes.ast;

import hobbes.parser.SourceLine;
import hobbes.parser.Token;

import java.util.ArrayList;

public class TryNode implements StatementNode {
	
	private Token origin;
	private BlockNode tryBlock;
	private ArrayList<CatchNode> catches;
	private BlockNode finallyBlock;
	
	public TryNode(Token o, BlockNode tb, ArrayList<CatchNode> c, BlockNode f) {
		origin = o;
		tryBlock = tb;
		catches = c;
		finallyBlock = f;
	}
	
	public String toString() {
		return "try(" + tryBlock + ","
				+ catches + ","
				+ (finallyBlock == null ? "" : finallyBlock)
				+ ")";
	}
	
	public BlockNode getTryBlock() {
		return tryBlock;
	}
	
	public ArrayList<CatchNode> getCatches() {
		return catches;
	}
	
	public BlockNode getFinally() {
		return finallyBlock;
	}
	
	public SourceLine getLine() {
		return origin.getLine();
	}
	
}
