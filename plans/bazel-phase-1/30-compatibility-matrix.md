# Bazel Phase 1 Compatibility Matrix

Status: accepted
Last updated: 2026-07-04

## Build System Parity

| Area | Buildr Baseline | Bazel Phase 1 Target | Intentional Divergence |
| --- | --- | --- | --- |
| Java version | Source/target 17 | Java/tool Java 17 | None |
| Compiler strictness | `-Werror`, `-Xlint:all,-processing,-serial` | Same plus Error Prone and strict Java deps; `this-escape` is excluded for Bazel toolchain parity | Bazel is stricter by design |
| Nullability model | `javax.annotation` | JSpecify for processor internals; `javax.annotation` remains for generated code and processor inputs | NullAway is scoped to processor main sources |
| Core build | Build core jar and GWT enhanced artifacts | Build core Java library and include `Sting.gwt.xml` resource | No GWT compile/classifier |
| Processor build | Build and shade release jar | Build unshaded library/plugin for build/test | No shaded release jar |
| Server build | Compile with Sting processor | Compile with explicit Sting `java_plugin` | Explicit plugin instead of processor discovery |
| Doc examples | Compile with Sting processor | Compile with explicit Sting `java_plugin` | None |
| Integration tests | TestNG module test with annotation processing | TestNG target near test source with explicit plugin | Target placement differs |
| Server integration tests | TestNG module test with annotation processing | TestNG target near test source with explicit plugin | Target placement differs |
| Downstream tests | Buildr supported | Out of scope | Deferred |
| Performance tests | Buildr supported | Out of scope | Deferred |
| Site deploy | Buildr task | Out of scope for GitHub Actions Bazel workflow | Deferred |

## Dependency Parity

| Dependency Source | Buildr | Bazel |
| --- | --- | --- |
| Versions | `build.yaml` | `third_party/java/dependencies.yml` mirrors `build.yaml` unless a compatibility exception is required |
| Resolution | Buildr artifacts | bazel-depgen generated `http_file` and `java_import` targets |
| Generated output | Buildr local artifact cache | depgen-owned sections in `MODULE.bazel` and `third_party/java/BUILD.bazel` |
| Freshness check | Not applicable | `tools/check.sh` runs depgen update and fails on drift |

## Test And Coverage Parity

| Area | Buildr Baseline | Bazel Phase 1 Target | Notes |
| --- | --- | --- | --- |
| Test framework | TestNG | TestNG | Keep existing tests |
| Test compile strictness | Buildr compiler strictness | Same as production strictness, including Error Prone | Stop and ask if excessive churn |
| Processor fixtures | Relative `src/test/fixtures` | Bazel `data` and runfiles-aware `sting.fixture_dir` | More hermetic than Buildr |
| Coverage threshold | No numeric local gate found | Initial baseline from first passing Bazel coverage | Processor/server unit tests only |
| Coverage inputs | N/A | Processor and server unit-test targets only | Integration tests excluded from gate |

## Documented Compatibility Exceptions

- Bazel uses TestNG `7.10.2` because `proton-qa` 0.72 calls TestNG assertion overloads missing from TestNG `6.11`.
- The Error Prone strict set includes `Varifier`; doc-examples disable only that check through the relaxed macro to keep published example sources unchanged.
- Bazel excludes `-Xlint:this-escape` to avoid warnings introduced by the Bazel javac toolchain beyond the Java 17 Buildr baseline.

## CI Parity

| Area | Previous State | Bazel Phase 1 Target |
| --- | --- | --- |
| Travis CI | Disabled legacy infrastructure | Removed |
| GitHub Actions | No workflow found | Add Bazel workflow running `tools/check.sh` |
| Buildr CI replacement | Travis ran Buildr `ci` historically | Out of scope for Phase 1 |

## Formatting

| Area | Current State | Bazel Phase 1 Target |
| --- | --- | --- |
| Java source formatting | Manual/project style | Tool-backed formatter check |
| Commit sequencing | N/A | Formatting-only commit before formatter tooling/check commit |
| Fixture scope | Existing fixtures manually maintained | Format `bad_input` and `input`; do not format expected-output fixture trees |
