package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify a component type that Sting can create by invoking the constructor.
 * This annotation is a top-level Sting processor entrypoint.
 *
 * <p>The type must be concrete and should have a single package-access constructor. The constructor
 * can accept zero or more services as arguments. Constructor parameters may be qualified with
 * {@link Named} and can be explicitly annotated with {@link Injector.Input}; otherwise the compiler
 * will treat the parameter as if it was annotated with a {@link Injector.Input} annotation with
 * default values for all the elements.</p>
 *
 * <p>When this annotation is present, Sting also actively processes {@link Named}, {@link Typed},
 * and {@link Eager} on the annotated type.</p>
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Injectable
{
}
