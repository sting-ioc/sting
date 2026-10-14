# T01 — Generate clean checked code and document the supported state

- Status: complete
- Blocked by: None
- Spec coverage: R1–R5, AC1–AC5

## Delivers

Generated code without the named warning suppressions or redundant guards, preserving DI construction and nullness behavior, with current-state documentation and JSpecify-focused tests.

## Acceptance

- [x] Remove named suppressions; resolve diagnostics by correcting generation.
- [x] Omit unused fields and retain eager side effects, input signatures and lazy/optional caching.
- [x] Remove legacy-annotation tests and preserve active generated checking controls.
- [x] Regenerate both golden trees and verify runtime behavior.
- [x] Humanize all PR-modified docs and prepare accurate PR prose.
- [x] Pass tools/check.sh and prepare for implementation review; publication/CI and plan deletion follow review.

## Validation

- Focused nullability and relevant golden/integration targets.
- Inspect generated warning configuration and regenerated sources.
- Existing eager-only and input fixtures/runtime tests; add a meaningful regression only for uncovered behavior.
- Full tools/check.sh: formatting, build, artifacts, tests, coverage and docs/site links.
- Read-only implementation review followed by final PR CI.

## Evidence

- Removing suppressions exposed unused eager fields and repeated fields for the same fragment. Node usage now controls eager storage; ComponentGraph deduplicates fragment descriptors before assigning instances.
- Generated binding arguments, required inputs and provider results no longer receive redundant requireNonNull wrappers. Redundant lazy-cache assertions and the null part of interceptor argument assertions are removed; allocation branches/flags, nullable collection filtering and argument-count assertions remain.
- The broad Varifier suppression is removed. Around argument arrays use var with nullable element annotations on their new-array expressions; focused NullAway and strict integration compilation passed.
- The fixture compiler registers all three named Error Prone checkers at error severity. Its generated negative control proves two NullAway errors and one error from each named checker. The legacy declaration matrix is deleted.
- Existing eager/runtime coverage passed. A new regression for an unused public eager proxy exposed an invalid cast statement; omitting the cast when the result is discarded fixes it and verifies target/interceptor construction order. Null argument-array contract violations now naturally throw NullPointerException rather than AssertionError; the existing runtime expectation was updated.
- All five golden families regenerated both output trees and passed (54 model, 3 factory, 51 injector, 112 interceptor, 4 includes cases).
- Audited all six PR-modified docs. Humanized the changed sections, removed migration instructions/framing and historical runtime-version evidence, updated the nullness anchor, and retained current setup/contracts. Factories already documented the current state and needed no further edit. Prepared PR prose in ../PR_DESCRIPTION.md.
- Final tools/check.sh passed: all 17 test targets; 95.11% line coverage (4379/4604), 86.20% branch coverage (1812/2102); artifact isolation/Java 17 checks; 2,723 site links, zero broken. Evidence: /tmp/sting-generated-cleanup-full-final.log.
- Diff check and scans confirm no named suppressions or Varifier suppression in generators/goldens, no legacy annotation tests, and no migration framing in the audited docs. Proton source wiring and the simplified artifact checker are unchanged.
