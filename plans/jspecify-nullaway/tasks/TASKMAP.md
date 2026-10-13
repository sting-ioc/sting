# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementing`
- Current frontier: `T04`
- Planning reviewer: `/root/sting_planning_review` (`2/3` rounds); Findings: none on round 2. Round 1's isolated processorpath gap is resolved in AC5/T04.
- Plan checkpoint: `automatic`; authorized by the user's confirmed grill and amendments plus passing planning review.
- Implementation reviewer: `pending` (`0/5` rounds)
- External Proton chat: `01a13718-d085-7a71-9872-93caf7fa46cf` on `local`, owned checkout `/Users/peter/Code/realityforge/proton`

## Full-scope validation

- Gate: `tools/check.sh` and explicit generated NullAway positive/negative controls plus owned legacy-reference scan.
- Evidence: pending.
- Baseline: `bazel build //core //server` passed; `bazel test //:all_tests` passed all 15 targets before implementation. Resolved Java toolchain release is Zulu OpenJDK 17.0.20.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Runtime JSpecify contracts](T01-runtime-contracts.md) | complete | None |
| T02 | [Local Proton and generated contract cutover](T02-processor-cutover.md) | complete | T01; consumable local Proton generator APIs |
| T03 | [Default and generated NullAway enforcement](T03-enforcement.md) | complete | T02 |
| T04 | [Distribution, documentation and full validation](T04-distribution.md) | in_progress | T03 |

## Sequencing notes

- Implement tasks sequentially; only the lowest-numbered unblocked pending task is active.
- Proton is a separate user-owned chat executing the same accepted grill and structured workflow. T01 is independent; T02 consumes its local API when available, without a release gate.
- Current opt-in compiler enforcement remains during early migration slices; T03 completes the hard cut to default enforcement. This is execution sequencing, not a final compatibility path.
- T02 owns the coherent mechanical migration across generation families and positive/negative input fixtures. T03 owns checked generated compilation; ordinary golden compilation alone is insufficient.
- Preserve the local source override at closeout because the user requested it until Proton can be released.
- Final task must record full-gate evidence before its commit; implementation review and closeout follow.

## Promoted knowledge

- not-required: no docs/adr, docs/glossary, docs/specs or docs/deferred directories exist.

- Proton delivery completed: https://github.com/realityforge/proton/pull/10, open against master; both CI checks passed. Local shaded core supplies formatter classes omitted from raw JavaInfo.
