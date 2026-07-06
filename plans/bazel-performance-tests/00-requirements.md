# Bazel Performance Tests Requirements

Status: accepted
Last updated: 2026-07-05

## Mission

Replace the Buildr-only `performance-tests` implementation with a Bazel-owned benchmark project that produces the
same release-facing outputs:

- `build-times.properties`
- `code-size.properties`
- `website/includes/BuildTimesTable.html`
- `website/includes/CodeSizeTable.html`

The replacement must preserve the benchmark meaning: compare Sting and Dagger annotation processor behavior and GWT
compiled output for procedurally generated dependency graphs. Bazel should provide the runner, dependencies, and
repeatable command surface; it must not become the thing being timed.

## Current Baseline Evidence

- `performance-tests` currently has no Bazel package; `bazel query //performance-tests/...` reports no targets.
- Buildr defines the module and performance tasks in `buildfile`.
- `tasks/perf.rake` generates the two website include tables from properties data.
- `tasks/release.rake` only checks that each monolithic properties file has at least one key for `PRODUCT_VERSION`.
- `bundle exec buildr sting:performance-tests:compile TEST=only GWT=no DOWNSTREAM=no` completed successfully in the
  local checkout on 2026-07-05.
- Bazel already has Sting core/processor targets and JavaPoet, but not the performance-only Dagger/GWT/Akasha deps.

## Scope

In scope:

- Add a new Bazel-backed benchmark project, expected under `performance-benchmarks/`.
- Import the missing benchmark dependencies through `third_party/java/dependencies.yml` and depgen.
- Keep benchmark timing based on in-process `javac`, using Bazel only as launcher/dependency provider.
- Keep code-size measurements based on GWT compile output.
- Store benchmark data as one directory per Sting/Dagger version comparison, with metric files present only when that
  comparison has that metric.
- Migrate historical monolithic properties data into the new per-comparison layout.
- Generate the checked-in website include tables from the new data layout.
- Keep existing Buildr/Rake task names as wrappers during the transition.
- Add fast benchmark schema/unit validation to normal `tools/check.sh` checks.
- Delete the old Buildr `performance-tests` implementation after parity is proven.

Out of scope:

- Timing actual `bazel build` invocations as the benchmark.
- Running full warmup/trial performance generation in normal CI.
- Emitting GWT archive/debug output by default.
- Updating Dagger away from `2.25.2` during this migration.
- Replacing unrelated Buildr release, publishing, downstream, or website deployment behavior.
- Adding compatibility shims for the old monolithic properties layout after migration is complete.

## Locked Decisions And Non-Negotiables

- Bazel is the runner/dependency manager only; benchmark timing remains in-process `javac`.
- Historical data moves to per-comparison files. Historical metric coverage may be sparse because the existing
  build-time and code-size files cover different Sting versions.
- Existing Rake task names remain as transition wrappers.
- Full performance generation stays manual/release-only; normal checks use benchmark schema/unit validation.
- Reduced tiny build-time/code-size smoke runs are required implementation gates but are not normal `tools/check.sh`
  checks unless a later implementation decision explicitly adds them.
- GIR must not be carried into the new implementation.
- GWT archive output is disabled by default and available only through an explicit debug option.
- Website include tables remain checked in.
- Newly generated build-time data stores raw trial timings as well as derived summary ratios.
- Warmup and trial counts are configurable; release defaults remain `20` seconds and `10` trials.
- Dagger remains pinned to `2.25.2` for this migration.
- The old `performance-tests` module is removed after Bazel parity is proven.
- Removing the old `performance-tests` module requires full all-variant wrapper generation to pass first, unless the
  user explicitly approves deferring removal.
- Release-facing newly generated data validates all required variants and keys before updating outputs. Historical
  migrated data uses a legacy-compatible validation profile that does not invent raw timing samples missing from the
  old files.
- This plan uses the structured-delivery workflow and remains draft until explicit user review/approval is recorded.

## Command Surface And Behavior Expectations

Target command names may be refined during implementation, but the implemented surface must support these behaviors:

