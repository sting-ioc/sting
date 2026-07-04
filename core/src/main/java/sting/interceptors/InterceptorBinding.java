package sting.interceptors;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Marks an annotation type as an interceptor binding.
 *
 * <p>Interceptor bindings are applied to service interfaces, {@code @Injectable} implementation classes, or
 * {@code @Fragment} provider methods. Sting resolves them at compile time and generates direct service-interface
 * proxies.</p>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.ANNOTATION_TYPE)
public @interface InterceptorBinding {
    /**
     * The canonical dotted name of the {@code @Injectable} interceptor implementation, or a template that resolves to
     * one.
     *
     * <p>Templates may contain placeholders such as {@code {value}}. Placeholder names refer to members on the
     * interceptor binding annotation and are supported only for scalar enum members. The effective enum value on each
     * reachable binding usage is converted to PascalCase by splitting the enum constant name on underscores and applying
     * locale-independent case conversion. Enum constants with leading, trailing, or repeated underscores are rejected
     * when selected by a reachable binding usage. The resolved value must be a canonical dotted Java name.</p>
     *
     * @return the canonical dotted name or enum-backed template for the interceptor implementation.
     */
    @Nonnull
    String implementedBy();

    /**
     * The interceptor priority.
     *
     * <p>Lower priorities run outermost. Equal effective priorities for one intercepted service are compile errors.</p>
     *
     * @return the interceptor priority.
     */
    int priority();
}
