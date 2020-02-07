package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Qualify a service with a name.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.METHOD, ElementType.TYPE } )
public @interface Named
{
  /**
   * An opaque string that qualifies the service.
   * The string is user-supplied and used to distinguish two different services with the same type
   * but different semantics.
   *
   * @return an opaque qualifier string or name.
   */
  @Nonnull
  String value();
}
