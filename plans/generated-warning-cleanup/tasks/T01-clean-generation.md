# T01 — Generate clean checked code and document the supported state

- Status: pending
- Blocked by: None
- Spec coverage: R1–R5, AC1–AC5

## Delivers

Generated code without the named warning suppressions or redundant guards, preserving DI construction and nullness behavior, with current-state documentation and JSpecify-focused tests.

## Acceptance

- [ ] Remove named suppressions; resolve diagnostics by correcting generation.
- [ ] Omit unused fields and retain eager side effects, input signatures and lazy/optional caching.
- [ ] Remove legacy-annotation tests and preserve active generated checking controls.
- [ ] Regenerate both golden trees and verify runtime behavior.
- [ ] Humanize all PR-modified docs and prepare accurate PR prose.
- [ ] Pass tools/check.sh and prepare for implementation review; publication/CI and plan deletion follow review.

## Validation

- Focused nullability and relevant golden/integration targets.
- Inspect generated warning configuration and regenerated sources.
- Existing eager-only and input fixtures/runtime tests; add a meaningful regression only for uncovered behavior.
- Full tools/check.sh: formatting, build, artifacts, tests, coverage and docs/site links.
- Read-only implementation review followed by final PR CI.

## Evidence

Pending.
