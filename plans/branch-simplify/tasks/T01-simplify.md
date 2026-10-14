# T01 — Simplify the branch and developer guide

- Status: `complete`
- Blocked by: `None`
- Spec coverage: `R1`, `R2`, `R3`, `AC1`, `AC2`, `AC3`

## Delivers

Actionable developer guidance and a branch-wide simplify pass, validated and published to the existing PR with completed closeout.

## Acceptance criteria

- [x] Rewrite the development guide around developer actions while retaining required constraints and linked anchors.
- [x] Review semantic branch changes and mechanical source/fixture changes; correct stale names, comments, visibility, links, and redundant work without changing behavior.
- [x] Run focused and full checks and record evidence for implementation review and closeout.

## Validation

- Review final prose against existing commands/configuration and verify README anchors.
- Run affected processor golden/nullability tests and Python syntax/artifact checks.
- Run `tools/check.sh` for formatting, build, tests, coverage, hermeticity, artifacts, and site links.
- Compare final generated-code goldens and inspect the complete correction diff.
- At workflow closeout, verify the closeout commit, clean worktree, preserved Proton override, and published CI status.

## Evidence

- Guide rewritten as developer instructions: build/test/formatting/goldens, dependency updates and local Proton override, nullness conventions/compiler options, Ahab checks, site registration/preview, and release steps. Reduced from 1,152 to 708 words; retained all existing linked section anchors. Humanize patterns checked against the prose.
- Inventoried all 872 changed branch paths. Directly reviewed semantic changes in service identity, nullness validation, all generators, graph storage/fragments, Invocation, transaction interceptors, compiler/test wrappers, Bazel/toolchain/dependency/release configuration, artifact validation, site nullness changes, integration regression tests, and all changed documentation. Reviewed mechanical annotation/BUILD changes and sampled optional request/provider output in both golden trees.
- Scanned all 827 changed Java files for legacy nullability references: only accepted processor rejection strings remain. The fixture inventory includes 321 input and 194 files in each generated Java tree. Scanned added non-fixture code comments for history or obsolete behavior descriptions.
- Simplifications: private generator helper renamed to describe optional storage; test source helper made private after confirming no external callers; erased-conversion and invalid-array comments describe present constraints; removed a redundant JAR open around an existing entry set; corrected README's stale migration link to the current nullness anchor. No new abstraction or compatibility path added.
- Retained the separate packaged-consumer check because it owns a distinct release contract and keeps the main artifact inventory readable. Kept shared/recursive nullness helpers and required erasure handling; they implement accepted behavior.
- `bazel test //processor/src/test/java/sting/processor:{nullability,injector_goldens,interceptor_goldens}` — all three passed (run with explicit labels).
- In-memory Python compile — checker syntax passed without creating cache files. README nullness target and five existing development-guide anchors verified directly.
- Compared generator source after the private rename/comment removal — emission bodies unchanged. `git diff --exit-code f8c5838b -- processor/src/test/fixtures/expected processor/src/test/fixtures/expectedFormatted` — no golden changes.
- `tools/check.sh` — exit 0: formatting/buildifier, Ahab baseline, module/artifact builds, isolated packaged-consumer compile/run, all 17 test targets, coverage, and site build/link checks passed. Line coverage 95.11% (4379/4604); branch coverage 86.20% (1812/2102); 2,725 local file/fragment links, zero broken.
- `git diff --check` — passed. Proton pin/build dependencies unchanged; local checkout clean at `2dec0fee99c5c5b3707f9f8e8b876ec800e2c40b`, with the ignored `.bazelrc.local` override intact.
- Prior closeout audit: `fdfb1d11` recorded passing implementation review and `f8c5838b` deleted the prior plan; both CI runs on that head passed. This followup records the missing branch-wide simplify pass and follows its own review/closeout gates.
- Rendered development guide checked for all six section anchors and removal of implementation commentary. Fresh implementation reviewer `/root/branch_simplify_implementation_review`, round 1/5: `Findings: none`; closeout/publication/CI remain the workflow's final gates.
