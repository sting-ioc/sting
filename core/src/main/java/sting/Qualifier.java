package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation applied to dependency parameters to restrict the values that can satisfy the dependency.
 * If a parameter is annotated with {@code @Qualifier} then the dependency is only satisfied by an
 * {@link Injectable} annotated type or a {@link Provides} annotated method with matching qualifier
 * attribute and a dependency type.
 */
@Target( ElementType.PARAMETER )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Qualifier
{
  /**
   * An opaque string that qualifies the dependency.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier();
}
