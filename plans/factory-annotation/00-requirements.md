# Requirements: `@Factory`

## Mission

Introduce a new `@sting.Factory` annotation on interfaces so the Sting annotation processor can generate dependency-injected factory implementations for one or more abstract factory methods.

## Scope

- In scope:
  - New public annotation in `core/`
  - Annotation processor support for validating `@Factory` interfaces
  - Generation of implementation types for valid factory interfaces
  - `@StingProvider` integration so injectors/fragments can include or depend on factory interfaces
  - Processor fixture coverage, integration tests, and documentation updates
- Out of scope:
  - Supporting generic factory types or generic factory methods unless they already fit existing processor constraints
  - Changing existing injector/fragment semantics outside what is required for factory support

## Locked Decisions And Non-Negotiables

- `@Factory` is a new public annotation in package `sting`.
- `@Factory` targets interfaces.
- The interface may declare any number of default methods.
- Candidate factory methods are abstract interface methods that return a value.
- Candidate factory method parameters must match constructor parameter names and types on the created type.
- Candidate factory methods may omit constructor parameters; omitted parameters are expected to be generated/injected by Sting.
- The created type must have exactly one constructor that is accessible from the generated factory implementation.
- Standard annotations should be copied to the generated implementation class.
- Nullability annotations should propagate to generated fields, constructor parameters, and method parameters/returns as appropriate.
- A single `@Factory` type may declare multiple factory methods that create different components.
- The change requires extensive processor tests, integration tests, and documentation updates.

## Behavior Expectations

1. The processor must recognize `@Factory` interfaces and reject invalid declarations with actionable diagnostics.
2. For each valid `@Factory` interface, the processor must generate a concrete implementation that implements the interface and constructs the requested component types.
3. Generated factory implementations must inject omitted constructor dependencies and pass through method-supplied constructor parameters by matching both name and type.
4. Generated types must preserve the existing Sting style for generated annotations, nullability propagation, and whitelisted annotation copying.
5. The `@Factory` model must integrate with Sting provider discovery so other Sting graphs can request or include the factory interface.

## Command Surface / Generated Surface

- New public annotation: `sting.Factory`
- New generated type(s): implementation and any provider-facing artifacts required for Sting graph inclusion
- Updated processor-supported annotation set and generation pipeline

## Quality Gates

- Targeted processor tests for successful generation, invalid definitions, and provider integration
- Integration tests covering injector use of generated factories
- Documentation updates in public docs/README/changelog as appropriate
- Full gate: `bundle exec buildr test`

## Known Intentional Divergences

- None yet

## Open Questions Register

### Q-01

- id: `Q-01`
- status: `resolved`
- question: What provider shape should `@Factory` use for `@StingProvider` integration?
- context: The public docs and one processor error message path describe `@StingProvider` as resolving to an `@Fragment` or `@Injector`, but the current processor implementation already accepts provider classes annotated with `@Injectable`. The `@Factory` design should align with the actual processor model and correct the stale documentation/message text.
- options:
  - `A`: Generate the factory implementation as an `@Injectable` provider that publishes the factory interface, and update `@StingProvider` handling/docs to allow provider classes annotated with `@Injectable`.
  - `B`: Preserve the current `@StingProvider` contract and generate two artifacts: the concrete factory implementation plus a generated `@Fragment`/provider facade that exposes it to Sting graphs.
- tradeoffs:
  - `A`: Smaller surface area, closer to your requested example, and reuses existing injectable graph behavior; it requires docs and diagnostics to be corrected to match existing processor behavior.
  - `B`: Preserves the documented contract and existing provider semantics; it adds more generated types and more processor complexity for a feature that conceptually wants a single implementation class.
- recommended_default: `A`, because it keeps the feature model direct and makes factories behave like ordinary Sting-created components while still integrating with provider discovery.
- user_decision: Resolved to `A`. Generated factories should be `@Injectable` providers that publish the factory interface via `@Typed( FactoryInterface.class )`; docs and stale diagnostics should be corrected because they do not match current processor behavior.
- artifacts_updated:
  - `plans/factory-annotation/00-requirements.md`
  - `plans/factory-annotation/10-implementation-plan.md`
  - `plans/factory-annotation/20-task-board.yaml`

### Q-02

- id: `Q-02`
- status: `resolved`
- question: What generated implementation naming convention should `@Factory` use?
- context: The feature could either follow Sting's existing generated type naming convention or introduce an `Impl` suffix specific to factories.
- options:
  - `A`: Use Sting's existing generated-type convention and generate `Sting_[SimpleName]`.
  - `B`: Generate `[SimpleName]Impl` specifically for factories.
- tradeoffs:
  - `A`: Consistent with the rest of Sting's generated surface and existing generator utilities; less surprising inside this codebase.
  - `B`: Matches common factory examples, but introduces a special-case naming scheme for one feature.
- recommended_default: `A`, because consistency with the rest of Sting's generated code is more valuable than adopting a second naming style.
- user_decision: Resolved to `A`. `@Factory` should use Sting's existing generated-type convention such as `Sting_MyComponentFactory`.
- artifacts_updated:
  - `plans/factory-annotation/00-requirements.md`
  - `plans/factory-annotation/10-implementation-plan.md`
  - `plans/factory-annotation/20-task-board.yaml`
