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
 * Interceptor for {@link Transactional.TxType#MANDATORY} transactional service boundaries.
 */
@Injectable
public final class MandatoryTransactionInterceptor extends TransactionInterceptorSupport {
    /**
     * Create the interceptor.
     *
     * @param transactionManager the JTA transaction manager.
     */
    MandatoryTransactionInterceptor(@Nonnull final TransactionManager transactionManager) {
        super(transactionManager);
    }

    /**
     * Invoke the service only when a transaction is already active.
     *
     * @param invocation the inner interceptor chain or target service invocation.
     * @return the service result.
     * @throws Throwable if the service invocation fails.
     */
    @Around
    @Nullable
    public Object around(@Proceed @Nonnull final Invocation invocation) throws Throwable {
        return mandatory(invocation);
    }
}
