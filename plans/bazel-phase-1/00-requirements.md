# Bazel Phase 1 Requirements

Status: accepted
Last updated: 2026-07-04

## Mission

Add a Bazel build system in parallel with the existing Buildr build. Phase 1 must build and test the main Java modules while preserving Buildr behavior unless explicitly changed.

## Scope

In scope:

- Build and test `core`, `doc-examples`, `integration-tests`, `processor`, `server`, and `server-integration-tests`.
- Add Bzlmod-based Bazel metadata, strict Java rules, TestNG test macros, depgen-managed dependencies, tool scripts, buildifier, Java formatter tooling, coverage gate, and GitHub Actions CI.
- Remove disabled Travis CI infrastructure and Travis-specific behavior.
- Keep planning and Bazel usage documentation internal/contributor-facing.

Out of scope:

- Replacing Buildr release, Maven publishing, site deployment, downstream-test, performance-test, GWT compile/classifier packaging, or public consumer setup.
- Recreating Buildr's shaded release `processor` jar in Bazel.
- Full NullAway/JSpecify migration beyond processor internals.
- Adding tests to raise initial coverage.

## Locked Decisions And Non-Negotiables

- Java stays at 17.
- Bazel uses version `9.1.1`, Bzlmod, and `rules_java 9.6.1`.
- Buildr remains parallel and must not be weakened by Bazel work.
- Bazel Java rules must preserve strict compilation with `-Werror`, `-Xlint:all,-processing,-serial,-this-escape`, Error Prone, strict Java deps, and explicit Java test deps.
- Tests use TestNG.
- Test compilation starts with the same strict options as production. If Error Prone creates excessive churn, stop and ask before weakening tests.
- Dependencies mirror `build.yaml` unless Bazel, Java 17, or Error Prone requires a documented exception.
- Annotation processing is explicit through a `java_plugin`; do not rely on service discovery from runtime deps.
- Existing `*/generated/**` trees are local output artifacts and must not be compiled by Bazel.
- Processor test fixtures are Bazel `data`, with runfiles-aware `sting.fixture_dir`.
- Java formatter adoption must be split: source formatting commit first, formatter tooling/check commit second.

## Command Surface And Behavior Expectations

- `tools/update_java_deps.sh`
  - Runs bazel-depgen for `third_party/java/dependencies.yml` and formatter dependencies.
  - Updates depgen-owned sections in `MODULE.bazel`, `third_party/java/BUILD.bazel`, and formatter BUILD files.
  - Runs buildifier on generated Bazel files.

- `tools/check.sh`
  - Runs `tools/update_java_deps.sh`.
  - Fails if generated dependency outputs are stale.
  - Runs buildifier check.
  - Runs Java format check after the separate formatting commit.
  - Runs Bazel build/test for Phase 1 targets.
  - Runs Bazel coverage for processor and server unit-test targets only.
  - Applies the coverage gate.

- GitHub Actions
  - Uses Java 17 and Bazel setup matching the `jdbt` workflow shape.
  - Runs only `tools/check.sh`.
  - Does not run Buildr site/deploy/release behavior.

## Quality, Test, And Coverage Gates

Required full gate after implementation:

```bash
tools/check.sh
```

Targeted checks during implementation:

- `bazel build //...`
- `bazel test //...`
- `bazel test //processor/src/test/java/sting/processor:all_tests`
- `bazel test //server/src/test/java/sting/server/interceptors:all_tests`
- `bazel coverage <processor/server unit test targets> --combined_report=lcov`
- `tools/java_format.sh check`

Coverage gate:

- Measures processor and server main code only.
- Uses only processor and server unit-test targets.
- Initial line/branch thresholds are established from the first passing Bazel coverage report and rounded down slightly.
- No tests should be added merely to raise the initial gate.

## Known Intentional Divergences From Buildr

- Bazel Phase 1 does not produce shaded release artifacts.
- Bazel Phase 1 does not run GWT compile/enhance or produce GWT classifier jars.
- Bazel Phase 1 does not cover downstream or performance test modules.
- Travis CI is removed because it is disabled legacy infrastructure.
- Public end-user setup docs remain Maven-focused; Bazel docs stay contributor-facing.
- Bazel uses TestNG `7.10.2` because `proton-qa` 0.72 requires newer TestNG assertion overloads at runtime.
- The strict Error Prone set includes `Varifier`; doc-examples use the relaxed macro so examples remain source-compatible with the published documentation.

