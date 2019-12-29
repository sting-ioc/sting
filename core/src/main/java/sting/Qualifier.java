package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a qualifier annotation.
 * A qualifier annotation is combined with the type of the dependency to restrict which values can satisfy the
 * dependency. A qualifier annotation is annotated with {@code @Qualifier} and
 * {@link Retention @Retention(RetentionPolicy.RUNTIME)}. The qualifier annotation is typically annotated with
 * {@link Documented @Documented} as the dependency is part of the public API of the type. A qualifier annotation
 * may also have attributes.
 *
 * <p>For example:
 *
 * <pre>
 *   &#064;Documented
 *   &#064;Retention(RetentionPolicy.RUNTIME)
 *   &#064;Qualifier
 *   public @interface Plugin {
 *     int priority() default 1000;
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
