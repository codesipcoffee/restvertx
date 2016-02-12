package rest.vertx.Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Blocking {

	String value() default "false";
	
	// The default vaule for serial in vertx is true, I want to coincide with what they do
	String serial() default "true";
}