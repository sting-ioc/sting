package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Indicates that the type should be included in the named auto-fragment.
 * See the {@link AutoFragment} documentation for constraints about using
 * an auto-fragment.
 *
 * @see AutoFragment
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface ContributeTo
{
  /**
   * An opaque string that matches a single auto-fragment key.
   * The annotation processor will generate an error if there are multiple {@link AutoFragment} types that
   * match the key on the same classpath. The annotation will generate a suppressable warning if there are
   * no {@link AutoFragment} types with a matching key.
   *
   * @return an auto-fragment key.
   */
  @Nonnull
  String value();
}
