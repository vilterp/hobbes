package hobbes.ast;

public class AnonymousFunctionNode implements AtomNode {
	
	private ArgsSpecNode args;
	private CallNode returnType;
	private BlockNode block;
	
	public AnonymousFunctionNode(ArgsSpecNode a, CallNode r, BlockNode b) {
		args = a;
		returnType = r;
		block = b;
	}
	
	public String toString() {
		return "|" + args + "|"
				+ (returnType == null ? "" : ":" + returnType)
				+ "{" + block + "}";
	}
	
}
