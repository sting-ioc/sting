package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public interceptor lifecycle method that runs after a successful intercepted service invocation.
 *
 * <p>A short-circuited {@link Around} method that returns normally is a successful invocation.</p>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface After
{
}
