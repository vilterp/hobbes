package hobbes.ast;

import hobbes.parser.Token;

public interface VarNode extends AtomNode {
	
	Token getOrigin();
	
}
