# Data And Output Contract

Status: accepted
Last updated: 2026-07-05

## Feature

- Name: Bazel performance benchmark data layout and output generation
- Owner: Sting maintainers
- Related plan/task IDs: `T03`, `T04`, `T05`, `T06`

## Problem Statement

The current performance data is stored in two monolithic properties files with every key prefixed by Sting version.
Consumers only need the current release version, and release validation only checks for one key per file. This makes
partial data easy to miss and forces table generation to scan and strip prefixes.

## Scope

In scope:

- New per-comparison data directories.
- Historical data migration from monolithic files.
- Explicit Sting and Dagger version metadata.
- Strict schema validation before newly generated release-facing writes.
- Legacy-compatible schema validation for migrated historical data.
- Raw build-time timing samples plus derived summary ratios for newly generated data.
- Checked-in website include table generation.

Out of scope:

- Backwards-compatible readers for the old monolithic layout after migration.
- Checked-in GWT archive output.
- Changing table content beyond data-source and caption metadata correctness.

## Data Layout

Each comparison stores its own data. A comparison directory may contain one or both metric files. Newly generated release
data should normally contain both files for the current Sting/Dagger pair, but migrated historical data may be sparse
because the old build-time and code-size fixtures cover different Sting versions.

```text
performance-benchmarks/data/sting-<sting-version>__dagger-<dagger-version>/
  build-times.properties
  code-size.properties
```

Example:

```text
performance-benchmarks/data/sting-0.39__dagger-2.25.2/
  build-times.properties
  code-size.properties
```

Table rendering must accept separate build-time and code-size comparison identifiers so the current checked-in includes
can be regenerated even when the latest historical build-time and code-size versions differ.

## Required Metadata

Every metric properties file must include:

```properties
sting.version=<sting-version>
dagger.version=<dagger-version>
generated.by=bazel-performance-benchmarks
```

The implementation may add additional metadata such as JVM version, OS, timestamp, or command-line parameters, but
table rendering must not require unstable host metadata.

## Build-Time Schema

The build-time schema has two validation profiles:

- `legacy-render`: migrated historical rows only need metadata, input keys, and derived `sting2dagger` ratios required
  by table rendering.
- `generated-release`: newly generated rows must include metadata, input keys, raw measured trials, min timings, and
  derived ratios.

Required variants:

```text
tiny small medium large huge
```

Required keys per variant for both profiles:

```properties
<variant>.input.warmupTimeInSeconds=<integer>
<variant>.input.measureTrials=<integer>
<variant>.input.layerCount=<integer>
<variant>.input.nodesPerLayer=<integer>
<variant>.input.inputsPerNode=<integer>
<variant>.input.eagerCount=<integer>
<variant>.output.sting2dagger.all.min=<decimal-ratio>
<variant>.output.sting2dagger.incremental.min=<decimal-ratio>
```

Additional required keys per variant for the `generated-release` profile:

```properties
<variant>.output.sting.all.min=<nanoseconds>
<variant>.output.dagger.all.min=<nanoseconds>
<variant>.output.sting.incremental.min=<nanoseconds>
<variant>.output.dagger.incremental.min=<nanoseconds>
```

Raw trial keys should be stable and ordered, for example:

```properties
<variant>.output.sting.all.trial.1=<nanoseconds>
<variant>.output.dagger.all.trial.1=<nanoseconds>
<variant>.output.sting.incremental.trial.1=<nanoseconds>
<variant>.output.dagger.incremental.trial.1=<nanoseconds>
```

Warmup trial storage is optional. Measured trial storage is required for `generated-release` and absent for old
historical rows.

## Code-Size Schema

Required variants:

```text
eager_tiny tiny lazy_tiny eager_small small lazy_small eager_medium medium lazy_medium eager_large large lazy_large eager_huge huge lazy_huge
```

Required keys per variant:

```properties
<variant>.input.layerCount=<integer>
<variant>.input.nodesPerLayer=<integer>
<variant>.input.inputsPerNode=<integer>
<variant>.input.eagerCount=<integer>
<variant>.output.sting.size=<bytes>
<variant>.output.dagger.size=<bytes>
```

## Archive Behavior

The default code-size path must not emit or check in compiled GWT archive output. Debugging may opt in with an explicit
option such as:

```text
--archive-dir=generated/perf/archive
```

or:

```text
--save-archive
```

The exact flag name can be refined during implementation, but archive output must remain optional.

## Error Handling And Diagnostics

- Unknown variants fail with a non-zero exit and list valid variants.
- Missing required data keys fail validation before writing website include files, using the correct validation profile
  for migrated historical data vs newly generated release data.
- Partial release-facing files must not replace existing checked-in outputs.
- GWT output discovery failures must report the expected module name and output path.
- Dependency/classpath failures should print the mode, variant, and compiler command context.

## Compatibility And Parity

- Current website table rows and captions remain semantically equivalent.
- Current release defaults for build-time warmup/trials remain `20` seconds and `10` trials.
- Dagger comparator stays `2.25.2` during migration.
- Historical rows from old properties files are migrated into `dagger-2.25.2` comparison directories because the old
  schema did not store Dagger version.
- Current checked-in build-time and code-size tables may render from different Sting comparison directories because the
  existing historical data has different latest versions for the two metrics.

## Acceptance Criteria

- [ ] Historical `build-times.properties` data is migrated into per-comparison files.
- [ ] Historical `code-size.properties` data is migrated into per-comparison files.
- [ ] Legacy migrated data validation does not require raw timing keys absent from old build-time fixtures.
- [ ] Newly generated release data validation fails on missing variants or required keys.
- [ ] Table rendering reads the new layout and rewrites the existing website include files.
- [ ] Debug archive output is opt-in only.
- [ ] Smoke data generation can use reduced trial counts without changing release defaults.

## Validation Plan

Targeted checks:

- Parse all migrated properties files.
- Validate migrated files with the legacy-compatible profile.
- Validate generated/synthetic files with the generated-release profile.
- Render website tables from separate migrated build-time and code-size comparisons and compare expected row counts.
- Run reduced build-time generation for `tiny`.
- Run reduced code-size generation for `tiny`.

Full gates:

- See `40-test-strategy.md`.

## Open Questions

No open questions remain for this contract.
