# Bazel Phase 1 Implementation Plan

Status: accepted
Last updated: 2026-07-04

## Phase Sequence

1. Planning and review
   - Emit planning artifacts.
   - Request user review.
   - Record user approval before implementation.

2. Formatting-only change
   - Add formatter dependency/tooling only enough to run formatting if needed locally, or use the forthcoming formatter path.
   - Format maintained Java source/test code plus `processor/src/test/fixtures/bad_input/**` and `processor/src/test/fixtures/input/**`.
   - Exclude expected-output fixtures.
   - Validate formatting did not change test meaning with targeted Buildr or Bazel checks available at that point.
   - Commit this change separately before committing formatter enforcement.

3. Bazel foundation
   - Add `.bazelversion`, `.bazelrc`, `MODULE.bazel`, root `BUILD.bazel`, strict Java macros, TestNG macro, depgen config, buildifier target, and tool scripts.
   - Add `third_party/java` dependencies matching `build.yaml`.
   - Add `tools/java-format` dependency/tooling and formatter check.
   - Add GitHub Actions workflow running `tools/check.sh`.

4. Module build targets
   - Add coarse BUILD targets for `core`, `processor`, `server`, `doc-examples`, `integration-tests`, and `server-integration-tests`.
   - Include `Sting.gwt.xml` as a `core` resource.
   - Exclude all existing `*/generated/**`.
   - Add explicit `//processor:sting_processor_plugin`.

5. Test targets
   - Add TestNG targets in packages with the test sources.
   - Use one target per source package/module unless a test class needs special flags.
   - Model processor fixtures as runfiles data.
   - Ensure processor and server unit test targets are distinct coverage gate inputs.

6. Error Prone and strict-deps iteration
   - Run targeted builds/tests.
   - Fix low-churn source issues caused by Error Prone or strict deps.
   - Stop and ask before broad source churn or test-specific weakening.

7. Coverage gate
   - Run Bazel coverage for processor and server unit-test targets only.
   - Filter measured files to processor/server main source trees.
   - Set initial line/branch thresholds from first passing report, rounded down slightly.
   - Add/adjust `tools/check_coverage.py` in `jdbt` style.

8. Final verification
   - Run `tools/check.sh`.
   - Ensure stale depgen detection works.
   - Ensure no Travis CI infrastructure remains.
   - Update task board evidence and commits.

## Delivery Approach

- Execute one task at a time with minimal diffs.
- Keep Buildr and Bazel parallel.
- Prefer existing patterns from `/Users/peter/Code/realityforge/jdbt` for Bzlmod, tools, CI, buildifier, depgen, formatter, and coverage.
- Prefer `/Users/peter/Code/stocksoftware/replicant/third_party/java/rules.bzl` for TestNG macro shape.
- Keep generated depgen sections machine-owned and hand-written aliases above generated blocks.
- Keep plan/task board/docs aligned with behavior changes.

## Required Full Gate

```bash
tools/check.sh
```

## Targeted Validation During Implementation

```bash
tools/update_java_deps.sh
bazel run //:buildifier_check
tools/java_format.sh check
bazel build //...
bazel test //...
bazel coverage //processor/src/test/java/sting/processor:all_tests --combined_report=lcov
bazel coverage //server/src/test/java/sting/server/interceptors:all_tests --combined_report=lcov
```

Coverage command labels may be adjusted to match final target names, but coverage scope must remain processor/server unit tests only.

## High-Risk Areas And Mitigations

- Error Prone churn
  - Impact: Could turn build migration into broad source cleanup.
  - Mitigation: Fix low-churn findings only; stop and ask before broad churn or weaker test rules.

- Annotation processor classpath and generated sources
  - Impact: Server/doc/integration tests may fail to compile generated types.
  - Mitigation: Use explicit `java_plugin`, direct deps, and formatter `--add-exports` where annotation processing needs them.

