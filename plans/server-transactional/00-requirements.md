# Server Transactional Requirements

Status: accepted.

## Mission

Add a `server` module that provides a Sting-native `@Transactional` interceptor binding backed by JTA
`javax.transaction.TransactionManager`.

The module should provide JavaEE-like transaction boundary behavior at Sting service-interface interception
boundaries while keeping dependencies and public API surface small.

## Scope Boundaries

In scope:

- Add Buildr project `server` and Maven artifact `sting-server`.
- Add Buildr project `server-integration-tests` for Sting wiring coverage with server-specific dependencies.
- Add `sting.server.Transactional` with `TxType` values:
  - `REQUIRED`
  - `REQUIRES_NEW`
  - `MANDATORY`
  - `SUPPORTS`
  - `NOT_SUPPORTED`
  - `NEVER`
- Use enum-backed `@InterceptorBinding.implementedBy` templates to select one concrete interceptor per `TxType`.
- Implement transaction behavior using constructor-injected `javax.transaction.TransactionManager`.
- Add focused `server` tests with a fake `TransactionManager` and exact operation-order assertions.
- Add one narrow `server-integration-tests` suite proving Sting processor/proxy wiring.
- Add `docs/server.md`, link it into documentation navigation, and update `CHANGELOG.md`.
- Add package-level and public-type Javadocs for the `sting.server` API.

Out of scope:

- Method-level `@Transactional`.
- `rollbackOn` and `dontRollbackOn`.
- JNDI, global lookup, runtime annotation scanning, reflection-driven interceptor dispatch, or runtime classpath scanning.
- A public Sting-owned transaction SPI.
- Any changes to core interceptor lifecycle inheritance rules, method-level binding support, or proxy generation semantics.
- Direct Jakarta namespace support in v1.
- JavaEE `UserTransaction` call restrictions inside `@Transactional` scopes.

## Locked Decisions And Non-Negotiables

- `server` depends narrowly on `core` and `javax.transaction:javax.transaction-api:1.3`.
- `@Transactional` lives in `sting.server`.
- `@Transactional` is `@Retention(RetentionPolicy.CLASS)`.
- `@Transactional` is `@Target(ElementType.TYPE)` only.
- `@Transactional` is not `@Inherited`.
- `@Transactional` uses `@InterceptorBinding( implementedBy = "sting.server.{value}TransactionInterceptor", priority = 200 )`.
- `@Transactional.value()` defaults to `TxType.REQUIRED`.
- Concrete interceptor classes are public:
  - `RequiredTransactionInterceptor`
  - `RequiresNewTransactionInterceptor`
  - `MandatoryTransactionInterceptor`
  - `SupportsTransactionInterceptor`
  - `NotSupportedTransactionInterceptor`
  - `NeverTransactionInterceptor`
- Shared base/helper types are package-private.
- Each concrete interceptor declares its own public `@Around` lifecycle method and delegates to the base. The
  processor rejects inherited lifecycle annotations, so the base must not own annotated lifecycle methods.
- Each concrete interceptor constructor takes `TransactionManager` and delegates to the base constructor.
- Use `javax.transaction.TransactionalException` directly for transaction infrastructure failures.
- Use JTA causes for invalid transaction preconditions:
  - `MANDATORY` with no transaction: `TransactionRequiredException`.
  - `NEVER` with a transaction: `InvalidTransactionException`.

## Command Surface And Behavior Expectations

Example API shape:

```java
@InterceptorBinding( implementedBy = "sting.server.{value}TransactionInterceptor", priority = 200 )
@Retention( RetentionPolicy.CLASS )
@Target( ElementType.TYPE )
public @interface Transactional
{
  TxType value() default TxType.REQUIRED;

  enum TxType
  {
    REQUIRED,
    REQUIRES_NEW,
    MANDATORY,
    SUPPORTS,
    NOT_SUPPORTED,
    NEVER
  }
}
```

Expected user wiring:

- Applications expose a `TransactionManager` as a Sting service, typically from a fragment/provider.
- Applications annotate published service interfaces or injectable implementation types with `@Transactional`.
- Sting resolves the concrete transaction interceptor at compile time through the enum-backed `implementedBy`
  template.
- Each transaction interceptor wraps its inner interceptor chain or target service call through
  `Invocation.proceed()`.

Interception limitations:

- Interception applies only at Sting-published service-interface boundaries.
- Self-invocation inside one implementation instance is not intercepted.
- Method-level transaction declarations are not part of v1.
- Fragment provider method `@Transactional` usage is not part of v1 because `@Transactional` targets types only.

## Quality, Test, And Coverage Gates

Targeted checks:

- `bundle exec buildr sting:server:test`
- `bundle exec buildr sting:server-integration-tests:test`

Required full gate:

```bash
bundle exec buildr test
```

Optional broader gate before PR:

```bash
bundle exec buildr ci J2CL=no
```

## Known Intentional Divergences

- JavaEE method-level override semantics are intentionally omitted because Sting currently rejects method-level
  interceptor bindings.
- `rollbackOn` and `dontRollbackOn` are intentionally omitted to keep v1 small and to use one concrete interceptor
  per `TxType` without binding-value parameters.
- `RUNTIME` retention and `@Inherited` are intentionally omitted because Sting resolves interceptor bindings at
  compile time and does not use runtime annotation lookup.
- Transaction context presence follows GlassFish and Quarkus/Narayana implementation behavior:
  `TransactionManager.getTransaction() == null` means absent; non-null means present.
