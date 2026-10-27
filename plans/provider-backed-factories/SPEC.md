# Provider-backed factories

## Source

- Accepted design: this chat's confirmed grill-me outcome, including choices 1A, 2A and 3A. Extend Sting's existing factory interfaces; keep Rose migration separate; accept abstract and interface return declarations; always honor provider metadata, including on concrete return declarations.
- Execution authority: the user explicitly requested implementation with structured-delivery-workflow, then requested a pushed pull request and babysit-pr. The interview confirmation is complete. Plan review is automatic; implementation proceeds after independent planning review.
- Repository evidence: `StingProcessor.processFactoryMethod` currently rejects abstract/interface return types; `FactoryMethodDescriptor` and `FactoryGenerator` already distinguish the declared return from the instantiated type. `resolveSingleStingProvider` and `deriveProviderQualifiedName` provide framework-neutral, simple-name-based mapping. Arez 0.256 is the release used by the inspected Rose configuration and its component annotation names its generated implementation.

## Problem and outcome

Factory authors currently have to name a framework's generated concrete implementation or maintain a forwarding factory themselves. A factory declaration should expose the authored component type while constructing the implementation selected by its framework metadata, capturing dependencies and accepting caller arguments as today.

## Scope and constraints

Implement Sting processor behavior, focused regression coverage, public documentation and changelog, and an integration proof against actual Arez-generated code. Publish a reviewable PR and maintain the requested babysit heartbeat. Rose migration, constructor-driven AutoFactory, inference annotations, persistence hooks, merging and auto-merge are outside this delivery.

Preserve Sting's simple-name-and-shape integration convention and existing constructor count, accessibility, generic-type, parameter name/type, and dependency rules. A created product need not be a Sting injectable. Existing include/discovery provider restrictions remain unchanged.

## Requirements and acceptance

- R1 / AC1: A declared class or interface with valid StingProvider metadata constructs the mapped concrete assignable class while retaining the declared factory return type. A mapping on a concrete declaration takes precedence over direct construction. Test all three declaration kinds and custom package-private meta-annotations.
- R2 / AC2: Constructor analysis uses the mapped implementation, including extra implementation-only parameters. Caller parameters match by existing names/types; remaining parameters are injected into the factory. Verify shared injected service identity and distinct products with different caller values, including nullable arguments.
- R3 / AC3: A factory waits across annotation-processing rounds for its mapped implementation and unresolved constructor types. Missing implementations eventually produce a specific useful diagnostic; generated implementations compile successfully regardless of processor order. Verify with a real source-generating processor and a separately compiled mapped implementation.
- R4 / AC4: Reject malformed/multiple metadata, nonclass/abstract/nonstatic/generic/unassignable mapped implementations, inaccessible or multiple constructors, and mismatched caller parameters. Abstract/interface declarations without a mapping remain invalid. Existing direct-concrete factories remain supported. Regression tests check diagnostic failure, not placeholder output.
- R5 / AC5: An isolated reproducible compile/run proof uses the released Arez 0.256 packaged processor and core with the changed Sting processor, exercising injected services, caller arguments and actual generated constructors. Resolve runtime dependencies rather than using handwritten replacement implementations. Record exact release, command and outcome in committed evidence and the PR; remove task-only scaffolding at closeout.
- R6 / AC6: Public Factory/StingProvider/framework-integration documentation describes precedence, implementation requirements and unchanged binding semantics; changelog calls out the concrete-return behavior change. Focused checks and the current CI command `tools/check.sh` pass before implementation review. The legacy Buildr command cannot run here because Bundler is unavailable; current CI uses tools/check.sh.

## Significant decisions

| Decision | Rationale and impact | User verification |
| --- | --- | --- |
| Reuse existing StingProvider mapping | Avoid a second factory generator or injection-inference API. Mapping is authoritative even on concrete declarations; fragment/unrelated provider mappings are rejected for construction. | Review precedence and the documented behavior change. |
| Products are constructible classes, not necessarily injectable bindings | Arez entities commonly disable Sting integration. Only the factory needs to participate in the injection graph. | Check ordinary Arez entities work without becoming singleton services. |
| Inspect actual generated constructors | Frameworks can append caller initializer parameters absent from authored constructors. Existing name/type rules remain authoritative. | Check the factory signature matches the generated constructor's caller values. |
| Keep application migration separate | Delivers the reusable library capability without changing Rose persistence or ownership behavior. | Rose sources are untouched. |

## Technical and testing decisions

Reuse provider selection/name expansion in factory analysis. Distinguish mapped-type readiness from ordinary superficial validation; use the existing deferred-element machinery and final-round diagnostics. Keep the existing generator shape unless evidence requires a local correction. Test success, failure, later-round generation and binary metadata in existing processor/integration suites. Use temporary isolated javac scaffolding for the actual Arez proof, avoiding a permanent cross-project build dependency solely for an experiment. Inspect generated source and run the result; exact evidence survives in git/PR after scaffolding removal.

## Open questions

None.
