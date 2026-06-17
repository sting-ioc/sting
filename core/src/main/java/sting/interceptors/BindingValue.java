package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Marks a lifecycle method parameter that receives a compile-time scalar member value from the binding annotation.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface BindingValue
{
  /**
   * The binding annotation member name.
   *
   * @return the binding annotation member name.
   */
  @Nonnull
  String value();
}
