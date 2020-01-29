package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation used to describe the dependency.
 * The annotation parameters restrict the bindings that can satisfy the dependency.
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
  String qualifier() default "";

  /**
   * The type of the dependency required.
   *
   * <p>If the {@code @Dependency} annotation is attached to a constructor or method parameter then the
   * default value of the annotation parameter is the the type of the constructor or method parameter.
   * If this annotation parameter is explicitly specified then the value MUST be assignable to the type of
   * the constructor or method parameter.</p>
   *
   * <p>If the {@code @Dependency} annotation is attached to a method then the default value of the annotation
   * parameter is the the return type of the method. If the annotation parameter is explicitly specified then the
   * value MUST be assignable to the return type of the method.</p>
   *
   * @return the type of the dependency required.
   */
  Class<?> type() default void.class;
}
