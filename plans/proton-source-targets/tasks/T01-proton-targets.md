# T01 — Use Proton's public source targets

- Status: `complete`
- Blocked by: None
- Spec coverage: `R1`–`R4`, `AC1`–`AC4`

## Delivers

The processor, QA harness, shaded distribution and Javadocs use the requested Proton revision through its supported public Bazel targets, with local source linkage and a portable pin.

## Acceptance criteria

- [x] Module and ignored local configuration select the requested source revision.
- [x] Maven Proton entries are removed and generated dependency output is refreshed.
- [x] Processor and QA targets use the public imports; release shading retains Proton's bundled formatter.
- [x] Local and pinned builds pass required validation.
- [x] Docs and prepared PR context reflect the source dependency.

## Validation

- Inspect `git rev-parse HEAD` and status in Proton; inspect module graph/repository mapping.
- Focused processor/nullness/release build and artifact check.
- Inspect the actual test launcher classpath and confirm Sting's TestNG 7.10.2 runner precedes Proton QA's 6.8 dependency.
- Full `tools/check.sh` with local source linkage.
- Git-pinned release build and isolated packaged-consumer check without the local override.
- Inspect diff and prepare the PR-description update. Implementation review, publication and CI confirmation follow task completion.

## Evidence

- Local Proton remains clean at the exact requested revision; remote master matches. `.bazelrc.local` selects that path and is ignored.
- `tools/update_java_deps.sh` passed and removed Maven Proton repositories/imports.
- Local processor/release build, focused nullability test and packaged-consumer check passed.
- Test launcher originally selected Proton QA's TestNG 6.8 first. Moving Sting's declared runner before other test dependencies selects 7.10.2 at classpath position 1, before 6.8 at position 24; the focused test and full gate passed afterward.
- `tools/check.sh` passed: 17 test targets; 95.18% line and 86.23% branch coverage; 2,723 site links, zero broken. Evidence: `/tmp/sting-proton-source-full.log`.
- With the local rc disabled, `bazel mod show_repo proton_project` selected the requested Git commit; processor/release build, nullability test and isolated packaged consumer passed. The local rc was restored in `finally`. Evidence: `/tmp/sting-proton-source-pinned.log`.
- Development documentation and `../PR_DESCRIPTION.md` explain source targets, the pin and local override. The prior artifact-check simplification remains unchanged.
