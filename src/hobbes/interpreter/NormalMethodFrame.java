package hobbes.interpreter;

import hobbes.parser.SourceLocation;
import hobbes.values.HbObject;

public class NormalMethodFrame extends MethodFrame {
	
	private String methodName;
	private String className;
	private SourceLocation callLoc;
	private HbObject receiver;
	
	public NormalMethodFrame(Interpreter i, Scope adoptGlobals, HbObject rec,
						String mn, SourceLocation p) {
		super(new Scope(i,adoptGlobals));
		methodName = mn;
		callLoc = p;
		receiver = rec;
		className = rec.getHbClass().getName();
	}
	
	public HbObject getReceiver() {
		return receiver;
	}
	
	public SourceLocation getLoc() {
		return callLoc;
	}
	
	public String getName() {
		return className + "#" + methodName;
	}
	
	public String show() {
		return "  in " + getName() + "\n"
				+ showLoc(callLoc);
	}
	
}
