package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Identify an interface that can contribute to a component graph.
 * The interface is expected to declare 1 or more default methods and/or
 * include 1 or more types in the {@link #includes() includes} parameter.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Fragment
{
  /**
   * A list of types that can contribute to the component graph.
   * The types can be {@link Injectable}-annotated classes or {@link Fragment}-annotated interfaces.
   * The {@link Fragment}-annotated interfaces contributions are added recursively and contributions are
   * de-duplicated before they are resolved.
   *
   * @return a list of types that contribute to the fragments component graph.
   */
  @Nonnull
  Class<?>[] includes() default {};
}
