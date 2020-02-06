package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;

/**
 * A service specification.
 * The annotation can be used to declare a service that is published by a component or consumed by an injector.
 *
 * <p>The annotation can be used to declare a service that is required by an injector and is
 * expected to be made available to other components to consume within the injectors component graph.
 * In this scenario, the service is added to the {@link Injector#inputs()} element. There is no default
 * value for the {@link #type()} element and the compiler will generate an error if it is not supplied.</p>
 *
 * <p>If the annotation is used to declare a service that is published by a component then it is declared in
 * the {@link Injectable#services()} element or the {@link Provides#services()} element. The default value of the
 * {@link #type()} element for services declared in the {@link Injectable#services()} element is the type of the
 * class that is annotated with the {@link Injectable} annotation. The default value of the {@link #type()} element
 * for services declared in the {@link Provides#services()} element is the return type of the method that is
 * annotated with the {@link Provides} annotation.
 * </p>
 */
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
