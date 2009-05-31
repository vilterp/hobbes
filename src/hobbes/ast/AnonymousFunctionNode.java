package hobbes.ast;

public class AnonymousFunctionNode implements AtomNode {
	
	private ArgsSpecNode args;
	private BlockNode block;
	
	public AnonymousFunctionNode(ArgsSpecNode a, BlockNode b) {
		args = a;
		block = b;
	}
	
	public String toString() {
		return "|" + args + "|"
				+ "{" + block + "}";
	}
	
}
