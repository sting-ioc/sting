# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementation-review
- Current frontier: implementation review
- Accepted scope and execution authority: SPEC.md Source; final interview confirmation and explicit implementation/PR/babysit instructions in this chat.
- Planning reviewer: /root/planning_review — Findings: none in rounds 1 and 2/3; final confirmation unused. Round 2 verified current JDK 21/JSpecify harness, aggregate wiring, strengthened CI and evidence/cleanup ownership after the upstream update.
- Plan checkpoint: automatic — unchanged accepted design and implementation authority, with passing planning re-review after the upstream update.
- Implementation reviewer: /root/implementation_review — round 1/5 found one P2: validation used host Bazel 9.3.0 instead of pinned 9.3.1. Corrected the temporary runner and reran full gate and actual-Arez proof with verified 9.3.1; both passed, no code changes. Round 2 confirmation pending. Four regular rounds and final confirmation remain.

## Full-scope validation

- Gate: tools/check.sh, plus the actual Arez compile/run proof in T02.
- Evidence: tools/check.sh and actual-Arez proof rerun successfully with verified pinned Bazel 9.3.1 / JDK 21 on the a586a1dc base: all 18 test targets, all 14 coverage targets (95.53% lines / 86.73% branches), package and external consumers, formatting/hermeticity, and site (2669 links, none broken). Focused factory scenarios: 25/25. Actual Arez 0.256 class/interface compile/run proof passed in both processor orders on JDK 21. T02 owns reproduction details and warning limits. Pre-rebase counts in T01 are historical only.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Resolve and validate factory construction mappings](T01-construction-mapping.md) | complete | None |
| T02 | [Prove generated implementation integration and document delivery](T02-generated-integration.md) | complete | T01 |

## Sequencing and review

Primary agent implements tasks sequentially. Independent reviewers remain read-only. Each task commits implementation and task evidence together. T02 runs the full gate before its commit. After passing implementation review, remove exactly plans/provider-backed-factories/ in a closeout commit, publish a non-draft PR, and invoke babysit-pr for that pinned PR. No merge authority is granted.

## Upstream reconciliation

Before publication, fetch revealed master had advanced from 9081183c to a586a1dc with JDK 21, JSpecify and a reorganized processor harness. Rebased the unpublished branch and kept upstream owners/build structure. T02 adapts its test integration and actual-Arez proof to the current compiler; full current tools/check.sh is required. Planning re-review attempt 2/3 passed with Findings: none; the initial pass remains round 1. No user-facing decision or scope changed.

## Promoted knowledge

Not required: this repository has no docs/adr, docs/glossary, docs/specs or docs/deferred directories. Existing public API documentation owns the lasting contract.
