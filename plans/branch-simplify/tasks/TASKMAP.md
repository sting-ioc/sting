# Task Map

- Spec: [`SPEC.md`](../SPEC.md)
- Status: `implementation-review`
- Current frontier: `none`
- Planning reviewer: `/root/branch_simplify_planning_review` (`1/3` rounds, `Findings: none`)
- Plan checkpoint: `automatic`; the accepted grill and passing planning review authorize implementation.
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: `tools/check.sh`.
- Evidence: `tools/check.sh` exited 0; all 17 tests passed, artifacts/isolated consumer passed, coverage 95.11% lines and 86.20% branches, 2,725 site links with zero broken. See [T01 evidence](T01-simplify.md#evidence).

## Review and closeout

- Pass implementation review, delete this exact plan tree in a closeout commit, publish to PR #67, and verify both CI workflows.
- Evidence: `pending`

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| `T01` | [Simplify the branch and developer guide](T01-simplify.md) | `complete` | None |

## Sequencing notes

- Review and simplify the existing branch as one behavior-preserving slice, then close this plan and update PR #67.

## Promoted knowledge

- `not-required`: no canonical domain-documentation directories exist.
