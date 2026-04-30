package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Declare the types published by a component.
 * This annotation is used to explicitly specify which types that a component can provide.
 *
 * <p>Sting actively processes this annotation on {@link Injectable} types and on provider methods
 * contained within a type annotated by {@link Fragment}. When applied to a fragment provider method,
 * the listed types become the complete set of published services for that binding.</p>
 *
 * <p>Sting also tolerates this annotation for framework integration on types annotated with an
 * annotation meta-annotated by {@link ActAsStingProvider}. Types annotated with annotations
 * meta-annotated by {@link ActAsStingComponent} receive the same validation allowance.</p>
 *
 * <p>{@link Typed} must not be applied to {@link Injector} output methods.</p>
 *
 * <p>If this annotation is applied to a class then the class must be able to be assigned to the
 * types specified by this annotation. If the annotation is applied to a method then the return type
 * of the method must be able to be assigned to the types specified by this annotation. The method
 * return type is not implicitly published unless it is listed in {@link #value()}.</p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.TYPE, ElementType.METHOD } )
public @interface Typed
{
  /**
   * The types published by the component.
   *
   * @return the types published by the component.
   */
  @Nonnull
  Class<?>[] value();
}