- Build benchmark runner:
  - `bazel build //performance-benchmarks:benchmark`
- Run build-time data generation:
  - `bazel run //performance-benchmarks:benchmark -- --mode=build-times --sting-version=<version> --dagger-version=2.25.2 --all-variants`
- Run code-size data generation:
  - `bazel run //performance-benchmarks:benchmark -- --mode=code-size --sting-version=<version> --dagger-version=2.25.2 --all-variants`
- Render checked-in website tables:
  - `bazel run //performance-benchmarks:benchmark -- --mode=render-tables --build-times-comparison=sting-<build-time-version>__dagger-2.25.2 --code-size-comparison=sting-<code-size-version>__dagger-2.25.2`
- Validate data without full performance runs:
  - `bazel test //performance-benchmarks:all_tests`
- Debug-only archive output:
  - Explicit option such as `--archive-dir=<path>` or `--save-archive`; default is no archive.
- Existing Rake wrappers:
  - `bundle exec buildr update_build_time_statistics`
  - `bundle exec buildr update_code_size_statistics`
  - `bundle exec buildr update_all`

## Quality, Test, And Coverage Gates

Required implementation full gate:

```bash
tools/check.sh
bazel test //performance-benchmarks:all_tests
bazel run //performance-benchmarks:benchmark -- --mode=build-times --variant=tiny --warmup-seconds=0 --trials=1 --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bazel run //performance-benchmarks:benchmark -- --mode=code-size --variant=tiny --output-dir=tmp/perf-smoke --sting-version=0.0-smoke --dagger-version=2.25.2
bundle exec buildr update_build_time_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
bundle exec buildr update_code_size_statistics PERF_SMOKE=true PRODUCT_VERSION=0.0-smoke
```

Required release/manual generation:

```bash
bundle exec buildr update_build_time_statistics
bundle exec buildr update_code_size_statistics
```

Coverage is not required for benchmark harness code unless it is added to the normal production/test coverage gate in a
later plan. Smoke/schema tests are required because full benchmark execution is intentionally not part of normal CI.
`PERF_SMOKE=true` must make the Rake wrappers run tiny/reduced Bazel benchmark modes and write only under an ignored
temporary output directory.

## Known Intentional Divergences From Buildr

- Data is stored by version-pair directory instead of two monolithic properties files.
- Historical version-pair directories may contain only one metric file when the old fixture data only has that metric.
- Dagger version becomes explicit metadata rather than inferred from Buildr artifact configuration.
- Release validation checks full schema completeness, not just one current-version-prefixed key.
- GIR helper APIs are replaced with JDK/local helpers.
- GWT archive output is no longer emitted by default.
- Bazel-owned benchmark targets are manual/local for full runs rather than part of `//:all_tests`.

## Open Questions Register

No questions remain open. Plan acceptance was granted by the user on 2026-07-05 and is tracked in
`20-task-board.yaml`.

### Q-01

- status: resolved
- question: Should Bazel be only the runner/dependency manager while benchmark timing remains in-process `javac`?
- context: Timing Bazel would include analysis, cache state, sandboxing, and graph layout rather than processor-only work.
- options: `Bazel runner only`; `time bazel build`
- tradeoffs: Bazel timing is closer to a Bazel user workflow but much noisier and not comparable to existing data.
- recommended_default: `Bazel runner only`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-02

- status: resolved
- question: Should historical data be split into the new per-comparison layout?
- context: Existing data is monolithic and version-prefixed.
- options: `migrate history`; `new layout only going forward`
- tradeoffs: Migrating history costs some scripting but avoids two reader paths.
- recommended_default: `migrate history`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `01-data-and-output-contract.md`, `10-implementation-plan.md`

### Q-03

- status: resolved
- question: Should existing Buildr/Rake performance task names stay as wrappers?
- context: Release and local workflows already use those task names.
- options: `keep wrappers`; `replace with Bazel-only commands`
- tradeoffs: Wrappers reduce transition friction; Bazel-only is simpler but breaks release muscle memory.
- recommended_default: `keep wrappers`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-04

