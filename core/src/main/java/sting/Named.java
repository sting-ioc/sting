package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;

/**
 * A string-based {@linkplain Qualifier qualifier}.
 *
 * <p>Example usage:
 *
 * <pre>
 *   &#064;Injectable
 *   public class Car {
 *     Car(<b>&#064;Named("driver")</b> Seat driverSeat,
 *         <b>&#064;Named("passenger")</b> Seat passengerSeat) {
 *       ...
 *     }
 *     ...
 *   }</pre>
 */
@Qualifier
@Documented
@Retention( RetentionPolicy.RUNTIME )
public @interface Named
{
  /**
   * Return the name qualifier.
   *
   * @return the name.
   */
  @Nonnull
  String value();
}
