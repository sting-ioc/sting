package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface that contributes to the object graph.
 * The interface is expected to declare 1 or more {@link Provides provider} methods and/or
 * include 1 or more types in the {@link #includes() includes} parameter.
 * As this annotation is can only be applied to an interface, the {@link Provides provider} methods must
 * be <code>default</code> methods. Tne methods may be annotated with {@link Provides @Provides}
 * but even if they are not they are expected to comply with the requirements of
 * {@link Provides @Provides} annotated methods.
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