- status: resolved
- question: Should full performance generation stay out of normal `tools/check.sh`/CI?
- context: Full runs use warmups, repeated trials, and GWT optimization.
- options: `manual full runs with smoke CI`; `full runs in CI`
- tradeoffs: CI full runs would be expensive and noisy.
- recommended_default: `manual full runs with smoke CI`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `40-test-strategy.md`

### Q-05

- status: resolved
- question: Should GIR be removed from the new implementation?
- context: Current GIR usage is limited to file helpers, subprocess execution, directory scoping, and patch cleanup.
- options: `replace with JDK/local helpers`; `import GIR into Bazel`
- tradeoffs: Replacing GIR keeps dependencies smaller and avoids carrying Buildr-era harness plumbing.
- recommended_default: `replace with JDK/local helpers`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`

### Q-06

- status: resolved
- question: Should code-size output archive compiled GWT artifacts by default?
- context: Archives are diagnostic only and have no checked-in consumer.
- options: `debug-only archive option`; `always emit archive`
- tradeoffs: Debug-only keeps default outputs clean while preserving investigation support.
- recommended_default: `debug-only archive option`
- user_decision: User revised the recommendation: default is no archive; optionally save during debugging.
- artifacts_updated: `00-requirements.md`, `01-data-and-output-contract.md`, `10-implementation-plan.md`

### Q-07

- status: resolved
- question: Should generated website include tables remain checked in?
- context: Documentation includes HTML snippets directly.
- options: `keep checked-in includes`; `generate during site build`
- tradeoffs: Checked-in includes avoid requiring benchmark data generation during ordinary site builds.
- recommended_default: `keep checked-in includes`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`

### Q-08

- status: resolved
- question: Should data files store raw timing samples as well as summaries?
- context: Current build-time data stores only derived ratios.
- options: `raw trials plus summaries`; `summaries only`
- tradeoffs: Raw trials improve auditability with modest file growth.
- recommended_default: `raw trials plus summaries`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `01-data-and-output-contract.md`

### Q-09

- status: resolved
- question: Should the runner support reduced warmup/trial counts?
- context: Release defaults are too slow for local smoke validation.
- options: `configurable counts`; `fixed release counts`
- tradeoffs: Configurable counts enable smoke tests while retaining release defaults.
- recommended_default: `configurable counts`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `40-test-strategy.md`

### Q-10

- status: resolved
- question: Should Dagger stay pinned at `2.25.2` initially?
- context: Historical tables compare against Dagger `2.25.2`.
- options: `pin 2.25.2`; `upgrade during migration`
- tradeoffs: Pinning isolates build-system/data-layout migration from comparator-version changes.
- recommended_default: `pin 2.25.2`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `30-compatibility-matrix.md`

### Q-11

- status: resolved
- question: Should the old `performance-tests` Buildr module be deleted after Bazel parity?
- context: Two implementations would invite release-data drift.
- options: `delete after parity`; `keep both`
- tradeoffs: Deletion reduces maintenance paths after the new wrappers work.
- recommended_default: `delete after parity`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

### Q-12

- status: resolved
- question: Should release-facing output validate every required variant/key?
- context: Current release only checks for one prefixed key per file.
- options: `strict completeness validation`; `keep shallow validation`
- tradeoffs: Strict validation catches partial performance data before release.
- recommended_default: `strict completeness validation`
- user_decision: Accepted recommended default.
- artifacts_updated: `00-requirements.md`, `01-data-and-output-contract.md`, `40-test-strategy.md`

### Q-13

- status: resolved
- question: Should this migration use the structured delivery workflow and planning artifacts?
- context: The work spans Bazel deps, harness behavior, release wrappers, data migration, and docs generation.
- options: `structured plan artifacts`; `informal plan only`
- tradeoffs: Structured artifacts create reviewable requirements, task state, and validation evidence.
- recommended_default: `structured plan artifacts`
- user_decision: User requested `$structured-delivery-workflow`.
- artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
