package sting.server.interceptors;

import jakarta.transaction.TransactionManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import sting.Injectable;
import sting.interceptors.Around;
import sting.interceptors.Invocation;
import sting.interceptors.Proceed;
import sting.server.Transactional;

/**
 * Interceptor for {@link Transactional.TxType#REQUIRES_NEW} transactional service boundaries.
 */
@Injectable
public final class RequiresNewTransactionInterceptor extends TransactionInterceptorSupport {
    /**
     * Create the interceptor.
     *
     * @param transactionManager the JTA transaction manager.
     */
    RequiresNewTransactionInterceptor(@Nonnull final TransactionManager transactionManager) {
        super(transactionManager);
    }

    /**
     * Suspend any current transaction and invoke the service in a new transaction.
     *
     * @param invocation the inner interceptor chain or target service invocation.
     * @return the service result.
     * @throws Throwable if the service invocation fails.
     */
    @Around
    @Nullable
    public Object around(@Proceed @Nonnull final Invocation invocation) throws Throwable {
        return requiresNew(invocation);
    }
}
