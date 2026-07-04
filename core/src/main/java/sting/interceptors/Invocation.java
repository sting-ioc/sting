package sting.interceptors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the next step inside an {@link Around} interceptor.
 *
 * <p>Each call proceeds through the remaining interceptor chain and may invoke the target service method.</p>
 */
public final class Invocation {
    @Nonnull
    private final Invoker _invoker;

    @Nonnull
    private final Object[] _arguments;

    /**
     * Create an invocation.
     *
     * @param invoker the function that continues the invocation.
     * @param arguments the active arguments for this interceptor boundary.
     */
    public Invocation(@Nonnull final Invoker invoker, @Nonnull final Object[] arguments) {
        _invoker = invoker;
        _arguments = arguments;
    }

    /**
     * Continue with the active arguments for this interceptor boundary.
     *
     * @return the boxed service method result, or {@code null} for a void service method.
     * @throws Throwable if an inner interceptor or target service method throws.
     */
    @Nullable
    public Object proceed() throws Throwable {
        return proceed(_arguments);
    }

    /**
     * Continue with replacement arguments for inner interceptors and the target service method.
     *
     * <p>The supplied array should be non-null and have the same length as the service method's formal parameter
     * count.</p>
     *
     * @param arguments the replacement arguments.
     * @return the boxed service method result, or {@code null} for a void service method.
     * @throws Throwable if an inner interceptor or target service method throws.
     */
    @Nullable
    public Object proceed(@Nonnull final Object[] arguments) throws Throwable {
        return _invoker.proceed(arguments);
    }

    /**
     * Function used to continue the invocation with replacement arguments.
     */
    @FunctionalInterface
    public interface Invoker {
        /**
         * Continue with replacement arguments for inner interceptors and the target service method.
         *
         * @param arguments the replacement arguments.
         * @return the boxed service method result, or {@code null} for a void service method.
         * @throws Throwable if an inner interceptor or target service method throws.
         */
        @Nullable
        Object proceed(@Nonnull Object[] arguments) throws Throwable;
    }
}
