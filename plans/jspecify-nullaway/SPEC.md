# JSpecify and NullAway migration

## Source

- Shared understanding: completed grill in this chat, explicitly accepted by the user with three amendments: exclude all GWT/J2CL work; execute Proton in its own local Codex chat on a master-based branch and raise a review PR; consume that checkout through a local override without waiting for release.
- Repository evidence: processor production already has a NullMarked package and opt-in NullAway; public runtime and generated sources use legacy annotations; generated paths are excluded from Error Prone; Proton QA invokes plain javac; JavaPoet 0.14.0 drops type-use annotations.
- Build reference: `/Users/peter/Code/realityforge/jdbt/third_party/java/rules.bzl`.

## Problem

Library users cannot express one consistent JSpecify contract across Sting runtime APIs, DI declarations and generated implementations. Existing checks cover processor internals but omit generated code and nested type nullness.

## Required outcome

All owned production, test and example code and valid generated output use JSpecify. NullAway's JSpecify mode checks handwritten and generated bodies at error severity. DI behavior is preserved while legacy annotations at relevant consumer boundaries produce migration errors. Sting builds against the unreleased local Proton changes.

## Scope

- In scope: runtime APIs, processor interpretation and generation, tests/fixtures/goldens, build enforcement, local Proton integration, release packaging, documentation and changelog.
- Out of scope: GWT/J2CL implementation, probes or changes; additional DI request forms; legacy generation compatibility; Proton release or PR merge; publishing Sting.

## Constraints

- Preserve runtime requireNonNull checks, optional binding rules, primitive/boxed interoperability, missing-service behavior, lazy synchronization and nullable-provider caching.
- Preserve existing DI forms, including Optional and Supplier of Optional; reject unsupported nullable DI shapes rather than invent new semantics.
- Support unmarked consumers using NullAway's optimistic external boundary: their implementation bodies and unspecified contracts are outside the guarantee. Preserve explicit JSpecify type-use positions; unannotated DI requests remain required.
- Generate NullMarked types; generated bodies remain checked without blanket NullAway suppression or NullUnmarked holes.
- Retain Java 17 output and a supported compiler; verify the resolved compiler before enabling JSpecifyMode and add the supported type-annotation symbol flag.
- Each source directory owns its BUILD and lists sources explicitly. Keep unrelated user changes intact.
- Temporary committed plan tree is `/Users/peter/.codex/worktrees/3624/sting/plans/jspecify-nullaway`; each task and its evidence are committed before moving on.

## Requirements

- R1: Public runtime APIs accurately express JSpecify nullness, including nullable entries in nonnull interceptor argument arrays and nullable boxed results.
- R2: Consumer outer nullness affects optional DI behavior independently of Java type identity; annotated and unannotated forms of the same supported Java service match.
- R3: Generated implementations preserve explicit type-use nullness in supported signatures and accurately annotate generated state while preserving runtime behavior.
- R4: Legacy nullability annotations at Sting-relevant declarations produce clear migration diagnostics. Existing request shapes and framework extension hooks retain their behavior.
- R5: Every owned sourceful build target and positive processor fixture/generated compilation is checked by NullAway JSpecify mode; a negative control proves generated checking is active.
- R6: Build and release compilation consume the local Proton source checkout immediately, with no requirement for a Maven release; the eventual switch to released Proton is documented.
- R7: Published API dependencies, Javadocs, shaded processor, docs, examples and contributor instructions use the new contract. Diagnostic strings and intentional rejection fixtures are the only retained owned legacy nullability references.

## Acceptance criteria

- AC1 (R1): Core/server compile under strict NullAway with correct array component/root nullness and no owned legacy nullability imports.
- AC2 (R2,R4): Focused source and classfile tests cover required/nullable matching, top-level versus nested annotations, missing optional bindings, legacy diagnostics, and unsupported nullable shapes.
- AC3 (R3): All golden families and runtime integration tests cover required/optional providers, cache counts, primitive binding, factories and interception; emitted types are marked and retain nested/array/override annotations.
- AC4 (R5): Default wrappers enable NullAway for sourceful libraries, binaries, tests and examples, include generated sources, and a deliberately invalid generated source fails specifically with NullAway while valid generated fixtures pass.
- AC5 (R6): Processor and test harness link the local Proton targets, shaded release artifacts contain that local implementation and its required runtime annotations, and metadata explains how to replace the temporary local override after release. A real consumer compilation uses core/JSpecify only on the compilation classpath and only the shaded processor jar on the processorpath, exercising generation and nested nullness preservation.
- AC6 (R7): Release artifact checks establish JSpecify API metadata and no legacy nullability annotation usage; docs/site/Javadoc and full `tools/check.sh` pass.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Breaking JSpecify-only consumer contract | User chose a hard cut | Legacy nullable requests produce errors rather than silently becoming required | Review migration errors and guide |
| D2 | Preserve DI/runtime semantics and shapes | Annotation migration should preserve product behavior | Nullable caching, missing requests and fail-fast behavior remain observable | Review integration coverage |
| D3 | Unmarked consumers use optimistic boundaries | User explicitly accepted this boundary | Generated bodies are checked; unmarked consumer implementations are trusted | Verify documentation distinguishes the guarantee |
| D4 | New Proton defaults; local source override during migration | User requested parallel Proton work and no release wait | This checkout depends on the specified local repository until released | Verify local path and replacement instructions |
| D5 | Full JSpecify mode with Java 17 output | Nested generic/array contracts require the named mode | Supported compiler and flag must be exercised; experimental features remain outside the initial configuration | Review compiler/checker configuration and controls |
| D6 | Exclude GWT/J2CL work | Explicit user scope amendment | No compatibility claim or smoke gate is added for those platforms | Verify diff scope |
| D7 | Automatic plan checkpoint | Accepted grill and requested delivery workflow supply authority | Proceed after read-only planning review and a plan commit | Review committed history and final result |

## Technical decisions

- Use model-based annotation-independent service identity and lookup rather than annotated TypeMirror display strings.
- Use Proton's annotation-preserving conversion and exact JSpecify outer type-use recognition. Distinguish array root nullness from element nullness.
- Use a Bazel local module override targeting Proton's source module and owned core/QA targets; release packaging must use those targets' jars rather than the old Maven jar.
- Resolve processor-classloader runtime annotation needs inside the processor artifact while preserving canonical JSpecify names and the no-runtime-POM-dependency contract. Archive shape checks alone do not establish an isolated processorpath.
- Keep legacy rejection fixtures isolated from the positive checked fixture pipeline; legitimate javax.annotation.processing and Java EE names are unrelated to this migration.
- Wide generator/fixture migration is one coherent cutover because Proton's removed legacy emission constants and generator APIs are shared across generation families.

## Testing decisions

- Run narrow runtime compilation first, then processor golden and behavior targets, then generated NullAway controls, then the full build/test/coverage/site/release gate.
- Verify both source-derived and compiled dependency paths; checking source alone cannot establish classfile type-use preservation.
- Preserve existing tests of runtime behavior, add tests for newly checkable nullness boundaries, regenerate expected and formatted golden trees through the existing family workflow.
- Run the resolved compiler and generated checking controls; cached toolchain inspection and plain javac successes are preliminary evidence only.

## Open questions

None. Proton API signatures and compiler integration details are implementation facts to verify, not reopened product decisions.
