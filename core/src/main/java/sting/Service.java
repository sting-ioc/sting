package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation to specify a service.
 * The annotation can be used to declare the service that is consumed or published by a component.
 *
 * <p>If the annotation is used to declare a service that a component consumes it can either be used to
 * annotate a constructor parameter for {@link Injectable} components or to annotate a method parameter
 * for {@link Provides} components. The default value for the {@link #type()} element is the type of the
 * parameter.</p>
 *
 * <p>The annotation can also be used to declare a service that is required by an injector and is
 * expected to be made available to other components to consume within the injector component graph.
 * In this scenario, the service is added to the {@link Injector#inputs()} element. There is no default
 * value for the {@link #type()} element and the compiler will generate an error if it is not supplied.</p>
 *
 * <p>
 *   TODO: Implement, document and describe how to publish a service spec.
 * </p>
 */
@Target( { ElementType.PARAMETER, ElementType.METHOD } )
@Retention( RetentionPolicy.RUNTIME )
@Documented
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
  Class<?> type() default void.class;

  /**
   * A parameter indicating whether the service is required or optional.
   *
   * @return a parameter indicating whether the service is required or optional.
   */
  @Nonnull
  NecessityType necessity() default NecessityType.AUTODETECT;
}
