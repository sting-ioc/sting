package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Qualify a service with a name.
 *
 * <p>Sting actively processes this annotation on {@link Injectable} types, {@link Injectable}
 * constructor parameters, {@link Fragment} provider methods, {@link Fragment} provider method
 * parameters, and {@link Injector} output methods.</p>
 *
 * <p>Sting also tolerates this annotation for framework integration on {@link InjectorFragment}
 * methods, on types or constructor parameters whose enclosing type is annotated with an
 * annotation meta-annotated by {@link StingProvider}, and on types or constructor parameters
 * whose enclosing type is annotated with an annotation meta-annotated by
 * {@link ActAsStingComponent}.</p>
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
