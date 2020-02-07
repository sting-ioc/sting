package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * A service specification.
 * The annotation can be used to declare a service that is supplied to an injector during construction.
 *
 * <p>The annotation can be used to declare a service that is required by an injector and is
 * expected to be made available to other components to consume within the injectors component graph.
 * In this scenario, the service is added to the {@link Injector#inputs()} element.</p>
 */
@Retention( RetentionPolicy.RUNTIME )
@Documented
@Target( {} )
public @interface Service
{
  /**
   * An opaque string that qualifies the service.
   * The string is user-supplied and used to distinguish two different services with the same {@link #type()}
   * but different semantics.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier() default "";

  /**
   * The java type of the service.
   *
   * <p>The default value for this element is derived according to the heuristics documented
   * at the class level. If the user specifies a type for this element then instances of the
   * type MUST be assignable to the type that would be derived using the above mentioned heuristics.</p>
   *
   * <p>Sting does not support classes defined with type parameters.</p>
   *
   * @return the java type of the service.
   */
  Class<?> type();
}
