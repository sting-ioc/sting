package sting.doc.examples.interceptors;

import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.interceptors.Arguments;
import sting.interceptors.Around;
import sting.interceptors.Invocation;
import sting.interceptors.Proceed;

@Injectable
public final class ValidationInterceptor {
    @Around
    public @Nullable Object around(@Proceed final Invocation invocation, @Arguments final @Nullable Object[] arguments)
            throws Throwable {
        if (arguments.length > 1 && arguments[1] instanceof Integer && (Integer) arguments[1] <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        return invocation.proceed();
    }
}
