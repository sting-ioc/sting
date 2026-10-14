# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: `planned`
- Current frontier: `T01`
- Planning reviewer: `/root/proton_targets_planning_review` (`1/3` rounds), Findings: none.
- Plan checkpoint: `automatic` — accepted grill, explicit source-override request and passing planning review.
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: `tools/check.sh` locally; release build and packaged-consumer check from the Git pin.
- Evidence: pending.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Use Proton's public source targets](T01-proton-targets.md) | pending | None |

## Sequencing notes

- One slice covers the complete dependency change. The local checkout is read-only throughout.
- After implementation review, publish the prepared PR update, confirm CI, record the result and remove the plan.

## Promoted knowledge

- Not required: no domain documentation directories exist.
