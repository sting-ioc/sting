# T02 — Local Proton and generated contract cutover

- Status: pending
- Blocked by: T01; consumable local Proton generator APIs
- Spec coverage: R2, R3, R4, R6, AC2, AC3, AC5 (source integration)

## Delivers

Sting consumes the unreleased local Proton source module and processes/generates JSpecify-only contracts across all generation families, preserving existing DI semantics.

## Acceptance criteria

- [ ] Add the local source module override and bind processor/test dependencies to Proton core/QA source targets without waiting for release.
- [ ] Make service matching, include discovery and interceptor type validation independent of type-use display annotations.
- [ ] Read exact outer JSpecify nullness; reject relevant legacy declarations and unsupported nullable DI forms.
- [ ] Emit marked types and preserve explicit type-use contracts, nullable cache state and runtime checks in injectable/fragment/injector/factory/interceptor output.
- [ ] Migrate owned input fixtures and source assertions, and regenerate both golden trees.
- [ ] Check source and classfile dependencies and marked/unmarked consumers; existing runtime optionality/caching behavior still passes.

## Validation

- Focused model/factory/injector/interceptor golden targets during iteration, followed by `bazel test //processor/src/test/java/sting/processor:all_tests`.
- `bazel test //:integration_tests` — verifies existing runtime semantics against new generated output.
- Focused new source/classfile type-use and migration-diagnostic cases prove AC2/AC3.
- Inspect dependency labels and resolved local module path to prove local source consumption.
- `git diff --check` and generated source inspection.

## Evidence

- pending
