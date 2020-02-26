package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Declare the types published by a component.
 * This annotation is used to explicitly specify which types that a component can provide.
 * The annotation may be applied to a class annotated with {@link Injectable} or to methods contained within a
 * type annotated by {@link Fragment}.
 *
 * <p>If this annotation is applied to a class then the class must be able to be assigned to the types specified
 * by this annotation. If the annotation is applied to a method then the return type of the method must be able
 * to be assigned to the types specified by this annotation.</p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.METHOD } )
public @interface Typed
{
  /**
   * The types published by the component.
   *
   * @return the types published by the component.
   */
  @Nonnull
  Class<?>[] value();
}
