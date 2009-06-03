package hobbes.values;

import java.lang.reflect.Method;
import java.util.HashMap;

import hobbes.interpreter.ObjectSpace;

@HobbesClass(name="Class")
public class HbClass extends HbObject {
	
	private String name;
	private HashMap<String,HbMethod> methods;
	
	/*
	 * For native classes: class name is determined by HobbesClass annotation. 
	 */
	public HbClass(ObjectSpace o, Class<? extends HbObject> c) {
		this(o,c,((HobbesClass)c.getAnnotation(HobbesClass.class)).name());
	}
	
	/*
	 * For classes defined in Hobbes: name supplied,
	 * builtin methods inherited from HbObject
	 */
	public HbClass(ObjectSpace o, String name) {
		this(o,HbObject.class,name);
	}
	
	public HbClass(ObjectSpace o, Class<? extends HbObject> methodSource, String na) {
		super(o);
		name = na;
		// add methods
		methods = new HashMap<String,HbMethod>();
		for(Method m: methodSource.getMethods()) {
			if(m.isAnnotationPresent(HobbesMethod.class)) {
				HobbesMethod ann = m.getAnnotation(HobbesMethod.class);
				String n = ann.name();
				HbNativeMethod meth =
								new HbNativeMethod(n,ann.numArgs(),m); 
				methods.put(n,meth);
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public HbMethod getMethod(String name) {
		return methods.get(name);
	}
	
	public void addMethod(String name, HbMethod method) {
		methods.put(name, method);
	}
	
	@HobbesMethod(name="show",numArgs=0)
	public HbString show() {
		StringBuilder ans = new StringBuilder("<Class ");
		ans.append(name);
		ans.append(">");
		return new HbString(getObjSpace(),ans);
	}
	
	@HobbesMethod(name="name",numArgs=0)
	public HbString name() {
		return new HbString(getObjSpace(),name);
	}

}
