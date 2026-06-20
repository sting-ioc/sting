package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public interceptor lifecycle method that runs after an observed intercepted service invocation failure.
 *
 * <p>An interceptor's own {@link AfterException} method observes failures from that same interceptor's
 * {@link Around} method, but not failures from that same interceptor's {@link Before} or {@link After} methods.
 * Outer interceptors observe failures from inner interceptors and the target service method.</p>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface AfterException
{
}
