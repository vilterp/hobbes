package hobbes.values;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

import hobbes.ast.ExpressionNode;
import hobbes.interpreter.ObjectSpace;
import hobbes.parser.SourceLine;
import hobbes.parser.SyntaxError;
import hobbes.parser.Tokenizer;
import hobbes.parser.Parser;

@HobbesClass(name="Class")
public class HbClass extends HbObject {
	
	private String name;
	private Class<?extends HbObject> javaClass;
	private HashMap<String,HbMethod> methods;
	
	public HbClass(ObjectSpace o) throws HbArgumentError {
		super(o);
		throw new HbArgumentError(getObjSpace(),"Use class ClassName {...} to make a new class");
	}
	
	/*
	 * For native classes: class name is determined by HobbesClass annotation. 
	 */
	public HbClass(ObjectSpace o, Class<? extends HbObject> c) {
		this(o,c,((HobbesClass)c.getAnnotation(HobbesClass.class)).name());
		javaClass = c;
	}
	
	/*
	 * For classes defined in Hobbes: name supplied,
	 * builtin methods inherited from HbObject
	 */
	public HbClass(ObjectSpace o, String name) {
		this(o,HbObject.class,name);
		javaClass = HbObject.class;
	}
	
	public HbClass(ObjectSpace o, Class<? extends HbObject> methodSource,
																	String na) {
		super(o);
		name = na;
		javaClass = methodSource;
		// check that it has a ObjectSpace constructor
		try {
			javaClass.getConstructor(ObjectSpace.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			System.err.println("Class " + javaClass.getName() + " needs a constructor " +
					"that takes an ObjectSpace as the sole parameter");
			System.exit(1);
		}
		// add methods
		methods = new HashMap<String,HbMethod>();
		for(Method m: methodSource.getMethods()) {
			// get method information
			if(m.isAnnotationPresent(HobbesMethod.class)) {
				HobbesMethod ann = m.getAnnotation(HobbesMethod.class);
				String n = ann.name();
				HbNativeMethod meth =
								new HbNativeMethod(n,ann.numArgs(),m);
				// get defaults
				Tokenizer t = new Tokenizer();
				Parser p = new Parser();
				boolean specAlready = false;
				for(int i=0; i < ann.defaults().length; i++) {
					if(ann.defaults()[i] != null) {
						try {
							t.addLine(new SourceLine(ann.defaults()[i],"<default>",1));
							meth.setDefault(i,(ExpressionNode)p.parse(t.getTokens()));
							specAlready = true;
						} catch(SyntaxError e) {
							throw new IllegalArgumentException("Invalid expression ("
										+ ann.defaults()[i]
										+ ") for method "
										+ ann.name()
										+ " in class " + name);
						}
					} else if(specAlready)
						throw new IllegalArgumentException("Defaults arguments must "
								+ "come last (method: " + ann.name() + ", "
								+ "class: " + name + ")");
				}
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
	
	public String toString() {
		StringBuilder ans = new StringBuilder("<Class ");
		ans.append(name);
		ans.append(">");
		return ans.toString();
	}
	
	public Class<?extends HbObject> getJavaClass() {
		return javaClass;
	}
	
	@HobbesMethod(name="name")
	public HbString name() {
		return new HbString(getObjSpace(),name);
	}
	
	@HobbesMethod(name="superclass")
	public HbClass getSuperClass() {
		return getObjSpace().getClass("Object");
	}

}
