package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation applied to dependencies that restrict the values that can satisfy the dependency.
 *
 * <p>This annotation can appear on constructor parameters in a type annotated with {@link Injectable}
 * or it can appear on methods on types annotated by {@link Injector}.
 * If a dependency is annotated with {@code @Dependency} then the dependency is only satisfied by an
 * {@link Injectable} annotated type or a {@link Provides} annotated method with matching qualifier
 * attribute and a matching dependency type.
 */
@Target( { ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Dependency
{
  /**
   * An opaque string that qualifies the dependency.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier();
}
