# Branch Simplification Spec

## Source

- Shared understanding: the accepted JSpecify/NullAway grill and subsequent user instructions; the current request asks for developer guidance, a branch-wide simplify pass, and completed closeout.
- Repository evidence: PR #67 at `f8c5838b`; the previous plan's passing review and deletion are recorded in `fdfb1d11` and `f8c5838b`. The earlier simplify pass covered the artifact checker rather than the whole branch.

## Problem

The development guide contains implementation commentary where contributors need instructions. The branch also needs a complete simplification review before human review.

## Required outcome

Contributors can find the commands and conventions needed to develop, check, and release Sting. Branch changes stand on their own, preserve the accepted behavior, and have a recorded simplify pass and completed delivery closeout.

## Scope

- In scope: review all branch changes for unnecessary abstraction, exposure, stale names/comments, migration prose, and redundant work; correct actionable findings; publish the corrections to PR #67.
- Out of scope: new features, changes to accepted nullness/DI behavior, Proton changes, GWT/J2CL checks, and merging the PR.

## Constraints

- Preserve behavior exactly, including the accepted JSpecify contracts, unsupported DI forms, and legacy-boundary diagnostics.
- Keep the Proton pin and ignored local override unchanged.
- Edit only branch-related code and documentation; do not broaden unrelated cleanup.

## Requirements

- `R1`: The development guide gives actionable build, test, dependency, nullness, hermeticity, website, and release guidance without migration or implementation history.
- `R2`: The full branch has a simplify review and corrections that preserve its accepted behavior.
- `R3`: Corrections pass relevant checks, implementation review, and closeout; the existing PR is ready for human review.

## Acceptance criteria

- `AC1` (`R1`): The guide retains usable commands and existing linked section anchors. README and site links point to current nullness documentation.
- `AC2` (`R2`): Evidence records semantic code/build/test/release review and mechanical annotation/fixture review; actionable simplifications are implemented without changing generated output or runtime behavior.
- `AC3` (`R3`): Focused checks and `tools/check.sh` pass; implementation review reports `Findings: none`; the temporary plan is removed in a closeout commit and both published CI workflows pass.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| `D1` | Keep developer actions and necessary constraints; remove implementation narration. | Developers need to know what to do and when. | The guide becomes shorter while retaining workflows and toolchain limits. | Review the development guide for sufficient practical instructions. |
| `D2` | Simplify within the accepted contracts without redesigning them. | This is a cleanup pass on a reviewed migration. | Generated output, DI behavior, Proton integration, and nullness diagnostics stay stable. | Review the small code diff and passing checks. |

## Technical decisions

- Preserve existing documentation anchors used by setup and agent guidance.
- Use private helpers by default and names that describe current behavior.
- Keep release checks that verify packaging contracts; remove only redundant operations.

## Testing decisions

- Run affected processor tests and Python syntax/artifact checks before the full repository gate.
- Existing golden tests verify unchanged generated output; no new implementation-mirroring tests are needed.
- Inspect README anchors separately because the site checker does not cover README.
- Use one read-only planning reviewer and a fresh read-only implementation reviewer.

## Open questions

None.
