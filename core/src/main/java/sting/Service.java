package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * A specification of a service that is supplied to an injector during construction.
 * The service is added to the the component graph and is made available for other components to consume
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
   * <p>Sting does not support classes defined with type parameters.</p>
   *
   * @return the java type of the service.
   */
  Class<?> type();
}
