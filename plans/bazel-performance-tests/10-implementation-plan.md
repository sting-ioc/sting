# Bazel Performance Tests Implementation Plan

Status: accepted
Last updated: 2026-07-05

## Phase Sequence

1. Planning and approval
   - Create requirements, data contract, implementation plan, task board, compatibility matrix, and test strategy.
   - Request explicit user review before marking the plan accepted.
2. Dependency import
   - Add benchmark-only Dagger/GWT/Akasha/BrainCheck and related dependencies to depgen configuration.
   - Regenerate `MODULE.bazel`, `MODULE.bazel.lock`, and `third_party/java/BUILD.bazel`.
3. Benchmark project skeleton
   - Add `performance-benchmarks/` Bazel package and runner entry point.
   - Port source generation and property ordering with JDK/local helpers instead of GIR.
4. Data model and historical migration
   - Implement per-comparison reader/writer/validator.
   - Support metric-specific comparison directories and separate validation profiles for legacy migrated data and newly
     generated release data.
   - Migrate old monolithic fixture data into the new layout.
5. Build-time benchmark behavior
   - Preserve in-process `javac` benchmark semantics.
   - Store raw trial samples and derived ratios.
   - Add configurable warmup/trial counts.
6. Code-size benchmark behavior
   - Preserve GWT compile measurement semantics.
   - Keep archive output opt-in.
   - Ensure source jars and generated source directories are on the GWT classpath.
7. Table rendering and wrappers
   - Generate checked-in website include tables from new data, allowing build-time and code-size tables to select
     different comparison directories.
   - Update existing Rake task names to call the Bazel runner, including a `PERF_SMOKE=true` wrapper validation path.
   - Strengthen release validation to schema completeness.
8. Remove old implementation
   - Run full all-variant Rake wrapper generation first, or explicitly defer deletion with user approval.
   - Delete Buildr `performance-tests` module and old Java sources after parity.
   - Remove obsolete fixture paths and generated-workdir assumptions.
9. Validation and closeout
   - Run targeted smoke checks and full required gates.
   - Update task evidence and request final review.

## Delivery Approach

- Execute one task at a time with minimal diffs.
- Keep full benchmark generation manual; use reduced smoke runs for implementation validation.
- Keep compatibility notes updated whenever parity or intentional divergence changes.
- Prefer deleting the old path after the new path is proven rather than keeping a long-term fallback.
- Use explicit Bazel source lists and one `BUILD.bazel` per source directory.

## Task Granularity Rules

- Split dependency import, runner skeleton, data migration, build-time behavior, code-size behavior, wrapper updates, and
  cleanup into separate tasks.
- Each task must have a targeted validation command.
- A task is not complete until evidence is recorded in `20-task-board.yaml`.
- Commit boundaries should align with task boundaries when implementation begins.

## High-Risk Areas

- Risk: GWT requires source jars/resources on the runtime classpath.
  - Impact: Code-size mode may compile Java classes but fail during GWT translation.
  - Mitigation: Make GWT classpath assembly explicit and validate with a reduced `tiny` code-size run.
- Risk: Dependency import creates large generated diffs or version conflicts.
  - Impact: Bazel dependency graph may drift from Buildr baseline.
  - Mitigation: Mirror Buildr versions unless documented, run `tools/update_java_deps.sh`, and inspect generated labels.
- Risk: Build-time comparability changes.
  - Impact: New ratios may no longer be comparable to historical data.
  - Mitigation: Keep in-process `javac`, same variants, same defaults, and Dagger `2.25.2`.
- Risk: Partial data writes.
  - Impact: Release docs can publish incomplete performance tables.
  - Mitigation: Validate complete schema before writing release-facing outputs.
- Risk: Rake wrapper/Bazel command environment mismatch.
  - Impact: Existing release tasks may fail despite direct Bazel commands working.
  - Mitigation: Validate direct Bazel commands, Rake wrapper smoke commands, and full wrapper generation before
    publishing performance data.
- Risk: Historical data cannot satisfy the newly generated data schema.
  - Impact: Migration could fail or invent raw timing data that was never stored.
  - Mitigation: Use a legacy-render validation profile for migrated historical rows and generated-release validation for
    new release data.

## Required Full Gates

```bash
tools/check.sh
bazel test //performance-benchmarks:all_tests
bazel run //performance-benchmarks:benchmark -- --mode=build-times --variant=tiny --warmup-seconds=0 --trials=1 --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bazel run //performance-benchmarks:benchmark -- --mode=code-size --variant=tiny --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bundle exec buildr update_build_time_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
bundle exec buildr update_code_size_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
```

## Completion Criteria

- All planned tasks in `20-task-board.yaml` are completed.
- No completed task has `commit.hash: pending`.
- New data layout contains migrated historical data.
- Website include tables are regenerated from the new data layout.
- Existing release-facing Rake task names work as wrappers around the Bazel runner.
- Old `performance-tests` implementation is removed after parity is validated.
- Required full gates pass, or any deviation is documented with user approval.

## Decision Log

| ID | Decision | Plan impact |
| --- | --- | --- |
| Q-01 | Bazel is runner/dependency manager only. | Build-time implementation preserves in-process `javac`; no `bazel build` timing. |
| Q-02 | Migrate historical data into per-comparison files. | Add data migration task and new reader/writer; do not keep old layout as runtime fallback. |
| Q-03 | Keep existing Rake task names as wrappers. | Wrapper update is a required phase before old module removal. |
| Q-04 | Full performance runs stay manual; schema/unit validation enters normal checks. | Test strategy separates normal schema tests, reduced implementation smoke, and full release generation. |
| Q-05 | Remove GIR dependency. | Port helper behavior to JDK/local utilities. |
| Q-06 | GWT archive output is debug-only. | Code-size mode defaults to data-only output; explicit archive flag is optional. |
| Q-07 | Keep website include tables checked in. | Table renderer remains part of release/update flow. |
| Q-08 | Store raw timing trials plus summaries. | Generated-release build-time schema includes trial keys and derived ratios. |
| Q-09 | Warmup/trial counts are configurable. | CLI includes `--warmup-seconds` and `--trials`; release defaults remain old values. |
| Q-10 | Pin Dagger at `2.25.2`. | Dep import and data directories use Dagger `2.25.2` initially. |
| Q-11 | Delete old Buildr module after parity. | Cleanup phase removes old Java module and fixture paths. |
| Q-12 | Validate all required variants/keys. | Schema validator gates release-facing writes and release checks. |
| Q-13 | Use structured delivery workflow. | Plan artifacts and approval task are required before implementation acceptance. |

## Review Amendments

| Review | Amendment |
| --- | --- |
| R1-F1 | Historical build-time rows use a legacy-render schema because old fixtures lack raw timing samples. |
| R1-F2 | Table rendering accepts separate build-time and code-size comparison selections. |
| R1-F3 | `tools/check.sh` must include benchmark schema/unit tests. |
| R1-F4 | Rake wrappers must provide and pass a `PERF_SMOKE=true` validation path. |
| R1-F5 | Dependency import includes `MODULE.bazel.lock` as a generated output. |
| R2-F1 | T06 owns `tools/check.sh`/root-suite integration for benchmark schema/unit tests. |
| R2-F2 | T07 old-module deletion is gated on full all-variant wrapper generation or explicit user-approved deferral. |
| R3 | Final review reported no actionable findings. |
