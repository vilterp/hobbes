package hobbes.values;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HobbesMethod {
	
	String name();
	int numArgs() default 0;
	String[] defaults() default {};
	
}
