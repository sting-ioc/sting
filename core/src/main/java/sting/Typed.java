package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Restricts the types provided by an instance.
 * May be applied to a bean class or a {@link Provides} annotated method. Only the bean types that are
 * explicitly listed using the {@link Typed#value() value} member are bean types of the bean.
 *
 * <pre>
 * &#064;Typed(MyService.class)
 * public class MyServiceImpl implements MyService {
 *    ...
 * }
 * </pre>
 */
@Documented
@Target( { FIELD, METHOD, TYPE } )
@Retention( RUNTIME )
public @interface Typed
{
  /**
   * Return the bean types of the bean.
   * The bean must be assignable to every bean listed in this parameter.
   *
   * @return the bean types of the bean.
   */
  Class<?>[] value() default {};
}
