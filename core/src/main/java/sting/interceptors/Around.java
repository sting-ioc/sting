package sting.interceptors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a public interceptor lifecycle method that wraps an inner interceptor or target service method call.
 *
 * <p>An around method must return {@link Object} and receive exactly one {@link Invocation} parameter marked
 * with {@link Proceed}. The method may call {@link Invocation#proceed()} to continue with the current
 * arguments, call {@link Invocation#proceed(Object[])} to continue with replacement arguments, or return
 * without proceeding to short-circuit the call.</p>
 *
 * <p>The value returned by an around method is used as the intercepted service method result. Void service methods
 * ignore this value. Primitive service methods unbox the value according to the service method return type.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Around {}
