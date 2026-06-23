package sting.server.interceptors;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.TransactionalException;
import javax.transaction.xa.XAResource;
import org.testng.annotations.Test;
import sting.interceptors.Invocation;
import static org.testng.Assert.*;

public final class TransactionInterceptorTest
{
  @Test
  public void requiredStartsTransactionWhenNoneExists()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final Object result = new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction begin target getStatus commit" );
  }

  @Test
  public void requiredUsesExistingTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();

    final Object result = new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction target" );
  }

  @Test
  public void requiresNewSuspendsExistingTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();

    final Object result = new RequiresNewTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction suspend begin target getStatus commit resume" );
  }

  @Test
  public void mandatoryRequiresExistingTransaction()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new MandatoryTransactionInterceptor( manager ).around( invocation( manager, "target", null ) ) );

    assertEquals( exception.getCause().getClass(), TransactionRequiredException.class );
    manager.assertTrace( "getTransaction" );
  }

  @Test
  public void mandatoryUsesExistingTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();

    final Object result = new MandatoryTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction target" );
  }

  @Test
  public void supportsRunsWithoutTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();

    final Object result = new SupportsTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "target" );
  }

  @Test
  public void notSupportedSuspendsExistingTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();

    final Object result =
      new NotSupportedTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction suspend target resume" );
  }

  @Test
  public void neverRejectsExistingTransaction()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new NeverTransactionInterceptor( manager ).around( invocation( manager, "target", null ) ) );

    assertEquals( exception.getCause().getClass(), InvalidTransactionException.class );
    manager.assertTrace( "getTransaction" );
  }

  @Test
  public void neverRunsWithoutTransaction()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();

    final Object result = new NeverTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction target" );
  }

  @Test
  public void uncheckedExceptionMarksExistingTransactionRollbackOnly()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> new SupportsTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    assertEquals( manager.current.status, Status.STATUS_MARKED_ROLLBACK );
    manager.assertTrace( "target getTransaction setRollbackOnly" );
  }

  @Test
  public void errorMarksExistingTransactionRollbackOnly()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    final AssertionError failure = new AssertionError( "boom" );

    final AssertionError exception =
      expectThrows( AssertionError.class,
                    () -> new SupportsTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    assertEquals( manager.current.status, Status.STATUS_MARKED_ROLLBACK );
    manager.assertTrace( "target getTransaction setRollbackOnly" );
  }

  @Test
  public void checkedExceptionDoesNotMarkExistingTransactionRollbackOnly()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    final Exception failure = new Exception( "boom" );

    final Exception exception =
      expectThrows( Exception.class,
                    () -> new SupportsTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    assertEquals( manager.current.status, Status.STATUS_ACTIVE );
    manager.assertTrace( "target" );
  }

  @Test
  public void startedTransactionRollsBackAfterUncheckedFailure()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    manager.assertTrace( "getTransaction begin target getTransaction setRollbackOnly rollback" );
  }

  @Test
  public void startedTransactionRollsBackAfterUncheckedFailureEvenWhenSetRollbackOnlyFails()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.setRollbackOnlyFailure = new SystemException( "setRollbackOnly" );
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    manager.assertTrace( "getTransaction begin target getTransaction setRollbackOnly rollback" );
  }

  @Test
  public void startedTransactionCommitsAfterCheckedFailureWhenNotMarkedRollback()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final Exception failure = new Exception( "boom" );

    final Exception exception =
      expectThrows( Exception.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception, failure );
    manager.assertTrace( "getTransaction begin target getStatus commit" );
  }

  @Test
  public void startedTransactionRollsBackWhenMarkedRollbackAfterSuccess()
    throws Throwable
  {
    final FakeTransactionManager manager = new FakeTransactionManager();

    final Object result = new RequiredTransactionInterceptor( manager ).around( invocation( manager,
                                                                                           "target",
                                                                                           "ok",
                                                                                           true ) );

    assertEquals( result, "ok" );
    manager.assertTrace( "getTransaction begin target setRollbackOnly getStatus rollback" );
  }

  @Test
  public void startedTransactionRollsBackWhenMarkedRollbackAfterCheckedFailure()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final Exception failure = new Exception( "boom" );

    final Exception exception =
      expectThrows( Exception.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( invocation( manager,
                                                                                           "target",
                                                                                           failure,
                                                                                           true ) ) );

    assertSame( exception, failure );
    manager.assertTrace( "getTransaction begin target setRollbackOnly getStatus rollback" );
  }

  @Test
  public void initialGetTransactionFailureIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.getTransactionFailure = new SystemException( "getTransaction" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", null ) ) );

    assertSame( exception.getCause(), manager.getTransactionFailure );
    manager.assertTrace( "getTransaction" );
  }

  @Test
  public void beginFailureIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.beginFailure = new SystemException( "begin" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", null ) ) );

    assertSame( exception.getCause(), manager.beginFailure );
    manager.assertTrace( "getTransaction begin" );
  }

  @Test
  public void getStatusFailureDuringCompletionIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.getStatusFailure = new SystemException( "getStatus" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) ) );

    assertSame( exception.getCause(), manager.getStatusFailure );
    manager.assertTrace( "getTransaction begin target getStatus" );
  }

  @Test
  public void commitFailureIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.commitFailure = new SystemException( "commit" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( invocation( manager, "target", "ok" ) ) );

    assertSame( exception.getCause(), manager.commitFailure );
    manager.assertTrace( "getTransaction begin target getStatus commit" );
  }

  @Test
  public void rollbackFailureIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.rollbackFailure = new SystemException( "rollback" );
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiredTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                    "target",
                                                                                                    failure ) ) );

    assertSame( exception.getCause(), manager.rollbackFailure );
    manager.assertTrace( "getTransaction begin target getTransaction setRollbackOnly rollback" );
  }

  @Test
  public void suspendFailureIsWrapped()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    manager.suspendFailure = new SystemException( "suspend" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( invocation( manager,
                                                                                               "target",
                                                                                               null ) ) );

    assertSame( exception.getCause(), manager.suspendFailure );
    manager.assertTrace( "getTransaction suspend" );
  }

  @Test
  public void requiresNewResumesAfterBeginFailure()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final FakeTransaction existing = new FakeTransaction();
    manager.current = existing;
    manager.beginFailure = new SystemException( "begin" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( invocation( manager,
                                                                                               "target",
                                                                                               null ) ) );

    assertSame( exception.getCause(), manager.beginFailure );
    assertSame( manager.current, existing );
    manager.assertTrace( "getTransaction suspend begin resume" );
  }

  @Test
  public void requiresNewResumesAfterGetStatusFailureDuringCompletion()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final FakeTransaction existing = new FakeTransaction();
    manager.current = existing;
    manager.getStatusFailure = new SystemException( "getStatus" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( invocation( manager,
                                                                                               "target",
                                                                                               "ok" ) ) );

    assertSame( exception.getCause(), manager.getStatusFailure );
    assertSame( manager.current, existing );
    manager.assertTrace( "getTransaction suspend begin target getStatus resume" );
  }

  @Test
  public void requiresNewResumesAfterCommitFailure()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final FakeTransaction existing = new FakeTransaction();
    manager.current = existing;
    manager.commitFailure = new SystemException( "commit" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( invocation( manager,
                                                                                               "target",
                                                                                               "ok" ) ) );

    assertSame( exception.getCause(), manager.commitFailure );
    assertSame( manager.current, existing );
    manager.assertTrace( "getTransaction suspend begin target getStatus commit resume" );
  }

  @Test
  public void requiresNewResumesAfterRollbackFailure()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    final FakeTransaction existing = new FakeTransaction();
    manager.current = existing;
    manager.rollbackFailure = new SystemException( "rollback" );
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                       "target",
                                                                                                       failure ) ) );

    assertSame( exception.getCause(), manager.rollbackFailure );
    assertSame( manager.current, existing );
    manager.assertTrace( "getTransaction suspend begin target getTransaction setRollbackOnly rollback resume" );
  }

  @Test
  public void requiresNewResumeFailureReplacesApplicationException()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    manager.resumeFailure = new SystemException( "resume" );
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new RequiresNewTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                       "target",
                                                                                                       failure ) ) );

    assertSame( exception.getCause(), manager.resumeFailure );
    manager.assertTrace( "getTransaction suspend begin target getTransaction setRollbackOnly rollback resume" );
  }

  @Test
  public void notSupportedResumeFailureReplacesApplicationException()
  {
    final FakeTransactionManager manager = new FakeTransactionManager();
    manager.current = new FakeTransaction();
    manager.resumeFailure = new SystemException( "resume" );
    final IllegalStateException failure = new IllegalStateException( "boom" );

    final TransactionalException exception =
      expectThrows( TransactionalException.class,
                    () -> new NotSupportedTransactionInterceptor( manager ).around( failingInvocation( manager,
                                                                                                        "target",
                                                                                                        failure ) ) );

    assertSame( exception.getCause(), manager.resumeFailure );
    manager.assertTrace( "getTransaction suspend target getTransaction resume" );
  }

  @Nonnull
  private static Invocation invocation( @Nonnull final FakeTransactionManager manager,
                                        @Nonnull final String trace,
                                        @Nullable final Object result )
  {
    return invocation( manager, trace, result, false );
  }

  @Nonnull
  private static Invocation invocation( @Nonnull final FakeTransactionManager manager,
                                        @Nonnull final String trace,
                                        @Nullable final Object result,
                                        final boolean markRollback )
  {
    return new Invocation( arguments -> {
      manager.trace.add( trace );
      if ( markRollback )
      {
        manager.setRollbackOnly();
      }
      if ( result instanceof Throwable )
      {
        throw (Throwable) result;
      }
      return result;
    }, new Object[ 0 ] );
  }

  @Nonnull
  private static Invocation failingInvocation( @Nonnull final FakeTransactionManager manager,
                                               @Nonnull final String trace,
                                               @Nonnull final Throwable failure )
  {
    return invocation( manager, trace, failure );
  }

  private static final class FakeTransactionManager
    implements TransactionManager
  {
    @Nonnull
    private final List<String> trace = new ArrayList<>();

    @Nullable
    private FakeTransaction current;

    @Nullable
    private SystemException getTransactionFailure;

    @Nullable
    private SystemException beginFailure;

    @Nullable
    private SystemException getStatusFailure;

    @Nullable
    private SystemException commitFailure;

    @Nullable
    private SystemException rollbackFailure;

    @Nullable
    private SystemException suspendFailure;

    @Nullable
    private SystemException resumeFailure;

    @Nullable
    private SystemException setRollbackOnlyFailure;

    @Override
    public void begin()
      throws NotSupportedException, SystemException
    {
      trace.add( "begin" );
      if ( null != beginFailure )
      {
        throw beginFailure;
      }
      current = new FakeTransaction();
    }

    @Override
    public void commit()
      throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException
    {
      trace.add( "commit" );
      if ( null != commitFailure )
      {
        throw commitFailure;
      }
      current = null;
    }

    @Override
    public int getStatus()
      throws SystemException
    {
      trace.add( "getStatus" );
      if ( null != getStatusFailure )
      {
        throw getStatusFailure;
      }
      return null == current ? Status.STATUS_NO_TRANSACTION : current.status;
    }

    @Nullable
    @Override
    public Transaction getTransaction()
      throws SystemException
    {
      trace.add( "getTransaction" );
      if ( null != getTransactionFailure )
      {
        throw getTransactionFailure;
      }
      return current;
    }

    @Override
    public void resume( @Nonnull final Transaction transaction )
      throws InvalidTransactionException, SystemException
    {
      trace.add( "resume" );
      if ( null != resumeFailure )
      {
        throw resumeFailure;
      }
      current = (FakeTransaction) transaction;
    }

    @Override
    public void rollback()
      throws SystemException
    {
      trace.add( "rollback" );
      if ( null != rollbackFailure )
      {
        throw rollbackFailure;
      }
      current = null;
    }

    @Override
    public void setRollbackOnly()
      throws SystemException
    {
      trace.add( "setRollbackOnly" );
      if ( null != setRollbackOnlyFailure )
      {
        throw setRollbackOnlyFailure;
      }
      if ( null != current )
      {
        current.status = Status.STATUS_MARKED_ROLLBACK;
      }
    }

    @Override
    public void setTransactionTimeout( final int seconds )
    {
      throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Transaction suspend()
      throws SystemException
    {
      trace.add( "suspend" );
      if ( null != suspendFailure )
      {
        throw suspendFailure;
      }
      final FakeTransaction transaction = current;
      current = null;
      return transaction;
    }

    private void assertTrace( @Nonnull final String expected )
    {
      assertEquals( String.join( " ", trace ), expected );
    }
  }

  private static final class FakeTransaction
    implements Transaction
  {
    private int status = Status.STATUS_ACTIVE;

    @Override
    public void commit()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean delistResource( @Nonnull final XAResource xaResource, final int flag )
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean enlistResource( @Nonnull final XAResource xaResource )
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus()
    {
      return status;
    }

    @Override
    public void registerSynchronization( @Nonnull final Synchronization synchronization )
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void rollback()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setRollbackOnly()
    {
      status = Status.STATUS_MARKED_ROLLBACK;
    }
  }
}
