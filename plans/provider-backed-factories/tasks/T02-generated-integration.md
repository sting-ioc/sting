# T02 — Prove generated implementation integration and document delivery

- Status: pending
- Blocked by: T01
- Spec coverage: R3, R5, R6; AC3, AC5, AC6

## Delivers

Provider-backed factories cooperate with another source generator across compiler rounds and compiled dependencies. The actual Arez proof demonstrates the intended consumer, and public documentation explains the complete contract.

## Acceptance criteria

- [ ] Later-generated implementation and later-resolved constructor parameter types succeed with both processor orders; a genuinely absent implementation has a useful final error.
- [ ] Separately compiled mapping metadata and implementation remain usable under the existing parameter-name metadata requirement.
- [ ] Actual Arez 0.256-generated implementation compiles/runs with the changed Sting processor, proving shared injected service and separate caller-valued products without product Injectable.
- [ ] Factory/StingProvider/framework-integration docs and changelog explain precedence, construction validation and existing dependency matching.
- [ ] tools/check.sh passes; committed evidence records the integration proof and verification limitations accurately.

## Validation

- Current focused processor suite with JSpecify/NullAway-checked generation and multistage scenarios; isolated actual-Arez javac compile/run with pinned release artifacts and no handwritten Arez implementation.
- tools/check.sh, git diff --check, source review and documentation link checks proportionate to changed docs.

## Evidence

Pending. Temporary proof scaffolding belongs inside the active plan tree and is removed at closeout; the integration result and exact reproduction details survive in this task's evidence commit and PR.
