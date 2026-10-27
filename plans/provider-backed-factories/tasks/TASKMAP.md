# Task map

- Spec: [SPEC.md](../SPEC.md)
- Status: implementing
- Current frontier: T02
- Accepted scope and execution authority: SPEC.md Source; final interview confirmation and explicit implementation/PR/babysit instructions in this chat.
- Planning reviewer: /root/planning_review — Findings: none, round 1/3; final confirmation unused. Reviewed full tree, scope, coverage, processor/test/build evidence and real Arez release POMs.
- Plan checkpoint: automatic — explicit implementation authority, completed grill-me confirmation, and passing independent planning review; no human plan checkpoint requested.
- Implementation reviewer: pending (0/5 rounds; final confirmation unused)

## Full-scope validation

- Gate: tools/check.sh, plus the actual Arez compile/run proof in T02.
- Evidence: pending. Legacy Buildr requires unavailable Bundler; current GitHub CI uses tools/check.sh.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Resolve and validate factory construction mappings](T01-construction-mapping.md) | complete | None |
| T02 | [Prove generated implementation integration and document delivery](T02-generated-integration.md) | pending | T01 |

## Sequencing and review

Primary agent implements tasks sequentially. Independent reviewers remain read-only. Each task commits implementation and task evidence together. T02 runs the full gate before its commit. After passing implementation review, remove exactly plans/provider-backed-factories/ in a closeout commit, publish a non-draft PR, and invoke babysit-pr for that pinned PR. No merge authority is granted.

## Promoted knowledge

Not required: this repository has no docs/adr, docs/glossary, docs/specs or docs/deferred directories. Existing public API documentation owns the lasting contract.
