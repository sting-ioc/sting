# T01 — Migrate formatting and verify enforcement

- Status: complete
- Blocked by: None
- Spec coverage: R1–R5, AC1–AC5

## Delivers

Shared released formatter rules with graph-only worker check in existing CI, public write/watch wrappers, removed obsolete plumbing, regenerated locks and accurate developer docs.

## Acceptance criteria

- [x] Verified pin, worker strategy and graph-only target list; no Java churn.
- [x] Dirty check fails without changing source/index, reports write command; repaired check passes.
- [x] Public write/watch commands exercised with existing roots; expected generated fixtures remain untouched.
- [x] Obsolete formatter dependency path gone; generated outputs stable and processor tests pass.
- [x] Full CI gate, Buildr attempt, diff inspection and clean experiments; implementation review gates publication.

## Validation

Checksum, bash -n, buildifier, depgen regeneration stability, aquery coverage, execution log worker strategy, dirty/repair/watch experiments, tools/check.sh, bundle exec buildr test, git diff --check and final diff inspection.

## Evidence

- Release archive downloaded and SHA256 verified as e21fe1d1c5663d0ffb8e45e7bd6ebdae60016df5ce3b3a17b8187780c243324f; prefix verified. Module BCR metadata returned 404.
- bash -n passed for all changed shell scripts. Buildifier and repeated depgen regeneration passed with no generated drift.
- aquery with include_aspects over deps(//:java_format_check) reported 30 PalantirJavaFormat actions covering exactly 201 Java source labels from the selected root graph. Restored checkout action arguments match the pre-pause snapshot exactly.
- Initial execution JSON contained 30 format records, all runner=worker; worker_verbose logged one singleplex PalantirJavaFormat worker. The strategy and max-instance settings are committed.
- Deliberately dirty StingProvider.java failed check with its path and tools/java_format.sh write remediation. Both working-tree bytes and separately staged dirty bytes remained unchanged. Write repaired the worktree and retained staged bytes. Watch wrapper repaired a subsequent edit. Expected/expectedFormatted/unresolved fixture hashes stayed unchanged. Experiment passed again after restoration; source/index restored and no Java diff remains.
- tools/check.sh passed after resumption: stable generated outputs, Buildifier, formatting, four module builds, all five test targets and both coverage suites. Coverage: 3889/4105 lines (94.74%), 1827/2112 branches (86.51%); thresholds 94%/85% passed. Full log /tmp/sting-resumed-full-check.log.
- Before pause bundle exec buildr test initially failed in downstream CollectDrumLoopBuildStats sample build. A retry exited 0 (cached build outputs, integration/server suites rerun); no Buildr code/dependencies changed. Local run log /tmp/sting-buildr-test.log.
- Initial full gate failed with No space left on device during coverage. User paused the retry. On resume the removed worktree was reconstructed from the preserved branch and recorded edits, disposable task-created outputs were cleaned, and the full gate passed. No environment blocker remains.
- rules_java 9.9.0 is selected by the formatter module; direct pin aligned to that requirement. Root-only visibility on five test targets permits aspect traversal through existing test suites. No main Maven version changes or Java churn.
- git diff --check and final source/scope inspection passed. No ahab tooling exists; active obsolete formatter references removed. Older historical phase-1 plans retained.
- Publication, assignment, exact-head CI and auto-merge follow implementation review and closeout; not claimed complete at this checkpoint.
