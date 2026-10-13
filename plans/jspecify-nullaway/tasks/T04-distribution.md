# T04 — Distribution, documentation and full validation

- Status: complete
- Blocked by: T03
- Spec coverage: R6, R7, AC5 (packaging/rollout), AC6

## Delivers

Release artifacts, documentation and examples expose the JSpecify contract and local Proton implementation, with complete migration and build evidence.

## Acceptance criteria

- [x] Shade the local Proton implementation into the processor release jar and preserve its isolated processorpath/no-runtime-POM-dependency contract.
- [x] Resolve required JSpecify classes on the processor's own classloader without relocating canonical annotation names or adding runtime POM dependencies.
- [x] Replace direct legacy annotation dependencies and Javadoc classpaths; regenerate dependency outputs through owned configuration.
- [x] Artifact checks verify JSpecify API metadata and absence of legacy annotation usage without rejecting legitimate processing/Java EE names or diagnostic strings.
- [x] Document consumer migration, unmarked trust boundary, Java 17/full-mode configuration and replacing the local override after Proton release.
- [x] Update changelog and repository annotation guidance; do no GWT/J2CL work.
- [x] Run and record the strongest full-scope gate before committing this task.

## Validation

- `bazel build //tools/release:maven_artifacts` and `python3 tools/release/check_artifacts.py` — prove packaged local implementation and published contracts.
- Compile a real consumer with core/JSpecify on the compilation classpath and only the shaded processor jar on the processorpath; exercise generation and nested nullness preservation. This proves runtime classloader resolution separately from archive shape checks.
- `tools/check.sh` — checks buildifier, hermeticity, Java formatting, build, tests, coverage, release artifacts and site/Javadoc.
- Explicit generated NullAway controls and owned legacy-reference scan with only diagnostic/rejection-fixture exceptions.
- Inspect complete diff and `git diff --check`.

## Evidence

- `bazel build //tools/release:maven_artifacts` and `python3 tools/release/check_artifacts.py` passed: all 12 artifacts, canonical JSpecify runtime classes, local Proton conversion API and expected POM contracts.
- The packaged consumer compiles with warnings as errors, inherits nested/array contracts from a compiled parent, generates formatted interception code with only the shaded processor jar on the processorpath, and runs with only core/JSpecify plus its own classes.
- Final `tools/check.sh` passed under Node 24.19.0: formatting, unchanged Ahab baseline, builds/artifacts, all 16 targets, coverage, Javadocs/site and 2,114 local links with zero broken. Coverage: 95.17% lines (4390/4613), 86.22% branches (1814/2104), above 94%/85% gates. The subsequent project-setup doc update passed a site rebuild/check with 2,116 links and zero broken.
- The full gate now scans owned sources, all fixture/golden trees and current documentation for legacy annotation references. Only migration detection and deliberate rejection tests remain. A temporary orphan-golden negative control was rejected; all generated golden files have NullMarked types. `git diff --check` passed.
- Added six missing eager, recursive-include and provider-naming positive compilation cases and regenerated their existing goldens after the final scan exposed legacy annotations in dormant outputs.
- Packaging validation exposed unchecked parameterized casts in around proxies; scoped javac unchecked suppressions preserve their existing runtime casts while NullAway remains active. Inherited signature tests now also require no warnings.
- A regression reproduced missed legacy annotations on factory runtime constructor parameters; matched constructor parameters now receive migration validation, and the focused diagnostic test/full gate passed.
- Packaging/docs landed in b6d78302 while T04 remained in progress. This completion snapshot includes the final fixture scan and missing golden coverage.
