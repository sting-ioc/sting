# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: None; T01 complete.
- Planning reviewer: `/root/proton_targets_planning_review` (`1/3` rounds), Findings: none.
- Plan checkpoint: `automatic` — accepted grill, explicit source-override request and passing planning review.
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: `tools/check.sh` locally; release build and packaged-consumer check from the Git pin.
- Evidence: `tools/check.sh` passed with all 17 tests, 95.18% line and 86.23% branch coverage, and zero broken links out of 2,723. Git-pinned processor/release build, focused nullability test and packaged-consumer check passed with the local rc disabled; local rc restored. See T01.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Use Proton's public source targets](T01-proton-targets.md) | complete | None |

## Sequencing notes

- One slice covers the complete dependency change. The local checkout is read-only throughout.
- After implementation review, publish the prepared PR update, confirm CI, record the result and remove the plan.

## Promoted knowledge

- Not required: no domain documentation directories exist.
