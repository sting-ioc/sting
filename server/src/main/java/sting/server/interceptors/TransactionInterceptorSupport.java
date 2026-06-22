package sting.server.interceptors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionalException;
import sting.interceptors.Invocation;

abstract class TransactionInterceptorSupport
{
  @Nonnull
  private final TransactionManager _transactionManager;

  TransactionInterceptorSupport( @Nonnull final TransactionManager transactionManager )
  {
    _transactionManager = transactionManager;
  }

  @Nullable
  final Object required( @Nonnull final Invocation invocation )
    throws Throwable
  {
    if ( null == getTransaction() )
    {
      begin();
      return invokeInStartedTransaction( invocation );
    }
    else
    {
      return invokeInExistingTransaction( invocation );
    }
  }

  @Nullable
  final Object requiresNew( @Nonnull final Invocation invocation )
    throws Throwable
  {
    final Transaction transaction = getTransaction();
    if ( null == transaction )
    {
      begin();
      return invokeInStartedTransaction( invocation );
    }
    else
    {
      final Transaction suspendedTransaction = suspend();
      try
      {
        begin();
        return invokeInStartedTransaction( invocation );
      }
      finally
      {
        resume( suspendedTransaction );
      }
    }
  }

  @Nullable
  final Object mandatory( @Nonnull final Invocation invocation )
    throws Throwable
  {
    if ( null == getTransaction() )
    {
      throw new TransactionalException( "Transaction required for MANDATORY transactional invocation",
                                        new TransactionRequiredException( "Transaction required" ) );
    }
    return invokeInExistingTransaction( invocation );
  }

  @Nullable
  final Object supports( @Nonnull final Invocation invocation )
    throws Throwable
  {
    return invokeInExistingTransaction( invocation );
  }

  @Nullable
  final Object notSupported( @Nonnull final Invocation invocation )
    throws Throwable
  {
    final Transaction transaction = getTransaction();
    if ( null == transaction )
    {
      return invokeInExistingTransaction( invocation );
    }
    else
    {
      final Transaction suspendedTransaction = suspend();
      try
      {
        return invokeInExistingTransaction( invocation );
      }
      finally
      {
        resume( suspendedTransaction );
      }
    }
  }

  @Nullable
  final Object never( @Nonnull final Invocation invocation )
    throws Throwable
  {
    if ( null != getTransaction() )
    {
      throw new TransactionalException( "Transaction present for NEVER transactional invocation",
                                        new InvalidTransactionException( "Transaction present" ) );
    }
    return invokeInExistingTransaction( invocation );
  }

  @Nullable
  private Object invokeInExistingTransaction( @Nonnull final Invocation invocation )
    throws Throwable
  {
    try
    {
      return invocation.proceed();
    }
    catch ( final RuntimeException | Error e )
    {
      markActiveTransactionRollbackOnly();
      throw e;
    }
  }

  @Nullable
  private Object invokeInStartedTransaction( @Nonnull final Invocation invocation )
    throws Throwable
  {
    boolean rollback = false;
    Throwable failure = null;
    Object result = null;
    try
    {
      result = invocation.proceed();
    }
    catch ( final RuntimeException | Error e )
    {
      rollback = true;
      failure = e;
      markActiveTransactionRollbackOnly();
    }
    catch ( final Throwable t )
    {
      failure = t;
    }

    completeStartedTransaction( rollback );

    if ( null != failure )
    {
      throw failure;
    }
    return result;
  }

  private void completeStartedTransaction( final boolean forceRollback )
  {
    if ( forceRollback )
    {
      rollback();
    }
    else if ( Status.STATUS_MARKED_ROLLBACK == getStatus() )
    {
      rollback();
    }
    else
    {
      commit();
    }
  }

  private void markActiveTransactionRollbackOnly()
  {
    try
    {
      if ( null != getTransaction() )
      {
        _transactionManager.setRollbackOnly();
      }
    }
    catch ( final Exception ignored )
    {
    }
  }

  @Nullable
  private Transaction getTransaction()
  {
    try
    {
      return _transactionManager.getTransaction();
    }
    catch ( final SystemException e )
    {
      throw failure( "Unable to determine current transaction", e );
    }
  }

  private int getStatus()
  {
    try
    {
      return _transactionManager.getStatus();
    }
    catch ( final SystemException e )
    {
      throw failure( "Unable to determine transaction status", e );
    }
  }

  private void begin()
  {
    try
    {
      _transactionManager.begin();
    }
    catch ( final Exception e )
    {
      throw failure( "Unable to begin transaction", e );
    }
  }

  private void commit()
  {
    try
    {
      _transactionManager.commit();
    }
    catch ( final Exception e )
    {
      throw failure( "Unable to commit transaction", e );
    }
  }

  private void rollback()
  {
    try
    {
      _transactionManager.rollback();
    }
    catch ( final Exception e )
    {
      throw failure( "Unable to rollback transaction", e );
    }
  }

  @Nonnull
  private Transaction suspend()
  {
    try
    {
      return _transactionManager.suspend();
    }
    catch ( final SystemException e )
    {
      throw failure( "Unable to suspend transaction", e );
    }
  }

  private void resume( @Nonnull final Transaction transaction )
  {
    try
    {
      _transactionManager.resume( transaction );
    }
    catch ( final Exception e )
    {
      throw failure( "Unable to resume transaction", e );
    }
  }

  @Nonnull
  private static TransactionalException failure( @Nonnull final String message, @Nonnull final Throwable cause )
  {
    return new TransactionalException( message, cause );
  }
}
