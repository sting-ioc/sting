# Palantir format workers Spec

## Source and completed design tree

User authorizes implementation, commits, publishing, assignment to realityforge and auto-merge without bypassing CI. The explicit grill exception permits evidence-settled decisions without confirmation.

- Outcome → replace local formatter plumbing with released rules and worker actions; retain existing CI.
- Dependency → v0.1.1 archive override: latest release, BCR metadata returns 404. Downloaded SHA256 e21fe1d1c5663d0ffb8e45e7bd6ebdae60016df5ce3b3a17b8187780c243324f, archive prefix rules_palantir_java_format-0.1.1. Formatter stays 2.93.0.
- Coverage → user explicitly limits checks to Java reachable through target graph. Aspect follows deps/runtime_deps/exports/tests and checks JavaInfo workspace sources. Root targets: core, processor, server, doc-examples, all_tests. Fixture data filegroups and unmodeled GWT/J2CL sources are excluded; no extra enumeration gate.
- Developer commands → preserve write/check default and roots, add watch via public executable and wrapper. Existing root enumeration is replaced by public tool roots; check is bazel build //:java_format_check. Check must leave working tree and index untouched.
- Specialized consumers → preserve FORMATTER_JVM_FLAGS for processor generated-fixture tests and Buildr dependencies; these do not belong to obsolete local tooling.
- Delivery → isolated default-branch worktree; no duplicate open PR; temporary plan, implementation/evidence, closeout commits; read-only planning and implementation reviews; assign realityforge and enable allowed auto-merge.

## Problem and required outcome

Existing CI launches a one-shot local formatter with separate dependency plumbing. Formatting checks must use persistent PalantirJavaFormat actions supplied by the shared rules module while retaining CI enforcement and usable developer repair commands.

## Scope and constraints

Only formatting tooling, its obsolete dependency plumbing, lock regeneration and development documentation change. No unrelated dependency upgrades, Java formatting churn, new CI workflow, graph expansion or protection/settings changes. Preserve explicit Bazel source ownership and generated fixture test behavior. No domain artifact directories exist.

## Requirements and acceptance criteria

- R1 / AC1: immutable verified module pin and worker settings; action graph and execution log demonstrate PalantirJavaFormat worker actions across workspace graph.
- R2 / AC2: existing CI check invokes aggregate Bazel build; a dirty graph source fails with remediation and leaves source/index untouched, repaired source passes.
- R3 / AC3: write/watch public tools preserve existing explicit source roots and command conventions; exercise write repair and watch edit behavior; do not format expected generated fixtures.
- R4 / AC4: obsolete tools/java-format dependency/binary and active depgen references removed; lockfiles regenerated without unrelated upgrades; processor fixture formatting still works.
- R5 / AC5: focused checks and full tools/check.sh pass; attempt required Buildr tests and report environmental limitations honestly; clean diff and no temporary experiments. Reviewed PR assigned and auto-merge enabled if settings permit; precise blocker otherwise.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Release archive override | BCR lacks module | Immutable checksum; no local formatter dependencies | Inspect module pin |
| D2 | Graph-only check | Explicit user clarification | Data fixtures and unmodeled GWT/J2CL excluded; writer retains old roots | Inspect root target list and docs |
| D3 | Preserve specialized test flags | Actual fixture formatting needs compiler exports | Generated fixture behavior remains covered by processor tests | Processor suite result |
| D4 | Existing CI wrapper | Already enforced in ci.yml | No separate workflow needed | tools/check.sh outcome |

## Testing decisions

Verify checksum, shell syntax, buildifier, generated dependencies, aggregate action input coverage, worker execution logs, deliberate dirty-source failure and no write/index changes, write repair, watch edit, full Bazel CI including coverage, and Buildr test attempt. Restore temporary source edits and remove scratch output. CI blockers must be reported rather than described as passed.

## Open questions

None.
