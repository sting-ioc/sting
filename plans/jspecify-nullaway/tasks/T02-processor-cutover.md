# T02 — Local Proton and generated contract cutover

- Status: complete
- Blocked by: T01; consumable local Proton generator APIs
- Spec coverage: R2, R3, R4, R6, AC2, AC3, AC5 (source integration)

## Delivers

Sting consumes the unreleased local Proton source module and processes/generates JSpecify-only contracts across all generation families, preserving existing DI semantics.

## Acceptance criteria

- [x] Add the local source module override and bind processor/test dependencies to Proton core/QA source targets without waiting for release.
- [x] Make service matching, include discovery and interceptor type validation independent of type-use display annotations.
- [x] Read exact outer JSpecify nullness; reject relevant legacy declarations and unsupported nullable DI forms.
- [x] Emit marked types and preserve explicit type-use contracts, nullable cache state and runtime checks in injectable/fragment/injector/factory/interceptor output.
- [x] Migrate owned input fixtures and source assertions, and regenerate both golden trees.
- [x] Check source and classfile dependencies and marked/unmarked consumers; existing runtime optionality/caching behavior still passes.

## Validation

- Focused model/factory/injector/interceptor golden targets during iteration, followed by `bazel test //processor/src/test/java/sting/processor:all_tests`.
- `bazel test //:integration_tests` — verifies existing runtime semantics against new generated output.
- Focused new source/classfile type-use and migration-diagnostic cases prove AC2/AC3.
- Inspect dependency labels and resolved local module path to prove local source consumption.
- `git diff --check` and generated source inspection.

## Evidence

- Local Proton core/QA built from the override. Raw core omitted its formatter, so the hand-maintained local import consumes the locally built shaded core/source artifacts; integration compilation then passed.
- `bazel test //processor/src/test/java/sting/processor:nullability` passed source, classfile, unmarked, inherited nested/array signature, legacy declaration and unsupported shape cases.
- Regenerated model, factory, injector, interceptor and includes families; expected and expectedFormatted trees changed.
- `bazel test //processor/src/test/java/sting/processor:all_tests` passed all 12 targets. Duplicate Deprecated/SuppressWarnings from applying both overrideMethod and old copy steps were removed after focused warning failures identified them.
- `bazel test //:integration_tests` passed all three targets after migration and formatting.
- Targeted Java formatting/buildifier and `git diff --check` passed; generated checking itself remains T03 ownership.
