# T01 — Migrate formatting and verify enforcement

- Status: pending
- Blocked by: None
- Spec coverage: R1–R5, AC1–AC5

## Delivers

Shared released formatter rules with graph-only worker check in existing CI, public write/watch wrappers, removed obsolete plumbing, regenerated locks and accurate developer docs.

## Acceptance criteria

- [ ] Verified pin, worker strategy and graph-only target list; no Java churn.
- [ ] Dirty check fails without changing source/index, reports write command; repaired check passes.
- [ ] Public write/watch commands exercised with existing roots; expected generated fixtures remain untouched.
- [ ] Obsolete formatter dependency path gone; generated outputs stable and processor tests pass.
- [ ] Full CI gate, Buildr attempt, diff inspection and clean experiments; delivery reviewed before publication.

## Validation

Checksum, bash -n, buildifier, depgen regeneration stability, aquery coverage, execution log worker strategy, dirty/repair/watch experiments, tools/check.sh, bundle exec buildr test, git diff --check and final diff inspection.

## Evidence

Pending.
