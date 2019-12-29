package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies qualifier annotations. Anyone can define a new qualifier. A
 * qualifier annotation:
 *
 * <ul>
 *   <li>is annotated with {@code @Qualifier}, {@code @Retention(RUNTIME)},
 *      and typically {@code @Documented}.</li>
 *   <li>can have attributes.</li>
 *   <li>may be part of the public API, much like the dependency type, but
 *      unlike implementation types which needn't be part of the public
 *      API.</li>
 * </ul>
 *
 * <p>For example:
 *
 * <pre>
 *   &#064;java.lang.annotation.Documented
 *   &#064;java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
 *   &#064;javax.inject.Qualifier
 *   public @interface Leather {
 *     Color color() default Color.TAN;
 *     public enum Color { RED, BLACK, TAN }
 *   }</pre>
 *
 * @see Named @Named
 */
@Target( ElementType.ANNOTATION_TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Qualifier
{
}
