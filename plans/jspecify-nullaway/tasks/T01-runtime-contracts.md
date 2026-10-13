# T01 — Runtime JSpecify contracts

- Status: pending
- Blocked by: None
- Spec coverage: R1, AC1

## Delivers

Public core and server runtime APIs compile with explicit JSpecify nullness and strict NullAway, including interceptor arrays whose entries may be null.

## Acceptance criteria

- [ ] Mark owned core/server packages and replace legacy nullability declarations while preserving API behavior.
- [ ] Express nonnull arrays with nullable argument entries and nullable invocation results.
- [ ] Enable full JSpecify NullAway on runtime targets using a verified supported Java 17 compiler and flag.

## Validation

- Run the resolved Bazel Java compiler/version and confirm type-annotation symbol support.
- `bazel build //core //server` — verifies runtime compilation and analyzer contracts.
- `bazel test //server/src/test/java/sting/server/interceptors:all_tests` — verifies server runtime behavior using the migrated APIs.
- `git diff --check` and targeted legacy import scan — verify coherent cleanup.

## Evidence

- pending
