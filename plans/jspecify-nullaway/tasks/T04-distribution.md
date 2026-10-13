# T04 — Distribution, documentation and full validation

- Status: pending
- Blocked by: T03
- Spec coverage: R6, R7, AC5 (packaging/rollout), AC6

## Delivers

Release artifacts, documentation and examples expose the JSpecify contract and local Proton implementation, with complete migration and build evidence.

## Acceptance criteria

- [ ] Shade the local Proton implementation into the processor release jar and preserve its isolated processorpath/no-runtime-POM-dependency contract.
- [ ] Resolve required JSpecify classes on the processor's own classloader without relocating canonical annotation names or adding runtime POM dependencies.
- [ ] Replace direct legacy annotation dependencies and Javadoc classpaths; regenerate dependency outputs through owned configuration.
- [ ] Artifact checks verify JSpecify API metadata and absence of legacy annotation usage without rejecting legitimate processing/Java EE names or diagnostic strings.
- [ ] Document consumer migration, unmarked trust boundary, Java 17/full-mode configuration and replacing the local override after Proton release.
- [ ] Update changelog and repository annotation guidance; do no GWT/J2CL work.
- [ ] Run and record the strongest full-scope gate before committing this task.

## Validation

- `bazel build //tools/release:maven_artifacts` and `python3 tools/release/check_artifacts.py` — prove packaged local implementation and published contracts.
- Compile a real consumer with core/JSpecify on the compilation classpath and only the shaded processor jar on the processorpath; exercise generation and nested nullness preservation. This proves runtime classloader resolution separately from archive shape checks.
- `tools/check.sh` — checks buildifier, hermeticity, Java formatting, build, tests, coverage, release artifacts and site/Javadoc.
- Explicit generated NullAway controls and owned legacy-reference scan with only diagnostic/rejection-fixture exceptions.
- Inspect complete diff and `git diff --check`.

## Evidence

- pending
