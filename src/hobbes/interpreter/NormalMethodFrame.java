package hobbes.interpreter;

import hobbes.parser.SourceLocation;
import hobbes.values.HbNormalObject;

public class NormalMethodFrame extends MethodFrame {
	
	private String methodName;
	private String className;
	private SourceLocation callLoc;
	private HbNormalObject receiver;
	
	public NormalMethodFrame(Interpreter i, Scope adoptGlobals, HbNormalObject rec,
						String mn, SourceLocation p) {
		super(new Scope(i,adoptGlobals));
		methodName = mn;
		callLoc = p;
		receiver = rec;
		className = rec.getHbClass().getName();
	}
	
	public HbNormalObject getReceiver() {
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
