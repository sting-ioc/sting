# Transaction Semantics Deep Dive

## Feature

- Name: `server` module `@Transactional`
- Related plan/task IDs: `server-transactional`, `API-01`, `TEST-01`, `INT-01`

## Problem Statement

Sting has compile-time service-interface interceptors but no transaction interceptor binding. The server module should
provide JavaEE-like transaction boundaries without changing the core processor or relying on runtime annotation lookup.

## Inputs and Interfaces

- `@Transactional` is a type-level interceptor binding.
- `@Transactional.value()` selects one `TxType` enum value.
- `@InterceptorBinding.implementedBy` resolves the concrete interceptor class with
  `sting.server.{value}TransactionInterceptor`.
- Concrete interceptors receive `TransactionManager` through constructor injection.

## Behavior Requirements

1. `REQUIRED`
   - If `TransactionManager.getTransaction()` is `null`, begin a transaction before invocation.
   - If a transaction exists, run inside it.
   - If this interceptor began the transaction, complete it after invocation.
2. `REQUIRES_NEW`
   - If a transaction exists, suspend it.
   - Begin a new transaction.
   - Complete the new transaction after invocation.
   - Always attempt to resume a successfully suspended transaction in cleanup, even if begin/proceed/completion fails.
3. `MANDATORY`
   - If no transaction exists, throw `TransactionalException` with `TransactionRequiredException` cause.
   - Otherwise proceed in the existing transaction.
4. `SUPPORTS`
   - Proceed whether a transaction exists or not.
5. `NOT_SUPPORTED`
   - If a transaction exists, suspend it.
   - Proceed outside a transaction.
   - Always attempt to resume a successfully suspended transaction in cleanup.
6. `NEVER`
   - If a transaction exists, throw `TransactionalException` with `InvalidTransactionException` cause.
   - Otherwise proceed outside a transaction.
7. Runtime exceptions and errors from `Invocation.proceed()` mark any active transaction rollback-only. This includes
   failures from inner interceptors as well as the target service method.
8. Checked exceptions from `Invocation.proceed()` do not mark rollback-only by default.
9. For transactions started by the interceptor, completion uses the invocation outcome:
   - If `Invocation.proceed()` threw `RuntimeException` or `Error`, call `rollback()` regardless of whether the earlier
     `setRollbackOnly()` attempt succeeded or changed the transaction status.
   - Otherwise, check `TransactionManager.getStatus()`.
   - If status is `STATUS_MARKED_ROLLBACK`, call `rollback()`.
   - For any other status, call `commit()` and let the transaction manager reject invalid states.
10. JTA operation failures from `getTransaction`, `begin`, `commit`, `rollback`, `suspend`, `resume`, and `getStatus`
    are wrapped in `javax.transaction.TransactionalException`.

## Error Handling and Diagnostics

- Invalid `MANDATORY` precondition: `TransactionalException` with `TransactionRequiredException` cause.
- Invalid `NEVER` precondition: `TransactionalException` with `InvalidTransactionException` cause.
- `begin`, `commit`, `rollback`, `suspend`, `resume`, `getTransaction`, and `getStatus` failures:
  `TransactionalException` with the original cause.
- If `setRollbackOnly()` fails while handling an application exception, continue to preserve the original application
  exception unless a later cleanup failure replaces it. For transactions started by the interceptor, an unchecked
  application exception still forces rollback even if `setRollbackOnly()` failed.
- If cleanup fails for a transaction started or suspended by the interceptor, throw `TransactionalException` even if
  that replaces an original application exception. This follows the accepted GlassFish-style cleanup rule.

## Compatibility and Parity

Baseline behavior:

- GlassFish transaction interceptors use `TransactionManager.getTransaction() == null` / `!= null` for transaction
  presence checks.
- GlassFish `REQUIRED` and `REQUIRES_NEW` complete transactions in `finally` and check `STATUS_MARKED_ROLLBACK`
  before rollback versus commit.
- Quarkus/Narayana similarly branches on an initial `TransactionManager.getTransaction()` value.

Intentional divergences:

- Method-level JavaEE override semantics are not supported in v1.
- `rollbackOn` and `dontRollbackOn` are not supported in v1.
- `@Inherited` and runtime retention are not used.
- JavaEE `UserTransaction` call restrictions are not implemented in v1 because the module does not control
  `UserTransaction` access and does not perform JNDI/global lookup.

## Acceptance Criteria

- [ ] Each `TxType` has exact fake-manager operation-order coverage.
- [ ] Runtime exception and `Error` mark rollback-only when a transaction is active.
- [ ] Checked exceptions do not mark rollback-only by default.
- [ ] Started transactions roll back after unchecked `Invocation.proceed()` failures, roll back when marked
      rollback-only after normal or checked-exception completion, and otherwise commit.
- [ ] `REQUIRES_NEW` and `NOT_SUPPORTED` always attempt resume after successful suspend.
- [ ] JTA operation failures wrap in `TransactionalException`, including initial transaction lookup, begin, status
      lookup, commit, rollback, suspend, resume, and cleanup resume replacing an original application exception.
- [ ] Sting integration test proves enum-template selection of concrete transaction interceptors.

## Validation Plan

- Targeted checks:
  - `bundle exec buildr sting:server:test`
  - `bundle exec buildr sting:server-integration-tests:test`
- Full gate:
  - `bundle exec buildr test`

## Open Questions

- None.
