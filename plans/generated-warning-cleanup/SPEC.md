# Generated warning cleanup

## Source and problem

The accepted JSpecify/NullAway grill remains the baseline. The user now permits removing runtime null checks that static checking makes redundant, asks to fix generated code instead of suppressing UnusedVariable, ImpossibleNullComparison and UnnecessaryCheckNotNull, requests removal of legacy-annotation tests from StingNullabilityTest, and asks for current-state documentation with humanize applied.

The generators currently suppress unused fields on every injector and null-check warnings on every binding creator with dependencies. Some generated fields are never read, and required arguments/results already have nonnull JSpecify contracts. The public dependency guide also contains migration instructions.

## Required outcome and scope

Fix the generator decisions that produce these warnings, remove the obsolete legacy test matrix, and make all documentation changed by this PR describe the supported state. Keep the completed Proton source override and artifact-check simplification.

- In scope: generated runtime null checks/assertions, unused injector storage, the named warning suppressions, related fixtures/tests and current PR documentation.
- Out of scope: unrelated handwritten runtime checks, library redesign, GWT/J2CL work, removal of the accepted legacy-rejection processor behavior, and merging the PR.
- Preserve eager creation order/side effects, lazy caching, nullable-provider allocation flags, injector input signatures and erased interceptor conversions.

## Requirements and acceptance

- R1 / AC1: Generated code no longer emits the three named suppressions. Remove null checks and redundant null assertions wherever the declared JSpecify contracts and compilation prove them unnecessary. Preserve actual lazy-initialization branches and meaningful argument-count assertions.
- R2 / AC2: Do not emit unused injector fields. Unrequested eager bindings are still constructed in order; unused input storage is omitted without changing positional input signatures. Existing runtime and golden coverage passes.
- R3 / AC3: Remove the legacyDeclarations provider and rejectLegacyDeclarations test; preserve positive JSpecify, compiled metadata and generated NullAway negative controls. Checking must expose the named warnings rather than disable them globally.
- R4 / AC4: Audit every docs file changed from the PR base. Describe current annotation semantics, supported request forms, compiler/build setup and interceptor contracts. Remove upgrade instructions and migration framing; apply humanize. Update PR prose to reflect removed redundant checks.
- R5 / AC5: Pass focused compiler/runtime checks, both golden trees, full tools/check.sh, read-only review and final CI. Publish the update to PR67 and remove the temporary plan after review.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Trust checked nonnull contracts instead of emitting redundant guards | User explicitly revises the earlier runtime-check preservation decision | Invalid unmarked implementations remain an optimistic boundary and may no longer fail immediately at a generated guard | Review generated calls and boundary documentation |
| D2 | Use graph getUsedBy edges to distinguish storage from construction | Edges include injector outputs and all dependency requests | Keeps eager side effects while removing fields whose values are never used | Review eager-only and unused-input fixtures/runtime tests |
| D3 | Keep the narrow erased Object-to-T conversion helper | Invocation carries nullable Object values and cannot express each service signature | Nullable arguments/results and primitive conversion behavior stay supported; service bodies remain checked | Review interceptor signatures and conversion tests |
| D4 | Remove legacy test coverage without changing processor rejection | User names the obsolete tests; rejection remains an accepted contract | JSpecify coverage remains the regression boundary | Review StingNullabilityTest and diagnostic coverage impact |

## Technical and testing decisions

- Implement the smallest generator changes; remove suppressions before inspecting any resulting diagnostics.
- Reuse the existing golden regeneration families and runtime tests. Add a focused runtime regression only if existing tests do not verify construction without stored results.
- Verify named warning severity and generated-code coverage; do not introduce new blanket suppressions.
- The fixture compiler currently registers only NullAway checkers. Register the three named Error Prone checkers as well, with error severity, so the golden tests exercise the warnings being fixed without enabling unrelated checks on input fixtures.
- Audit docs/agents/java.md, dependency_kinds.md, development_process.md, factories.md, interceptors.md and project_setup.md against the PR base. Preserve useful compiler requirements and supported nullness forms.
- Run focused generation/nullness/integration checks, regenerate expected and formatted outputs, then tools/check.sh. Review the full delivered diff and publish final CI evidence.

## Open questions

None.
