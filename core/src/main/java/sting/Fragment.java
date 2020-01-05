package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface that contributes to the object graph.
 * The interface is expected to declare 1 or more methods and/or
 * include 1 or more fragments in the {@link #includes() includes} parameter.
 * As this annotation is can only be applied to an interface, the methods must
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
   * Additional {@code @Fragment}-annotated classes from which this fragment is
   * composed. The de-duplicated contributions of the fragments in
   * {@code includes}, and of their inclusions recursively, are all contributed
   * to the object graph.
   *
   * @return additional {@code @Fragment}-annotated classes from which this fragment is composed.
   */
  Class<?>[] includes() default {};
}