## Open Questions Register

All known design questions are resolved. The user approved implementation on 2026-07-04.

- id: Q-01
  status: resolved
  question: Should Bazel produce a release-equivalent shaded processor artifact?
  context: Buildr shades JavaPoet and Proton into the packaged processor jar.
  options: Shaded release jar; unshaded build/test processor.
  tradeoffs: Shading adds packaging complexity; unshaded target supports Phase 1 build/test.
  recommended_default: Unshaded build/test processor target.
  user_decision: Accepted recommended default.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-02
  status: resolved
  question: Should strict Java rules include NullAway/JSpecify and Error Prone?
  context: `jdbt` uses NullAway/JSpecify; Sting initially used `javax.annotation` throughout.
  options: Adopt all; adopt Error Prone only; keep Buildr compiler flags only.
  tradeoffs: NullAway/JSpecify is a source migration; Error Prone improves strictness but may cause churn.
  recommended_default: Adopt Error Prone first; migrate processor internals to JSpecify/NullAway in the follow-up.
  user_decision: Initially accepted Error Prone-only; follow-up adopted JSpecify/NullAway for processor internals only.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-03
  status: resolved
  question: Should tests stay on TestNG?
  context: Existing tests use TestNG throughout phase-1 modules.
  options: Keep TestNG; migrate to JUnit.
  tradeoffs: Migration adds unrelated churn; TestNG preserves behavior.
  recommended_default: Keep TestNG with a Replicant-style macro strategy.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-04
  status: resolved
  question: Should tests compile with production strictness?
  context: Replicant weakens TestNG compilation; user wants production parity unless churn is excessive.
  options: Production strictness; test-specific weaker rule.
  tradeoffs: Production strictness catches more issues; may require source fixes.
  recommended_default: Use production strictness and ask before weakening.
  user_decision: Use the same compile options as production unless excessive change appears.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-05
  status: resolved
  question: Should targets be fine-grained or coarse module-level?
  context: Buildr is module-oriented and processor fixtures include many intentionally invalid packages.
  options: Fine package targets; coarse module targets.
  tradeoffs: Fine targets improve precision but risk fixture/model complexity; coarse targets match Buildr.
  recommended_default: Coarse module-level targets by default.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-06
  status: resolved
  question: Should Bazel dependency versions mirror `build.yaml`?
  context: Dependency drift would make Buildr/Bazel differences harder to diagnose.
  options: Mirror Buildr; update opportunistically.
  tradeoffs: Mirroring improves parity; updates may be needed only for Bazel/JDK/Error Prone compatibility.
  recommended_default: Mirror `build.yaml` unless forced.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-07
  status: resolved
  question: How should processor fixtures be modeled?
  context: Buildr passes `sting.fixture_dir=src/test/fixtures`; Bazel tests run from runfiles.
  options: Bazel data/runfiles; copy/link into old relative path.
  tradeoffs: Runfiles are hermetic; copying preserves assumptions but is brittle.
  recommended_default: Bazel `data` plus runfiles-aware `sting.fixture_dir`.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-08
  status: resolved
  question: Should Bazel version and `.bazelrc` follow `jdbt`?
  context: `jdbt` and Replicant both use Bazel `9.1.1`.
  options: Copy `jdbt` core defaults; choose new local defaults.
  tradeoffs: Copying reduces uncertainty; local defaults require more validation.
  recommended_default: Use Bazel `9.1.1`, Java 17 toolchain, strict deps, no header compilation.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-09
  status: resolved
  question: How should coverage thresholds be chosen?
  context: No existing numeric coverage threshold was found.
  options: Measure and pin baseline; choose aspirational threshold.
  tradeoffs: Baseline matches current coverage; aspirational gate would force test additions.
  recommended_default: Measure first passing Bazel coverage and round down slightly.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-10
  status: resolved
  question: Should Travis be kept and should GitHub Actions run Buildr too?
  context: Travis is disabled; Buildr site/deploy is outside Bazel Phase 1.
  options: Keep Travis; remove Travis and run only Bazel check; run Buildr in Actions too.
  tradeoffs: Removing Travis deletes dead infra; Buildr CI replacement expands scope.
  recommended_default: Remove Travis and make Actions run only `tools/check.sh`.
  user_decision: Remove all Travis infrastructure; Actions should run only Bazel check.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-11
  status: resolved
  question: Should annotation processing use explicit `java_plugin` targets?
  context: Sting has a service descriptor, but Replicant models processors explicitly.
  options: Explicit plugin; service discovery.
  tradeoffs: Explicit plugin fits strict deps; service discovery is less controlled.
  recommended_default: Explicit `//processor:sting_processor_plugin`.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-12
  status: resolved
  question: Should Bazel compile existing `*/generated/**` trees?
  context: Generated directories are ignored and untracked local Buildr output.
  options: Compile existing generated trees; exclude and regenerate under Bazel.
  tradeoffs: Excluding avoids stale artifacts; compiling could mask processor failures.
  recommended_default: Exclude existing generated trees.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-13
  status: resolved
  question: Should third-party BUILD files include hand-written aliases?
  context: Replicant has a manual alias/aggregate section above depgen output.
  options: Stable aliases; generated labels only.
  tradeoffs: Aliases improve readability and reduce depgen-name churn; generated-only is simpler mechanically.
  recommended_default: Small manual alias/aggregate section above generated block.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-14
  status: resolved
  question: Should public docs/README mention Bazel Phase 1?
  context: Public docs describe consumer setup, not internal repository builds.
  options: Public docs; contributor/internal docs only.
  tradeoffs: Public docs would be premature; internal docs support contributors.
  recommended_default: Contributor/internal docs only.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-15
  status: resolved
  question: Where should TestNG targets live and how coarse should they be?
  context: User wants targets in packages with test source.
  options: Module-root tests; test-source package tests; per-class tests.
  tradeoffs: Source-package tests follow local convention; per-class creates more boilerplate.
  recommended_default: One TestNG target per test source package/module unless class-specific flags are needed.
  user_decision: Accepted with test targets located alongside test source packages.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-16
  status: resolved
  question: Should Java formatting be part of Phase 1?
  context: `jdbt` gates Palantir Java Format, but Sting style differs today.
  options: No Java formatter; introduce formatter with isolated formatting commit.
  tradeoffs: Formatter adds churn but creates enforceable style; separate commit keeps review clean.
  recommended_default: Initially no formatter.
  user_decision: Perform formatting, with formatting changes committed before formatter tooling/check.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-17
  status: resolved
  question: Which processor fixtures should formatting include?
  context: Fixture expected outputs can encode test baselines.
  options: Exclude all fixtures; include `bad_input` and `input`; format all fixtures.
  tradeoffs: Input fixtures are maintained source samples; expected outputs should stay deliberate.
  recommended_default: Exclude all fixtures.
  user_decision: Format `processor/src/test/fixtures/bad_input/**` and `processor/src/test/fixtures/input/**`, not expected-output trees.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-18
  status: resolved
  question: Should Bazel reproduce Buildr GWT compile/enhance behavior?
  context: Buildr `gwt_enhance` performs GWT compilation and classifier packaging.
  options: Reproduce GWT behavior; include only `Sting.gwt.xml` resource.
  tradeoffs: GWT reproduction expands scope; resource inclusion preserves main jar content.
  recommended_default: Include resource only in Phase 1.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-19
  status: resolved
  question: Which tests feed the coverage gate?
  context: The gate focuses processor and server modules.
  options: Unit tests only; unit plus integration tests.
  tradeoffs: Unit-only matches user request; integration tests give broader exercised behavior.
  recommended_default: Include tests that exercise gated code.
  user_decision: Use only processor and server unit-test targets.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-20
  status: resolved
  question: Should `tools/check.sh` fail on stale depgen output?
  context: depgen owns large generated Bazel sections.
  options: Fail on drift; leave updater manual.
  tradeoffs: Drift check keeps CI reproducible; updater-first check can modify working tree before failing.
  recommended_default: Run updater first and fail on stale generated output.
  user_decision: Accepted.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`

- id: Q-21
  status: resolved
  question: Should implementation start immediately?
  context: The structured workflow requires planning artifacts and explicit plan review before acceptance.
  options: Implement now; emit planning artifacts first.
  tradeoffs: Planning first creates traceability and review point; immediate implementation moves faster.
  recommended_default: Emit planning artifacts first, then request review.
  user_decision: Emit planning artifacts using `structured-delivery-workflow`.
  artifacts_updated: `00-requirements.md`, `10-implementation-plan.md`, `20-task-board.yaml`
