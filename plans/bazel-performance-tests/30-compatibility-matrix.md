# Bazel Performance Tests Compatibility Matrix

Status: accepted
Last updated: 2026-07-05

## Behavior Parity

| Area | Buildr baseline | Bazel target | Intentional divergence |
| --- | --- | --- | --- |
| Benchmark runner | Buildr compiles a Java module and invokes Java mains | Bazel builds/runs a benchmark binary | Bazel is launcher/dependency provider only |
| Build-time measurement | In-process `javax.tools.JavaCompiler` | Same | No timing of `bazel build` |
| Build-time variants | `tiny small medium large huge` | Same | None |
| Build-time defaults | 20s warmup, 10 trials | Same for release | CLI can reduce counts for smoke runs |
| Code-size measurement | Compile generated Java, run GWT compiler, measure `.nocache.js` | Same | Archive output is opt-in only |
| Code-size variants | 15 eager/default/lazy variants | Same | None |
| Dagger comparator | `2.25.2` | `2.25.2` | None during migration |
| Sting artifacts | Buildr package jars/classpath | Bazel-built core/processor targets on runner classpath | Must preserve generated-code behavior |
| Data storage | Two monolithic version-prefixed properties files | One directory per Sting/Dagger comparison, with metric files present when available | Old layout removed after migration |
| Raw timing data | Not stored | Stored for newly generated measured trials | Historical migrated build-time data remains ratio-only |
| Website tables | Generated checked-in HTML includes | Same checked-in files | Source data changes |
| Release validation | At least one current-version key per file | Complete metadata/variant/key validation for newly generated release data | Stricter by design; legacy migration uses render-compatible validation |
| Rake task names | Direct Buildr implementation | Thin wrappers around Bazel runner | Transition wrapper retained |

## Dependency Parity

| Dependency | Buildr baseline | Bazel target |
| --- | --- | --- |
| Dagger | `com.google.dagger:dagger:2.25.2` and related compiler/runtime deps | Add matching depgen-managed imports |
| GWT | Buildr GWT dependency expansion | Add required GWT compile/runtime/source deps explicitly |
| Akasha | `org.realityforge.akasha:akasha-gwt:0.30` | Add matching depgen-managed import |
| BrainCheck | `org.realityforge.braincheck:braincheck-core:1.33.0` | Add matching depgen-managed import |
| GIR | `org.realityforge.gir:gir-core:0.12` | Do not import; replace helper uses |
| JavaPoet | Palantir JavaPoet for Sting generator, Square JavaPoet for Dagger deps | Keep Palantir existing; add Square if Dagger compiler/runtime requires it |
| Dagger version metadata | Inferred from Buildr artifact | Explicit in data directory and properties metadata |
| Bazel lockfile | N/A | `MODULE.bazel.lock` refreshed with depgen/Bazel dependency updates |

## Data Migration

| Source | Target |
| --- | --- |
| `performance-tests/src/test/fixtures/build-times.properties` | `performance-benchmarks/data/sting-<version>__dagger-2.25.2/build-times.properties` where build-time data exists |
| `performance-tests/src/test/fixtures/code-size.properties` | `performance-benchmarks/data/sting-<version>__dagger-2.25.2/code-size.properties` where code-size data exists |
| Version-prefixed keys | Unprefixed keys inside version-pair directory |
| Missing Dagger metadata in old files | Encoded as `dagger-2.25.2` based on current table configuration |

## CI And Release

| Area | Bazel target |
| --- | --- |
| Normal CI | Compile/test/schema only; no full performance generation |
| Normal check script | `tools/check.sh` includes benchmark schema/unit tests |
| Release/manual | Existing Rake task names run full generation through Bazel |
| Wrapper smoke | Existing Rake task names support `PERF_SMOKE=true` for tiny/reduced wrapper validation |
| Old module removal | Deferred until full all-variant wrapper generation passes or user-approved deferral is recorded |
| Site build | Uses checked-in include files; does not run benchmarks |
| Debug artifacts | Optional archive path only |
