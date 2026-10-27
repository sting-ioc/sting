# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: planned
- Current frontier: T02
- Accepted scope and execution authority: SPEC.md Source; final interview confirmation and explicit implementation/PR/babysit instructions in this chat.
- Planning reviewer: /root/planning_review — Findings: none in rounds 1 and 2/3; final confirmation unused. Round 2 verified current JDK 21/JSpecify harness, aggregate wiring, strengthened CI and evidence/cleanup ownership after the upstream update.
- Plan checkpoint: automatic — unchanged accepted design and implementation authority, with passing planning re-review after the upstream update.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh, plus the actual Arez compile/run proof in T02.
- Evidence: pending against current upstream. Pre-rebase suites passed on 9081183c; those counts do not substitute for current validation.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Resolve and validate factory construction mappings](T01-construction-mapping.md) | complete | None |
| T02 | [Prove generated implementation integration and document delivery](T02-generated-integration.md) | in_progress | T01 |

## Sequencing and review

Primary agent implements tasks sequentially. Independent reviewers remain read-only. Each task commits implementation and task evidence together. T02 runs the full gate before its commit. After passing implementation review, remove exactly plans/provider-backed-factories/ in a closeout commit, publish a non-draft PR, and invoke babysit-pr for that pinned PR. No merge authority is granted.

## Upstream reconciliation

Before publication, fetch revealed master had advanced from 9081183c to a586a1dc with JDK 21, JSpecify and a reorganized processor harness. Rebased the unpublished branch and kept upstream owners/build structure. T02 adapts its test integration and actual-Arez proof to the current compiler; full current tools/check.sh is required. Planning re-review attempt 2/3 passed with Findings: none; the initial pass remains round 1. No user-facing decision or scope changed.

## Promoted knowledge

Not required: this repository has no docs/adr, docs/glossary, docs/specs or docs/deferred directories. Existing public API documentation owns the lasting contract.
