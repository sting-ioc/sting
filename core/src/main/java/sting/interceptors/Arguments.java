package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a lifecycle method parameter that receives the target service method arguments as a read-only array.
 *
 * <p>Mutating this array does not change the values passed to the target service method.</p>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Arguments
{
}
