# T01 — Runtime JSpecify contracts

- Status: complete
- Blocked by: None
- Spec coverage: R1, AC1

## Delivers

Public core and server runtime APIs compile with explicit JSpecify nullness and strict NullAway, including interceptor arrays whose entries may be null.

## Acceptance criteria

- [x] Mark owned core/server packages and replace legacy nullability declarations while preserving API behavior.
- [x] Express nonnull arrays with nullable argument entries and nullable invocation results.
- [x] Enable full JSpecify NullAway on runtime targets using a verified supported Java 17 compiler and flag.

## Validation

- Run the resolved Bazel Java compiler/version and confirm type-annotation symbol support.
- `bazel build //core //server` — verifies runtime compilation and analyzer contracts.
- `bazel test //server/src/test/java/sting/server/interceptors:all_tests` — verifies server runtime behavior using the migrated APIs.
- `git diff --check` and targeted legacy import scan — verify coherent cleanup.

## Evidence

- `bazel build //core //server` passed with strict JSpecify-mode NullAway.
- `bazel test //server/src/test/java/sting/server/interceptors:all_tests` passed before and after formatting.
- Resolved runtimes executed: Java 17.0.20 for target runtime; JavaBuilder compiler uses Zulu 25.0.4 while output remains --release 17.
- Targeted formatting/buildifier, legacy-import scan, and `git diff --check` passed.
- Server retains its existing legacy annotation build dependency until T02 because the not-yet-migrated generator still emits javax annotations; no handwritten runtime declaration retains them.