- JavaEE `UserTransaction` call restrictions are intentionally omitted in v1 because this module does not control
  `UserTransaction` access and does not perform JNDI/global lookup.

## Evidence From Existing Code

- `docs/interceptors.md` documents that Sting interception occurs at service-interface boundaries and uses direct
  generated proxies.
- `processor/src/main/java/sting/processor/StingProcessor.java` rejects method-level interceptor bindings and
  inherited interceptor lifecycle annotations.
- `docs/interceptors.md` documents enum-backed `implementedBy` templates and PascalCase enum conversion.
- Existing `plans/interceptor-binding-enum-template` artifacts record the accepted enum-template behavior.
- Local Maven cache contains `javax.transaction:javax.transaction-api:1.3`, including `TransactionManager`,
  `TransactionalException`, `TransactionRequiredException`, and `InvalidTransactionException`.

## Open Questions Register

### Q-01

- status: resolved
- question: Should v1 be a pure `server` module using existing Sting interceptor bindings, or should it change
  `StingProcessor` to support method-level JavaEE-style override semantics?
- context: Sting currently rejects method-level interceptor bindings.
- options:
  - A: Pure `server` module using existing type-level interceptor binding support.
  - B: Extend the processor for method-level binding semantics.
- tradeoffs: A keeps scope small and uses existing processor features. B is closer to JavaEE but is a core
  interceptor feature, not just a server module.
- recommended_default: A.
- user_decision: Option A. Pure `server` module for v1.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-02

- status: resolved
- question: Should the module use a narrow JTA dependency and constructor-injected `TransactionManager`?
- context: The requested dependency scope is narrow, and the user explicitly rejected JNDI/global lookup.
- options:
  - A: Use `javax.transaction-api` and inject `TransactionManager`.
  - B: Create a public Sting transaction SPI.
  - C: Use JNDI/global lookup.
- tradeoffs: A minimizes public API and keeps tests simple. B increases public API surface. C creates hidden
  runtime coupling and was explicitly rejected.
- recommended_default: A.
- user_decision: Option A. Use `javax.transaction-api:1.3`, constructor-injected `TransactionManager`, no JNDI.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-03

- status: resolved
- question: Should `@Transactional` include JavaEE `rollbackOn` and `dontRollbackOn` members?
- context: Enum-backed `implementedBy` templates can select one concrete interceptor per `TxType`, removing the
  need for binding-value parameters for transaction mode.
- options:
  - A: Omit rollback arrays.
  - B: Mirror JavaEE rollback arrays.
- tradeoffs: A keeps v1 small and avoids per-binding runtime metadata. B is closer to JavaEE but increases
  implementation complexity.
- recommended_default: A.
- user_decision: Option A. Remove `rollbackOn` and `dontRollbackOn`.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-04

- status: resolved
- question: Which JavaEE implementation behavior should guide transaction context checks and cleanup?
- context: GlassFish and Quarkus/Narayana use `TransactionManager.getTransaction()` presence for TxType branching.
- options:
  - A: Follow GlassFish/Quarkus presence checks and GlassFish-style cleanup failure behavior.
  - B: Pre-classify JTA `Status` values before TxType branching.
- tradeoffs: A follows existing EE implementation behavior. B may be more defensive but diverges from EE practice.
- recommended_default: A.
- user_decision: Option A. Use `getTransaction() == null` / `!= null` for presence; cleanup failures throw
  `TransactionalException` even if they replace an application exception.
- artifacts_updated: `00-requirements.md`, `01-transaction-semantics.md`, `10-implementation-plan.md`,
  `20-task-board.yaml`

### Q-05

- status: resolved
- question: How should tests and docs be structured?
- context: Server behavior needs fake JTA coverage and distinct integration dependencies.
- options:
  - A: Add `server` tests plus a separate `server-integration-tests` project, and add `docs/server.md`.
  - B: Put all coverage in existing `integration-tests` and rely mostly on Javadoc.
- tradeoffs: A isolates server-specific dependencies and gives clearer behavior coverage. B is smaller upfront but
  couples unrelated integration suites and under-documents a new module.
- recommended_default: A.
- user_decision: Option A. Add `server` tests, new `server-integration-tests`, and `docs/server.md`.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-06

- status: resolved
- question: Should v1 implement the JavaEE restriction that `UserTransaction` methods are illegal inside most
  `@Transactional` scopes?
- context: The JavaEE source text mentions restrictions on `UserTransaction` calls inside `@Transactional` scopes
  except `NOT_SUPPORTED` and `NEVER`. Implementing that requires controlling or wrapping `UserTransaction` access,
  which is outside the current constructor-injected `TransactionManager` design and conflicts with the no-JNDI,
  narrow-infrastructure goal.
- options:
  - A: Omit `UserTransaction` restrictions in v1 and document this as an intentional divergence.
  - B: Add infrastructure to expose/wrap `UserTransaction` so illegal calls can be detected.
- tradeoffs: A keeps v1 focused on service-boundary transaction management. B is closer to JavaEE but adds public
  infrastructure, lookup/wrapping concerns, and substantially more tests.
- recommended_default: A.
- user_decision: Option A. Omit `UserTransaction` restrictions in v1 and document this as an intentional divergence.
- artifacts_updated: `00-requirements.md`, `01-transaction-semantics.md`, `10-implementation-plan.md`,
  `20-task-board.yaml`
