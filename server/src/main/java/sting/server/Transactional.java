package sting.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import sting.interceptors.InterceptorBinding;

/**
 * Declares the JTA transaction boundary used when a Sting-published service interface is invoked.
 *
 * <p>The binding is resolved at compile time. The selected {@link TxType} chooses one concrete interceptor class
 * in the {@code sting.server.interceptors} package through the enum-backed {@code implementedBy} template.</p>
 */
@InterceptorBinding( implementedBy = "sting.server.interceptors.{value}TransactionInterceptor", priority = 200 )
@Retention( RetentionPolicy.CLASS )
@Target( ElementType.TYPE )
public @interface Transactional
{
  /**
   * The transaction behavior for the intercepted service boundary.
   *
   * @return the transaction behavior.
   */
  TxType value() default TxType.REQUIRED;

  /**
   * Transaction propagation behavior supported by the Sting server interceptor binding.
   */
  enum TxType
  {
    /**
     * Use the current transaction or begin a new one when none exists.
     */
    REQUIRED,

    /**
     * Suspend the current transaction, if present, and invoke the service in a new transaction.
     */
    REQUIRES_NEW,

    /**
     * Require an existing transaction before invoking the service.
     */
    MANDATORY,

    /**
     * Invoke the service with or without an existing transaction.
     */
    SUPPORTS,

    /**
     * Suspend the current transaction, if present, and invoke the service outside a transaction.
     */
    NOT_SUPPORTED,

    /**
     * Require that no transaction exists before invoking the service.
     */
    NEVER
  }
}
