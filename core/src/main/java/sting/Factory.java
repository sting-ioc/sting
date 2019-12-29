package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * An annotation applied to a type responsible for creating a bean instance.
 * The type may be an abstract class or an interface. Every instance method in the type must return the
 * same type and there must be one abstract method that will be implemented by the framework. The abstract
 * method may take parameters and these are added as dependencies of the bean in the object graph.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Factory
{
  /**
   * An opaque string that qualifies this factory.
   * This can be any arbitrary string and is used to restrict the dependencies that this factory can satisfy.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier() default "";
}
