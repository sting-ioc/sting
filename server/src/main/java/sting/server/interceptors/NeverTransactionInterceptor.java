package sting.server.interceptors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.transaction.TransactionManager;
import sting.Injectable;
import sting.interceptors.Around;
import sting.interceptors.Invocation;
import sting.interceptors.Proceed;
import sting.server.Transactional;

/**
 * Interceptor for {@link Transactional.TxType#NEVER} transactional service boundaries.
 */
@Injectable
public final class NeverTransactionInterceptor
  extends TransactionInterceptorSupport
{
  /**
   * Create the interceptor.
   *
   * @param transactionManager the JTA transaction manager.
   */
  NeverTransactionInterceptor( @Nonnull final TransactionManager transactionManager )
  {
    super( transactionManager );
  }

  /**
   * Invoke the service only when no transaction is active.
   *
   * @param invocation the inner interceptor chain or target service invocation.
   * @return the service result.
   * @throws Throwable if the service invocation fails.
   */
  @Around
  @Nullable
  public Object around( @Proceed @Nonnull final Invocation invocation )
    throws Throwable
  {
    return never( invocation );
  }
}