- Processor fixture hermeticity
  - Impact: Tests may assume Buildr working directories.
  - Mitigation: Pass `sting.fixture_dir` through runfiles-aware paths and model fixtures as `data`.

- Depgen output drift
  - Impact: CI may pass with stale generated Bazel files.
  - Mitigation: `tools/check.sh` runs depgen updater and fails on drift.

- Coverage baseline instability
  - Impact: Initial thresholds could be noisy across environments.
  - Mitigation: Set from first passing Bazel coverage report, rounded down slightly, and measure only processor/server main source files.

- Formatting review noise
  - Impact: Formatting can obscure functional Bazel changes.
  - Mitigation: Commit formatting separately before formatter tooling/check.

## Completion Criteria

- Planning artifacts reviewed and accepted.
- Formatting change completed in its own commit.
- Bazel foundation, module targets, test targets, formatter tooling, CI, and coverage gate implemented.
- `tools/check.sh` passes.
- Task board evidence records commands and outcomes.
- Completed tasks record commit metadata or an explicit `not_required`.

## Decision Log

| ID | Decision | Concrete Plan Impact |
| --- | --- | --- |
| Q-01 | Use unshaded build/test processor target. | No Bazel shaded release jar in Phase 1. |
| Q-02 | Adopt Error Prone, not NullAway/JSpecify. | Strict rules include Error Prone only; `javax.annotation` stays. |
| Q-03 | Keep TestNG. | Add TestNG macro and TestNG targets. |
| Q-04 | Tests use production strict compile options unless churn is excessive. | Test macro starts with same strict javacopts. |
| Q-05 | Coarse module-level targets by default. | BUILD target structure mirrors Buildr modules unless needed otherwise. |
| Q-06 | Mirror `build.yaml` dependency versions unless forced. | `third_party/java/dependencies.yml` starts from Buildr versions. |
| Q-07 | Fixtures are Bazel data with runfiles-aware path. | Processor tests receive `sting.fixture_dir` from runfiles. |
| Q-08 | Use Bazel 9.1.1 and `jdbt` `.bazelrc` core defaults. | Add `.bazelversion` and Java 17 strict `.bazelrc`. |
| Q-09 | Measure and pin coverage baseline. | Thresholds are set after first passing coverage run. |
| Q-10 | Remove Travis; GitHub Actions runs only Bazel check. | Delete Travis infra and add Bazel-only workflow. |
| Q-11 | Use explicit `java_plugin`. | Targets list `//processor:sting_processor_plugin`. |
| Q-12 | Do not compile `*/generated/**`. | BUILD globs exclude generated output trees. |
| Q-13 | Allow stable aliases above depgen block. | `third_party/java/BUILD.bazel` has manual aliases/aggregates. |
| Q-14 | Keep docs contributor/internal. | Avoid public consumer Bazel docs in Phase 1. |
| Q-15 | Test targets live with test source packages. | BUILD files go under `*/src/test/java/...` packages. |
| Q-16 | Formatting is included but committed separately. | Formatting-only task precedes formatter tooling/check task. |
| Q-17 | Format selected input fixtures only. | Include `bad_input` and `input`; exclude expected outputs. |
| Q-18 | Do not reproduce GWT compile/enhance. | Include `Sting.gwt.xml` resource only. |
| Q-19 | Coverage uses unit-test targets only. | Coverage gate excludes integration-test targets. |
| Q-20 | `tools/check.sh` fails on stale depgen output. | Updater runs first; generated drift fails CI. |
| Q-21 | Emit planning artifacts before implementation. | Plan was accepted before implementation. |

## Implementation Notes

- Bazel uses TestNG `7.10.2` rather than the `build.yaml` `6.11` artifact because `proton-qa` 0.72 calls TestNG byte-array assertion overloads that are absent in 6.11.
- The Error Prone strict set includes `Varifier`; doc-examples disable only that check through their relaxed macro to avoid rewriting published example sources.
- Bazel excludes `-Xlint:this-escape` because the Bazel javac toolchain emits this newer warning while the project target remains Java 17.
