package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface that contributes to the object graph.
 * The interface is expected to declare 1 or more default methods and/or
 * include 1 or more types in the {@link #includes() includes} parameter.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Fragment
{
  /**
   * A list of types that contribute to the object graph.
   * These types can be {@code @Fragment}-annotated interfaces or {@link Injectable @Injectable}-annotated classes.
   * The de-duplicated contributions of the {@code @Fragment}-annotated interfaces in the
   * {@code includes}, and of their inclusions recursively, are all contributed
   * to the object graph.
   *
   * @return a list of types that contribute to the fragments object graph.
   */
  Class<?>[] includes() default {};
}
