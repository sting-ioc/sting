package sting.server.interceptors;

import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.TransactionalException;
import org.jspecify.annotations.Nullable;
import sting.interceptors.Invocation;

abstract class TransactionInterceptorSupport {
    private final TransactionManager _transactionManager;

    TransactionInterceptorSupport(final TransactionManager transactionManager) {
        _transactionManager = transactionManager;
    }

    @Nullable
    final Object required(final Invocation invocation) throws Throwable {
        if (null == getTransaction()) {
            begin();
            return invokeInStartedTransaction(invocation);
        } else {
            return invokeInExistingTransaction(invocation);
        }
    }

    @Nullable
    final Object requiresNew(final Invocation invocation) throws Throwable {
        final Transaction transaction = getTransaction();
        if (null == transaction) {
            begin();
            return invokeInStartedTransaction(invocation);
        } else {
            final Transaction suspendedTransaction = suspend();
            try {
                begin();
                return invokeInStartedTransaction(invocation);
            } finally {
                resume(suspendedTransaction);
            }
        }
    }

    @Nullable
    final Object mandatory(final Invocation invocation) throws Throwable {
        if (null == getTransaction()) {
            throw new TransactionalException(
                    "Transaction required for MANDATORY transactional invocation",
                    new TransactionRequiredException("Transaction required"));
        }
        return invokeInExistingTransaction(invocation);
    }

    @Nullable
    final Object supports(final Invocation invocation) throws Throwable {
        return invokeInExistingTransaction(invocation);
    }

    @Nullable
    final Object notSupported(final Invocation invocation) throws Throwable {
        final Transaction transaction = getTransaction();
        if (null == transaction) {
            return invokeInExistingTransaction(invocation);
        } else {
            final Transaction suspendedTransaction = suspend();
            try {
                return invokeInExistingTransaction(invocation);
            } finally {
                resume(suspendedTransaction);
            }
        }
    }

    @Nullable
    final Object never(final Invocation invocation) throws Throwable {
        if (null != getTransaction()) {
            throw new TransactionalException(
                    "Transaction present for NEVER transactional invocation",
                    new InvalidTransactionException("Transaction present"));
        }
        return invokeInExistingTransaction(invocation);
    }

    @Nullable
    private Object invokeInExistingTransaction(final Invocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (final RuntimeException | Error e) {
            markActiveTransactionRollbackOnly();
            throw e;
        }
    }

    @Nullable
    private Object invokeInStartedTransaction(final Invocation invocation) throws Throwable {
        boolean rollback = false;
        Throwable failure = null;
        Object result = null;
        try {
            result = invocation.proceed();
        } catch (final RuntimeException | Error e) {
            rollback = true;
            failure = e;
            markActiveTransactionRollbackOnly();
        } catch (final Throwable t) {
            failure = t;
        }

        completeStartedTransaction(rollback);

        if (null != failure) {
            throw failure;
        }
        return result;
    }

    private void completeStartedTransaction(final boolean forceRollback) {
        if (forceRollback) {
            rollback();
        } else if (Status.STATUS_MARKED_ROLLBACK == getStatus()) {
            rollback();
        } else {
            commit();
        }
    }

    private void markActiveTransactionRollbackOnly() {
        try {
            if (null != getTransaction()) {
                _transactionManager.setRollbackOnly();
            }
        } catch (final Exception ignored) {
            // Preserve the original application failure.
        }
    }

    @Nullable
    private Transaction getTransaction() {
        try {
            return _transactionManager.getTransaction();
        } catch (final SystemException e) {
            throw failure("Unable to determine current transaction", e);
        }
    }

    private int getStatus() {
        try {
            return _transactionManager.getStatus();
        } catch (final SystemException e) {
            throw failure("Unable to determine transaction status", e);
        }
    }

    private void begin() {
        try {
            _transactionManager.begin();
        } catch (final Exception e) {
            throw failure("Unable to begin transaction", e);
        }
    }

    private void commit() {
        try {
            _transactionManager.commit();
        } catch (final Exception e) {
            throw failure("Unable to commit transaction", e);
        }
    }

    private void rollback() {
        try {
            _transactionManager.rollback();
        } catch (final Exception e) {
            throw failure("Unable to rollback transaction", e);
        }
    }

    private Transaction suspend() {
        try {
            return _transactionManager.suspend();
        } catch (final SystemException e) {
            throw failure("Unable to suspend transaction", e);
        }
    }

    private void resume(final Transaction transaction) {
        try {
            _transactionManager.resume(transaction);
        } catch (final Exception e) {
            throw failure("Unable to resume transaction", e);
        }
    }

    private static TransactionalException failure(final String message, final Throwable cause) {
        return new TransactionalException(message, cause);
    }
}
