package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Marks a lifecycle method parameter that receives a compile-time member value from the binding annotation.
 *
 * <p>Supported binding members are strings, primitives, {@code char}, {@code Class}, enums, and arrays of those
 * types. {@code Class} and enum members are supplied as {@code String} values; {@code Class[]} and enum array members
 * are supplied as {@code String[]} values.</p>
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
