# Bazel Performance Tests Test Strategy

Status: accepted
Last updated: 2026-07-05

## Goals

- Prove the new Bazel runner preserves benchmark semantics.
- Prove migrated historical data is complete and renderable.
- Keep normal validation fast enough for CI.
- Provide a clear manual release path for full performance generation.

## Validation Layers

### Static/Build Validation

Commands:

```bash
tools/update_java_deps.sh
bazel build //performance-benchmarks:benchmark
```

Purpose:

- Verify dependency generation is fresh.
- Verify `MODULE.bazel`, `MODULE.bazel.lock`, and `third_party/java/BUILD.bazel` match depgen output.
- Verify the runner compiles under repo Bazel Java rules.

### Unit And Schema Validation

Command:

```bash
bazel test //performance-benchmarks:all_tests
```

Coverage:

- Variant definitions.
- Scenario source generation.
- Properties ordering and round-trip parsing.
- New per-comparison path resolution.
- Required key validation.
- Table rendering from sample/migrated data.
- CLI argument validation.

`tools/check.sh` must invoke this target explicitly or include it through a root suite so normal CI does not skip the
benchmark schema/unit tests.
Reduced compiler/GWT smoke runs are implementation full-gate checks, not normal `tools/check.sh` checks.

### Reduced Smoke Generation

Commands:

```bash
bazel run //performance-benchmarks:benchmark -- --mode=build-times --variant=tiny --warmup-seconds=0 --trials=1 --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bazel run //performance-benchmarks:benchmark -- --mode=code-size --variant=tiny --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
```

Purpose:

- Exercise in-process `javac` compilation.
- Exercise Dagger and Sting processor construction under Bazel classpath.
- Exercise GWT compiler invocation and output discovery.
- Avoid full benchmark runtime during normal implementation validation.

### Full Release Generation

Commands:

```bash
bundle exec buildr update_build_time_statistics
bundle exec buildr update_code_size_statistics
```

Purpose:

- Generate release-quality data with default warmup/trial counts and all variants.
- Regenerate checked-in website include tables.
- Validate Rake wrappers around the Bazel runner.
- Gate deletion of the old Buildr `performance-tests` implementation unless the user explicitly approves deferral.

### Rake Wrapper Smoke

Commands:

```bash
bundle exec buildr update_build_time_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
bundle exec buildr update_code_size_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
```

Purpose:

- Verify the existing task names can invoke the Bazel runner without paying full benchmark cost.
- Ensure smoke wrapper output is written only to ignored temporary paths.

## Required Full Gate For Implementation Completion

```bash
tools/check.sh
bazel test //performance-benchmarks:all_tests
bazel run //performance-benchmarks:benchmark -- --mode=build-times --variant=tiny --warmup-seconds=0 --trials=1 --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bazel run //performance-benchmarks:benchmark -- --mode=code-size --variant=tiny --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bundle exec buildr update_build_time_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
bundle exec buildr update_code_size_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
```

Full all-variant release generation is required before publishing performance data, but it is not required for every
implementation iteration.
If the implementation deletes the old Buildr `performance-tests` module, full all-variant wrapper generation is required
before that deletion unless user-approved deferral is recorded in the task board.

## Manual Review Checks

- Confirm `website/includes/BuildTimesTable.html` row count and caption.
- Confirm `website/includes/CodeSizeTable.html` row count and caption.
- Confirm current historical build-time and code-size tables can render from different comparison directories.
- Confirm migrated legacy build-time data is not required to include raw timings that the old fixture never stored.
- Confirm no default archive output is produced.
- Confirm debug archive flag emits artifacts only when requested.
- Confirm old `performance-tests` paths are gone after cleanup.

## Residual Risks

- GWT compile behavior may depend on exact source-jar/resource classpath ordering.
- Timing values are inherently host-dependent; implementation validation should focus on successful execution and schema
  correctness, not fixed numeric ratios.
- Historical data migration assumes Dagger `2.25.2` for old rows because the old schema did not store Dagger version.
- Historical metric coverage differs between build-time and code-size data, so table rendering must keep metric-specific
  comparison selection.
