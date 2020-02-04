package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Identify a component type that Sting can create by invoking the constructor.
 * The type must be concrete and should have a single package-access constructor.
 * The constructor can accept zero or more services as arguments. The constructor parameters
 * can be explicitly annotated with a {@link Service} annotation otherwise the compiler will
 * treat the parameter as if it was annotated with a {@link Service} annotation with default
 * values for all the elements.
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Injectable
{
  /**
   * A unique identifier representing the component.
   * If not specified then the avlue of this element default to "[Qualified Classname]".
   * This element is primarily used so that specific components can be overridden.
   *
   * @return a unique identifier representing the component.
   */
  @Nonnull
  String id() default "";

  /**
   * A flag indicating whether the component should be eagerly instantiated when the {@link Injector} is created.
   *
   * @return a flag indicating whether the component should be eagerly instantiated when the {@link Injector} is created.
   */
  boolean eager() default false;

  /**
   * The services published by the component.
   *
   * @return the services published by the component.
   */
  Service[] services() default { @Service };
}
