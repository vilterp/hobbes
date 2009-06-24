package hobbes.values;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import hobbes.ast.ExpressionNode;
import hobbes.interpreter.Interpreter;
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
	private HbClass superClass;
	
	public HbClass(Interpreter i) throws HbArgumentError {
		super(i);
		throw new HbArgumentError(getInterp(),"Use class ClassName {...} to make a new class");
	}
	
	/*
	 * For native classes: class name is determined by HobbesClass annotation. 
	 */
	public HbClass(Interpreter i, Class<? extends HbObject> c, String superclass) {
		this(i,c,((HobbesClass)c.getAnnotation(HobbesClass.class)).name(),superclass);
		javaClass = c;
	}
	
	/*
	 * For classes defined in Hobbes: name supplied,
	 * builtin methods inherited from HbObject
	 */
	public HbClass(Interpreter i, String name, String superclass) {
		this(i,HbObject.class,name,superclass);
		javaClass = HbNormalObject.class;
		setClass(getObjSpace().getClass("Class"));
	}
	
	public HbClass(Interpreter i, Class<? extends HbObject> methodSource,
														String na, String sc) {
		super(i);
		name = na;
		javaClass = methodSource;
		if(name.equals("Object"))
			superClass = null;
		else
			superClass = getObjSpace().getClass(sc);
		// check that it has a ObjectSpace constructor
		try {
			javaClass.getConstructor(Interpreter.class);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			System.err.println("Class " + javaClass.getName() + " needs a constructor " +
					"that takes an Interpreter as the sole parameter");
			e1.printStackTrace();
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
								new HbNativeMethod(n,name,ann.numArgs(),m);
				// get defaults
				Tokenizer t = new Tokenizer();
				Parser p = new Parser();
				boolean specAlready = false;
				for(int j=0; j < ann.defaults().length; j++) {
					if(ann.defaults()[j] != null) {
						try {
							t.addLine(new SourceLine(null,ann.defaults()[j],1));
							meth.setDefault(j,(ExpressionNode)p.parse(t.getTokens()));
							specAlready = true;
						} catch(SyntaxError e) {
							throw new IllegalArgumentException("Invalid expression ("
										+ ann.defaults()[j]
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
		// add methods from superclass
		if(!sc.equals("Object"))
			for(String methodName: superClass.getMethodNames())
				methods.put(methodName,superClass.getMethod(methodName));
	}
	
	public void setSuperclass(HbClass c) {
		superClass = c;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasMethod(String name) {
		return methods.containsKey(name);
	}
	
	public HbMethod getMethod(String name) {
		return methods.get(name);
	}
	
	public void addMethod(String name, HbMethod method) {
		methods.put(name, method);
	}
	
	@HobbesMethod(name="show")
	public HbString hbShow() {
		StringBuilder ans = new StringBuilder("<Class ");
		ans.append(name);
		if(superClass != null && !superClass.getName().equals("Object")) {
			ans.append('(');
			ans.append(superClass.getName());
			ans.append(')');
		}
		ans.append('>');
		return getObjSpace().getString(ans);
	}
	
	public Class<?extends HbObject> getJavaClass() {
		return javaClass;
	}
	
	@HobbesMethod(name="name")
	public HbString name() {
		return getObjSpace().getString(name);
	}
	
	@HobbesMethod(name="superclass")
	public HbObject hbGetSuperClass() {
		return (superClass != null ? superClass : getObjSpace().getNil());
	}
	
	@HobbesMethod(name="descends_from?",numArgs=1)
	public HbObject descendsFrom(HbObject klass) throws HbArgumentError {
		if(klass instanceof HbClass) {
			if(klass == this)
				return getObjSpace().getTrue();
			else if(name.equals("Object"))
				return getObjSpace().getFalse();
			else
				return getSuperclass().descendsFrom(klass);
		} else
			throw new HbArgumentError(getInterp(),"descends_from?",klass,"Class");
	}
	
	public HbClass getSuperclass() {
		return superClass;
	}
	
	public Set<String> getMethodNames() {
		return methods.keySet();
	}

}
