package hobbes.ast;

import java.util.ArrayList;

public class TryNode implements ControlStructureNode {
	
	private BlockNode tryBlock;
	private ArrayList<CatchNode> catches;
	private BlockNode finallyBlock;
	
	public TryNode(BlockNode tb, ArrayList<CatchNode> c, BlockNode f) {
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
	
}
