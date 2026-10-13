# T03 — Default and generated NullAway enforcement

- Status: complete
- Blocked by: T02
- Spec coverage: R5, AC4

## Delivers

Every sourceful owned target and positive generated fixture is checked in NullAway JSpecify mode, with a failing generated control proving the analyzer is active.

## Acceptance criteria

- [x] Default library/binary/test/example wrappers attach JSpecify and NullAway, following the JDBT pattern.
- [x] Generated sources are included in checking and contain no blanket NullAway suppression or NullUnmarked holes.
- [x] Positive fixture compilation explicitly uses an Error Prone/NullAway-capable compiler path rather than relying on Bazel options reaching plain nested javac.
- [x] A deliberately invalid generated source fails specifically with NullAway; all valid generated fixtures pass the same checked path.
- [x] Migrate test/example contracts exposed by strict checking, including arrays and nullable interception results.
- [x] Invalid processor fixtures remain in a separate diagnostics path and cannot falsely satisfy positive coverage.

## Validation

- Run positive and negative generated checking controls and all processor tests.
- `bazel build //core //processor //server //doc-examples` and `bazel test //:all_tests` — verify default enforcement across owned sources and generation.
- Inspect effective compiler flags and exclusions; ensure checking cannot silently skip generated output.
- `git diff --check`.

## Evidence

- `bazel build //core //processor //server //doc-examples` passed; `bazel test --keep_going //:all_tests --test_arg=-verbose --test_arg=2` passed all 16 targets.
- All five golden families regenerated and passed using the checked compiler. Negative processor diagnostics and the formatter subprocess failure probe explicitly use plain javac.
- `StingNullabilityTest.generatedNullnessViolationsFailChecking` observed exactly two NullAway errors in emitted `InvalidGenerated.java` (dereference and nested generic mismatch). Both normal and around inherited nested/array signatures passed strict checking.
- Effective integration Javac command contains NullAway ERROR, JSpecifyMode and the type-symbol flag, with no generated-path exclusion. `git diff --check` passed.
- Invocation's erased conversions use one narrowly suppressed cast helper; no generated type or service method suppresses NullAway. Cast/unboxing/null runtime behavior remains covered by integration tests.
- Error Prone 2.42.0/NullAway 0.14.2 and Guava 33.4.0 match the nested compiler's runtime needs. Local QA is imported from its release jar to avoid mixing upstream TestNG/Guava versions.

- T04 final scan added six previously missing eager/recursive/provider-naming positive fixture cases; their generated goldens now use JSpecify and pass the strict compiler.
