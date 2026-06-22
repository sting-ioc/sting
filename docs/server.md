---
title: Server
---

The `sting-server` module provides a Sting-native {@link: sting.server.Transactional @Transactional} interceptor
binding backed by `javax.transaction.TransactionManager`.

It manages transaction boundaries when calls cross Sting-published service interfaces. Like all Sting interception,
this is compile-time proxy wiring: no reflection, runtime annotation lookup, JNDI lookup, or classpath scanning is
performed by the module.

## Setup

Add `sting-server` to the application classpath alongside `sting-core` and configure the normal Sting annotation
processor.

```xml
<dependency>
  <groupId>org.realityforge.sting</groupId>
  <artifactId>sting-server</artifactId>
  <version>0.37</version>
</dependency>
```

Applications must expose a `javax.transaction.TransactionManager` as a Sting service. The server module does not look
up a transaction manager globally.

```java
@Fragment
interface TransactionFragment
{
  default TransactionManager transactionManager()
  {
    return obtainTransactionManager();
  }
}
```

## Declaring Boundaries

Apply `@Transactional` to a published service interface or to an injectable implementation type that publishes service
interfaces.

```java
@Transactional
public interface AccountService
{
  void transfer(AccountId from, AccountId to, Money amount);
}
```

The default transaction type is `REQUIRED`. Non-default modes are selected with
`@Transactional( Transactional.TxType.REQUIRES_NEW )` and resolve to one concrete interceptor at compile time through
the enum-backed interceptor binding template.

## Transaction Types

| Type | Behavior |
| ---- | -------- |
| `REQUIRED` | Uses the current transaction, or begins and completes a new transaction when none exists. |
| `REQUIRES_NEW` | Suspends the current transaction when present, begins and completes a new transaction, then resumes the suspended transaction. |
| `MANDATORY` | Requires a current transaction. If none exists, throws `TransactionalException` with a `TransactionRequiredException` cause. |
| `SUPPORTS` | Invokes the service with or without a current transaction. |
| `NOT_SUPPORTED` | Suspends the current transaction when present, invokes outside a transaction, then resumes the suspended transaction. |
| `NEVER` | Requires no current transaction. If one exists, throws `TransactionalException` with an `InvalidTransactionException` cause. |

Transaction presence is determined by `TransactionManager.getTransaction()`: `null` means no transaction and non-null
means a transaction is present.

## Completion Rules

Runtime exceptions and errors from the inner interceptor chain or target service mark the active transaction
rollback-only when a transaction is present. Checked exceptions do not mark rollback-only by default.

When a transaction is started by the transaction interceptor:

- runtime exceptions and errors force rollback;
- otherwise `STATUS_MARKED_ROLLBACK` causes rollback;
- all other statuses are passed to `commit()`, allowing the transaction manager to reject invalid states.

JTA failures from transaction lookup, begin, status lookup, commit, rollback, suspend, resume, and rollback-only
marking are reported as `javax.transaction.TransactionalException` where they are not preserving an original
application exception. Cleanup failures, such as rollback or resume failures, throw `TransactionalException` even when
that replaces an application exception.

## Limitations

Interception applies only at Sting service-interface boundaries. Self-invocation inside the same implementation
instance is not intercepted.

`@Transactional` targets types only. Method-level transaction declarations, JavaEE-style method override semantics,
`rollbackOn`, and `dontRollbackOn` are not part of this module.

The module intentionally omits JavaEE `UserTransaction` call restrictions. It does not control `UserTransaction`
access and does not perform JNDI or global lookup.
