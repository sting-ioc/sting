package sting.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.interceptors.InterceptorBinding;

/**
 * Defines the transaction boundary for a Sting-published service.
 *
 * <p>Apply this annotation to a service interface, or to an injectable implementation type that publishes service
 * interfaces, when calls to that service should execute with a JTA transaction policy. Sting applies the policy when
 * callers enter the service through a generated service proxy. Direct calls inside the same implementation instance
 * are not intercepted.</p>
 *
 * <p>The default policy is {@link TxType#REQUIRED}: use the caller's transaction when one is active, otherwise start
 * and complete a new transaction for the service call.</p>
 *
 * <p>Applications must provide a {@link jakarta.transaction.TransactionManager} as a Sting service. This module does not
 * look up a transaction manager globally.</p>
 *
 * <p>Runtime exceptions and {@link Error}s mark the active transaction rollback-only. Checked exceptions do not mark
 * rollback-only by default. Unlike {@link jakarta.transaction.Transactional}, this annotation does not provide
 * {@code rollbackOn} or {@code dontRollbackOn} members.</p>
 *
 * <p>This annotation is type-level only. Method-level transaction policies and Jakarta Transactions method override
 * rules are not supported.</p>
 */
@InterceptorBinding(implementedBy = "sting.server.interceptors.{value}TransactionInterceptor", priority = 200)
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Transactional {
    /**
     * Selects the transaction policy to use when a caller invokes the intercepted service boundary.
     *
     * @return the transaction policy.
     */
    TxType value() default TxType.REQUIRED;

    /**
     * Transaction policies supported by the Sting server interceptor binding.
     */
    enum TxType {
        /**
         * Run the service call in a transaction.
         *
         * <p>If the caller already has an active transaction, the service call runs in that transaction. If the caller has
         * no active transaction, Sting starts a new JTA transaction and completes it when the service call returns or
         * fails.</p>
         */
        REQUIRED,

        /**
         * Run the service call in its own new transaction.
         *
         * <p>If the caller already has an active transaction, that transaction is suspended for the duration of the service
         * call and resumed after the new transaction completes. If the caller has no active transaction, Sting simply
         * starts and completes a new transaction for the service call.</p>
         */
        REQUIRES_NEW,

        /**
         * Require the caller to already have an active transaction.
         *
         * <p>The service call runs in the caller's transaction. If there is no active transaction, the call fails with a
         * {@link jakarta.transaction.TransactionalException} whose cause is a
         * {@link jakarta.transaction.TransactionRequiredException}.</p>
         */
        MANDATORY,

        /**
         * Run with the caller's transaction state unchanged.
         *
         * <p>If the caller has an active transaction, the service call runs in that transaction. If the caller has no active
         * transaction, the service call runs without one. Choose this policy only for services that behave correctly in both
         * modes.</p>
         */
        SUPPORTS,

        /**
         * Run the service call without an active transaction.
         *
         * <p>If the caller has an active transaction, that transaction is suspended while the service call runs and then
         * resumed afterwards. If the caller has no active transaction, the service call proceeds without starting one.</p>
         */
        NOT_SUPPORTED,

        /**
         * Require the caller to have no active transaction.
         *
         * <p>The service call runs without a transaction. If there is an active transaction, the call fails with a
         * {@link jakarta.transaction.TransactionalException} whose cause is a
         * {@link jakarta.transaction.InvalidTransactionException}.</p>
         */
        NEVER
    }
}
