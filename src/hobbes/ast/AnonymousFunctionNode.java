package hobbes.ast;

public class AnonymousFunctionNode implements AtomNode {
	
	private ArgsSpecNode args;
	private VariableNode returnType;
	private BlockNode block;
	
	public AnonymousFunctionNode(ArgsSpecNode a, VariableNode r, BlockNode b) {
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