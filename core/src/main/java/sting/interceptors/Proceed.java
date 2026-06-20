package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@link Around} lifecycle method parameter that receives the invocation.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Proceed
{
}
