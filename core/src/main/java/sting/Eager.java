package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the component should be eagerly instantiated when the {@link Injector} is created.
 * The annotation may be applied to types annotated by {@link Injectable} or to methods contained in types annotated
 * by {@link Fragment}.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.METHOD } )
public @interface Eager
{
}
