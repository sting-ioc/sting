# Server Transactional Implementation Plan

Status: accepted.

## Phase Sequence

1. Add build metadata for `server`, `server-integration-tests`, and `javax_transaction_api`.
2. Add `sting.server.Transactional` and public concrete interceptor classes in `sting.server.interceptors`.
3. Add package-private transaction base/helper logic.
4. Add focused `server` tests with a fake `TransactionManager`.
5. Add `server-integration-tests` with annotation processing enabled.
6. Add `docs/server.md`, docs navigation updates, Javadocs, and `CHANGELOG.md`.
7. Run targeted checks, then the full validation gate.

## Delivery Approach

- Keep the implementation as a normal Sting interceptor module; do not change processor behavior.
- Use one concrete public interceptor per `TxType`, selected by enum-backed `implementedBy` templates.
- Keep the shared transaction state machine package-private.
- Use exact operation-order tests to lock semantics before relying on integration coverage.
- Keep docs aligned with the final behavior and explicit v1 limitations.

## Task Granularity Rules

- Build metadata is one task because module wiring must compile before code/tests can run.
- API and implementation are one task because concrete interceptor names, constructors, and base delegation are tightly
  coupled.
- Server behavior tests are separate from integration tests so failures identify either transaction semantics or Sting
  wiring.
- Docs are separate but must land before final gate completion.

## High-Risk Areas

- Risk: Inherited lifecycle method annotations are invalid in Sting.
  - Impact: A base class with `@Around` would fail processor validation.
  - Mitigation: Each concrete interceptor declares its own `@Around` method and delegates to package-private base logic.
- Risk: Cleanup failure behavior can obscure application exceptions.
  - Impact: This is surprising but intentionally follows accepted GlassFish-style behavior.
  - Mitigation: Lock behavior in exact operation-order and failure-path tests; document it where relevant.
- Risk: `REQUIRES_NEW`/`NOT_SUPPORTED` resume cleanup can be missed on begin/proceed/completion failures.
  - Impact: Caller transaction may remain detached from the thread.
  - Mitigation: Use `finally` after successful suspend and test begin failure, proceed failure, completion failure, and
    resume failure cases.
- Risk: New module dependency leaks into processor or unrelated integration tests.
  - Impact: Wider classpaths and accidental coupling.
  - Mitigation: Keep `server` out of processor shading and create `server-integration-tests` instead of expanding the
    existing integration suite.

## Required Full Gate

```bash
bundle exec buildr test
```

## Optional Pre-PR Gate

```bash
bundle exec buildr ci J2CL=no
```

## Decision Log

### Resolved

- Q-01: v1 is a pure `server` module using existing Sting interceptor bindings. No method-level processor work.
- Q-02: Use narrow `javax.transaction-api:1.3`; inject `TransactionManager`; no JNDI/global lookup.
- Q-03: Omit `rollbackOn` and `dontRollbackOn`.
- Q-04: Follow EE implementation behavior for transaction presence and GlassFish-style cleanup failure replacement.
- Q-05: Add `server` tests, a separate `server-integration-tests` project, and `docs/server.md`.
- Q-06: Omit JavaEE `UserTransaction` call restrictions in v1 and document this as an intentional divergence.

## Expected Files To Touch

- `build.yaml`
- `buildfile`
- `server/src/main/java/sting/server/*.java`
- `server/src/main/java/sting/server/interceptors/*.java`
- `server/src/main/java/sting/server/package-info.java`
- `server/src/main/java/sting/server/interceptors/package-info.java`
- `server/src/test/java/sting/server/interceptors/*.java`
- `server-integration-tests/src/test/java/sting/server/integration/*.java`
- `docs/server.md`
- `website/sidebars.json`
- `core/src/main/java/sting/interceptors/InterceptorBinding.java` only if docs/Javadoc need cross-reference touch-ups.
- `CHANGELOG.md`

## Acceptance Criteria

- `sting:server` compiles and packages as `sting-server`.
- `@Transactional` has no `rollbackOn` or `dontRollbackOn`.
- `@Transactional` resolves concrete interceptors through `sting.server.interceptors.{value}TransactionInterceptor`.
- All six concrete interceptors are public, effectively public, `@Injectable`, and constructor-inject
  `TransactionManager`.
- `sting.server` and `sting.server.interceptors` have package-level Javadocs and all public server API types have
  concise Javadocs.
- No server implementation performs JNDI or global lookup.
- Unit tests cover each `TxType`, unchecked rollback marking, checked exception behavior, started transaction
  completion, suspend/resume cleanup, forced rollback after unchecked `Invocation.proceed()` failures from inner
  interceptors or target service methods, and the explicit JTA operation failure matrix from `TEST-01`.
- `server-integration-tests` proves processor/proxy wiring for at least one default and one non-default `TxType`.
- Docs describe setup, behavior matrix, service-interface boundary limitations, method-level omission, and
  intentional divergences, including omitted `UserTransaction` call restrictions.
- `bundle exec buildr test` exits 0.

## Plan Acceptance

This plan must not be marked accepted until:

- The user reviews the latest plan.
- Any review feedback is incorporated.
- `20-task-board.yaml` records the approval outcome.
